// 이 파일이 exception 패키지에 속한다고 알려주는 코드
package com.webintro.wakesong.exception;

// HTTP 상태 코드 클래스를 가져오는 코드
import org.springframework.http.HttpStatus;
// 스프링이 입력이 잘못됐을 때 던지는 예외 클래스들을 가져오는 코드
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
// 예외 처리용 어노테이션을 가져오는 코드
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 입력이 잘못됐을 때(400) 오류 응답 JSON을 만들어주는 곳.
 * 404, 405, 500 같은 나머지 오류는 스프링이 기본으로 처리한다.
 */
// 모든 @RestController에서 발생한 예외를 이 클래스가 잡아서 JSON으로 응답하게 하는 코드
@RestControllerAdvice
// 전역 예외 처리 클래스를 선언하는 코드
public class GlobalExceptionHandler {

	// BadRequestException(서비스의 입력 검사 실패)이 터지면 이 메서드가 처리하게 하는 코드
	@ExceptionHandler(BadRequestException.class)
	// 응답 상태를 400으로 정하는 코드
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	// 입력 검사 오류를 응답으로 바꾸는 메서드를 선언하는 코드
	public ErrorResponse badRequest(BadRequestException e) {
		// 예외에 담긴 메시지와 틀린 입력칸 이름으로 오류 응답을 만들어 돌려주는 코드
		return new ErrorResponse("VALIDATION_ERROR", e.getMessage(), e.getField());
	}

	// ?date= / ?month= 형식이 틀렸거나(MethodArgumentTypeMismatch) 아예 없을 때(MissingServletRequestParameter) 이 메서드가 처리하게 하는 코드
	@ExceptionHandler({ MethodArgumentTypeMismatchException.class, MissingServletRequestParameterException.class })
	// 응답 상태를 400으로 정하는 코드
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	// 쿼리 파라미터 오류를 응답으로 바꾸는 메서드를 선언하는 코드
	public ErrorResponse badParam() {
		// 날짜·월 형식을 안내하는 오류 응답을 만들어 돌려주는 코드
		return new ErrorResponse("VALIDATION_ERROR", "날짜 형식이 올바르지 않아요. (date: YYYY-MM-DD, month: YYYY-MM)", null);
	}

	// POST 본문 JSON이 깨졌거나 비어 있을 때 이 메서드가 처리하게 하는 코드
	@ExceptionHandler(HttpMessageNotReadableException.class)
	// 응답 상태를 400으로 정하는 코드
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	// 깨진 JSON 오류를 응답으로 바꾸는 메서드를 선언하는 코드
	public ErrorResponse badJson() {
		// 어느 칸이 틀렸는지 알 수 없으므로 field 없이 오류 응답을 만들어 돌려주는 코드
		return new ErrorResponse("VALIDATION_ERROR", "요청 형식이 올바르지 않아요.", null);
	}

}
