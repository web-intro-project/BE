package com.webintro.wakesong.calendar;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.webintro.wakesong.request.SongRequest;
import com.webintro.wakesong.request.SongRequestRepository;

@RestController
public class CalendarController {

	private final SongRequestRepository repository;

	public CalendarController(SongRequestRepository repository) {
		this.repository = repository;
	}

	/** 달력 그리드를 그대로 그릴 수 있게 신청이 없는 날도 포함해 그 달의 모든 날짜를 돌려준다. */
	@GetMapping("/api/calendar")
	@Transactional(readOnly = true)
	public CalendarResponse calendar(@RequestParam YearMonth month) {
		Map<LocalDate, List<CalendarResponse.Song>> songsByDate = repository
				.findByRequestDateBetweenOrderByRequestDateAscCreatedAtAscIdAsc(month.atDay(1), month.atEndOfMonth())
				.stream()
				.collect(Collectors.groupingBy(SongRequest::getRequestDate,
						Collectors.mapping(CalendarController::toSong, Collectors.toList())));

		List<CalendarResponse.Day> days = month.atDay(1).datesUntil(month.plusMonths(1).atDay(1))
				.map(date -> {
					List<CalendarResponse.Song> songs = songsByDate.getOrDefault(date, List.of());
					return new CalendarResponse.Day(date, songs.size(), songs);
				})
				.toList();
		return new CalendarResponse(month, days);
	}

	private static CalendarResponse.Song toSong(SongRequest r) {
		return new CalendarResponse.Song(r.getId(), r.getSongTitle(), r.getArtist());
	}

}
