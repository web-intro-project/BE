// 이 파일이 com.webintro.wakesong 패키지에 속한다고 알려주는 코드
package com.webintro.wakesong;

// 스프링 부트 실행에 필요한 클래스를 가져오는 코드
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 기상송 백엔드 서버의 시작점.
 */
// 이 패키지와 하위 패키지의 컨트롤러·서비스·설정을 자동으로 찾아 등록해주는 코드
@SpringBootApplication
// 애플리케이션 시작 클래스를 선언하는 코드
public class WakesongApplication {

	// 프로그램을 실행하면 가장 먼저 호출되는 main 메서드를 선언하는 코드
	public static void main(String[] args) {
		// 스프링 부트를 실행해서 내장 톰캣 서버(기본 8080 포트)를 띄우는 코드
		SpringApplication.run(WakesongApplication.class, args);
	}

}
