import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig : WebMvcConfigurer {

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")  // 모든 엔드포인트에 대해
            .allowedOrigins("http://localhost:5173")  // 허용할 도메인
            .allowedMethods("GET", "POST", "PUT", "DELETE")  // 허용할 HTTP 메소드
            .allowedHeaders("*")  // 허용할 헤더들
    }
}