// 이 파일이 dto 패키지에 속한다고 알려주는 코드
package com.webintro.wakesong.dto;

// 시각(Instant)과 날짜(LocalDate) 클래스를 가져오는 코드
import java.time.Instant;
import java.time.LocalDate;

// 신청 엔티티를 가져오는 코드
import com.webintro.wakesong.domain.SongRequest;

/**
 * [응답] 신청 1건. 신청 완료와 날짜별 목록에서 쓴다.
 * 예) { "id": 1, "date": "2026-10-21", "songTitle": "밤양갱", "artist": "비비",
 *       "applicantName": "김서연", "createdAt": "2026-10-20T09:55:46.893188Z" }
 */
// 신청 1건의 응답 JSON 모양(id, 날짜, 곡, 가수, 신청자, 접수 시각)을 정의하는 record를 선언하는 코드
public record SongResponse(Long id, LocalDate date, String songTitle, String artist, String applicantName,
		Instant createdAt) {

	// 엔티티(SongRequest)를 응답 DTO로 바꿔주는 메서드를 선언하는 코드
	public static SongResponse from(SongRequest r) {
		// 엔티티의 값을 하나씩 꺼내 응답 DTO를 만들어 돌려주는 코드
		return new SongResponse(r.getId(), r.getRequestDate(), r.getSongTitle(), r.getArtist(),
				r.getApplicantName(), r.getCreatedAt());
	}

}
