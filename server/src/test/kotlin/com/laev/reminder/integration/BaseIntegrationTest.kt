package com.laev.reminder.integration

import com.laev.reminder.entity.Member
import com.laev.reminder.repository.MemberRepository
import com.laev.reminder.security.JwtTokenProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder

@SpringBootTest
@AutoConfigureMockMvc
abstract class BaseIntegrationTest {

    @Autowired
    protected lateinit var mockMvc: MockMvc

    @Autowired
    protected lateinit var passwordEncoder: BCryptPasswordEncoder

    @Autowired
    protected lateinit var memberRepository: MemberRepository

    @Autowired
    private lateinit var jwtTokenProvider: JwtTokenProvider

    protected fun withAuth(requestBuilder: MockHttpServletRequestBuilder): MockHttpServletRequestBuilder {
        val testEmail = "test@example.com"
        val testToken = "Bearer ${jwtTokenProvider.generateToken(testEmail)}"

        return requestBuilder.header("Authorization", testToken)
    }

    protected fun createOrGetMember(
        email: String,
        password: String = "1234567890_1234567890_1234567890_default_password",
        name: String = "default_name"
    ): Member {
        val hashedPassword = passwordEncoder.encode(password)
        return memberRepository.findByEmail(email) ?: memberRepository.save(
            Member(email = email, password = hashedPassword, name = name)
        )
    }
}