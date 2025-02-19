package com.laev.remem.integration

import com.laev.remem.entity.Member
import com.laev.remem.entity.ReviewItem
import com.laev.remem.repository.MemberRepository
import com.laev.remem.repository.ReviewItemRepository
import com.laev.remem.security.JwtTokenProvider
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpHeaders
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
abstract class BaseIntegrationTest {

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Autowired
    protected lateinit var mockMvc: MockMvc

    @Autowired
    protected lateinit var passwordEncoder: BCryptPasswordEncoder

    @Autowired
    private lateinit var jwtTokenProvider: JwtTokenProvider

    @Autowired
    protected lateinit var memberRepository: MemberRepository

    @Autowired
    lateinit var reviewItemRepository: ReviewItemRepository

    private val testEmail = "test@example.com"

    @BeforeEach
    fun setUpMember() {
        val testMember = createOrGetMember(testEmail)
        reviewItemRepository.save(
            ReviewItem(
                mainText = "setup item",
                subText = "",
                member = testMember,
            )
        )
    }

    protected fun getAuthHeaders(): HttpHeaders {
        val testToken = "Bearer ${jwtTokenProvider.generateToken(testEmail)}"
        val headers = HttpHeaders()
        headers.set("Authorization", testToken)
        return headers
    }

    protected fun getInvalidAuthHeaders(): HttpHeaders {
        val testToken = "Bearer invalid_token"
        val headers = HttpHeaders()
        headers.set("Authorization", testToken)
        return headers
    }

    protected fun withAuth(requestBuilder: MockHttpServletRequestBuilder): MockHttpServletRequestBuilder {
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
