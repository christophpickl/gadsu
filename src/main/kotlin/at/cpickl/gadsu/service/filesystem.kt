package at.cpickl.gadsu.service

import at.cpickl.gadsu.GadsuException
import com.google.common.io.Files
import net.lingala.zip4j.core.ZipFile
import net.lingala.zip4j.model.ZipParameters
import net.lingala.zip4j.util.Zip4jConstants
import org.slf4j.LoggerFactory
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

fun main(args: Array<String>) {
//    FileSystemImpl().zip(File("notes/"), File("tut.zip"))
    File("foo.pdf").writeByClasspath("/gadsu/reports/form/Anamnesebogen.pdf", true)
}

val LOG_File = LoggerFactory.getLogger(File::class.java)

object ChooseFile {
    fun savePdf(
            fileTypeLabel: String,
            onSuccess: (File) -> Unit,
            proposedFilename: String = fileTypeLabel,
            currentDirectory: File? = null
    ) {
        val chooser = JFileChooser()
        if (currentDirectory != null) chooser.currentDirectory = currentDirectory
        chooser.dialogTitle = "$fileTypeLabel speichern"
        chooser.selectedFile = File("$proposedFilename.pdf")
        val pdfFilter = FileNameExtensionFilter("PDF Dateien (*.pdf)", "pdf")
        chooser.addChoosableFileFilter(pdfFilter)
        chooser.fileFilter = pdfFilter
        val retrival = chooser.showSaveDialog(null)
        if (retrival != JFileChooser.APPROVE_OPTION) {
            return
        }
        val pdfTarget = chooser.selectedFile.ensureExtension("pdf")
        onSuccess.invoke(pdfTarget)
    }
}

fun File.readContent(): String {
    return Files.toString(this, Charsets.UTF_8)
}

fun File.writeByClasspath(classpath: String, overwrite: Boolean = false) {
    LOG_File.debug("writeByClasspath(classpath='{}')", classpath)
    if (this.exists()) {
        if (overwrite) {
            LOG_File.trace("Deleting yet existing file at: {}", this.absolutePath)
            val wasDeleted = this.delete()
            if (!wasDeleted) {
                throw IOException("File could not be deleted at: ${this.absolutePath}")
            }
        } else {
            throw IOException("File already exists at: ${this.absolutePath}")
        }
    }

    val input = this.javaClass.getResourceAsStream(classpath) ?: throw IllegalArgumentException("Not existing classpath resource '$classpath'!")
    val output = BufferedOutputStream(FileOutputStream(this))
    try {
        val buffer = ByteArray(4096)
        var readBytes = input.read(buffer)
        println("readBytes: $readBytes")
        while (readBytes > 0) {
            output.write(buffer, 0, readBytes)
            readBytes = input.read(buffer)
            println("readBytes: $readBytes")
        }
    } finally {
        input.close()
        output.close()
    }
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
