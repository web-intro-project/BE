package com.webintro.wakesong.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import com.webintro.wakesong.ApiTestSupport;

class CreateSongRequestTest extends ApiTestSupport {

	private ResultActions postJson(String json) throws Exception {
		return mvc.perform(post("/api/requests").contentType(MediaType.APPLICATION_JSON).content(json));
	}

	@Test
	void 신청하면_201과_저장된_신청을_돌려준다() throws Exception {
		postJson("""
				{"date":"2026-10-21","songTitle":"예시곡","artist":"예시가수","applicantName":"홍길동"}""")
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").isNumber())
				.andExpect(jsonPath("$.date").value("2026-10-21"))
				.andExpect(jsonPath("$.songTitle").value("예시곡"))
				.andExpect(jsonPath("$.artist").value("예시가수"))
				.andExpect(jsonPath("$.applicantName").value("홍길동"))
				.andExpect(jsonPath("$.createdAt").value("2026-10-20T01:00:00Z"));

		assertThat(repository.count()).isEqualTo(1);
	}

	@Test
	void 앞뒤_공백은_지우고_저장한다() throws Exception {
		postJson("""
				{"date":"2026-10-21","songTitle":"  예시곡 ","artist":" 예시가수","applicantName":"홍길동  "}""")
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.songTitle").value("예시곡"))
				.andExpect(jsonPath("$.artist").value("예시가수"))
				.andExpect(jsonPath("$.applicantName").value("홍길동"));
	}

	@Test
	void 같은_날짜에_같은_곡과_신청자도_여러_번_신청할_수_있다() throws Exception {
		String body = """
				{"date":"2026-10-21","songTitle":"예시곡","artist":"예시가수","applicantName":"홍길동"}""";
		postJson(body).andExpect(status().isCreated());
		postJson(body).andExpect(status().isCreated());

		assertThat(repository.count()).isEqualTo(2);
	}

	@Test
	void 필수값이_비면_400이고_저장하지_않는다() throws Exception {
		postJson("""
				{"date":"2026-10-21","songTitle":"","artist":"예시가수","applicantName":"홍길동"}""")
				.andExpect(status().isBadRequest());

		assertThat(repository.count()).isZero();
	}

}
