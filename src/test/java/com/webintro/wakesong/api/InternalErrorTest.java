package com.webintro.wakesong.api;

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

import com.webintro.wakesong.repository.SongRequestRepository;

/**
 * [FR-07] DB 장애 시 500이 나가되, 응답에 내부 정보(접속 주소·비밀번호·예외 이름)가 실리지 않는지 확인한다.
 *
 * <p>{@code @MockitoBean}: 진짜 리포지토리 대신 가짜(mock)를 넣어서 일부러 DB 오류를 일으킨다.
 * 가짜 빈을 쓰면 스프링을 따로 띄워야 하므로 ApiTestSupport를 상속하지 않는다.
 */
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
		given(repository.findAllByDate(any()))
				.willThrow(new DataAccessResourceFailureException(SECRET));

		mvc.perform(get("/api/today"))
				.andExpect(status().isInternalServerError())
				.andExpect(jsonPath("$.code").value("INTERNAL_SERVER_ERROR"))
				.andExpect(content().string(not(containsString("s3cret"))));
	}

}
