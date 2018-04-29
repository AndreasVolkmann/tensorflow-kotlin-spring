package me.avo.spring.kotlin

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("application")
class ApplicationProperties(
    val graph: String,
    val label: String,
    val outputDir: String,
    val uploadDir: String,
    val imageSize: Int,
    val imageMean: Float
)