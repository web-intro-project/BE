package com.webintro.wakesong.today;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.webintro.wakesong.request.SongRequest;
import com.webintro.wakesong.request.SongRequestRepository;

@RestController
public class TodayController {

	private final SongRequestRepository repository;
	private final Clock clock;

	public TodayController(SongRequestRepository repository, Clock clock) {
		this.repository = repository;
		this.clock = clock;
	}

	@GetMapping("/api/today")
	@Transactional(readOnly = true)
	public TodayResponse today() {
		LocalDate today = LocalDate.now(clock);
		List<SongRequest> requests = repository.findByRequestDateOrderByCreatedAtAscIdAsc(today);
		TodayResponse.Song song = requests.isEmpty() ? null : toSong(requests.get(0));
		return new TodayResponse(today, requests.size(), song);
	}

	private static TodayResponse.Song toSong(SongRequest r) {
		return new TodayResponse.Song(r.getId(), r.getSongTitle(), r.getArtist(), r.getApplicantName());
	}

}
