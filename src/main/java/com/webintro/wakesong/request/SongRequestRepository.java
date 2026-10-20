package com.webintro.wakesong.request;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SongRequestRepository extends JpaRepository<SongRequest, Long> {

	/** 접수 순. 접수 시각이 같으면 id로 순서를 고정한다. */
	List<SongRequest> findByRequestDateOrderByCreatedAtAscIdAsc(LocalDate requestDate);

	List<SongRequest> findByRequestDateBetweenOrderByRequestDateAscCreatedAtAscIdAsc(LocalDate from, LocalDate to);

}
