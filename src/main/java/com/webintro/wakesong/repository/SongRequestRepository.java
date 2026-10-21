package com.webintro.wakesong.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.webintro.wakesong.domain.SongRequest;

/**
 * song_requests 테이블에 접근하는 창구.
 *
 * <p>인터페이스만 만들면 Spring Data JPA가 구현체를 자동으로 만들어 준다.
 * {@code JpaRepository<SongRequest, Long>}을 상속해서 save(), findById(), count(), deleteAll() 같은
 * 기본 메서드는 따로 작성하지 않아도 쓸 수 있다.
 *
 * <p>아래 두 메서드는 {@code @Query}에 JPQL(엔티티 기준 SQL)을 직접 적었다.
 * 정렬 기준 "접수 시각 → 같으면 id"는 대표곡·목록 순서를 항상 똑같이 만들기 위한 것이다.
 */
public interface SongRequestRepository extends JpaRepository<SongRequest, Long> {

	/**
	 * 하루치 신청을 접수 순으로 가져온다.
	 *
	 * @param date 신청 날짜
	 * @return 접수 시각 오름차순(같으면 id 오름차순). 없으면 빈 리스트
	 */
	@Query("select r from SongRequest r where r.requestDate = :date order by r.createdAt, r.id")
	List<SongRequest> findAllByDate(LocalDate date);

	/**
	 * 기간(from ~ to, 양 끝 포함) 안의 신청을 가져온다. 달력에서 한 달치를 한 번에 읽을 때 쓴다.
	 *
	 * @return 날짜 순 → 같은 날짜 안에서는 접수 순
	 */
	@Query("select r from SongRequest r where r.requestDate between :from and :to order by r.requestDate, r.createdAt, r.id")
	List<SongRequest> findAllBetween(LocalDate from, LocalDate to);

}
