package com.webintro.wakesong.dto;

import java.time.LocalDate;

import com.webintro.wakesong.domain.SongRequest;

/**
 * [응답] GET /api/today — 오늘의 대표 신청곡.
 *
 * <pre>
 * { "date": "2026-10-21", "requestCount": 2,
 *   "song": { "id": 1, "title": "밤양갱", "artist": "비비", "applicantName": "김서연" } }
 * </pre>
 *
 * @param date         오늘 날짜 (서울 기준)
 * @param requestCount 오늘 신청 건수 (FE 메인의 "오늘 신청 N건")
 * @param song         대표곡 = 오늘 가장 먼저 접수된 신청. 오늘 신청이 없으면 null
 */
public record TodayResponse(LocalDate date, int requestCount, Song song) {

	/**
	 * 대표곡 정보.
	 * 주의: 곡 제목 키가 다른 API의 "songTitle"과 달리 "title"이다. (원본 API 명세를 따름)
	 */
	public record Song(Long id, String title, String artist, String applicantName) {

		/** 엔티티 → 대표곡 DTO 변환. */
		public static Song from(SongRequest r) {
			return new Song(r.getId(), r.getSongTitle(), r.getArtist(), r.getApplicantName());
		}

	}

}
