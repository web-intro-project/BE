// 이 파일이 config(설정) 패키지에 속한다고 알려주는 코드
package com.webintro.wakesong.config;

// 설정값 주입(@Value)과 설정 클래스용 어노테이션을 가져오는 코드
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
// CORS 설정에 필요한 스프링 MVC 클래스를 가져오는 코드
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS 설정: 다른 주소(FE)에서 오는 브라우저 요청을 허용해주는 설정.
 * 예) FE http://localhost:8000 → BE http://localhost:8080 (포트가 달라서 허용이 필요)
 */
// 이 클래스가 스프링 설정 클래스임을 표시하는 코드
@Configuration
// 스프링 MVC 설정을 바꿀 수 있도록 WebMvcConfigurer를 구현하는 클래스를 선언하는 코드
public class CorsConfig implements WebMvcConfigurer {

	// application.yml의 app.cors.allowed-origins 값을 읽어오는 코드 ("a,b" → ["a", "b"])
	@Value("${app.cors.allowed-origins}")
	// 허용할 FE 주소 목록을 담는 필드를 선언하는 코드
	private String[] allowedOrigins;

	// 부모(WebMvcConfigurer)의 메서드를 다시 정의한다고 표시하는 코드
	@Override
	// CORS 규칙을 등록하는 메서드를 선언하는 코드
	public void addCorsMappings(CorsRegistry registry) {
		// /api로 시작하는 모든 주소에 CORS 규칙을 적용하는 코드
		registry.addMapping("/api/**")
				// 설정에 적힌 FE 주소에서 온 요청만 허용하는 코드
				.allowedOrigins(allowedOrigins)
				// GET, POST 요청만 허용하는 코드
				.allowedMethods("GET", "POST")
				// JSON 본문을 보낼 때 필요한 Content-Type 헤더만 허용하는 코드
				.allowedHeaders("Content-Type");
	}

}
