# BE

기상송 — 기숙사 아침 기상송 신청 게시판 백엔드. 요구사항과 API 계약은 [`BACKEND_SPEC.md`](BACKEND_SPEC.md) 참고.

## 기술 스택

- Java 17, Spring Boot 4.1 (Web MVC, Validation, Data JPA)
- DB: MySQL (기본) / H2 (`local` 프로필, 테스트)
- 빌드: Gradle Wrapper, 테스트: JUnit 5 + MockMvc

> 명세서의 추천안은 Spring Boot 3.x였지만, 3.x는 오픈소스 지원이 끝나 Spring Initializr 기본값인 4.1을 사용한다.

## 실행

JDK 17 이상이 필요하다.

### MySQL 없이 바로 실행 (`local` 프로필)

```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

`./data` 폴더의 H2 파일에 저장되므로 서버를 다시 켜도 데이터가 유지된다.

### MySQL로 실행

```sql
CREATE DATABASE wakesong CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'wakesong'@'localhost' IDENTIFIED BY 'change-me';
GRANT ALL PRIVILEGES ON wakesong.* TO 'wakesong'@'localhost';
```

```bash
export DB_URL='jdbc:mysql://localhost:3306/wakesong?characterEncoding=UTF-8'
export DB_USERNAME=wakesong
export DB_PASSWORD=change-me
./gradlew bootRun
```

환경변수 목록은 `.env.example` 참고. 테이블(`song_requests`)은 시작 시 자동 생성된다 (`DDL_AUTO`, 기본 `update`).

## API

모든 날짜 판단은 Asia/Seoul 기준이다.

| 메서드 | 경로 | 설명 |
|---|---|---|
| GET | `/api/today` | 오늘의 대표 신청곡(오늘 가장 먼저 접수된 신청)과 오늘 신청 건수. 없으면 `song: null` |
| POST | `/api/requests` | 신청 저장 → 201. 본문 `{date, songTitle, artist, applicantName}` |
| GET | `/api/requests?date=YYYY-MM-DD` | 날짜별 신청 목록 (접수 순) |
| GET | `/api/calendar?month=YYYY-MM` | 그 달 모든 날짜의 신청 건수와 곡 목록 |

오류 응답은 `{code, message, field?, fieldErrors?}` 형식이다 (`VALIDATION_ERROR` 400, `NOT_FOUND` 404, `METHOD_NOT_ALLOWED` 405, `INTERNAL_SERVER_ERROR` 500).

### CORS

`CORS_ALLOWED_ORIGINS`(쉼표 구분)에 FE 주소를 넣는다. 기본값은 FE 개발 서버 `http://localhost:8000`, `http://127.0.0.1:8000`.

## 테스트

```bash
./gradlew test
```

인메모리 H2를 쓰므로 로컬/운영 DB에 영향을 주지 않는다.
