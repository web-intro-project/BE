// 이 파일이 exception 패키지에 속한다고 알려주는 코드
package com.webintro.wakesong.exception;

/**
 * 입력 오류(400) 응답 모양.
 * 예) { "code": "VALIDATION_ERROR", "message": "곡 제목을 입력해 주세요.", "field": "songTitle" }
 */
// 오류 종류(code), 화면에 보여줄 문구(message), 틀린 입력칸(field)을 담는 record를 선언하는 코드
public record ErrorResponse(String code, String message, String field) {
}
