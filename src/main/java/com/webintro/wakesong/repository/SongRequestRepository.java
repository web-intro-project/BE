package com.webintro.wakesong.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.webintro.wakesong.domain.SongRequest;

public interface SongRequestRepository extends JpaRepository<SongRequest, Long> {

	/** 하루치 신청, 접수 순 (시각이 같으면 id 순) */
	@Query("select r from SongRequest r where r.requestDate = :date order by r.createdAt, r.id")
	List<SongRequest> findAllByDate(LocalDate date);

	/** 기간 신청 (달력용), 날짜 → 접수 순 */
	@Query("select r from SongRequest r where r.requestDate between :from and :to order by r.requestDate, r.createdAt, r.id")
	List<SongRequest> findAllBetween(LocalDate from, LocalDate to);

}
