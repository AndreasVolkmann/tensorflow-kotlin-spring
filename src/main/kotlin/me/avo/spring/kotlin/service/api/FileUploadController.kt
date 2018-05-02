package me.avo.spring.kotlin.service.api

import me.avo.spring.kotlin.service.classifier.ObjectDetector
import me.avo.spring.kotlin.service.exception.ServiceException
import me.avo.spring.kotlin.service.storage.StorageService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@Controller
class FileUploadController @Autowired constructor(
    private val storageService: StorageService,
    private val objectDetector: ObjectDetector
) {

    @GetMapping("/")
    fun listUploadedFiles(model: Model) = "upload-image"

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    fun serveFile(@PathVariable filename: String): ResponseEntity<Resource> {
        val file = storageService.loadAsResource(filename)
        return ResponseEntity
            .ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachement; filename=\"${file.filename}\"")
            .body(file)
    }

    @PostMapping("/")
    fun handleFileUpload(@RequestParam("file") file: MultipartFile, model: Model): String {
        val originalImagePath = "/upload-dir/${storageService.store(file)}"
        val result = objectDetector.detect(".$originalImagePath")
        with(model) {
            addAttribute("originalName", file.originalFilename)
            addAttribute("originalImage", originalImagePath)
            addAttribute("predictedImage", result["labeledFilePath"])
            addAttribute("recognitions", result["recognitions"])
        }

        return "display-result"
    }

    @ExceptionHandler(ServiceException::class)
    fun handleStorageFileNotFound(ex: ServiceException) = ResponseEntity.notFound().build<Any>()

}