package com.webintro.wakesong.today;

import java.time.LocalDate;

/** song은 오늘 가장 먼저 접수된 신청(대표 신청곡). 실제 방송 확정곡이 아니다. 신청이 없으면 null. */
public record TodayResponse(LocalDate date, int requestCount, Song song) {

	public record Song(Long id, String title, String artist, String applicantName) {
	}

}
