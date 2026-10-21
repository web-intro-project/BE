package com.webintro.wakesong.dto;

/**
 * [요청] POST /api/requests 의 본문 JSON.
 *
 * <pre>
 * { "date": "2026-10-21", "songTitle": "밤양갱", "artist": "비비", "applicantName": "김서연" }
 * </pre>
 *
 * <p>record: 값만 담는 불변 클래스. 생성자·getter(date() 처럼 괄호로 호출)·equals를 자동으로 만들어 준다.
 *
 * <p>date를 LocalDate가 아니라 String으로 받는 이유: 형식이 틀린 날짜("2026-02-30")가 와도
 * JSON 변환 단계에서 실패하지 않고 SongService까지 들어오게 해서,
 * "날짜 형식이 올바르지 않아요." 같은 입력칸별 메시지를 돌려주기 위해서다.
 *
 * @param date          신청 날짜 (YYYY-MM-DD)
 * @param songTitle     곡 제목
 * @param artist        가수
 * @param applicantName 신청자 이름
 */
public record SongCreateRequest(String date, String songTitle, String artist, String applicantName) {
}
