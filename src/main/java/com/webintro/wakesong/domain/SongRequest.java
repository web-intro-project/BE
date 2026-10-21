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

/**
 * 기상송 신청 1건. DB의 {@code song_requests} 테이블 한 줄과 1:1로 대응한다(JPA 엔티티).
 *
 * <ul>
 *   <li>{@code @Entity}: JPA가 관리하는 클래스라는 표시. 이 클래스를 보고 테이블을 만든다.</li>
 *   <li>{@code @Table}: 테이블 이름과 인덱스. 인덱스 (날짜, 접수 시각, id)는
 *       "특정 날짜의 신청을 접수 순으로" 찾는 조회를 빠르게 해 준다.</li>
 *   <li>{@code @Getter}(Lombok): 모든 필드의 getXxx() 메서드를 만들어 준다.</li>
 *   <li>{@code @NoArgsConstructor(PROTECTED)}: JPA가 DB에서 읽어 올 때 쓰는 빈 생성자.
 *       우리 코드에서 실수로 빈 객체를 만들지 못하게 protected로 막는다.</li>
 * </ul>
 *
 * <p>setter가 없다: 한 번 접수된 신청은 바뀌지 않는다.
 */
@Entity
@Table(name = "song_requests", indexes = @Index(columnList = "request_date, created_at, id"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SongRequest {

	/** 신청 번호. DB가 1, 2, 3… 순서로 자동으로 매긴다(IDENTITY = MySQL AUTO_INCREMENT). */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/** 신청 날짜. "이 날 아침에 틀어 주세요" 하는 날. */
	@Column(name = "request_date", nullable = false)
	private LocalDate requestDate;

	/** 곡 제목 (최대 100자). */
	@Column(name = "song_title", nullable = false, length = 100)
	private String songTitle;

	/** 가수 (최대 100자). */
	@Column(nullable = false, length = 100)
	private String artist;

	/** 신청자 이름 (최대 20자). */
	@Column(name = "applicant_name", nullable = false, length = 20)
	private String applicantName;

	/**
	 * 접수 시각 (UTC). 서버가 저장하는 순간에 기록한다.
	 * 같은 날짜의 신청은 이 시각 순서로 줄을 세우고, 가장 빠른 것이 대표곡이 된다.
	 * DATETIME(6) = 마이크로초까지 저장해서 거의 동시에 들어온 신청도 순서를 구분한다.
	 */
	@Column(name = "created_at", nullable = false, columnDefinition = "DATETIME(6)")
	private Instant createdAt;

	/** 새 신청을 만든다. id는 저장할 때 DB가 채운다. */
	public SongRequest(LocalDate requestDate, String songTitle, String artist, String applicantName, Instant createdAt) {
		this.requestDate = requestDate;
		this.songTitle = songTitle;
		this.artist = artist;
		this.applicantName = applicantName;
		this.createdAt = createdAt;
	}

}
