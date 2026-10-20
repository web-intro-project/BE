package com.webintro.wakesong.request;

import java.time.Instant;
import java.time.LocalDate;

public record SongRequestResponse(
		Long id,
		LocalDate date,
		String songTitle,
		String artist,
		String applicantName,
		Instant createdAt) {

	public static SongRequestResponse from(SongRequest r) {
		return new SongRequestResponse(r.getId(), r.getRequestDate(), r.getSongTitle(), r.getArtist(),
				r.getApplicantName(), r.getCreatedAt());
	}

}
