package com.webintro.wakesong.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateSongRequest(
		@NotNull LocalDate date,
		@NotBlank @Size(max = 100) String songTitle,
		@NotBlank @Size(max = 100) String artist,
		@NotBlank @Size(max = 20) String applicantName) {
}
