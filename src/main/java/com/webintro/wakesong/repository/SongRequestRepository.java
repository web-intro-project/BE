// 이 파일이 repository 패키지에 속한다고 알려주는 코드
package com.webintro.wakesong.repository;

// 날짜(LocalDate)와 목록(List) 클래스를 가져오는 코드
import java.time.LocalDate;
import java.util.List;

// Spring Data JPA의 기본 리포지토리를 가져오는 코드
import org.springframework.data.jpa.repository.JpaRepository;

// 신청 엔티티를 가져오는 코드
import com.webintro.wakesong.domain.SongRequest;

/**
 * song_requests 테이블에 접근하는 창구.
 * 메서드 이름만 규칙대로 지으면 Spring Data JPA가 SQL을 자동으로 만들어 준다.
 */
// SongRequest(id 타입 Long)용 리포지토리를 선언하고, save·count·deleteAll 같은 기본 기능을 물려받는 코드
public interface SongRequestRepository extends JpaRepository<SongRequest, Long> {

	// 특정 날짜(requestDate)의 신청을 id 순서(= 먼저 접수된 순서)로 가져오는 메서드를 선언하는 코드
	List<SongRequest> findByRequestDateOrderById(LocalDate requestDate);

	// start~end 기간의 신청을 id 순서로 가져오는 메서드를 선언하는 코드 (달력 한 달치 조회용)
	List<SongRequest> findByRequestDateBetweenOrderById(LocalDate start, LocalDate end);

}
