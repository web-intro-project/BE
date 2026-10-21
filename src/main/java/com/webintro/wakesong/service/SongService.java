// 이 파일이 service 패키지에 속한다고 알려주는 코드
package com.webintro.wakesong.service;

// 시각·날짜·연월·시간대 클래스와 날짜 변환 실패 예외를 가져오는 코드
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
// 목록(List, ArrayList) 클래스를 가져오는 코드
import java.util.ArrayList;
import java.util.List;

// 서비스 등록·트랜잭션용 스프링 어노테이션을 가져오는 코드
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 엔티티, 요청·응답 DTO, 입력 오류 예외, 리포지토리를 가져오는 코드
import com.webintro.wakesong.domain.SongRequest;
import com.webintro.wakesong.dto.CalendarResponse;
import com.webintro.wakesong.dto.DateResponse;
import com.webintro.wakesong.dto.SongCreateRequest;
import com.webintro.wakesong.dto.SongResponse;
import com.webintro.wakesong.dto.TodayResponse;
import com.webintro.wakesong.exception.BadRequestException;
import com.webintro.wakesong.repository.SongRequestRepository;

// final 필드용 생성자를 자동으로 만들어주는 Lombok 어노테이션을 가져오는 코드
import lombok.RequiredArgsConstructor;

/**
 * 기상송 업무 규칙이 전부 모여 있는 곳.
 * <ol>
 *   <li>"오늘"은 항상 서울 기준이다.</li>
 *   <li>대표 신청곡 = 그날 가장 먼저 접수된 신청 (실제 방송 확정곡은 아님).</li>
 *   <li>한 날짜에 여러 곡을 신청할 수 있다. 곡·신청자가 같아도 따로 저장한다.</li>
 *   <li>지난 날짜로 새로 신청할 수는 없지만, 지난 날짜 조회는 된다.</li>
 * </ol>
 */
// 이 클래스를 스프링 서비스 빈으로 등록해 컨트롤러에 주입될 수 있게 하는 코드
@Service
// final 필드(repository)를 받는 생성자를 자동으로 만들어주는 코드
@RequiredArgsConstructor
// 모든 메서드를 읽기 전용 트랜잭션으로 실행하게 하는 코드 (DB에 쓰는 create()만 따로 풀어줌)
@Transactional(readOnly = true)
// 기상송 서비스 클래스를 선언하는 코드
public class SongService {

	// 서울 시간대를 상수로 정하는 코드 ("오늘"을 구할 때 사용)
	private static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");

	// DB에 저장·조회하는 리포지토리를 담는 필드
	private final SongRequestRepository repository;

	// ===================== 조회·저장 =====================

	// [FR-01] 오늘의 대표 신청곡을 조회하는 메서드를 선언하는 코드
	public TodayResponse today() {
		// 서울 기준 오늘 날짜를 구하는 코드
		LocalDate today = LocalDate.now(SEOUL);

		// 오늘 날짜의 신청을 접수 순으로 DB에서 가져오는 코드
		List<SongRequest> todayRequests = repository.findByRequestDateOrderById(today);

		// 대표곡을 담을 변수를 만드는 코드 (신청이 없으면 null 그대로)
		TodayResponse.Song first = null;
		// 오늘 신청이 하나라도 있는지 확인하는 코드
		if (!todayRequests.isEmpty()) {
			// 맨 앞(가장 먼저 접수된) 신청을 대표곡으로 정하는 코드
			first = TodayResponse.Song.from(todayRequests.get(0));
		}

		// 오늘 날짜, 오늘 신청 건수, 대표곡을 담아 응답을 만들어 돌려주는 코드
		return new TodayResponse(today, todayRequests.size(), first);
	}

	// 이 메서드만 DB 쓰기가 가능한 트랜잭션으로 실행하게 하는 코드
	@Transactional
	// [FR-02, FR-03] 신청을 검사하고 저장하는 메서드를 선언하는 코드
	public SongResponse create(SongCreateRequest req) {
		// 신청 날짜를 검사하고 LocalDate로 바꾸는 코드 (틀리면 400 예외)
		LocalDate date = checkDate(req.date());
		// 곡 제목을 검사하고 앞뒤 공백을 지운 값을 받는 코드 (틀리면 400 예외)
		String songTitle = checkText("songTitle", req.songTitle(), 100, "곡 제목을 입력해 주세요.");
		// 가수를 검사하고 앞뒤 공백을 지운 값을 받는 코드 (틀리면 400 예외)
		String artist = checkText("artist", req.artist(), 100, "가수를 입력해 주세요.");
		// 신청자 이름을 검사하고 앞뒤 공백을 지운 값을 받는 코드 (틀리면 400 예외)
		String applicantName = checkText("applicantName", req.applicantName(), 20, "신청자 이름을 입력해 주세요.");

		// 검사를 통과한 값과 현재 시각(접수 시각)으로 신청 엔티티를 만드는 코드
		SongRequest songRequest = new SongRequest(date, songTitle, artist, applicantName, Instant.now());
		// 신청을 DB에 저장하는 코드 (저장된 객체에는 DB가 매긴 id가 들어 있음)
		SongRequest saved = repository.save(songRequest);

		// 저장된 엔티티를 응답 DTO로 바꿔 돌려주는 코드
		return SongResponse.from(saved);
	}

	// [FR-05] 지정한 날짜의 신청 목록을 조회하는 메서드를 선언하는 코드
	public DateResponse byDate(LocalDate date) {
		// 응답으로 보낼 신청 목록을 담을 빈 리스트를 만드는 코드
		List<SongResponse> requests = new ArrayList<>();
		// 그 날짜의 신청을 접수 순으로 하나씩 꺼내는 코드
		for (SongRequest r : repository.findByRequestDateOrderById(date)) {
			// 꺼낸 신청을 응답 DTO로 바꿔 리스트에 추가하는 코드
			requests.add(SongResponse.from(r));
		}

		// 조회한 날짜와 신청 목록(없으면 빈 배열)을 담아 응답을 만들어 돌려주는 코드
		return new DateResponse(date, requests);
	}

	// [FR-04] 월별 달력 데이터를 조회하는 메서드를 선언하는 코드
	public CalendarResponse calendar(YearMonth month) {
		// 그 달 1일~말일의 신청을 DB에서 한 번에 가져오는 코드
		List<SongRequest> monthRequests = repository.findByRequestDateBetweenOrderById(month.atDay(1), month.atEndOfMonth());

		// 하루 칸들을 담을 빈 리스트를 만드는 코드
		List<CalendarResponse.Day> days = new ArrayList<>();
		// 1일부터 그 달의 말일(28~31일)까지 하루씩 반복하는 코드
		for (int day = 1; day <= month.lengthOfMonth(); day++) {
			// 지금 반복 중인 날짜를 만드는 코드
			LocalDate date = month.atDay(day);

			// 이 날짜의 신청곡을 담을 빈 리스트를 만드는 코드
			List<CalendarResponse.Song> songs = new ArrayList<>();
			// 한 달치 신청을 하나씩 꺼내는 코드
			for (SongRequest r : monthRequests) {
				// 꺼낸 신청의 날짜가 지금 날짜와 같은지 확인하는 코드
				if (r.getRequestDate().equals(date)) {
					// 같으면 달력용 곡 DTO로 바꿔 이 날짜의 리스트에 추가하는 코드
					songs.add(CalendarResponse.Song.from(r));
				}
			}

			// 날짜, 신청 건수, 신청곡 목록으로 하루 칸을 만들어 리스트에 추가하는 코드
			days.add(new CalendarResponse.Day(date, songs.size(), songs));
		}

		// 조회한 달과 모든 날짜 칸을 담아 응답을 만들어 돌려주는 코드
		return new CalendarResponse(month, days);
	}

	// ===================== 입력 검사 (FR-06) =====================
	// 메시지는 FE 신청 폼(js/common.js의 validateField)과 같은 문구다.

	// 신청 날짜를 검사하고 LocalDate로 바꿔주는 메서드를 선언하는 코드
	private LocalDate checkDate(String value) {
		// 날짜가 없거나 공백뿐인지 확인하는 코드
		if (value == null || value.isBlank()) {
			// 비어 있으면 "신청 날짜를 선택해 주세요." 400 오류를 던지는 코드
			throw new BadRequestException("date", "신청 날짜를 선택해 주세요.");
		}

		// 문자열을 날짜로 바꾸는 코드 (형식이 틀리거나 없는 날짜면 null)
		LocalDate date = parseDate(value.strip());
		// 날짜로 바꾸지 못했는지 확인하는 코드
		if (date == null) {
			// 바꾸지 못했으면 "날짜 형식이 올바르지 않아요." 400 오류를 던지는 코드
			throw new BadRequestException("date", "날짜 형식이 올바르지 않아요.");
		}

		// 신청 날짜가 서울 기준 오늘보다 이전(지난 날짜)인지 확인하는 코드
		if (date.isBefore(LocalDate.now(SEOUL))) {
			// 지난 날짜면 "지난 날짜는 신청할 수 없어요." 400 오류를 던지는 코드
			throw new BadRequestException("date", "지난 날짜는 신청할 수 없어요.");
		}

		// 모든 검사를 통과한 날짜를 돌려주는 코드
		return date;
	}

	// 글자 입력칸(곡 제목·가수·신청자)을 검사하고 앞뒤 공백을 지운 값을 돌려주는 메서드를 선언하는 코드
	private String checkText(String field, String value, int max, String emptyMessage) {
		// 값이 없거나 공백뿐인지 확인하는 코드
		if (value == null || value.isBlank()) {
			// 비어 있으면 칸별 안내 문구로 400 오류를 던지는 코드
			throw new BadRequestException(field, emptyMessage);
		}

		// 앞뒤 공백을 지우는 코드
		String text = value.strip();
		// 글자 수가 최대 길이를 넘는지 확인하는 코드
		if (text.length() > max) {
			// 넘으면 "N자 이내로 입력해 주세요." 400 오류를 던지는 코드
			throw new BadRequestException(field, max + "자 이내로 입력해 주세요.");
		}

		// 검사를 통과한(공백 제거된) 값을 돌려주는 코드
		return text;
	}

	// "YYYY-MM-DD" 문자열을 날짜로 바꿔주는 메서드를 선언하는 코드 (실패하면 null)
	private static LocalDate parseDate(String text) {
		// 숫자 4자리-2자리-2자리 모양인지 확인하는 코드 (예: 20261021, 2026-10-1은 탈락)
		if (!text.matches("\\d{4}-\\d{2}-\\d{2}")) {
			// 모양이 틀리면 null을 돌려주는 코드
			return null;
		}
		// 날짜로 바꾸기를 시도하는 코드
		try {
			// 문자열을 LocalDate로 바꿔 돌려주는 코드
			return LocalDate.parse(text);
		}
		// 13월, 2월 30일처럼 없는 날짜라서 바꾸기에 실패한 경우를 잡는 코드
		catch (DateTimeParseException e) {
			// 없는 날짜면 null을 돌려주는 코드
			return null;
		}
	}

}
