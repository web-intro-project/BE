package com.webintro.wakesong.exception;

import lombok.Getter;

/**
 * 잘못된 입력이 들어왔을 때 SongService가 던지는 예외. → GlobalExceptionHandler가 400으로 응답한다.
 *
 * <p>RuntimeException을 상속해서 메서드마다 {@code throws}를 적지 않아도 된다.
 *
 * <pre>
 * throw new BadRequestException("date", "지난 날짜는 신청할 수 없어요.");
 * → 400 { "code": "VALIDATION_ERROR", "message": "지난 날짜는 신청할 수 없어요.", "field": "date" }
 * </pre>
 */
@Getter
public class BadRequestException extends RuntimeException {

	/** 틀린 입력칸 이름 (date, songTitle, artist, applicantName). FE가 이 칸 아래에 메시지를 띄운다. */
	private final String field;

	/**
	 * @param field   틀린 입력칸 이름
	 * @param message 화면에 보여 줄 메시지 (getMessage()로 꺼낸다)
	 */
	public BadRequestException(String field, String message) {
		super(message);
		this.field = field;
	}

}
