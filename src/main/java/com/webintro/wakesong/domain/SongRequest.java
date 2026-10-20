package com.webintro.wakesong.domain;

import java.time.Instant;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** 기상송 신청 1건 = song_requests 테이블의 한 줄 */
@Entity
@Table(name = "song_requests", indexes = @Index(columnList = "request_date, created_at, id"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SongRequest {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "request_date", nullable = false)
	private LocalDate requestDate;     // 신청한 날짜 (이 날 아침에 틀어 달라는 날)

	@Column(name = "song_title", nullable = false, length = 100)
	private String songTitle;

	@Column(nullable = false, length = 100)
	private String artist;

	@Column(name = "applicant_name", nullable = false, length = 20)
	private String applicantName;

	@Column(name = "created_at", nullable = false, columnDefinition = "DATETIME(6)")
	private Instant createdAt;         // 접수 시각 (UTC) — 이 순서로 줄 세운다

	public SongRequest(LocalDate requestDate, String songTitle, String artist, String applicantName, Instant createdAt) {
		this.requestDate = requestDate;
		this.songTitle = songTitle;
		this.artist = artist;
		this.applicantName = applicantName;
		this.createdAt = createdAt;
	}

}
