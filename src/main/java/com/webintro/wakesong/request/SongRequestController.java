package com.webintro.wakesong.request;

import java.time.LocalDate;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/requests")
public class SongRequestController {

	private final SongRequestService service;

	public SongRequestController(SongRequestService service) {
		this.service = service;
	}

	@GetMapping
	public DateRequestsResponse findByDate(@RequestParam LocalDate date) {
		return service.findByDate(date);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public SongRequestResponse create(@RequestBody CreateSongRequest body) {
		return service.create(body);
	}

}
