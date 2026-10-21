package com.webintro.wakesong.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;

import com.webintro.wakesong.ApiTestSupport;

/** [FR-01] GET /api/today — 오늘의 대표 신청곡 (가장 먼저 접수된 곡, 신청 없음). */
class TodayTest extends ApiTestSupport {

	@Test
	void 오늘_가장_먼저_접수된_신청이_대표곡이다() throws Exception {
		save(day(0), "먼저곡", "가수A", "홍길동");
		save(day(1), "내일곡", "가수C", "성춘향");
		save(day(0), "나중곡", "가수B", "이몽룡");

		mvc.perform(get("/api/today"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.date").value(day(0)))
				.andExpect(jsonPath("$.requestCount").value(2))
				.andExpect(jsonPath("$.song.id").isNumber())
				.andExpect(jsonPath("$.song.title").value("먼저곡"))
				.andExpect(jsonPath("$.song.artist").value("가수A"))
				.andExpect(jsonPath("$.song.applicantName").value("홍길동"));
	}

	@Test
	void 오늘_신청이_없으면_song은_null() throws Exception {
		save(day(-1), "어제곡", "가수", "A");

		mvc.perform(get("/api/today"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.requestCount").value(0))
				.andExpect(jsonPath("$.song").isEmpty());
	}

}
