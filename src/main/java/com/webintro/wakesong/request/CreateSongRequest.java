package com.webintro.wakesong.request;

/** 신청 요청 본문. 형식·길이·날짜 검증은 SongRequestValidator에서 한다. */
public record CreateSongRequest(String date, String songTitle, String artist, String applicantName) {
}
