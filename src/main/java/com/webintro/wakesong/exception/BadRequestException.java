// 이 파일이 exception 패키지에 속한다고 알려주는 코드
package com.webintro.wakesong.exception;

// getter를 자동으로 만들어주는 Lombok 어노테이션을 가져오는 코드
import lombok.Getter;

/**
 * 잘못된 입력이 들어왔을 때 던지는 예외. GlobalExceptionHandler가 잡아서 400으로 응답한다.
 * 예) throw new BadRequestException("date", "지난 날짜는 신청할 수 없어요.");
 */
// field의 getter(getField())를 자동으로 만들어주는 코드
@Getter
// RuntimeException을 상속해서 throws 선언 없이 던질 수 있는 입력 오류 예외 클래스를 선언하는 코드
public class BadRequestException extends RuntimeException {

	// 틀린 입력칸 이름(date, songTitle, artist, applicantName)을 담는 필드
	private final String field;

	// 틀린 입력칸 이름과 보여줄 메시지를 받아 예외를 만드는 생성자를 선언하는 코드
	public BadRequestException(String field, String message) {
		// 메시지를 부모(RuntimeException)에 넘겨 getMessage()로 꺼낼 수 있게 하는 코드
		super(message);
		// 틀린 입력칸 이름을 저장하는 코드
		this.field = field;
	}

}
