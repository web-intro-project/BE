package com.webintro.wakesong.common;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.webintro.wakesong.request.SongRequestRepository;

/** DB 장애 시 500이 나가되 내부 정보는 응답에 실리지 않는다. */
@SpringBootTest
@AutoConfigureMockMvc
class InternalErrorTest {

	private static final String SECRET = "jdbc:mysql://db.internal password=s3cret";

	@Autowired
	MockMvc mvc;

	@MockitoBean
	SongRequestRepository repository;

	@Test
	void 저장_실패는_안전한_500() throws Exception {
		given(repository.save(any())).willThrow(new DataAccessResourceFailureException(SECRET));

		mvc.perform(post("/api/requests").contentType(MediaType.APPLICATION_JSON).content("""
				{"date":"2099-01-01","songTitle":"곡","artist":"가수","applicantName":"홍길동"}"""))
				.andExpect(status().isInternalServerError())
				.andExpect(jsonPath("$.code").value("INTERNAL_SERVER_ERROR"))
				.andExpect(content().string(not(containsString("s3cret"))))
				.andExpect(content().string(not(containsString("Exception"))));
	}

	@Test
	void 조회_실패도_안전한_500() throws Exception {
		given(repository.findByRequestDateOrderByCreatedAtAscIdAsc(any()))
				.willThrow(new DataAccessResourceFailureException(SECRET));

		mvc.perform(get("/api/today"))
				.andExpect(status().isInternalServerError())
				.andExpect(jsonPath("$.code").value("INTERNAL_SERVER_ERROR"))
				.andExpect(content().string(not(containsString("s3cret"))));
	}

}
