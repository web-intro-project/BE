package com.webintro.wakesong.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import com.webintro.wakesong.ApiTestSupport;

/** FR-06 서버 입력 검증. 고정된 오늘은 서울 기준 2026-10-20. */
class ValidationTest extends ApiTestSupport {

	private static final String VALID_DATE = "\"2026-10-21\"";

	private ResultActions postJson(String json) throws Exception {
		return mvc.perform(post("/api/requests").contentType(MediaType.APPLICATION_JSON).content(json));
	}

	private ResultActions post4(String date, String songTitle, String artist, String applicantName) throws Exception {
		return postJson("{\"date\":%s,\"songTitle\":%s,\"artist\":%s,\"applicantName\":%s}"
				.formatted(date, songTitle, artist, applicantName));
	}

	private static String q(String s) {
		return "\"" + s + "\"";
	}

	private void assertRejected(ResultActions res, String field, String message) throws Exception {
		res.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
				.andExpect(jsonPath("$.field").value(field))
				.andExpect(jsonPath("$.message").value(message));
		assertThat(repository.count()).isZero();
	}

	@ParameterizedTest
	@ValueSource(strings = { "null", "\"\"", "\"   \"" })
	void 곡_제목이_없거나_공백뿐이면_400(String songTitle) throws Exception {
		assertRejected(post4(VALID_DATE, songTitle, q("가수"), q("홍길동")), "songTitle", "곡 제목을 입력해 주세요.");
	}

	@ParameterizedTest
	@ValueSource(strings = { "null", "\"\"", "\"   \"" })
	void 가수가_없거나_공백뿐이면_400(String artist) throws Exception {
		assertRejected(post4(VALID_DATE, q("곡"), artist, q("홍길동")), "artist", "가수를 입력해 주세요.");
	}

	@ParameterizedTest
	@ValueSource(strings = { "null", "\"\"", "\"   \"" })
	void 신청자가_없거나_공백뿐이면_400(String applicantName) throws Exception {
		assertRejected(post4(VALID_DATE, q("곡"), q("가수"), applicantName), "applicantName", "신청자 이름을 입력해 주세요.");
	}

	@ParameterizedTest
	@ValueSource(strings = { "null", "\"\"", "\"  \"" })
	void 날짜가_없으면_400(String date) throws Exception {
		assertRejected(post4(date, q("곡"), q("가수"), q("홍길동")), "date", "신청 날짜를 선택해 주세요.");
	}

	@Test
	void 필드가_아예_빠져도_400() throws Exception {
		assertRejected(postJson("{\"songTitle\":\"곡\",\"artist\":\"가수\",\"applicantName\":\"홍길동\"}"),
				"date", "신청 날짜를 선택해 주세요.");
	}

	@Test
	void 곡_제목과_가수는_101자부터_400() throws Exception {
		assertRejected(post4(VALID_DATE, q("가".repeat(101)), q("가수"), q("홍길동")), "songTitle", "100자 이내로 입력해 주세요.");
		assertRejected(post4(VALID_DATE, q("곡"), q("a".repeat(101)), q("홍길동")), "artist", "100자 이내로 입력해 주세요.");
	}

	@Test
	void 신청자는_21자부터_400() throws Exception {
		assertRejected(post4(VALID_DATE, q("곡"), q("가수"), q("가".repeat(21))), "applicantName", "20자 이내로 입력해 주세요.");
	}

	@Test
	void 최대_길이는_허용하고_길이는_공백을_지운_뒤_센다() throws Exception {
		post4(VALID_DATE, q("  " + "가".repeat(100) + "  "), q("a".repeat(100)), q(" " + "가".repeat(20) + " "))
				.andExpect(status().isCreated());
	}

	@ParameterizedTest
	@ValueSource(strings = { "2026-02-30", "2026-13-01", "20261021", "2026-10-1", "+2026-10-21", "내일" })
	void 날짜_형식이_틀리거나_없는_날짜면_400(String date) throws Exception {
		assertRejected(post4(q(date), q("곡"), q("가수"), q("홍길동")), "date", "날짜 형식이 올바르지 않아요.");
	}

	@Test
	void 서울_기준_어제는_400() throws Exception {
		assertRejected(post4(q("2026-10-19"), q("곡"), q("가수"), q("홍길동")), "date", "지난 날짜는 신청할 수 없어요.");
	}

	@ParameterizedTest
	@ValueSource(strings = { "2026-10-20", "2026-10-21", "2027-12-31" })
	void 오늘과_미래는_신청할_수_있다(String date) throws Exception {
		post4(q(date), q("곡"), q("가수"), q("홍길동")).andExpect(status().isCreated());
	}

	@Test
	void 여러_필드가_틀리면_위에서부터_첫_오류만_알려준다() throws Exception {
		post4(q("2026-10-19"), q(""), q("가수"), q(""))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.field").value("date"))
				.andExpect(jsonPath("$.message").value("지난 날짜는 신청할 수 없어요."));
	}

	@Test
	void 조회_API는_잘못된_날짜와_월에_오류_형식으로_400() throws Exception {
		mvc.perform(get("/api/requests").param("date", "2026-02-30"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
				.andExpect(jsonPath("$.field").value("date"));
		mvc.perform(get("/api/requests"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.field").value("date"));
		mvc.perform(get("/api/calendar").param("month", "2026-13"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
				.andExpect(jsonPath("$.field").value("month"));
	}

}
