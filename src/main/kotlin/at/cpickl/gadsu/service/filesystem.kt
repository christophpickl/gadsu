package at.cpickl.gadsu.service

import at.cpickl.gadsu.GadsuException
import com.google.common.io.Files
import net.lingala.zip4j.core.ZipFile
import net.lingala.zip4j.model.ZipParameters
import net.lingala.zip4j.util.Zip4jConstants
import org.slf4j.LoggerFactory
import java.io.File

fun main(args: Array<String>) {
    FileSystemImpl().zip(File("notes/"), File("tut.zip"))
}

val LOG_File = LoggerFactory.getLogger(File::class.java)

fun File.writeByClasspath(classpath: String) {
    LOG_File.debug("writeByClasspath(classpath='{}')", classpath)
    val stream = FileSystemImpl::class.java.getResourceAsStream(classpath) ?: throw IllegalArgumentException("Not existing classpath resource '$classpath'!")
    val buffer = ByteArray(stream.available())
    stream.read(buffer)
    Files.write(buffer, this)
}

fun File.ensureExtension(extension: String): File {
    if (name.toLowerCase().endsWith(extension)) {
        return this
    }
    return File(parentFile, "$name.$extension")
}

interface FileSystem {

    fun listFiles(directory: File, filterSuffix: String? = null): List<File>

    fun ensureExists(directory: File)

    fun zip(directoryToZip: File, targetZipFile: File)

    fun delete(toDelete: File)
}

class FileSystemImpl : FileSystem {
    private val log = LOG(javaClass)

    override fun ensureExists(directory: File) {
        if (directory.exists() == false) {
            log.debug("Creating new directory to ensure it exists: {}", directory.absolutePath)
            val successfull = directory.mkdirs()
            if (!successfull) {
                throw GadsuException("Could not create dirs: ${directory.absolutePath}")
            }
        }
    }

    override fun listFiles(directory: File, filterSuffix: String?): List<File> {
        if (directory.exists() == false) {
            throw GadsuException("Could not list files as directory does not exist: ${directory.absolutePath}")
        }
        if (filterSuffix == null) {
            return directory.listFiles().toList()
        }
        return directory.listFiles { file, s -> s.endsWith(filterSuffix) }.toList()
    }

    override fun zip(directoryToZip: File, targetZipFile: File) {
        log.debug("zip(directory={}, target={}, filter)", directoryToZip.absolutePath, targetZipFile.absolutePath)
        val zipFile = ZipFile(targetZipFile)
        val params = ZipParameters()
        params.compressionLevel = Zip4jConstants.DEFLATE_LEVEL_MAXIMUM
        params.compressionMethod = Zip4jConstants.COMP_DEFLATE
        zipFile.addFolder(directoryToZip, params)
    }

    override fun delete(toDelete: File) {
        log.trace("delete(toDelete={})", toDelete.absolutePath)
        if (toDelete.delete() == false) {
            throw GadsuException("Could not delete: ${toDelete.absolutePath}")
        }
    }

}
