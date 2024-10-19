package com.laev.reminder

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@SpringBootApplication
class ReminderApplication {

	@Bean
	fun corsConfigurer(): WebMvcConfigurer {
		return object : WebMvcConfigurer {
			override fun addCorsMappings(registry: CorsRegistry) {
				registry.addMapping("/**") // Apply CORS settings to all endpoints
					.allowedOrigins("http://localhost:5173") // Allowed domain
					.allowedMethods("*") // Allow all HTTP methods
					.allowedHeaders("*") // Allow all headers
					.allowCredentials(true) // Allow credentials (cookies, etc.)
			}
		}
	}
}

fun main(args: Array<String>) {
	runApplication<ReminderApplication>(*args)
}