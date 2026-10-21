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
import jakarta.persistence.Table;

// getter와 빈 생성자를 자동으로 만들어주는 Lombok 어노테이션을 가져오는 코드
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 기상송 신청 1건. DB의 song_requests 테이블 한 줄과 1:1로 연결된다.
 */
// 이 클래스를 DB 테이블과 연결되는 JPA 엔티티로 등록하는 코드
@Entity
// 연결할 테이블 이름을 song_requests로 정하는 코드
@Table(name = "song_requests")
// 모든 필드의 getXxx() 메서드를 자동으로 만들어주는 코드
@Getter
// JPA가 DB에서 읽어올 때 쓰는 빈 생성자를 자동으로 만들어주는 코드
@NoArgsConstructor
// 신청 엔티티 클래스를 선언하는 코드
public class SongRequest {

	// 이 필드가 테이블의 기본 키(PK)임을 표시하는 코드
	@Id
	// id 값을 DB가 1, 2, 3… 순서로 자동으로 매기게 하는 코드 (먼저 저장된 신청일수록 id가 작음)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	// 신청 번호를 담는 필드
	private Long id;

	// request_date 컬럼과 연결하는 코드
	@Column(name = "request_date")
	// 신청 날짜("이 날 아침에 틀어 주세요")를 담는 필드
	private LocalDate requestDate;

	// song_title 컬럼과 연결하는 코드
	@Column(name = "song_title")
	// 곡 제목을 담는 필드
	private String songTitle;

	// 가수 이름을 담는 필드 (컬럼 이름도 artist)
	private String artist;

	// applicant_name 컬럼과 연결하는 코드
	@Column(name = "applicant_name")
	// 신청자 이름을 담는 필드
	private String applicantName;

	// created_at 컬럼과 연결하는 코드
	@Column(name = "created_at")
	// 접수 시각(UTC)을 담는 필드
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
