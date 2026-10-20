package com.webintro.wakesong.dto;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import com.webintro.wakesong.domain.SongRequest;

/** GET /api/calendar?month= 응답. days = 그 달의 모든 날짜 */
public record CalendarResponse(YearMonth month, List<Day> days) {

	public record Day(LocalDate date, int requestCount, List<Song> songs) {
	}

	public record Song(Long id, String title, String artist) {

		public static Song from(SongRequest r) {
			return new Song(r.getId(), r.getSongTitle(), r.getArtist());
		}

	}

}
