package com.webintro.wakesong.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import com.webintro.wakesong.ApiTestSupport;

/** [FR-07] 깨진 JSON 400, 스프링 기본 처리(404, 405, 415), CORS 허용/거부. */
class ErrorResponseTest extends ApiTestSupport {

	@Test
	void JSON이_깨졌거나_본문이_없으면_400() throws Exception {
		mvc.perform(post("/api/requests").contentType(MediaType.APPLICATION_JSON).content("{\"date\":"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
				.andExpect(jsonPath("$.message").value("요청 형식이 올바르지 않아요."));
		mvc.perform(post("/api/requests").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
		assertThat(repository.count()).isZero();
	}

	@Test
	void JSON이_아니면_415() throws Exception {
		mvc.perform(post("/api/requests").contentType(MediaType.TEXT_PLAIN).content("hi"))
				.andExpect(status().isUnsupportedMediaType());
	}

	@Test
	void 없는_경로는_404() throws Exception {
		mvc.perform(get("/api/nope"))
				.andExpect(status().isNotFound());
	}

	@Test
	void 지원하지_않는_메서드는_405() throws Exception {
		mvc.perform(delete("/api/requests"))
				.andExpect(status().isMethodNotAllowed());
	}

	@Test
	void FE_Origin은_CORS를_허용한다() throws Exception {
		mvc.perform(options("/api/requests")
				.header("Origin", "http://localhost:8000")
				.header("Access-Control-Request-Method", "POST")
				.header("Access-Control-Request-Headers", "Content-Type"))
				.andExpect(status().isOk())
				.andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:8000"));
	}

	@Test
	void 허용하지_않은_Origin은_막는다() throws Exception {
		mvc.perform(get("/api/today").header("Origin", "http://evil.example"))
				.andExpect(status().isForbidden());
	}

}
