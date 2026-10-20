package com.webintro.wakesong.calendar;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.webintro.wakesong.ApiTestSupport;

class CalendarTest extends ApiTestSupport {

	@Test
	void 그_달의_모든_날짜와_날짜별_건수_곡을_돌려준다() throws Exception {
		save("2026-10-21", "둘째", "가수B", "B", Instant.parse("2026-10-19T02:00:00Z"));
		save("2026-10-21", "첫째", "가수A", "A", Instant.parse("2026-10-19T01:00:00Z"));
		save("2026-10-01", "월초", "가수C", "C", Instant.parse("2026-09-30T01:00:00Z"));
		save("2026-10-31", "월말", "가수D", "D", Instant.parse("2026-10-19T01:00:00Z"));

		mvc.perform(get("/api/calendar").param("month", "2026-10"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.month").value("2026-10"))
				.andExpect(jsonPath("$.days.length()").value(31))
				.andExpect(jsonPath("$.days[0].date").value("2026-10-01"))
				.andExpect(jsonPath("$.days[0].requestCount").value(1))
				.andExpect(jsonPath("$.days[1].requestCount").value(0))
				.andExpect(jsonPath("$.days[1].songs").isEmpty())
				.andExpect(jsonPath("$.days[20].date").value("2026-10-21"))
				.andExpect(jsonPath("$.days[20].requestCount").value(2))
				.andExpect(jsonPath("$.days[20].songs[0].title").value("첫째"))
				.andExpect(jsonPath("$.days[20].songs[0].artist").value("가수A"))
				.andExpect(jsonPath("$.days[20].songs[1].title").value("둘째"))
				.andExpect(jsonPath("$.days[30].songs[0].title").value("월말"));
	}

	@Test
	void 다른_달_신청은_섞이지_않는다() throws Exception {
		save("2026-09-30", "지난달", "가수", "A", Instant.parse("2026-09-29T01:00:00Z"));
		save("2026-11-01", "다음달", "가수", "B", Instant.parse("2026-10-19T01:00:00Z"));

		mvc.perform(get("/api/calendar").param("month", "2026-10"))
				.andExpect(jsonPath("$.days.length()").value(31))
				.andExpect(jsonPath("$.days[?(@.requestCount > 0)]").isEmpty());
	}

	@Test
	void 달력_건수와_날짜별_상세_건수가_같다() throws Exception {
		for (int i = 0; i < 3; i++) {
			save("2026-10-23", "곡" + i, "가수", "A", Instant.parse("2026-10-19T01:00:00Z"));
		}

		mvc.perform(get("/api/calendar").param("month", "2026-10"))
				.andExpect(jsonPath("$.days[22].requestCount").value(3));
		mvc.perform(get("/api/requests").param("date", "2026-10-23"))
				.andExpect(jsonPath("$.requests.length()").value(3));
	}

	@Test
	void 날짜_수는_달마다_다르다() throws Exception {
		mvc.perform(get("/api/calendar").param("month", "2028-02"))
				.andExpect(jsonPath("$.days.length()").value(29));
		mvc.perform(get("/api/calendar").param("month", "2026-11"))
				.andExpect(jsonPath("$.days.length()").value(30));
	}

	@Test
	void 월_형식이_틀리면_400() throws Exception {
		mvc.perform(get("/api/calendar").param("month", "2026-13")).andExpect(status().isBadRequest());
		mvc.perform(get("/api/calendar").param("month", "2026-1")).andExpect(status().isBadRequest());
		mvc.perform(get("/api/calendar").param("month", "2026-10-01")).andExpect(status().isBadRequest());
		mvc.perform(get("/api/calendar")).andExpect(status().isBadRequest());
	}

}
