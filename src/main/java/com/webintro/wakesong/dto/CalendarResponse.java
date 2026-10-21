// 이 파일이 dto 패키지에 속한다고 알려주는 코드
package com.webintro.wakesong.dto;

// 날짜(LocalDate), 연-월(YearMonth), 목록(List) 클래스를 가져오는 코드
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

// 신청 엔티티를 가져오는 코드
import com.webintro.wakesong.domain.SongRequest;

/**
 * [응답] GET /api/calendar?month= — 월별 달력.
 * 예) { "month": "2026-10",
 *       "days": [ { "date": "2026-10-01", "requestCount": 0, "songs": [] }, ... 31일까지 ] }
 */
// 조회한 연-월과 그 달의 모든 날짜 칸 목록을 담는 record를 선언하는 코드
public record CalendarResponse(YearMonth month, List<Day> days) {

	// 달력의 하루 칸(날짜, 신청 건수, 신청곡 목록)을 담는 record를 선언하는 코드
	public record Day(LocalDate date, int requestCount, List<Song> songs) {
	}

	// 달력에 보여줄 곡 정보(id, 곡 제목, 가수)를 담는 record를 선언하는 코드
	public record Song(Long id, String title, String artist) {

		// 엔티티를 달력용 곡 DTO로 바꿔주는 메서드를 선언하는 코드
		public static Song from(SongRequest r) {
			// 엔티티에서 id·곡 제목·가수만 꺼내 달력용 DTO를 만들어 돌려주는 코드
			return new Song(r.getId(), r.getSongTitle(), r.getArtist());
		}

	}

}
