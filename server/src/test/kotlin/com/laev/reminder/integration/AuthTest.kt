package com.laev.reminder.integration

import com.laev.reminder.dto.SignUpRequest
import com.laev.reminder.entity.Member
import com.laev.reminder.repository.MemberRepository
import com.laev.reminder.utils.ObjectMapperUtil
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@SpringBootTest
@AutoConfigureMockMvc
class AuthTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val memberRepository: MemberRepository,
) {
    private val objectMapper = ObjectMapperUtil.createObjectMapper()

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
        createMember(duplicatedEmail)

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

    private fun performPostSignUp(request: SignUpRequest) = mockMvc.perform(
        MockMvcRequestBuilders.post("/auth/sign-up")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
    )

    private fun createMember(email: String, password: String = "default_password", name: String = "default_name") {
        memberRepository.save(Member(email = email, password = password, name = name))
    }
}