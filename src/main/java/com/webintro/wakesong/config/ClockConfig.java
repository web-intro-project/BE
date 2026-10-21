// 이 파일이 config(설정) 패키지에 속한다고 알려주는 코드
package com.webintro.wakesong.config;

// 시계(Clock)와 시간대(ZoneId) 클래스를 가져오는 코드
import java.time.Clock;
import java.time.ZoneId;

// 스프링 설정용 어노테이션을 가져오는 코드
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * "지금 몇 시인지"를 알려주는 시계를 서울 시간대로 만들어 등록하는 설정.
 * 테스트에서는 이 시계를 고정된 시각으로 바꿔 끼운다.
 */
// 이 클래스가 스프링 설정 클래스임을 표시하는 코드
@Configuration
// 시계 설정 클래스를 선언하는 코드
public class ClockConfig {

	// 서울 시간대(Asia/Seoul)를 상수로 만들어 다른 곳에서도 쓸 수 있게 하는 코드
	public static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");

	// 아래 메서드가 돌려주는 객체를 스프링 빈으로 등록하는 코드
	@Bean
	// Clock 빈을 만들어주는 메서드를 선언하는 코드
	public Clock clock() {
		// 서울 시간대 기준으로 현재 시각을 알려주는 시계를 만들어 돌려주는 코드
		return Clock.system(SEOUL);
	}

}
