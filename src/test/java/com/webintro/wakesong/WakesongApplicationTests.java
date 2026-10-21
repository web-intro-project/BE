package com.webintro.wakesong;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/** 가장 기본 테스트: 설정 오류 없이 스프링 애플리케이션이 뜨는지 확인한다. */
@SpringBootTest
class WakesongApplicationTests {

	/** 스프링이 모든 빈을 만들다가 실패하면 이 테스트가 실패한다. 본문은 비어 있어도 된다. */
	@Test
	void contextLoads() {
	}

}
