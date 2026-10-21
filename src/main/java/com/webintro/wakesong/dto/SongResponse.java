package com.webintro.wakesong.dto;

import java.time.Instant;
import java.time.LocalDate;

import com.webintro.wakesong.domain.SongRequest;

/**
 * [응답] 신청 1건. 신청 완료(POST /api/requests)와 날짜별 목록(GET /api/requests)에서 쓴다.
 *
 * <pre>
 * { "id": 1, "date": "2026-10-21", "songTitle": "밤양갱", "artist": "비비",
 *   "applicantName": "김서연", "createdAt": "2026-10-20T09:55:46.893188Z" }
 * </pre>
 *
 * <p>엔티티(SongRequest)를 그대로 응답하지 않고 DTO로 옮겨 담는 이유:
 * DB 구조가 바뀌어도 API 모양은 그대로 유지할 수 있고, 내보낼 필드만 골라서 보낼 수 있다.
 *
 * @param createdAt 접수 시각. Instant라서 JSON에는 UTC("...Z")로 나간다
 */
public record SongResponse(Long id, LocalDate date, String songTitle, String artist, String applicantName,
		Instant createdAt) {

	/** 엔티티 → 응답 DTO 변환. */
	public static SongResponse from(SongRequest r) {
		return new SongResponse(r.getId(), r.getRequestDate(), r.getSongTitle(), r.getArtist(),
				r.getApplicantName(), r.getCreatedAt());
	}

}
