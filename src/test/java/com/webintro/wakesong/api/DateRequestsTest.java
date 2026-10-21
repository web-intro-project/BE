package com.webintro.wakesong.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;

import com.webintro.wakesong.ApiTestSupport;

/** [FR-05] GET /api/requests?date= — 날짜별 신청 목록 (접수 순, 빈 목록, 지난 날짜 조회, 잘못된 날짜). */
class DateRequestsTest extends ApiTestSupport {

	@Test
	void 지정한_날짜의_신청을_접수_순으로_돌려준다() throws Exception {
		save("2030-05-10", "첫번째", "가수A", "A");
		save("2030-05-11", "다른날", "가수C", "C");
		save("2030-05-10", "두번째", "가수B", "B");

		mvc.perform(get("/api/requests").param("date", "2030-05-10"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.date").value("2030-05-10"))
				.andExpect(jsonPath("$.requests.length()").value(2))
				.andExpect(jsonPath("$.requests[0].songTitle").value("첫번째"))
				.andExpect(jsonPath("$.requests[0].date").value("2030-05-10"))
				.andExpect(jsonPath("$.requests[1].songTitle").value("두번째"));
	}

	@Test
	void 신청이_없으면_빈_목록과_200() throws Exception {
		mvc.perform(get("/api/requests").param("date", "2030-05-25"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.requests").isEmpty());
	}

	@Test
	void 지난_날짜도_조회할_수_있다() throws Exception {
		save("2020-01-01", "지난곡", "가수", "A");

		mvc.perform(get("/api/requests").param("date", "2020-01-01"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.requests.length()").value(1));
	}

	@Test
	void 날짜가_없거나_형식이_틀리면_400() throws Exception {
		mvc.perform(get("/api/requests").param("date", "2026-13-01"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
		mvc.perform(get("/api/requests").param("date", "20261021")).andExpect(status().isBadRequest());
		mvc.perform(get("/api/requests")).andExpect(status().isBadRequest());
	}

}
