// 이 파일이 repository 패키지에 속한다고 알려주는 코드
package com.webintro.wakesong.repository;

// 날짜(LocalDate)와 목록(List) 클래스를 가져오는 코드
import java.time.LocalDate;
import java.util.List;

// Spring Data JPA의 기본 리포지토리와 @Query 어노테이션을 가져오는 코드
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

// 신청 엔티티를 가져오는 코드
import com.webintro.wakesong.domain.SongRequest;

/**
 * song_requests 테이블에 접근하는 창구.
 * 인터페이스만 만들면 Spring Data JPA가 구현을 자동으로 만들어 준다.
 */
// SongRequest(id 타입 Long)용 리포지토리를 선언하고, save·findById·count·deleteAll 같은 기본 기능을 물려받는 코드
public interface SongRequestRepository extends JpaRepository<SongRequest, Long> {

	// 특정 날짜의 신청을 접수 시각 순(같으면 id 순)으로 조회하는 JPQL을 지정하는 코드
	@Query("select r from SongRequest r where r.requestDate = :date order by r.createdAt, r.id")
	// 하루치 신청 목록을 가져오는 메서드를 선언하는 코드
	List<SongRequest> findAllByDate(LocalDate date);

	// from~to 기간의 신청을 날짜 순 → 접수 순으로 조회하는 JPQL을 지정하는 코드
	@Query("select r from SongRequest r where r.requestDate between :from and :to order by r.requestDate, r.createdAt, r.id")
	// 기간(달력 한 달치) 신청 목록을 가져오는 메서드를 선언하는 코드
	List<SongRequest> findAllBetween(LocalDate from, LocalDate to);

}
