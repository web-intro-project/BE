package com.webintro.wakesong.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS(교차 출처 요청) 설정.
 *
 * <p>브라우저는 보안상 "다른 주소(Origin)"의 서버로 요청을 보내면 막는다.
 * 예) FE는 http://localhost:8000, BE는 http://localhost:8080 → 포트가 다르므로 다른 Origin.
 * 그래서 서버가 "이 FE 주소에서 오는 요청은 받아도 된다"고 알려줘야 한다.
 *
 * <p>허용할 주소는 application.yml의 {@code app.cors.allowed-origins}
 * (환경변수 {@code CORS_ALLOWED_ORIGINS})에서 읽는다. 아무 데서나 허용하는 {@code *}는 쓰지 않는다.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

	/** 허용할 FE 주소 목록. 설정값 "a,b"가 자동으로 배열 ["a", "b"]로 바뀐다. */
	@Value("${app.cors.allowed-origins}")
	private String[] allowedOrigins;

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/api/**")              // /api로 시작하는 모든 주소에 적용
				.allowedOrigins(allowedOrigins)     // 허용한 FE 주소에서 온 요청만
				.allowedMethods("GET", "POST")      // 우리 API가 쓰는 메서드만
				.allowedHeaders("Content-Type");    // POST의 JSON 본문을 위해 필요한 헤더만
	}

}
