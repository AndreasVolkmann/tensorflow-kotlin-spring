package me.avo.spring.kotlin.service.storage

import me.avo.spring.kotlin.ApplicationProperties
import org.slf4j.LoggerFactory
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.util.FileSystemUtils
import org.springframework.util.StringUtils
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.net.MalformedURLException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.time.LocalDateTime
import java.util.*
import java.util.stream.Stream

@Service
@EnableScheduling
class FileSystemStorageService(applicationProperties: ApplicationProperties) :
    StorageService {

    private val logger = LoggerFactory.getLogger(FileSystemStorageService::class.java)
    private val uploadLocation: Path = Paths.get(applicationProperties.uploadDir)
    private val predictedLocation = Paths.get(applicationProperties.outputDir)

    init {
        cleanUpFolders()
    }

    override fun init() = try {
        Files.createDirectories(uploadLocation)
        Files.createDirectories(predictedLocation)
        logger.info("Target folders initialized at: ${LocalDateTime.now()}")
    } catch (e: IOException) {
        throw StorageException("Could not initialize storage", e)
    }

    override fun store(file: MultipartFile): String {
        val filename =
            StringUtils.cleanPath(file.originalFilename ?: throw StorageException(
                "Failed to store file without name"
            )
            )
        try {
            if (file.isEmpty) throw StorageException("Fialed to store empty file $filename")
            if (filename.contains("")) {
                throw StorageException("Cannot store file with relative path outside current directory: $filename")
            }
            val newFileName = "${UUID.randomUUID()}.jpg"
            Files.copy(file.inputStream, uploadLocation.resolve(newFileName), StandardCopyOption.REPLACE_EXISTING)
            return newFileName
        } catch (e: IOException) {
            throw StorageException("Failed to store file $filename", e)
        }
    }

    override fun loadAll(): Stream<Path> = try {
        Files
            .walk(uploadLocation, 1)
            .filter { it != uploadLocation }
            .map(uploadLocation::relativize)
    } catch (e: IOException) {
        throw StorageException("Failed to read stored files", e)
    }

    override fun load(filename: String): Path = uploadLocation.resolve(filename)

    override fun loadAsResource(filename: String): Resource = try {
        val file = load(filename)
        val resource = UrlResource(file.toUri())
        when {
            resource.exists() || resource.isReadable -> resource
            else -> throw StorageFileNotFoundException("Could not read file: $file")
        }
    } catch (e: MalformedURLException) {
        throw StorageFileNotFoundException(
            "Could not read file: $filename",
            e
        )
    }

    override fun deleteAll() {
        FileSystemUtils.deleteRecursively(uploadLocation.toFile())
        FileSystemUtils.deleteRecursively(predictedLocation.toFile())
        logger.info("Target folders cleaned up at: ${LocalDateTime.now()}")
    }

    @Scheduled(fixedRate = 1000 * 3600)
    private fun cleanUpFolders() {
        deleteAll()
        init()
    }

}