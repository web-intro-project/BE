package com.webintro.wakesong.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import com.webintro.wakesong.ApiTestSupport;

/**
 * [FR-02, FR-03] POST /api/requests — 신청 저장의 정상 동작.
 * 입력 검증 실패 케이스는 {@link ValidationTest}에 있다.
 */
class CreateSongRequestTest extends ApiTestSupport {

	/** 날짜와 곡 정보로 POST /api/requests 요청을 보낸다. */
	private ResultActions request(String date, String songTitle, String artist, String applicantName) throws Exception {
		String json = "{\"date\":\"%s\",\"songTitle\":\"%s\",\"artist\":\"%s\",\"applicantName\":\"%s\"}"
				.formatted(date, songTitle, artist, applicantName);
		return mvc.perform(post("/api/requests").contentType(MediaType.APPLICATION_JSON).content(json));
	}

	@Test
	void 신청하면_201과_저장된_신청을_돌려준다() throws Exception {
		request(day(1), "예시곡", "예시가수", "홍길동")
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").isNumber())
				.andExpect(jsonPath("$.date").value(day(1)))
				.andExpect(jsonPath("$.songTitle").value("예시곡"))
				.andExpect(jsonPath("$.artist").value("예시가수"))
				.andExpect(jsonPath("$.applicantName").value("홍길동"))
				.andExpect(jsonPath("$.createdAt").isString());

		assertThat(repository.count()).isEqualTo(1);
	}

	@Test
	void 앞뒤_공백은_지우고_저장한다() throws Exception {
		request(day(1), "  예시곡 ", " 예시가수", "홍길동  ")
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.songTitle").value("예시곡"))
				.andExpect(jsonPath("$.artist").value("예시가수"))
				.andExpect(jsonPath("$.applicantName").value("홍길동"));
	}

	@Test
	void 같은_날짜에_같은_곡과_신청자도_여러_번_신청할_수_있다() throws Exception {
		request(day(1), "예시곡", "예시가수", "홍길동").andExpect(status().isCreated());
		request(day(1), "예시곡", "예시가수", "홍길동").andExpect(status().isCreated());

		assertThat(repository.count()).isEqualTo(2);
	}

}
