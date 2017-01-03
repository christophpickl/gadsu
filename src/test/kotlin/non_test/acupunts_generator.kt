package non_test

import at.cpickl.gadsu.service.LOG
import at.cpickl.gadsu.service.formatDateTime
import at.cpickl.gadsu.tcm.model.Meridian
import com.google.common.io.Files
import org.joda.time.DateTime
import org.jopendocument.model.OpenDocument
import org.jopendocument.model.table.TableTable
import java.io.File
import java.io.FileNotFoundException
import java.nio.charset.Charset
import java.util.LinkedList

private val ODS_SOURCE_PATH = "gadsu/src/misc/acupoints.ods"
private val KT_TARGET_PATH = "gadsu/src/main/kotlin/gadsu/generated/acupuncts.kt"

fun main(args: Array<String>) {
    AcupunctsGenerator.generate()
}

object AcupunctsGenerator {
    fun generate() {
        println("Read ...")
        val acupuncts = AcupunctsReader().read(ODS_SOURCE_PATH)
        println(acupuncts.joinToString("\n"))

        println("Write ...")
        AcupunctsWriter().write(acupuncts, KT_TARGET_PATH)

        println("Done!")
    }
}

data class OdsAcupunct(
        val meridian: Meridian,
        val number: Int,
        val germanName: String,
        val chineseName: String,
        val note: String,
        val localisation: String,
        val joinedIndications: String,
        val flags: String
)


class AcupunctsReader {
    companion object {

        private var colCount = 0
        private val COL_MERIDIAN = colCount++
        private val COL_NUMBER = colCount++
        private val COL_NAME_DE = colCount++
        private val COL_NAME_CH = colCount++
        private val COL_FLAGS = colCount++
        private val COL_LOCALISATION = colCount++
        private val COL_INDICATIONS = colCount++
        private val COL_NOTES = colCount++

        private val COL_LAST = colCount - 1

        private fun meridianByLabel(label: String) = Meridian.values().first { it.labelShort == label }
    }

    private val log = LOG(javaClass)

    fun read(odsPath: String): List<OdsAcupunct> {
        val doc = openDoc(odsPath)

        val table: TableTable = doc.body.officeSpreadsheets[0].tables[0]

        var recentMeridian: Meridian? = null
        val result = LinkedList<OdsAcupunct>()
        var i = 1
        while (true) {
            val row = table.rows[i++]
            val cells = row.getCellsInRange(0, COL_LAST)
            val rawMeridian = cells[COL_MERIDIAN].fullText
            val meridian = if (rawMeridian.isEmpty()) recentMeridian!! else meridianByLabel(rawMeridian)
            recentMeridian = meridian
            val rawNumber = cells[COL_NUMBER].fullText

            if (rawNumber.isEmpty()) break

            result.add(OdsAcupunct(
                    meridian = meridian,
                    number = rawNumber.toInt(),
                    germanName = cells[COL_NAME_DE].fullText,
                    chineseName = cells[COL_NAME_CH].fullText,
                    note = cells[COL_NOTES].fullText,
                    localisation = cells[COL_LOCALISATION].fullText,
                    joinedIndications = cells[COL_INDICATIONS].fullText,
                    flags = cells[COL_FLAGS].fullText
            ))
        }
        return result

    }

    private fun openDoc(odsPath: String) = OpenDocument().apply {
        val file = File(odsPath)
        if (!file.exists()) {
            throw FileNotFoundException("File does not exist at: ${file.absolutePath}")
        }
        log.info("Load ODS: ${file.name}")
        loadFrom(file)
    }
}

private object FlagMapper {

    private val FLAGS = mapOf<String, String>(
            "!!!" to "AcupunctFlag.Marinaportant",
            "ort" to "AcupunctFlag.Orientation",
            "bo" to "AcupunctFlag.BoPoint.Companion.",
            "yu" to "AcupunctFlag.YuPoint.Companion.",
            "holz" to "AcupunctFlag.ElementPoint.Wood",
            "feuer" to "AcupunctFlag.ElementPoint.Fire",
            "erde" to "AcupunctFlag.ElementPoint.Earth",
            "metall" to "AcupunctFlag.ElementPoint.Metal",
            "wasser" to "AcupunctFlag.ElementPoint.Water",
            "orig" to "AcupunctFlag.OriginalPoint",
            "nex" to "AcupunctFlag.NexusPoint",
            "master" to "AcupunctFlag.MasterPoint",
            //    TODO override fun onKeyPoint(flag: AcupunctFlag.KeyPoint) = "${flag.labelShort} ${flag.meridianx.label}"
            "ton" to "AcupunctFlag.TonePoint",
            "sed" to "AcupunctFlag.SedatePoint",
            "jing" to "AcupunctFlag.JingPoint"
//    TODO override fun onEntryPoint(flag: AcupunctFlag.EntryPoint) = "${flag.labelShort} ${flag.meridian.labelShort}"

    )

    /**
     * @param input e.g.: "wichtig, Bo"
     * @return "AcupunctFlag.BoPoint.Companion.Lung, AcupunctFlag.Marinaportant"
     */
    fun mapFlags(input: String): String {
        return input.split(",").map {
            val singleFlagText = it.trim().toLowerCase()
            val before = """
                """
            if (singleFlagText.startsWith("bo") || singleFlagText.startsWith("yu")) {
                if (singleFlagText.length <= 2) {
                    throw IllegalArgumentException("Forgot to define meridian, huh?! ;) Flag was '$singleFlagText'.")
                }
                before + flagFor(singleFlagText.substring(0, 2)) + Meridian.byLabelShort(singleFlagText.substring(3, singleFlagText.length))
            } else {
                before + flagFor(singleFlagText)
            }
        }.joinToString(", ")
    }

    private fun flagFor(key: String) = FLAGS[key] ?: throw IllegalArgumentException("Invalid flag key: '$key'! (Allowed: ${FLAGS.keys.joinToString(", ")})")
}

class AcupunctsWriter {

    private val log = LOG(javaClass)


    fun write(acupuncts: List<OdsAcupunct>, targetPath: String) {
        val acupunctsText = StringBuilder()
        acupuncts.forEach { punct ->
            punct.apply {

                val meridian3ESafe =
                        if (meridian == Meridian.TripleBurner) "EEE"
                        else meridian.labelShort
                val flagsText =
                        if (flags.isEmpty()) "emptyList<AcupunctFlag>()"
                        else "listOf<AcupunctFlag>(${FlagMapper.mapFlags(flags)})"

                acupunctsText.append("""
    val $meridian3ESafe$number = Acupunct.build(
        meridian = Meridian.${meridian.name},
        number = $number,
        germanName = "$germanName",
        chineseName="$chineseName",
        note = "$note",
        localisation = "$localisation",
        joinedIndications = "$joinedIndications",
        flags = $flagsText
    )
""")
            }
        }

        val content = StringBuilder()
        content.append(
                """package gadsu.generated

import at.cpickl.gadsu.acupuncture.Acupunct
import at.cpickl.gadsu.acupuncture.AcupunctFlag
import at.cpickl.gadsu.tcm.model.Meridian

//
// !!! ATTENTION !!!
// ---------------------------------------
//
// this is a GENERATED file! dont edit it!
// see: AcupunctsGenerator.generate()
//
// generated: ${DateTime.now().formatDateTime()}
//
@Suppress("unused")
object Acupuncts {
$acupunctsText
}
""")

        val target = File(targetPath)
        log.info("Write acupunct content to: {}", target.absolutePath)
        Files.write(content, target, Charset.defaultCharset())
    }

}
