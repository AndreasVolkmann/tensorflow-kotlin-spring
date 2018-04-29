package me.avo.spring.kotlin.test

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