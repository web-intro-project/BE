package com.webintro.wakesong;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import com.webintro.wakesong.domain.SongRequest;
import com.webintro.wakesong.repository.SongRequestRepository;

/**
 * API 테스트들이 상속해서 쓰는 공통 부모 클래스.
 * 스프링 전체를 띄우고(DB는 테스트용 인메모리 H2), 테스트마다 DB를 비우고 시작한다.
 */
@SpringBootTest
@AutoConfigureMockMvc
public abstract class ApiTestSupport {

	/** 서울 기준 오늘 */
	protected static final LocalDate TODAY = LocalDate.now(ZoneId.of("Asia/Seoul"));

	/** 가짜 HTTP 요청을 보내는 도구. 예) mvc.perform(get("/api/today")) */
	@Autowired
	protected MockMvc mvc;

	/** 테스트 데이터를 넣거나 저장 건수를 확인할 때 쓴다. */
	@Autowired
	protected SongRequestRepository repository;

	/** 각 테스트 시작 전에 DB를 비운다. */
	@BeforeEach
	void clearDb() {
		repository.deleteAll();
	}

	/** 오늘에서 days일 뒤(음수면 앞) 날짜를 "YYYY-MM-DD" 문자열로 돌려준다. 예) day(1) = 내일 */
	protected static String day(int days) {
		return TODAY.plusDays(days).toString();
	}

	/** 테스트 데이터 1건을 API를 거치지 않고 DB에 바로 넣는다. (지난 날짜도 넣을 수 있다) */
	protected SongRequest save(String date, String title, String artist, String name) {
		return repository.save(new SongRequest(LocalDate.parse(date), title, artist, name, Instant.now()));
	}

}
