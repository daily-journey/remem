package com.laev.remem.security

import com.laev.remem.repository.MemberRepository
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component
import java.security.Key
import java.util.*

@Component
class JwtTokenProvider(
    private val memberRepository: MemberRepository,
    @Value("\${security.jwt.secret-key}") private val secretKey: String
) {
    private val SECRET_KEY: Key = Keys.hmacShaKeyFor(secretKey.toByteArray())

    fun generateToken(email: String): String {
        return Jwts.builder()
            .setSubject(email)
            .setIssuedAt(Date()) // Set the creation time of the token
            .signWith(SECRET_KEY)
            .compact()
    }

    fun validateTokenAndGetAuthentication(token: String): Authentication {
        val email = Jwts.parserBuilder()
            .setSigningKey(SECRET_KEY)
            .build()
            .parseClaimsJws(token)
            .body
            .subject

        val member = memberRepository.findByEmail(email)
            ?: throw UsernameNotFoundException("User not found")

        // Create and return an Authentication object for Spring Security
        return UsernamePasswordAuthenticationToken(
            member.email, // Principal: typically email or username
            null,         // Credentials: usually set to null
            emptyList()   // Authorities: list of roles or permissions (e.g., ROLE_USER)
        )
    }

    fun extractEmail(token: String): String {
        return try {
            val claims = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY) // Set the signing key for verification
                .build()
                .parseClaimsJws(token) // Parse the JWT
                .body

            claims.subject // Return the subject (email)
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid token: Unable to extract email")
        }
    }
}
