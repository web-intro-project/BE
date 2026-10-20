package com.webintro.wakesong.request;

import java.time.Instant;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(name = "song_requests",
		indexes = @Index(name = "idx_song_requests_date_order", columnList = "request_date, created_at, id"))
public class SongRequest {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "request_date", nullable = false)
	private LocalDate requestDate;

	@Column(name = "song_title", nullable = false, length = 100)
	private String songTitle;

	@Column(nullable = false, length = 100)
	private String artist;

	@Column(name = "applicant_name", nullable = false, length = 20)
	private String applicantName;

	/** 접수 시각. 서버가 정하며 UTC로 저장한다. */
	@Column(name = "created_at", nullable = false, columnDefinition = "DATETIME(6)")
	private Instant createdAt;

	protected SongRequest() {
	}

	public SongRequest(LocalDate requestDate, String songTitle, String artist, String applicantName, Instant createdAt) {
		this.requestDate = requestDate;
		this.songTitle = songTitle;
		this.artist = artist;
		this.applicantName = applicantName;
		this.createdAt = createdAt;
	}

	public Long getId() {
		return id;
	}

	public LocalDate getRequestDate() {
		return requestDate;
	}

	public String getSongTitle() {
		return songTitle;
	}

	public String getArtist() {
		return artist;
	}

	public String getApplicantName() {
		return applicantName;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

}
