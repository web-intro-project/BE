package com.webintro.wakesong.exception;

import lombok.Getter;

/** 잘못된 입력 → 400. field = 틀린 입력칸 이름 (FE가 그 칸에 메시지를 띄운다) */
@Getter
public class BadRequestException extends RuntimeException {

	private final String field;

	public BadRequestException(String field, String message) {
		super(message);
		this.field = field;
	}

}
