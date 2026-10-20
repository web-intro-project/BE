package com.webintro.wakesong.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

/** 모든 오류 응답의 모양: {code, message, field?} */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(String code, String message, String field) {

	public ErrorResponse(String code, String message) {
		this(code, message, null);
	}

}
