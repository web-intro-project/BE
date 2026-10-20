package com.webintro.wakesong.dto;

import java.time.LocalDate;

import com.webintro.wakesong.domain.SongRequest;

/** GET /api/today 응답. song = 오늘 첫 신청 (없으면 null) */
public record TodayResponse(LocalDate date, int requestCount, Song song) {

	public record Song(Long id, String title, String artist, String applicantName) {

		public static Song from(SongRequest r) {
			return new Song(r.getId(), r.getSongTitle(), r.getArtist(), r.getApplicantName());
		}

	}

}
