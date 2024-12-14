package com.laev.reminder.integration

import com.laev.reminder.dto.SignInRequest
import com.laev.reminder.dto.SignUpRequest
import com.laev.reminder.utils.ObjectMapperUtil
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class AuthTest: BaseIntegrationTest() {
    private val objectMapper = ObjectMapperUtil.createObjectMapper()

    @BeforeEach
    fun setUp() {
        memberRepository.deleteAll()
    }

    @Test
    fun `Sign up should create a new member when the input is valid`() {
        val request = SignUpRequest(
            email = "test@example.com",
            password = "123456789_123456789_123456789_abcdef",
            name = "tester",
        )

        performPostSignUp(request)
            .andExpect(MockMvcResultMatchers.status().isCreated)

        val newMember = memberRepository.findByEmail(request.email)
        assertNotNull(newMember) // Ensure the member is created
        assertEquals(request.email, newMember?.email) // Validate email
        assertEquals(request.name, newMember?.name) // Validate name
    }

    @Test
    fun `When signing up, invalid email formats should return Bad Request`() {
        val invalidEmails = listOf(
            "",
            "plainaddress",
            "missingatsign.com",
            "@missinglocalpart.com",
            "missingdot@com"
        )

        invalidEmails.forEach { email ->
            val request = SignUpRequest(
                email = email,
                password = "123456789_123456789_123456789_abcdef",
                name = "tester"
            )

            performPostSignUp(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
        }
    }

    @Test
    fun `When signing up, the email must not be duplicated with existing email`() {
        val duplicatedEmail = "duplicate@example.com"
        createOrGetMember(duplicatedEmail)

        val request = SignUpRequest(
            email = duplicatedEmail,
            password = "123456789_123456789_123456789_abcdef",
            name = "tester",
        )

        performPostSignUp(request)
            .andExpect(MockMvcResultMatchers.status().isConflict)
            .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("The email '$duplicatedEmail' is already registered."))
    }

    @Test
    fun `When signing up, the password must be at least 36 characters long`() {
        val request = SignUpRequest(
            email = "test@example.com",
            password = "123456789_123456789_123456789_abcde", // 35 characters
            name = "short_password_user",
        )

        performPostSignUp(request)
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun `Sign in should return a JWT token in the Authorization header when credentials are valid`() {
        // create a member
        val email = "new@example.com"
        val password = "123456789_123456789_123456789_abcdef"
        createOrGetMember(email, password, "tester")

        // sign-in
        val signInRequest = SignInRequest(email, password)

        val result = mockMvc.perform(
            MockMvcRequestBuilders.post("/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signInRequest))
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.header().exists("Authorization"))
            .andReturn()

        // check JWT token in Authorization header
        val authorizationHeader = result.response.getHeader("Authorization")
        assertNotNull(authorizationHeader)
        authorizationHeader?.let { assert(it.startsWith("Bearer ")) }
    }

    @Test
    fun `Sign in should return unauthorized when credentials are invalid`() {
        // create a member
        val email = "test@example.com"
        val password = "123456789_123456789_123456789_abcdef"
        createOrGetMember(email, password, "tester")

        // sign-in with wrong password
        val invalidLoginRequest = SignInRequest(email, "123456789_123456789_123456789_wrong_password")

        mockMvc.perform(
            MockMvcRequestBuilders.post("/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidLoginRequest))
        )
            .andExpect(MockMvcResultMatchers.status().isUnauthorized)
            .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Invalid email or password"))
    }

    private fun performPostSignUp(request: SignUpRequest) = mockMvc.perform(
        MockMvcRequestBuilders.post("/auth/sign-up")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
    )
}