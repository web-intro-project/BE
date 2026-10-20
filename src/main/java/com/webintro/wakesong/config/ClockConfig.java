package com.webintro.wakesong.config;

import java.time.Clock;
import java.time.ZoneId;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** "오늘"은 서버가 어디서 돌든 항상 서울 기준. 테스트에서는 이 Clock을 고정 시각으로 바꾼다. */
@Configuration
public class ClockConfig {

	public static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");

	@Bean
	public Clock clock() {
		return Clock.system(SEOUL);
	}

}
