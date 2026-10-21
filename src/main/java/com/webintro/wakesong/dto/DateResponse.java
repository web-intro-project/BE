// 이 파일이 dto 패키지에 속한다고 알려주는 코드
package com.webintro.wakesong.dto;

// 날짜(LocalDate)와 목록(List) 클래스를 가져오는 코드
import java.time.LocalDate;
import java.util.List;

/**
 * [응답] GET /api/requests?date= — 날짜별 신청 목록.
 * 예) { "date": "2026-10-21", "requests": [ {신청1}, {신청2} ] }
 */
// 조회한 날짜와 그 날짜의 신청 목록(접수 순, 없으면 [])을 담는 record를 선언하는 코드
public record DateResponse(LocalDate date, List<SongResponse> requests) {
}
