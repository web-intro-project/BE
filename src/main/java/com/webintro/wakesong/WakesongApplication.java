package com.webintro.wakesong;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 기상송 백엔드 시작점.
 *
 * <p>{@code @SpringBootApplication}이 이 패키지(com.webintro.wakesong)와 하위 패키지를 훑어서
 * {@code @RestController}, {@code @Service}, {@code @Configuration} 등을 찾아 자동으로 등록한다.
 */
@SpringBootApplication
public class WakesongApplication {

	/** main을 실행하면 내장 톰캣 서버가 뜨고(기본 8080 포트) API 요청을 받기 시작한다. */
	public static void main(String[] args) {
		SpringApplication.run(WakesongApplication.class, args);
	}

}
