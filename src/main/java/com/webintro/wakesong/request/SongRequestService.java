package com.webintro.wakesong.request;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SongRequestService {

	private final SongRequestRepository repository;
	private final Clock clock;

	public SongRequestService(SongRequestRepository repository, Clock clock) {
		this.repository = repository;
		this.clock = clock;
	}

	@Transactional
	public SongRequestResponse create(CreateSongRequest body) {
		SongRequestValidator.Valid v = SongRequestValidator.validate(body, LocalDate.now(clock));
		SongRequest saved = repository.save(
				new SongRequest(v.date(), v.songTitle(), v.artist(), v.applicantName(), Instant.now(clock)));
		return SongRequestResponse.from(saved);
	}

	@Transactional(readOnly = true)
	public DateRequestsResponse findByDate(LocalDate date) {
		return new DateRequestsResponse(date, repository.findByRequestDateOrderByCreatedAtAscIdAsc(date).stream()
				.map(SongRequestResponse::from)
				.toList());
	}

}
