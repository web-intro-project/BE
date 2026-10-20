package com.webintro.wakesong.dto;

import java.time.LocalDate;
import java.util.List;

/** GET /api/requests?date= 응답 */
public record DateResponse(LocalDate date, List<SongResponse> requests) {
}
