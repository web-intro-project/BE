package com.webintro.wakesong.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.webintro.wakesong.ApiTestSupport;
import com.webintro.wakesong.config.ClockConfig;
import com.webintro.wakesong.dto.TodayResponse;
import com.webintro.wakesong.service.SongService;

class TodayTest extends ApiTestSupport {

	@Test
	void 오늘_가장_먼저_접수된_신청이_대표곡이다() throws Exception {
		save("2026-10-20", "나중곡", "가수B", "이몽룡", Instant.parse("2026-10-18T05:00:00Z"));
		save("2026-10-20", "먼저곡", "가수A", "홍길동", Instant.parse("2026-10-18T01:00:00Z"));
		save("2026-10-21", "내일곡", "가수C", "성춘향", Instant.parse("2026-10-17T00:00:00Z"));

		mvc.perform(get("/api/today"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.date").value("2026-10-20"))
				.andExpect(jsonPath("$.requestCount").value(2))
				.andExpect(jsonPath("$.song.id").isNumber())
				.andExpect(jsonPath("$.song.title").value("먼저곡"))
				.andExpect(jsonPath("$.song.artist").value("가수A"))
				.andExpect(jsonPath("$.song.applicantName").value("홍길동"));
	}

	@Test
	void 오늘_신청이_없으면_song은_null() throws Exception {
		save("2026-10-19", "어제곡", "가수", "A", Instant.parse("2026-10-18T01:00:00Z"));

		mvc.perform(get("/api/today"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.date").value("2026-10-20"))
				.andExpect(jsonPath("$.requestCount").value(0))
				.andExpect(jsonPath("$.song").isEmpty());
	}

	@Test
	void 오늘은_서울_기준이다() {
		// UTC로는 10/19 16:00이지만 서울로는 10/20 01:00
		Clock seoulAfterMidnight = Clock.fixed(Instant.parse("2026-10-19T16:00:00Z"), ClockConfig.SEOUL);
		save("2026-10-19", "UTC기준오늘", "가수", "A", Instant.parse("2026-10-18T01:00:00Z"));
		save("2026-10-20", "서울기준오늘", "가수", "B", Instant.parse("2026-10-18T01:00:00Z"));

		TodayResponse res = new SongService(repository, seoulAfterMidnight).today();

		assertThat(res.date()).isEqualTo(LocalDate.of(2026, 10, 20));
		assertThat(res.song().title()).isEqualTo("서울기준오늘");
	}

}
