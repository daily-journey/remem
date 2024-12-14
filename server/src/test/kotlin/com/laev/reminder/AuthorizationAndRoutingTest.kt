package com.laev.reminder

import com.laev.reminder.integration.BaseIntegrationTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class AuthorizationAndRoutingTest : BaseIntegrationTest() {

    @Test
    fun `should return 401 when requesting an invalid URL with invalid token`() {
        val headers = getInvalidAuthHeaders()
        val request = HttpEntity<String>(null, headers)
        val response = restTemplate.exchange("/non-existent-url", HttpMethod.GET, request, String::class.java)
        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
    }

    @Test
    fun `should return 404 when requesting an invalid URL with valid token`() {
        val headers = getAuthHeaders()
        val request = HttpEntity<String>(null, headers)
        val response = restTemplate.exchange("/non-existent-url", HttpMethod.GET, request, String::class.java)
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun `should return 401 when there is invalid token`() {
        val headers = getInvalidAuthHeaders()
        val request = HttpEntity<String>(null, headers)
        val response = restTemplate.exchange("/review-items", HttpMethod.GET, request, String::class.java)
        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
    }
}