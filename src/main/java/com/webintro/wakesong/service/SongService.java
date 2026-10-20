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

/** 기상송 업무 규칙이 전부 여기 있다. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SongService {

	private final SongRequestRepository repository;
	private final Clock clock;   // 서울 기준 (ClockConfig)

	/** 오늘 신청 중 가장 먼저 접수된 곡이 대표곡. 없으면 song = null */
	public TodayResponse today() {
		LocalDate today = LocalDate.now(clock);
		List<SongRequest> list = repository.findAllByDate(today);

		TodayResponse.Song first = list.isEmpty() ? null : TodayResponse.Song.from(list.get(0));
		return new TodayResponse(today, list.size(), first);
	}

	/** 입력을 검사하고 저장한다. 같은 날 같은 곡도 따로 저장된다. */
	@Transactional
	public SongResponse create(SongCreateRequest req) {
		LocalDate date = checkDate(req.date());
		String songTitle = checkText("songTitle", req.songTitle(), 100, "곡 제목을 입력해 주세요.");
		String artist = checkText("artist", req.artist(), 100, "가수를 입력해 주세요.");
		String applicantName = checkText("applicantName", req.applicantName(), 20, "신청자 이름을 입력해 주세요.");

		SongRequest saved = repository.save(new SongRequest(date, songTitle, artist, applicantName, Instant.now(clock)));
		return SongResponse.from(saved);
	}

	/** 그 날짜의 신청 전부, 접수 순. 지난 날짜도 조회 가능 */
	public DateResponse byDate(LocalDate date) {
		List<SongResponse> requests = repository.findAllByDate(date).stream().map(SongResponse::from).toList();
		return new DateResponse(date, requests);
	}

	/** 그 달 1일~말일 모두. 신청 없는 날은 0건 */
	public CalendarResponse calendar(YearMonth month) {
		List<SongRequest> monthList = repository.findAllBetween(month.atDay(1), month.atEndOfMonth());

		List<CalendarResponse.Day> days = new ArrayList<>();
		for (int d = 1; d <= month.lengthOfMonth(); d++) {
			LocalDate date = month.atDay(d);
			List<CalendarResponse.Song> songs = monthList.stream()
					.filter(r -> r.getRequestDate().equals(date))
					.map(CalendarResponse.Song::from)
					.toList();
			days.add(new CalendarResponse.Day(date, songs.size(), songs));
		}
		return new CalendarResponse(month, days);
	}

	// ---------- 입력 검사 (FR-06). 메시지는 FE 신청 폼과 같다. 첫 오류에서 멈춘다 ----------

	/** YYYY-MM-DD 형식의 실제 날짜이고, 서울 기준 오늘 이후여야 한다 */
	private LocalDate checkDate(String value) {
		String v = trim(value);
		if (v.isEmpty()) {
			throw new BadRequestException("date", "신청 날짜를 선택해 주세요.");
		}
		LocalDate date = parseDate(v);
		if (date == null) {
			throw new BadRequestException("date", "날짜 형식이 올바르지 않아요.");
		}
		if (date.isBefore(LocalDate.now(clock))) {
			throw new BadRequestException("date", "지난 날짜는 신청할 수 없어요.");
		}
		return date;
	}

	/** 앞뒤 공백을 뺀 뒤 1자 이상, max자 이하 */
	private String checkText(String field, String value, int max, String emptyMessage) {
		String v = trim(value);
		if (v.isEmpty()) {
			throw new BadRequestException(field, emptyMessage);
		}
		if (v.length() > max) {
			throw new BadRequestException(field, max + "자 이내로 입력해 주세요.");
		}
		return v;
	}

	private static String trim(String value) {
		return value == null ? "" : value.strip();
	}

	/** "2026-10-21" 같은 정확한 형식만. 2026-02-30처럼 없는 날짜는 null */
	private static LocalDate parseDate(String v) {
		if (!v.matches("\\d{4}-\\d{2}-\\d{2}")) {
			return null;
		}
		try {
			return LocalDate.parse(v);
		}
		catch (DateTimeParseException e) {
			return null;
		}
	}

}
