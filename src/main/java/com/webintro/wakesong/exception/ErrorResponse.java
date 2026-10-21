// 이 파일이 exception 패키지에 속한다고 알려주는 코드
package com.webintro.wakesong.exception;

// JSON 변환 시 null 필드를 빼주는 Jackson 어노테이션을 가져오는 코드
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 모든 오류 응답의 공통 모양.
 * 예) { "code": "VALIDATION_ERROR", "message": "곡 제목을 입력해 주세요.", "field": "songTitle" }
 *     { "code": "NOT_FOUND", "message": "요청한 주소를 찾을 수 없어요." }   ← field 없음
 */
// 값이 null인 필드(field)는 JSON에서 아예 빼주는 코드
@JsonInclude(JsonInclude.Include.NON_NULL)
// 오류 종류(code), 화면에 보여줄 문구(message), 틀린 입력칸(field)을 담는 record를 선언하는 코드
public record ErrorResponse(String code, String message, String field) {

	// field 없이 code와 message만으로 오류 응답을 만드는 생성자를 선언하는 코드 (404, 405, 500 등)
	public ErrorResponse(String code, String message) {
		// field 자리에 null을 넣어 기본 생성자를 호출하는 코드
		this(code, message, null);
	}

}
