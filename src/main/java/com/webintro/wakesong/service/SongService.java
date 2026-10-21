package com.webintro.wakesong.service;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.webintro.wakesong.domain.SongRequest;
import com.webintro.wakesong.dto.CalendarResponse;
import com.webintro.wakesong.dto.DateResponse;
import com.webintro.wakesong.dto.SongCreateRequest;
import com.webintro.wakesong.dto.SongResponse;
import com.webintro.wakesong.dto.TodayResponse;
import com.webintro.wakesong.exception.BadRequestException;
import com.webintro.wakesong.repository.SongRequestRepository;

import lombok.RequiredArgsConstructor;

/**
 * 기상송 업무 규칙이 전부 모여 있는 곳.
 *
 * <p>핵심 규칙
 * <ol>
 *   <li>"오늘"은 항상 서울 기준이다. (Clock 사용)</li>
 *   <li>대표 신청곡 = 그날 가장 먼저 접수된 신청. 실제 방송 확정곡은 아니다.</li>
 *   <li>한 날짜에 여러 곡을 신청할 수 있다. 곡·신청자가 같아도 따로 저장한다.</li>
 *   <li>지난 날짜로 새로 신청할 수는 없지만, 지난 날짜를 조회하는 건 된다.</li>
 * </ol>
 *
 * <ul>
 *   <li>{@code @Service}: 스프링이 이 클래스를 하나 만들어 컨트롤러에 넣어 준다.</li>
 *   <li>{@code @Transactional(readOnly = true)}: 모든 메서드를 읽기 전용 트랜잭션으로 실행한다.
 *       DB에 쓰는 create()만 따로 {@code @Transactional}을 붙여 쓰기를 허용한다.</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SongService {

	/** 입력 글자 수 제한 (앞뒤 공백을 뺀 길이). DB 컬럼 길이와 같다. */
	private static final int SONG_TITLE_MAX = 100;
	private static final int ARTIST_MAX = 100;
	private static final int APPLICANT_NAME_MAX = 20;

	private final SongRequestRepository repository;

	/** 서울 시간대 시계 (ClockConfig). LocalDate.now(clock) = 서울 기준 오늘 */
	private final Clock clock;

	// ===================== 조회·저장 =====================

	/**
	 * [FR-01] 오늘의 대표 신청곡.
	 *
	 * @return 오늘 날짜, 오늘 신청 건수, 대표곡(오늘 신청이 없으면 null)
	 */
	public TodayResponse today() {
		// 1. 서울 기준 오늘 날짜
		LocalDate today = LocalDate.now(clock);

		// 2. 오늘 신청을 접수 순으로 가져온다
		List<SongRequest> todayRequests = repository.findAllByDate(today);

		// 3. 맨 앞(가장 먼저 접수된) 신청이 대표곡. 아무도 신청 안 했으면 null
		TodayResponse.Song first = todayRequests.isEmpty()
				? null
				: TodayResponse.Song.from(todayRequests.get(0));

		return new TodayResponse(today, todayRequests.size(), first);
	}

	/**
	 * [FR-02, FR-03] 기상송 신청을 검사하고 저장한다.
	 *
	 * <p>{@code @Transactional}: 클래스의 readOnly 설정을 덮어써서 DB 쓰기를 허용한다.
	 *
	 * @throws BadRequestException 입력이 잘못됐을 때 (→ 400)
	 */
	@Transactional
	public SongResponse create(SongCreateRequest req) {
		// 1. 입력 검사. 위에서부터 순서대로 보고, 처음 틀린 곳에서 바로 400을 던진다
		//    (FE 신청 폼도 같은 순서·같은 문구로 검사한다)
		LocalDate date = checkDate(req.date());
		String songTitle = checkText("songTitle", req.songTitle(), SONG_TITLE_MAX, "곡 제목을 입력해 주세요.");
		String artist = checkText("artist", req.artist(), ARTIST_MAX, "가수를 입력해 주세요.");
		String applicantName = checkText("applicantName", req.applicantName(), APPLICANT_NAME_MAX, "신청자 이름을 입력해 주세요.");

		// 2. 접수 시각은 사용자가 아니라 서버가 정한다 (지금 이 순간)
		Instant now = Instant.now(clock);

		// 3. DB에 저장. save()가 돌려준 객체에는 DB가 매긴 id가 들어 있다
		SongRequest saved = repository.save(new SongRequest(date, songTitle, artist, applicantName, now));

		return SongResponse.from(saved);
	}

	/**
	 * [FR-05] 지정한 날짜의 신청 전체를 접수 순으로. 지난 날짜도 조회할 수 있다.
	 *
	 * @return 신청이 없으면 requests = [] (오류 아님)
	 */
	public DateResponse byDate(LocalDate date) {
		List<SongResponse> requests = repository.findAllByDate(date).stream()
				.map(SongResponse::from)    // 엔티티 → 응답 DTO
				.toList();

		return new DateResponse(date, requests);
	}

	/**
	 * [FR-04] 월별 신청 현황(달력).
	 * 그 달의 1일부터 말일까지 모든 날짜를 돌려준다. 신청이 없는 날은 0건, 빈 목록.
	 */
	public CalendarResponse calendar(YearMonth month) {
		// 1. 한 달치 신청을 DB에서 한 번에 가져온다 (날짜마다 DB를 31번 부르지 않도록)
		List<SongRequest> monthRequests = repository.findAllBetween(month.atDay(1), month.atEndOfMonth());

		// 2. 1일 ~ 말일(28~31일)을 돌면서 그날 신청만 골라 하루 칸을 만든다
		List<CalendarResponse.Day> days = new ArrayList<>();
		for (int day = 1; day <= month.lengthOfMonth(); day++) {
			LocalDate date = month.atDay(day);

			List<CalendarResponse.Song> songs = monthRequests.stream()
					.filter(r -> r.getRequestDate().equals(date))   // 이 날짜 신청만
					.map(CalendarResponse.Song::from)               // 엔티티 → 달력용 DTO
					.toList();

			days.add(new CalendarResponse.Day(date, songs.size(), songs));
		}

		return new CalendarResponse(month, days);
	}

	// ===================== 입력 검사 (FR-06) =====================
	// 메시지는 FE 신청 폼(js/common.js의 validateField)과 같은 문구다.

	/**
	 * 신청 날짜 검사.
	 * <ol>
	 *   <li>비어 있으면 안 된다</li>
	 *   <li>YYYY-MM-DD 형식의 실제 있는 날짜여야 한다 (2026-02-30 같은 날짜는 안 됨)</li>
	 *   <li>서울 기준 오늘이거나 미래여야 한다 (어제 이전은 안 됨)</li>
	 * </ol>
	 *
	 * @return 검사를 통과한 날짜
	 */
	private LocalDate checkDate(String value) {
		String text = trim(value);
		if (text.isEmpty()) {
			throw new BadRequestException("date", "신청 날짜를 선택해 주세요.");
		}

		LocalDate date = parseDate(text);
		if (date == null) {
			throw new BadRequestException("date", "날짜 형식이 올바르지 않아요.");
		}

		LocalDate today = LocalDate.now(clock);
		if (date.isBefore(today)) {
			throw new BadRequestException("date", "지난 날짜는 신청할 수 없어요.");
		}

		return date;
	}

	/**
	 * 글자 입력칸(곡 제목·가수·신청자) 검사. 앞뒤 공백을 뺀 뒤 1자 이상, max자 이하여야 한다.
	 *
	 * @param field        입력칸 이름 (오류 응답의 field)
	 * @param value        사용자가 보낸 값 (null일 수도 있다)
	 * @param max          최대 글자 수
	 * @param emptyMessage 비어 있을 때 보여 줄 문구
	 * @return 앞뒤 공백을 지운 값 (이 값으로 저장한다)
	 */
	private String checkText(String field, String value, int max, String emptyMessage) {
		String text = trim(value);
		if (text.isEmpty()) {
			throw new BadRequestException(field, emptyMessage);
		}
		if (text.length() > max) {
			throw new BadRequestException(field, max + "자 이내로 입력해 주세요.");
		}
		return text;
	}

	/** 앞뒤 공백 제거. null이면 빈 문자열로 바꿔서 "입력 안 함"으로 처리한다. */
	private static String trim(String value) {
		return value == null ? "" : value.strip();
	}

	/**
	 * "2026-10-21" 같은 정확한 형식의 날짜만 LocalDate로 바꾼다.
	 *
	 * @return 형식이 틀리거나("20261021", "2026-10-1") 없는 날짜("2026-02-30")면 null
	 */
	private static LocalDate parseDate(String text) {
		// 숫자 4-2-2 자리 모양인지 먼저 확인
		if (!text.matches("\\d{4}-\\d{2}-\\d{2}")) {
			return null;
		}
		// 모양은 맞아도 13월, 2월 30일처럼 없는 날짜면 parse가 예외를 던진다
		try {
			return LocalDate.parse(text);
		}
		catch (DateTimeParseException e) {
			return null;
		}
	}

}
