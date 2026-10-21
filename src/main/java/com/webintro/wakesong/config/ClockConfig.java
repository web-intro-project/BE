package com.webintro.wakesong.config;

import java.time.Clock;
import java.time.ZoneId;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * "지금 몇 시인지"를 알려주는 시계(Clock) 설정.
 *
 * <p>왜 필요한가?
 * <ul>
 *   <li>서비스의 "오늘"은 항상 서울 기준이어야 한다. 서버가 해외(UTC)에서 돌아도
 *       서울 날짜로 판단하도록 시계에 서울 시간대를 박아 둔다.</li>
 *   <li>코드에서 {@code LocalDate.now()}를 직접 쓰지 않고 이 Clock을 주입받아 쓰면,
 *       테스트에서 시계를 원하는 시각으로 고정할 수 있다. (테스트의 ApiTestSupport 참고)</li>
 * </ul>
 */
@Configuration
public class ClockConfig {

	/** 서울 시간대. 다른 곳(테스트 등)에서도 같은 값을 쓰도록 상수로 공개한다. */
	public static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");

	/**
	 * 서울 시간대의 실제 시계를 스프링 빈으로 등록한다.
	 * {@code Clock}을 생성자 파라미터로 받는 클래스(SongService)에 자동으로 들어간다.
	 */
	@Bean
	public Clock clock() {
		return Clock.system(SEOUL);
	}

}
