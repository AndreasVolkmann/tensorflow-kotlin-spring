package me.avo.spring.kotlin.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
@EnableWebMvc
class WebMvcConfig: WebMvcConfigurer {

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/upload-dir/**").addResourceLocations("file:./upload-dir/")
        registry.addResourceHandler("/predicted/**").addResourceLocations("file:./predicted/")
        super.addResourceHandlers(registry)
    }

}