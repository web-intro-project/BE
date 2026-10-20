package com.webintro.wakesong.common;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 공통 오류 응답. field는 첫 번째 오류 필드(FE가 해당 입력칸에 메시지를 표시),
 * fieldErrors는 오류가 난 모든 필드의 메시지다.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(String code, String message, String field, Map<String, String> fieldErrors) {

	public static ApiError of(String code, String message) {
		return new ApiError(code, message, null, null);
	}

}
