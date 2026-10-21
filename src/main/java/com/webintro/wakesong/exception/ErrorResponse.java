package com.webintro.wakesong.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 모든 오류 응답의 공통 모양.
 *
 * <pre>
 * { "code": "VALIDATION_ERROR", "message": "곡 제목을 입력해 주세요.", "field": "songTitle" }
 * { "code": "NOT_FOUND", "message": "요청한 주소를 찾을 수 없어요." }        ← field 없음
 * </pre>
 *
 * <p>{@code @JsonInclude(NON_NULL)}: 값이 null인 필드는 JSON에서 아예 빼 준다.
 * 그래서 입력 오류가 아닐 때는 "field": null 대신 field 자체가 나오지 않는다.
 *
 * @param code    오류 종류 (VALIDATION_ERROR, NOT_FOUND, METHOD_NOT_ALLOWED, ...)
 * @param message 화면에 그대로 보여 줄 수 있는 한국어 문구
 * @param field   입력 오류일 때만: 틀린 입력칸 / 파라미터 이름
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(String code, String message, String field) {

	/** field가 없는 오류(404, 405, 500 등)용 생성자. */
	public ErrorResponse(String code, String message) {
		this(code, message, null);
	}

}
