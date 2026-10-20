package com.webintro.wakesong.common;

import java.util.Map;

/** 입력 검증 실패. fieldErrors는 필드 순서를 유지한 필드명 → 메시지. */
public class ValidationException extends RuntimeException {

	private final Map<String, String> fieldErrors;

	public ValidationException(Map<String, String> fieldErrors) {
		super("입력값을 확인해 주세요.");
		this.fieldErrors = fieldErrors;
	}

	public Map<String, String> getFieldErrors() {
		return fieldErrors;
	}

}
