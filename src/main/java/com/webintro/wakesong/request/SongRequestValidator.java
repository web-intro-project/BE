package com.webintro.wakesong.request;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import com.webintro.wakesong.common.ValidationException;

/**
 * 신청 입력 검증 (FR-06). 메시지는 FE 신청 폼(js/common.js validateField)과 같다.
 * 문자열은 앞뒤 공백을 지운 뒤 길이를 센다.
 */
final class SongRequestValidator {

	static final int SONG_TITLE_MAX = 100;
	static final int ARTIST_MAX = 100;
	static final int APPLICANT_NAME_MAX = 20;

	private static final Pattern DATE_FORMAT = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");

	private SongRequestValidator() {
	}

	/** 검증을 통과하면 공백을 지운 값으로 엔티티 필드를 돌려주고, 아니면 ValidationException. */
	static Valid validate(CreateSongRequest body, LocalDate today) {
		Map<String, String> errors = new LinkedHashMap<>();
		LocalDate date = parseDate(body.date(), today, errors);
		String songTitle = text("songTitle", "곡 제목을", body.songTitle(), SONG_TITLE_MAX, errors);
		String artist = text("artist", "가수를", body.artist(), ARTIST_MAX, errors);
		String applicantName = text("applicantName", "신청자 이름을", body.applicantName(), APPLICANT_NAME_MAX, errors);
		if (!errors.isEmpty()) {
			throw new ValidationException(errors);
		}
		return new Valid(date, songTitle, artist, applicantName);
	}

	record Valid(LocalDate date, String songTitle, String artist, String applicantName) {
	}

	private static LocalDate parseDate(String raw, LocalDate today, Map<String, String> errors) {
		String v = raw == null ? "" : raw.strip();
		if (v.isEmpty()) {
			errors.put("date", "신청 날짜를 선택해 주세요.");
			return null;
		}
		LocalDate date;
		try {
			date = DATE_FORMAT.matcher(v).matches() ? LocalDate.parse(v) : null;
		}
		catch (DateTimeParseException e) {
			date = null;
		}
		if (date == null) {
			errors.put("date", "날짜 형식이 올바르지 않아요.");
		}
		else if (date.isBefore(today)) {
			errors.put("date", "지난 날짜는 신청할 수 없어요.");
		}
		return date;
	}

	private static String text(String field, String label, String raw, int max, Map<String, String> errors) {
		String v = raw == null ? "" : raw.strip();
		if (v.isEmpty()) {
			errors.put(field, label + " 입력해 주세요.");
		}
		else if (v.length() > max) {
			errors.put(field, max + "자 이내로 입력해 주세요.");
		}
		return v;
	}

}
