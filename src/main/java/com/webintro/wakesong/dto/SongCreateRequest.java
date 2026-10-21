// 이 파일이 dto 패키지에 속한다고 알려주는 코드
package com.webintro.wakesong.dto;

/**
 * [요청] POST /api/requests 본문 JSON.
 * 예) { "date": "2026-10-21", "songTitle": "밤양갱", "artist": "비비", "applicantName": "김서연" }
 */
// 신청 요청 JSON의 네 값을 그대로 받아 담는 record를 선언하는 코드
// (date를 String으로 받는 이유: 형식이 틀린 날짜도 받아서 "날짜 형식이 올바르지 않아요." 메시지를 돌려주기 위해서)
public record SongCreateRequest(String date, String songTitle, String artist, String applicantName) {
}
