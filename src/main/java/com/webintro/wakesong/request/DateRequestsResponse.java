package com.webintro.wakesong.request;

import java.time.LocalDate;
import java.util.List;

public record DateRequestsResponse(LocalDate date, List<SongRequestResponse> requests) {
}
