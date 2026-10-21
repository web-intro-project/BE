package com.webintro.wakesong;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.test.web.servlet.MockMvc;

import com.webintro.wakesong.config.ClockConfig;
import com.webintro.wakesong.domain.SongRequest;
import com.webintro.wakesong.repository.SongRequestRepository;

/**
 * API 테스트들이 상속해서 쓰는 공통 부모 클래스.
 *
 * <ul>
 *   <li>{@code @SpringBootTest}: 실제 서버와 똑같이 스프링 전체를 띄운다. DB는 테스트용 인메모리 H2
 *       (src/test/resources/application.yml).</li>
 *   <li>{@code @AutoConfigureMockMvc}: 진짜 포트를 열지 않고 {@link MockMvc}로 가짜 HTTP 요청을 보낸다.</li>
 *   <li>시계를 <b>서울 기준 2026-10-20 10:00</b>으로 고정한다. 그래서 테스트를 언제 돌려도
 *       "오늘"은 10/20이고, 10/19는 "지난 날짜"다.</li>
 *   <li>테스트마다 DB를 비우고 시작한다. 다른 테스트가 넣은 데이터에 영향받지 않는다.</li>
 * </ul>
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(ApiTestSupport.FixedClock.class)
public abstract class ApiTestSupport {

	/** 고정된 오늘 (서울 기준) */
	protected static final LocalDate TODAY = LocalDate.of(2026, 10, 20);

	/** 고정된 지금. UTC 01:00 = 서울 10:00 */
	protected static final Instant NOW = Instant.parse("2026-10-20T01:00:00Z");

	/** 가짜 HTTP 요청을 보내고 응답을 검사하는 도구. 예) mvc.perform(get("/api/today")) */
	@Autowired
	protected MockMvc mvc;

	/** 테스트 데이터를 넣거나 DB에 몇 건 저장됐는지 확인할 때 쓴다. */
	@Autowired
	protected SongRequestRepository repository;

	/** 각 테스트 시작 전에 DB를 비운다. */
	@BeforeEach
	void clearDb() {
		repository.deleteAll();
	}

	/**
	 * 테스트 데이터 1건을 DB에 바로 넣는다. (API를 거치지 않으므로 지난 날짜나 원하는 접수 시각도 넣을 수 있다)
	 *
	 * @param date      신청 날짜 "YYYY-MM-DD"
	 * @param createdAt 접수 시각 (정렬 순서를 정할 때 쓴다)
	 */
	protected SongRequest save(String date, String title, String artist, String name, Instant createdAt) {
		return repository.save(new SongRequest(LocalDate.parse(date), title, artist, name, createdAt));
	}

	/**
	 * 테스트에서만 쓰는 고정 시계.
	 * {@code @Primary}: 같은 타입(Clock) 빈이 두 개일 때 이쪽을 우선 사용한다 → ClockConfig의 실제 시계 대신 들어간다.
	 */
	@TestConfiguration
	static class FixedClock {

		@Bean
		@Primary
		Clock fixedClock() {
			return Clock.fixed(NOW, ClockConfig.SEOUL);
		}

	}

}
