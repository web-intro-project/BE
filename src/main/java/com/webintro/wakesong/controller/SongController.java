// 이 파일이 controller 패키지에 속한다고 알려주는 코드
package com.webintro.wakesong.controller;

// 날짜(LocalDate)와 연-월(YearMonth) 클래스를 가져오는 코드
import java.time.LocalDate;
import java.time.YearMonth;

// HTTP 상태 코드와 컨트롤러용 어노테이션을 가져오는 코드
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

// 요청·응답 DTO와 서비스 클래스를 가져오는 코드
import com.webintro.wakesong.dto.CalendarResponse;
import com.webintro.wakesong.dto.DateResponse;
import com.webintro.wakesong.dto.SongCreateRequest;
import com.webintro.wakesong.dto.SongResponse;
import com.webintro.wakesong.dto.TodayResponse;
import com.webintro.wakesong.service.SongService;

// final 필드용 생성자를 자동으로 만들어주는 Lombok 어노테이션을 가져오는 코드
import lombok.RequiredArgsConstructor;

/**
 * 기상송 API 4개의 입구. 요청을 받아 SongService에 넘기고 결과를 JSON으로 응답한다.
 */
// 이 클래스를 REST API 컨트롤러로 등록하고, 반환값을 JSON으로 바꿔 응답해주는 코드
@RestController
// 이 클래스의 모든 주소 앞에 /api를 붙여주는 코드
@RequestMapping("/api")
// final 필드(songService)를 받는 생성자를 자동으로 만들어주는 코드
@RequiredArgsConstructor
// 기상송 컨트롤러 클래스를 선언하는 코드
public class SongController {

	// 실제 업무를 처리하는 서비스를 담는 필드 (스프링이 생성자로 넣어줌)
	private final SongService songService;

	// [FR-01] GET /api/today 요청을 이 메서드로 연결하는 코드
	@GetMapping("/today")
	// 오늘의 대표 신청곡을 조회하는 메서드를 선언하는 코드
	public TodayResponse today() {
		// 서비스에서 오늘의 대표곡을 가져와 그대로 응답하는 코드
		return songService.today();
	}

	// [FR-02, FR-03] POST /api/requests 요청을 이 메서드로 연결하는 코드
	@PostMapping("/requests")
	// 성공하면 200 대신 201(Created) 상태 코드로 응답하게 하는 코드
	@ResponseStatus(HttpStatus.CREATED)
	// 요청 본문 JSON을 SongCreateRequest로 받아 신청을 저장하는 메서드를 선언하는 코드
	public SongResponse create(@RequestBody SongCreateRequest request) {
		// 서비스에 신청 저장을 맡기고 저장된 신청을 응답하는 코드
		return songService.create(request);
	}

	// [FR-05] GET /api/requests 요청을 이 메서드로 연결하는 코드
	@GetMapping("/requests")
	// 주소의 ?date= 값을 LocalDate로 받아 날짜별 목록을 조회하는 메서드를 선언하는 코드
	public DateResponse byDate(@RequestParam LocalDate date) {
		// 서비스에서 그 날짜의 신청 목록을 가져와 응답하는 코드
		return songService.byDate(date);
	}

	// [FR-04] GET /api/calendar 요청을 이 메서드로 연결하는 코드
	@GetMapping("/calendar")
	// 주소의 ?month= 값을 YearMonth로 받아 월별 달력을 조회하는 메서드를 선언하는 코드
	public CalendarResponse calendar(@RequestParam YearMonth month) {
		// 서비스에서 그 달의 달력 데이터를 가져와 응답하는 코드
		return songService.calendar(month);
	}

}
