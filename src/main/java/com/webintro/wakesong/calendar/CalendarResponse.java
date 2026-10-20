package com.webintro.wakesong.calendar;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public record CalendarResponse(YearMonth month, List<Day> days) {

	/** songs는 그날의 신청 전체를 접수 순으로 담는다. */
	public record Day(LocalDate date, int requestCount, List<Song> songs) {
	}

	public record Song(Long id, String title, String artist) {
	}

}
