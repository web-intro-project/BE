package com.webintro.wakesong.common;

import java.time.Clock;
import java.time.ZoneId;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TimeConfig {

	/** 날짜 판단은 서버 OS 시간대와 무관하게 항상 서울 기준이다. */
	public static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");

	@Bean
	public Clock clock() {
		return Clock.system(SEOUL);
	}

}
