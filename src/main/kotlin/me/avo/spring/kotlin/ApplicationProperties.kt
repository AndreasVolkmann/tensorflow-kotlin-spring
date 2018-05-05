package me.avo.spring.kotlin

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@ConfigurationProperties(prefix = "application")
@Configuration
class ApplicationProperties {

    lateinit var graph: String
    lateinit var label: String
    lateinit var outputDir: String
    lateinit var uploadDir: String
    var imageSize: Int = 0
    var imageMean: Float = 0f

}