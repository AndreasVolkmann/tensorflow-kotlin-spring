package me.avo.spring.kotlin

import me.avo.spring.kotlin.service.storage.StorageService
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
@EnableConfigurationProperties(ApplicationProperties::class)
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}

@Bean fun init(storageService: StorageService) = CommandLineRunner {
    storageService.deleteAll()
    storageService.init()
}