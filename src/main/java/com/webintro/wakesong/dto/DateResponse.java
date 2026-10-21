package com.webintro.wakesong.dto;

import java.time.LocalDate;
import java.util.List;

/**
 * [응답] GET /api/requests?date= — 날짜별 전체 신청 목록.
 *
 * <pre>
 * { "date": "2026-10-21", "requests": [ {신청1}, {신청2}, ... ] }
 * </pre>
 *
 * @param date     조회한 날짜
 * @param requests 그 날짜의 신청, 접수 순. 없으면 빈 배열 []
 */
public record DateResponse(LocalDate date, List<SongResponse> requests) {
}
