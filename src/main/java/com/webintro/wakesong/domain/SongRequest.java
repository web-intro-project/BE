// 이 파일이 domain 패키지에 속한다고 알려주는 코드
package com.webintro.wakesong.domain;

// 시각(Instant)과 날짜(LocalDate) 클래스를 가져오는 코드
import java.time.Instant;
import java.time.LocalDate;

// DB 테이블과 연결하는 JPA 어노테이션을 가져오는 코드
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

// getter와 기본 생성자를 자동으로 만들어주는 Lombok 어노테이션을 가져오는 코드
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 기상송 신청 1건. DB의 song_requests 테이블 한 줄과 1:1로 연결된다.
 * setter가 없어서 한 번 저장된 신청은 바뀌지 않는다.
 */
// 이 클래스를 DB 테이블과 연결되는 JPA 엔티티로 등록하는 코드
@Entity
// 테이블 이름을 song_requests로 정하고, (날짜, 접수 시각, id) 순서의 인덱스를 만들어 조회를 빠르게 해주는 코드
@Table(name = "song_requests", indexes = @Index(columnList = "request_date, created_at, id"))
// 모든 필드의 getXxx() 메서드를 자동으로 만들어주는 코드
@Getter
// JPA가 DB에서 읽어올 때 쓰는 빈 생성자를 만들고, 밖에서는 못 쓰게 protected로 막는 코드
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// 신청 엔티티 클래스를 선언하는 코드
public class SongRequest {

	// 이 필드가 테이블의 기본 키(PK)임을 표시하는 코드
	@Id
	// id 값을 DB가 1, 2, 3… 순서로 자동으로 매기게 하는 코드 (MySQL AUTO_INCREMENT)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	// 신청 번호를 담는 필드
	private Long id;

	// request_date 컬럼과 연결하고, 비어 있으면 안 된다고 정하는 코드
	@Column(name = "request_date", nullable = false)
	// 신청 날짜("이 날 아침에 틀어 주세요")를 담는 필드
	private LocalDate requestDate;

	// song_title 컬럼과 연결하고, 필수·최대 100자로 정하는 코드
	@Column(name = "song_title", nullable = false, length = 100)
	// 곡 제목을 담는 필드
	private String songTitle;

	// artist 컬럼과 연결하고, 필수·최대 100자로 정하는 코드
	@Column(nullable = false, length = 100)
	// 가수 이름을 담는 필드
	private String artist;

	// applicant_name 컬럼과 연결하고, 필수·최대 20자로 정하는 코드
	@Column(name = "applicant_name", nullable = false, length = 20)
	// 신청자 이름을 담는 필드
	private String applicantName;

	// created_at 컬럼과 연결하고, 마이크로초까지 저장(DATETIME(6))해서 거의 동시에 들어온 신청도 순서를 구분하게 하는 코드
	@Column(name = "created_at", nullable = false, columnDefinition = "DATETIME(6)")
	// 접수 시각(UTC)을 담는 필드 — 이 순서로 줄을 세워 가장 빠른 신청이 대표곡이 됨
	private Instant createdAt;

	// 새 신청 객체를 만드는 생성자를 선언하는 코드 (id는 저장할 때 DB가 채움)
	public SongRequest(LocalDate requestDate, String songTitle, String artist, String applicantName, Instant createdAt) {
		// 신청 날짜를 저장하는 코드
		this.requestDate = requestDate;
		// 곡 제목을 저장하는 코드
		this.songTitle = songTitle;
		// 가수 이름을 저장하는 코드
		this.artist = artist;
		// 신청자 이름을 저장하는 코드
		this.applicantName = applicantName;
		// 접수 시각을 저장하는 코드
		this.createdAt = createdAt;
	}

}
