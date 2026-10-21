// 이 파일이 dto 패키지에 속한다고 알려주는 코드
package com.webintro.wakesong.dto;

// 날짜(LocalDate) 클래스를 가져오는 코드
import java.time.LocalDate;

// 신청 엔티티를 가져오는 코드
import com.webintro.wakesong.domain.SongRequest;

/**
 * [응답] GET /api/today — 오늘의 대표 신청곡.
 * 예) { "date": "2026-10-21", "requestCount": 2,
 *       "song": { "id": 1, "title": "밤양갱", "artist": "비비", "applicantName": "김서연" } }
 */
// 오늘 날짜, 오늘 신청 건수, 대표곡(없으면 null)을 담는 record를 선언하는 코드
public record TodayResponse(LocalDate date, int requestCount, Song song) {

	// 대표곡 정보를 담는 record를 선언하는 코드 (곡 제목 키가 songTitle이 아니라 title인 점 주의)
	public record Song(Long id, String title, String artist, String applicantName) {

		// 엔티티를 대표곡 DTO로 바꿔주는 메서드를 선언하는 코드
		public static Song from(SongRequest r) {
			// 엔티티의 값을 꺼내 대표곡 DTO를 만들어 돌려주는 코드
			return new Song(r.getId(), r.getSongTitle(), r.getArtist(), r.getApplicantName());
		}

	}

}
