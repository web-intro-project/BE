package com.webintro.wakesong.dto;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import com.webintro.wakesong.domain.SongRequest;

/**
 * [응답] GET /api/calendar?month= — 월별 신청 현황(달력).
 *
 * <pre>
 * { "month": "2026-10",
 *   "days": [ { "date": "2026-10-01", "requestCount": 0, "songs": [] },
 *             { "date": "2026-10-02", "requestCount": 1, "songs": [ {"id": 3, "title": "Ditto", "artist": "NewJeans"} ] },
 *             ... 31일까지 ] }
 * </pre>
 *
 * @param month 조회한 연-월. YearMonth라서 JSON에는 "2026-10"으로 나간다
 * @param days  그 달의 1일~말일 전부 (신청 없는 날도 포함). FE가 이 배열을 그대로 달력 칸으로 그린다
 */
public record CalendarResponse(YearMonth month, List<Day> days) {

	/**
	 * 달력의 하루 칸.
	 *
	 * @param requestCount 그날 신청 건수 (= songs 개수)
	 * @param songs        그날 신청곡 전체, 접수 순. FE는 songs[0]을 미리보기로 보여 준다
	 */
	public record Day(LocalDate date, int requestCount, List<Song> songs) {
	}

	/** 달력에 보여 줄 곡 정보 (신청자 이름은 빼고 곡·가수만). */
	public record Song(Long id, String title, String artist) {

		/** 엔티티 → 달력용 곡 DTO 변환. */
		public static Song from(SongRequest r) {
			return new Song(r.getId(), r.getSongTitle(), r.getArtist());
		}

	}

}
