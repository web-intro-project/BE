// 이 파일이 exception 패키지에 속한다고 알려주는 코드
package com.webintro.wakesong.exception;

// 서버 로그를 남기는 클래스를 가져오는 코드
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
// HTTP 상태 코드 클래스를 가져오는 코드
import org.springframework.http.HttpStatus;
// 스프링이 상황별로 던지는 예외 클래스들을 가져오는 코드
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
// 예외 처리용 어노테이션을 가져오는 코드
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 모든 컨트롤러에서 터진 예외를 한곳에서 잡아 ErrorResponse JSON으로 바꿔주는 곳.
 *
 * <pre>
 * 예외                                  → 상태  code
 * BadRequestException (입력 검사 실패)   → 400   VALIDATION_ERROR
 * ?date= / ?month= 형식 틀림 · 없음      → 400   VALIDATION_ERROR
 * JSON이 깨졌거나 본문 없음              → 400   VALIDATION_ERROR
 * 없는 주소                              → 404   NOT_FOUND
 * 지원하지 않는 메서드 (DELETE 등)       → 405   METHOD_NOT_ALLOWED
 * 본문이 JSON이 아님                     → 415   UNSUPPORTED_MEDIA_TYPE
 * 그 밖의 모든 예외 (DB 장애 등)         → 500   INTERNAL_SERVER_ERROR
 * </pre>
 */
// 모든 @RestController에서 발생한 예외를 이 클래스가 잡아서 JSON으로 응답하게 하는 코드
@RestControllerAdvice
// 전역 예외 처리 클래스를 선언하는 코드
public class GlobalExceptionHandler {

	// 이 클래스에서 서버 로그를 남길 로거를 만드는 코드
	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	// 입력 오류를 뜻하는 code 값을 상수로 정의하는 코드
	private static final String VALIDATION_ERROR = "VALIDATION_ERROR";
	// 없는 주소를 뜻하는 code 값을 상수로 정의하는 코드
	private static final String NOT_FOUND = "NOT_FOUND";
	// 허용되지 않은 메서드를 뜻하는 code 값을 상수로 정의하는 코드
	private static final String METHOD_NOT_ALLOWED = "METHOD_NOT_ALLOWED";
	// JSON이 아닌 본문을 뜻하는 code 값을 상수로 정의하는 코드
	private static final String UNSUPPORTED_MEDIA_TYPE = "UNSUPPORTED_MEDIA_TYPE";
	// 서버 내부 오류를 뜻하는 code 값을 상수로 정의하는 코드
	private static final String INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";

	// ===================== 400 Bad Request =====================

	// BadRequestException(서비스의 입력 검사 실패)이 터지면 이 메서드가 처리하게 하는 코드
	@ExceptionHandler(BadRequestException.class)
	// 응답 상태를 400으로 정하는 코드
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	// 입력 검사 오류를 응답으로 바꾸는 메서드를 선언하는 코드
	public ErrorResponse badRequest(BadRequestException e) {
		// 예외에 담긴 메시지와 틀린 입력칸 이름으로 오류 응답을 만들어 돌려주는 코드
		return new ErrorResponse(VALIDATION_ERROR, e.getMessage(), e.getField());
	}

	// 쿼리 파라미터 형식이 틀렸을 때(예: ?date=2026-13-01) 이 메서드가 처리하게 하는 코드
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	// 응답 상태를 400으로 정하는 코드
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	// 형식이 틀린 파라미터 오류를 응답으로 바꾸는 메서드를 선언하는 코드
	public ErrorResponse wrongParam(MethodArgumentTypeMismatchException e) {
		// 틀린 파라미터 이름(date/month)으로 오류 응답을 만들어 돌려주는 코드
		return paramError(e.getName());
	}

	// 필수 쿼리 파라미터가 없을 때(예: GET /api/requests) 이 메서드가 처리하게 하는 코드
	@ExceptionHandler(MissingServletRequestParameterException.class)
	// 응답 상태를 400으로 정하는 코드
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	// 빠진 파라미터 오류를 응답으로 바꾸는 메서드를 선언하는 코드
	public ErrorResponse missingParam(MissingServletRequestParameterException e) {
		// 빠진 파라미터 이름(date/month)으로 오류 응답을 만들어 돌려주는 코드
		return paramError(e.getParameterName());
	}

	// POST 본문 JSON이 깨졌거나 비어 있을 때 이 메서드가 처리하게 하는 코드
	@ExceptionHandler(HttpMessageNotReadableException.class)
	// 응답 상태를 400으로 정하는 코드
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	// 깨진 JSON 오류를 응답으로 바꾸는 메서드를 선언하는 코드
	public ErrorResponse badJson() {
		// 어느 칸이 틀렸는지 알 수 없으므로 field 없이 오류 응답을 만들어 돌려주는 코드
		return new ErrorResponse(VALIDATION_ERROR, "요청 형식이 올바르지 않아요.");
	}

	// ===================== 404 / 405 / 415 =====================

	// 없는 주소로 요청했을 때(예: GET /api/nope) 이 메서드가 처리하게 하는 코드
	@ExceptionHandler(NoResourceFoundException.class)
	// 응답 상태를 404로 정하는 코드
	@ResponseStatus(HttpStatus.NOT_FOUND)
	// 없는 주소 오류를 응답으로 바꾸는 메서드를 선언하는 코드
	public ErrorResponse notFound() {
		// "요청한 주소를 찾을 수 없어요." 오류 응답을 만들어 돌려주는 코드
		return new ErrorResponse(NOT_FOUND, "요청한 주소를 찾을 수 없어요.");
	}

	// 주소는 있지만 그 메서드를 지원하지 않을 때(예: DELETE /api/requests) 이 메서드가 처리하게 하는 코드
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	// 응답 상태를 405로 정하는 코드
	@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
	// 지원하지 않는 메서드 오류를 응답으로 바꾸는 메서드를 선언하는 코드
	public ErrorResponse methodNotAllowed() {
		// "지원하지 않는 요청 방식이에요." 오류 응답을 만들어 돌려주는 코드
		return new ErrorResponse(METHOD_NOT_ALLOWED, "지원하지 않는 요청 방식이에요.");
	}

	// POST 본문이 JSON이 아닐 때(예: Content-Type: text/plain) 이 메서드가 처리하게 하는 코드
	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	// 응답 상태를 415로 정하는 코드
	@ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
	// JSON이 아닌 본문 오류를 응답으로 바꾸는 메서드를 선언하는 코드
	public ErrorResponse notJson() {
		// "JSON으로 보내 주세요." 오류 응답을 만들어 돌려주는 코드
		return new ErrorResponse(UNSUPPORTED_MEDIA_TYPE, "JSON(application/json)으로 보내 주세요.");
	}

	// ===================== 500 Internal Server Error =====================

	// 위에서 잡지 못한 나머지 모든 예외(DB 장애 등)를 이 메서드가 처리하게 하는 코드
	@ExceptionHandler(Exception.class)
	// 응답 상태를 500으로 정하는 코드
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	// 서버 내부 오류를 응답으로 바꾸는 메서드를 선언하는 코드
	public ErrorResponse serverError(Exception e) {
		// 오류 원인(스택트레이스)을 서버 로그에만 남기는 코드
		log.error("처리하지 못한 오류", e);
		// 내부 정보(SQL, 비밀번호 등)는 빼고 안내 문구만 담은 오류 응답을 돌려주는 코드
		return new ErrorResponse(INTERNAL_SERVER_ERROR, "일시적인 오류로 요청을 처리하지 못했어요. 잠시 후 다시 시도해 주세요.");
	}

	// ===================== 도우미 =====================

	// 쿼리 파라미터 오류 응답을 만들어주는 도우미 메서드를 선언하는 코드
	private static ErrorResponse paramError(String name) {
		// 파라미터가 month면 월 형식 안내, 아니면(date) 날짜 형식 안내 문구를 고르는 코드
		String message = name.equals("month")
				? "월 형식이 올바르지 않아요. (YYYY-MM)"
				: "날짜 형식이 올바르지 않아요. (YYYY-MM-DD)";
		// 고른 문구와 파라미터 이름으로 400 오류 응답을 만들어 돌려주는 코드
		return new ErrorResponse(VALIDATION_ERROR, message, name);
	}

}
