package com.webintro.wakesong.dto;

/** POST /api/requests 요청 본문. 검사는 SongService가 한다. */
public record SongCreateRequest(String date, String songTitle, String artist, String applicantName) {
}
