package me.avo.spring.kotlin.service.api

import me.avo.spring.kotlin.service.classifier.ObjectDetector
import me.avo.spring.kotlin.service.storage.StorageService
import org.springframework.stereotype.Controller

@Controller
class FileUploadController(private val storageService: StorageService, private val objectDetector: ObjectDetector) {

}