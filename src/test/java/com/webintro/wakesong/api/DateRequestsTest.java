package com.webintro.wakesong.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.webintro.wakesong.ApiTestSupport;

class DateRequestsTest extends ApiTestSupport {

	@Test
	void 지정한_날짜의_신청을_접수_순으로_돌려준다() throws Exception {
		save("2026-10-21", "두번째", "가수B", "B", Instant.parse("2026-10-19T02:00:00Z"));
		save("2026-10-21", "첫번째", "가수A", "A", Instant.parse("2026-10-19T01:00:00Z"));
		save("2026-10-22", "다른날", "가수C", "C", Instant.parse("2026-10-19T00:00:00Z"));

		mvc.perform(get("/api/requests").param("date", "2026-10-21"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.date").value("2026-10-21"))
				.andExpect(jsonPath("$.requests.length()").value(2))
				.andExpect(jsonPath("$.requests[0].songTitle").value("첫번째"))
				.andExpect(jsonPath("$.requests[0].date").value("2026-10-21"))
				.andExpect(jsonPath("$.requests[0].createdAt").value("2026-10-19T01:00:00Z"))
				.andExpect(jsonPath("$.requests[1].songTitle").value("두번째"));
	}

	@Test
	void 접수_시각이_같으면_먼저_저장된_신청이_앞이다() throws Exception {
		Instant same = Instant.parse("2026-10-19T01:00:00Z");
		save("2026-10-21", "먼저", "가수", "A", same);
		save("2026-10-21", "나중", "가수", "B", same);

		mvc.perform(get("/api/requests").param("date", "2026-10-21"))
				.andExpect(jsonPath("$.requests[0].songTitle").value("먼저"))
				.andExpect(jsonPath("$.requests[1].songTitle").value("나중"));
	}

	@Test
	void 신청이_없으면_빈_목록과_200() throws Exception {
		mvc.perform(get("/api/requests").param("date", "2026-10-25"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.date").value("2026-10-25"))
				.andExpect(jsonPath("$.requests").isEmpty());
	}

	@Test
	void 지난_날짜도_조회할_수_있다() throws Exception {
		save("2026-10-01", "지난곡", "가수", "A", Instant.parse("2026-09-30T12:00:00Z"));

		mvc.perform(get("/api/requests").param("date", "2026-10-01"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.requests.length()").value(1));
	}

	@Test
	void 날짜_형식이_틀리면_400() throws Exception {
		mvc.perform(get("/api/requests").param("date", "2026-13-01")).andExpect(status().isBadRequest());
		mvc.perform(get("/api/requests").param("date", "20261021")).andExpect(status().isBadRequest());
		mvc.perform(get("/api/requests")).andExpect(status().isBadRequest());
	}

}
