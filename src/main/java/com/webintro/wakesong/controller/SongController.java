package com.webintro.wakesong.controller;

import java.time.LocalDate;
import java.time.YearMonth;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.webintro.wakesong.dto.CalendarResponse;
import com.webintro.wakesong.dto.DateResponse;
import com.webintro.wakesong.dto.SongCreateRequest;
import com.webintro.wakesong.dto.SongResponse;
import com.webintro.wakesong.dto.TodayResponse;
import com.webintro.wakesong.service.SongService;

import lombok.RequiredArgsConstructor;

/**
 * 기상송 API 4개의 입구.
 *
 * <p>컨트롤러는 HTTP 요청을 받아서 SongService에 넘기고, 돌려받은 값을 응답으로 내보내기만 한다.
 * 업무 규칙(대표곡 고르기, 입력 검사 등)은 전부 SongService에 있다.
 *
 * <ul>
 *   <li>{@code @RestController}: 메서드가 돌려주는 객체를 JSON으로 바꿔서 응답한다.</li>
 *   <li>{@code @RequestMapping("/api")}: 이 클래스의 모든 주소 앞에 /api가 붙는다.</li>
 *   <li>{@code @RequiredArgsConstructor}(Lombok): final 필드를 받는 생성자를 만들어 준다.
 *       스프링이 이 생성자로 SongService를 넣어 준다(생성자 주입).</li>
 * </ul>
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SongController {

	private final SongService songService;

	/**
	 * [FR-01] 오늘의 대표 신청곡.
	 *
	 * <pre>GET /api/today</pre>
	 *
	 * @return 오늘 날짜, 오늘 신청 건수, 대표곡(없으면 null)
	 */
	@GetMapping("/today")
	public TodayResponse today() {
		return songService.today();
	}

	/**
	 * [FR-02, FR-03] 기상송 신청.
	 *
	 * <pre>POST /api/requests  {"date", "songTitle", "artist", "applicantName"}</pre>
	 *
	 * <p>{@code @RequestBody}: 요청 본문 JSON을 SongCreateRequest 객체로 바꿔서 받는다.
	 * {@code @ResponseStatus(CREATED)}: 성공하면 200 대신 201(새로 만들어짐)로 응답한다.
	 *
	 * @return 저장된 신청 (id, 접수 시각 포함)
	 */
	@PostMapping("/requests")
	@ResponseStatus(HttpStatus.CREATED)
	public SongResponse create(@RequestBody SongCreateRequest request) {
		return songService.create(request);
	}

	/**
	 * [FR-05] 날짜별 전체 신청 목록.
	 *
	 * <pre>GET /api/requests?date=2026-10-21</pre>
	 *
	 * <p>{@code @RequestParam}: 주소 뒤의 {@code ?date=} 값을 LocalDate로 바꿔서 받는다.
	 * 값이 없거나 형식이 틀리면 GlobalExceptionHandler가 400으로 응답한다.
	 */
	@GetMapping("/requests")
	public DateResponse byDate(@RequestParam LocalDate date) {
		return songService.byDate(date);
	}

	/**
	 * [FR-04] 월별 신청 현황(달력).
	 *
	 * <pre>GET /api/calendar?month=2026-10</pre>
	 *
	 * <p>{@code ?month=} 값을 YearMonth(연-월)로 바꿔서 받는다.
	 */
	@GetMapping("/calendar")
	public CalendarResponse calendar(@RequestParam YearMonth month) {
		return songService.calendar(month);
	}

}
