# BE

기상송 — 기숙사 아침 기상송 신청 게시판 백엔드.

- API 명세: [`API_SPEC.md`](API_SPEC.md)
- 요구사항: [`BACKEND_SPEC.md`](BACKEND_SPEC.md)

## 기술 스택

- Java 17, Spring Boot 4.1 (Web MVC, Data JPA), Lombok
- DB: MySQL (기본) / H2 (`local` 프로필, 테스트)
- 빌드: Gradle Wrapper, 테스트: JUnit 5 + MockMvc

> 명세서의 추천안은 Spring Boot 3.x였지만, 3.x는 오픈소스 지원이 끝나 Spring Initializr 기본값인 4.1을 사용한다.

## 코드 구조

요청은 항상 **Controller → Service → Repository → DB** 순서로 흐른다.

```text
src/main/java/com/webintro/wakesong/
├── controller/SongController       API 4개. 받아서 Service에 넘기기만 한다
├── service/SongService             업무 규칙 전부 (대표곡, 저장+입력 검사, 날짜별, 달력)
├── repository/SongRequestRepository  DB 조회 2개: findByRequestDateOrderById, findByRequestDateBetweenOrderById
├── domain/SongRequest              song_requests 테이블 한 줄
├── dto/                            요청/응답 JSON 모양 (SongCreateRequest, SongResponse, ...)
├── exception/                      입력 오류(400) → {code, message, field} 응답 변환
└── config/                         CORS 설정
```

| API | Controller | Service |
|---|---|---|
| `GET /api/today` | `today()` | 오늘 신청 중 첫 번째 |
| `POST /api/requests` | `create()` | `checkDate` → `checkText` ×3 → 저장 |
| `GET /api/requests?date=` | `byDate()` | 그 날짜 신청 목록 |
| `GET /api/calendar?month=` | `calendar()` | 1일~말일 돌며 날짜별로 묶기 |

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

입력 오류(400)는 `{code, message, field}` 형식이고, 위에서부터 검사해 첫 번째 오류만 돌려준다. 404·405·500 등은 스프링 기본 오류 응답이다. 자세한 내용은 [`API_SPEC.md`](API_SPEC.md).

### CORS

`CORS_ALLOWED_ORIGINS`(쉼표 구분)에 FE 주소를 넣는다. 기본값은 FE 개발 서버 `http://localhost:8000`, `http://127.0.0.1:8000`.

## 테스트

```bash
./gradlew test
```

인메모리 H2를 쓰므로 로컬/운영 DB에 영향을 주지 않는다.
