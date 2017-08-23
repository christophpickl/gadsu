package at.cpickl.gadsu.export

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.image.MyImage
import at.cpickl.gadsu.image.defaultImage
import at.cpickl.gadsu.treatment.Treatment
import com.google.common.base.MoreObjects
import com.google.common.io.BaseEncoding
import com.google.inject.AbstractModule
import com.thoughtworks.xstream.XStream
import com.thoughtworks.xstream.converters.Converter
import com.thoughtworks.xstream.converters.MarshallingContext
import com.thoughtworks.xstream.converters.UnmarshallingContext
import com.thoughtworks.xstream.io.HierarchicalStreamReader
import com.thoughtworks.xstream.io.HierarchicalStreamWriter
import com.thoughtworks.xstream.security.NoTypePermission
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import org.slf4j.LoggerFactory
import java.util.ArrayList


class ExportModule : AbstractModule() {
    override fun configure() {
        bind(ExportService::class.java).to(ExportXstreamService::class.java)
    }
}


class ExportData(val created: DateTime, _clients: List<Client>, _treatments: List<Treatment>) {
    val clients: List<Client>
    // data version
    val treatments: List<Treatment>
    init {
        clients = ArrayList(_clients)
        treatments = ArrayList(_treatments)
    }

    override fun toString() = MoreObjects.toStringHelper(javaClass)
            .add("created", created)
            .add("clients", clients)
            .add("treatments", treatments)
            .toString()

}

interface ExportService {
    fun export(toExport: ExportData): String
    fun import(xml: String): ExportData
}

class ExportXstreamService : ExportService {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun export(toExport: ExportData): String {
        log.info("export(toExport)")
        return xstream().toXML(toExport)
    }

    override fun import(xml: String): ExportData {
        log.info("export(toExport)")
        val export = xstream().fromXML(xml) as ExportData

        return ExportData(
                export.created,
                export.clients.map {

                    // YES, converter might have set to null (although kotlin disallowed)
                    val maybeMyImage: MyImage? = it.picture
                    if (maybeMyImage == null) {
                        it.copy(picture = it.gender.defaultImage)
                    } else {
                        it
                    }
                },
                export.treatments)
    }

    private fun xstream(): XStream {
        val xstream = XStream()

        // http://x-stream.github.io/graphs.html
        xstream.setMode(XStream.XPATH_RELATIVE_REFERENCES)
        xstream.addPermission(NoTypePermission.NONE)

        xstream.registerConverter(MyImageConverter())
        xstream.registerConverter(JodaDateTimeConverter())
        xstream.alias("treatment", Treatment::class.java)
        xstream.alias("client", Client::class.java)
        xstream.alias("gadsuExport", ExportData::class.java)
        return xstream
    }

}


private class JodaDateTimeConverter : BaseConverter<DateTime>(DateTime::class.java, "dateTime") {
    private val formatter = ISODateTimeFormat.dateTime()

    override fun _marshal(source: DateTime) = formatter.print(source)!!
    override fun _unmarshal(read: String): DateTime = formatter.parseDateTime(read)
}

private class MyImageConverter : BaseConverter<MyImage>(MyImage::class.java, "myImage") {
    private val base64 = BaseEncoding.base64()

    override fun _marshal(source: MyImage) = if (source.isUnsavedDefaultPicture) null else base64.encode(source.toSaveRepresentation()!!)
    override fun _unmarshal(read: String) = MyImage.byByteArray(base64.decode(read))

}

abstract private class BaseConverter<T>(private val targetClass: Class<T>, private val nodeName: String) : Converter {

    abstract protected fun _marshal(source: T): String?
    abstract protected fun _unmarshal(read: String): T?

    override final fun canConvert(type: Class<*>): Boolean {
        return targetClass.isAssignableFrom(type)
    }

    @Suppress("UNCHECKED_CAST")
    override final fun marshal(source: Any, writer: HierarchicalStreamWriter, context: MarshallingContext) {
        val marshalled = _marshal(source as T)

        writer.startNode(nodeName)
        if (marshalled != null) {
            writer.setValue(marshalled)
        }
        writer.endNode()
    }

    override final fun unmarshal(reader: HierarchicalStreamReader, context: UnmarshallingContext): T? {
        reader.moveDown()
        val read = reader.value
        reader.moveUp()
        if (read.isEmpty()) {
            return null
        }
        return _unmarshal(read)
    }

}

