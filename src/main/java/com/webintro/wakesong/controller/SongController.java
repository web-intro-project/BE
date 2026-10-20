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

/** API 4개. 요청을 받아 SongService에 넘기기만 한다. */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SongController {

	private final SongService songService;

	// FR-01 오늘의 대표 신청곡
	@GetMapping("/today")
	public TodayResponse today() {
		return songService.today();
	}

	// FR-02, FR-03 신청 저장
	@PostMapping("/requests")
	@ResponseStatus(HttpStatus.CREATED)
	public SongResponse create(@RequestBody SongCreateRequest request) {
		return songService.create(request);
	}

	// FR-05 날짜별 신청 목록
	@GetMapping("/requests")
	public DateResponse byDate(@RequestParam LocalDate date) {
		return songService.byDate(date);
	}

	// FR-04 월별 달력
	@GetMapping("/calendar")
	public CalendarResponse calendar(@RequestParam YearMonth month) {
		return songService.calendar(month);
	}

}
