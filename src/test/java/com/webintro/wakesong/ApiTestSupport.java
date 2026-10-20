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

import com.webintro.wakesong.common.TimeConfig;
import com.webintro.wakesong.request.SongRequest;
import com.webintro.wakesong.request.SongRequestRepository;

/** API 테스트 공통: 서울 기준 2026-10-20 10:00으로 고정된 시계, 테스트마다 빈 DB. */
@SpringBootTest
@AutoConfigureMockMvc
@Import(ApiTestSupport.FixedClock.class)
public abstract class ApiTestSupport {

	protected static final LocalDate TODAY = LocalDate.of(2026, 10, 20);
	protected static final Instant NOW = Instant.parse("2026-10-20T01:00:00Z");

	@Autowired
	protected MockMvc mvc;

	@Autowired
	protected SongRequestRepository repository;

	@BeforeEach
	void clearDb() {
		repository.deleteAll();
	}

	protected SongRequest save(String date, String title, String artist, String name, Instant createdAt) {
		return repository.save(new SongRequest(LocalDate.parse(date), title, artist, name, createdAt));
	}

	@TestConfiguration
	static class FixedClock {

		@Bean
		@Primary
		Clock fixedClock() {
			return Clock.fixed(NOW, TimeConfig.SEOUL);
		}

	}

}
