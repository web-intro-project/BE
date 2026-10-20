package com.webintro.wakesong.dto;

import java.time.Instant;
import java.time.LocalDate;

import com.webintro.wakesong.domain.SongRequest;

/** 신청 1건 응답 (신청 완료, 날짜별 목록) */
public record SongResponse(Long id, LocalDate date, String songTitle, String artist, String applicantName,
		Instant createdAt) {

	public static SongResponse from(SongRequest r) {
		return new SongResponse(r.getId(), r.getRequestDate(), r.getSongTitle(), r.getArtist(),
				r.getApplicantName(), r.getCreatedAt());
	}

}
