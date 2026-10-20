# 기상송 API 명세서

> 기준 코드: `main` 브랜치 (`SongController`, `SongService`, `GlobalExceptionHandler`)
> 응답 예시는 실제 서버(`local` 프로필)를 실행해 받은 값이다.

## 1. 공통 규칙

| 항목 | 내용 |
|---|---|
| Base URL | 로컬 개발: `http://localhost:8080` |
| 경로 | 모든 API는 `/api`로 시작 |
| 데이터 형식 | 요청·응답 모두 JSON (`Content-Type: application/json`), UTF-8 |
| 인증 | 없음 (로그인 없이 사용) |
| 오늘 기준 | 항상 **Asia/Seoul**. 서버가 어느 시간대에서 돌든 서울 날짜로 판단 |
| 날짜 형식 | `date`: `YYYY-MM-DD` (예: `2026-10-21`), `month`: `YYYY-MM` (예: `2026-10`) |
| 시각 형식 | `createdAt`: ISO 8601 **UTC** (예: `2026-10-20T09:55:46.893188Z`). 화면에는 서울 시각으로 바꿔 표시 |
| 정렬 | 신청 목록은 항상 **접수 순** (`createdAt` 오름차순, 같으면 `id` 오름차순) |
| 빈 결과 | 오류가 아니다. `200` + `null` 또는 빈 배열 `[]` |

## 2. API 목록

| 기능 ID | 메서드 | 경로 | 설명 | 성공 |
|---|---|---|---|---|
| FR-01 | `GET` | `/api/today` | 오늘의 대표 신청곡 | 200 |
| FR-02, 03 | `POST` | `/api/requests` | 기상송 신청 | 201 |
| FR-05 | `GET` | `/api/requests?date=YYYY-MM-DD` | 날짜별 전체 신청 목록 | 200 |
| FR-04 | `GET` | `/api/calendar?month=YYYY-MM` | 월별 신청 현황 (달력) | 200 |

---

## 3. 오늘의 대표 신청곡 조회

```
GET /api/today
```

서울 기준 오늘 날짜의 신청 중 **가장 먼저 접수된 1곡**을 돌려준다.

> 대표 신청곡은 실제로 방송되는 확정곡이 아니다. 화면에 "실제 방송곡과 다를 수 있어요" 같은 안내를 함께 표시한다.

### 요청
파라미터 없음.

### 응답 `200 OK`

| 필드 | 타입 | 설명 |
|---|---|---|
| `date` | string | 오늘 날짜 (서울 기준) |
| `requestCount` | number | 오늘 날짜 신청 건수 |
| `song` | object \| null | 대표 신청곡. 오늘 신청이 없으면 `null` |
| `song.id` | number | 신청 ID |
| `song.title` | string | 곡 제목 ※ 다른 API의 `songTitle`과 키 이름이 다름 |
| `song.artist` | string | 가수 |
| `song.applicantName` | string | 신청자 이름 |

```json
{
  "date": "2026-10-20",
  "requestCount": 2,
  "song": {
    "id": 1,
    "title": "밤양갱",
    "artist": "비비 (BIBI)",
    "applicantName": "김서연"
  }
}
```

오늘 신청이 없을 때:

```json
{ "date": "2026-10-20", "requestCount": 0, "song": null }
```

---

## 4. 기상송 신청

```
POST /api/requests
Content-Type: application/json
```

### 요청 본문

| 필드 | 타입 | 필수 | 규칙 |
|---|---|---|---|
| `date` | string | O | `YYYY-MM-DD`, 실제 있는 날짜, 서울 기준 **오늘 또는 미래** |
| `songTitle` | string | O | 앞뒤 공백 제거 후 1~100자 |
| `artist` | string | O | 앞뒤 공백 제거 후 1~100자 |
| `applicantName` | string | O | 앞뒤 공백 제거 후 1~20자 |

```json
{
  "date": "2026-10-20",
  "songTitle": "밤양갱",
  "artist": "비비 (BIBI)",
  "applicantName": "김서연"
}
```

- 앞뒤 공백은 지운 값으로 저장한다.
- 같은 날짜에 여러 곡을 신청할 수 있다. 곡이나 신청자가 같아도 별도 신청으로 저장한다.
- 중복 제출 방지는 서버에서 하지 않는다. 화면에서 제출 중 버튼을 비활성화한다.

### 응답 `201 Created`

| 필드 | 타입 | 설명 |
|---|---|---|
| `id` | number | 신청 ID |
| `date` | string | 신청 날짜 |
| `songTitle` | string | 곡 제목 (공백 제거된 값) |
| `artist` | string | 가수 |
| `applicantName` | string | 신청자 이름 |
| `createdAt` | string | 접수 시각 (UTC, 서버가 기록) |

```json
{
  "id": 1,
  "date": "2026-10-20",
  "songTitle": "밤양갱",
  "artist": "비비 (BIBI)",
  "applicantName": "김서연",
  "createdAt": "2026-10-20T09:55:46.893188Z"
}
```

### 입력 오류 `400 Bad Request`

`date` → `songTitle` → `artist` → `applicantName` 순서로 검사하고 **첫 번째 오류 하나만** 돌려준다. `field`는 틀린 입력칸 이름이다.

| 상황 | `field` | `message` |
|---|---|---|
| 날짜 없음 / 빈 값 | `date` | 신청 날짜를 선택해 주세요. |
| 날짜 형식 틀림, 없는 날짜 (`2026-02-30`) | `date` | 날짜 형식이 올바르지 않아요. |
| 지난 날짜 (서울 기준 어제 이전) | `date` | 지난 날짜는 신청할 수 없어요. |
| 곡 제목 없음 / 공백만 | `songTitle` | 곡 제목을 입력해 주세요. |
| 가수 없음 / 공백만 | `artist` | 가수를 입력해 주세요. |
| 신청자 없음 / 공백만 | `applicantName` | 신청자 이름을 입력해 주세요. |
| 곡 제목·가수 101자 이상 | `songTitle` / `artist` | 100자 이내로 입력해 주세요. |
| 신청자 21자 이상 | `applicantName` | 20자 이내로 입력해 주세요. |
| JSON이 깨졌거나 본문 없음 | (없음) | 요청 형식이 올바르지 않아요. |

```json
{ "code": "VALIDATION_ERROR", "message": "지난 날짜는 신청할 수 없어요.", "field": "date" }
```

`Content-Type`이 JSON이 아니면 `415`:

```json
{ "code": "UNSUPPORTED_MEDIA_TYPE", "message": "JSON(application/json)으로 보내 주세요." }
```

---

## 5. 날짜별 전체 신청 조회

```
GET /api/requests?date=YYYY-MM-DD
```

지정한 날짜의 신청을 **접수 순**으로 모두 돌려준다. 지난 날짜도 조회할 수 있다.

### 쿼리 파라미터

| 이름 | 필수 | 형식 | 예 |
|---|---|---|---|
| `date` | O | `YYYY-MM-DD` | `2026-10-20` |

### 응답 `200 OK`

| 필드 | 타입 | 설명 |
|---|---|---|
| `date` | string | 조회한 날짜 |
| `requests` | array | 신청 목록 (접수 순). 없으면 `[]` |
| `requests[]` | object | 신청 1건 — 필드는 [4. 신청 응답](#응답-201-created)과 같음 |

```json
{
  "date": "2026-10-20",
  "requests": [
    {
      "id": 1,
      "date": "2026-10-20",
      "songTitle": "밤양갱",
      "artist": "비비 (BIBI)",
      "applicantName": "김서연",
      "createdAt": "2026-10-20T09:55:46.893188Z"
    },
    {
      "id": 2,
      "date": "2026-10-20",
      "songTitle": "APT.",
      "artist": "로제",
      "applicantName": "이도윤",
      "createdAt": "2026-10-20T09:55:46.919471Z"
    }
  ]
}
```

신청이 없을 때:

```json
{ "date": "2026-10-25", "requests": [] }
```

### 오류 `400 Bad Request`
`date`가 없거나 형식이 틀리면 (`2026-13-01`, `20261020` 등):

```json
{ "code": "VALIDATION_ERROR", "message": "날짜 형식이 올바르지 않아요. (YYYY-MM-DD)", "field": "date" }
```

---

## 6. 월별 신청 현황 조회 (달력)

```
GET /api/calendar?month=YYYY-MM
```

그 달의 **1일부터 말일까지 모든 날짜**에 대해 신청 건수와 곡 목록을 돌려준다. 신청이 없는 날도 `requestCount: 0`으로 포함되므로 `days` 배열을 그대로 달력 칸으로 그리면 된다.

### 쿼리 파라미터

| 이름 | 필수 | 형식 | 예 |
|---|---|---|---|
| `month` | O | `YYYY-MM` | `2026-10` |

### 응답 `200 OK`

| 필드 | 타입 | 설명 |
|---|---|---|
| `month` | string | 조회한 월 |
| `days` | array | 그 달의 날짜 수만큼 (28~31개), 날짜 순 |
| `days[].date` | string | 날짜 |
| `days[].requestCount` | number | 그날 신청 건수 |
| `days[].songs` | array | 그날 신청곡 전체 (접수 순). 미리보기는 `songs[0]` 사용 |
| `days[].songs[].id` | number | 신청 ID |
| `days[].songs[].title` | string | 곡 제목 |
| `days[].songs[].artist` | string | 가수 |

예시 (31일 중 20~22일만 표시):

```json
{
  "month": "2026-10",
  "days": [
    {
      "date": "2026-10-20",
      "requestCount": 2,
      "songs": [
        { "id": 1, "title": "밤양갱", "artist": "비비 (BIBI)" },
        { "id": 2, "title": "APT.", "artist": "로제" }
      ]
    },
    {
      "date": "2026-10-21",
      "requestCount": 1,
      "songs": [
        { "id": 3, "title": "Ditto", "artist": "NewJeans" }
      ]
    },
    { "date": "2026-10-22", "requestCount": 0, "songs": [] }
  ]
}
```

- `requestCount`는 같은 날짜의 `GET /api/requests?date=` 결과 개수와 항상 같다.
- 다른 달의 신청은 포함되지 않는다.

### 오류 `400 Bad Request`
`month`가 없거나 형식이 틀리면 (`2026-13`, `2026-1`, `2026-10-01` 등):

```json
{ "code": "VALIDATION_ERROR", "message": "월 형식이 올바르지 않아요. (YYYY-MM)", "field": "month" }
```

---

## 7. 오류 응답

모든 오류는 같은 모양이다.

| 필드 | 타입 | 설명 |
|---|---|---|
| `code` | string | 오류 종류 (아래 표) |
| `message` | string | 화면에 그대로 보여줄 수 있는 한국어 문구 |
| `field` | string | 입력 오류일 때만. 틀린 입력칸 / 파라미터 이름 |

| HTTP | `code` | 언제 |
|---|---|---|
| 400 | `VALIDATION_ERROR` | 입력값·쿼리 파라미터 오류, 깨진 JSON |
| 404 | `NOT_FOUND` | 없는 주소 (예: `GET /api/nope`). **빈 목록은 404가 아니라 200** |
| 405 | `METHOD_NOT_ALLOWED` | 지원하지 않는 메서드 (예: `DELETE /api/requests`) |
| 415 | `UNSUPPORTED_MEDIA_TYPE` | POST 본문이 JSON이 아님 |
| 500 | `INTERNAL_SERVER_ERROR` | DB 장애 등 서버 오류. 내부 정보(SQL, 스택트레이스)는 응답에 포함되지 않음 |

```json
{ "code": "INTERNAL_SERVER_ERROR", "message": "일시적인 오류로 요청을 처리하지 못했어요. 잠시 후 다시 시도해 주세요." }
```

## 8. CORS

- `/api/**`에 `GET`, `POST`만 허용한다.
- 허용 Origin은 환경변수 `CORS_ALLOWED_ORIGINS`(쉼표 구분)로 정한다. 기본값은 FE 개발 서버 `http://localhost:8000`, `http://127.0.0.1:8000`.
- 허용되지 않은 Origin에서 요청하면 `403 Invalid CORS request` (JSON 아님).

## 9. 원본 명세(`BACKEND_SPEC.md`)와 다른 점

| 항목 | 원본 명세 | 구현 | 이유 |
|---|---|---|---|
| `GET /api/today` | `{date, song}` | `requestCount` 추가 | FE 메인 화면이 "오늘 신청 N건" 표시 |
| 달력 `days` | 신청 있는 날만 (제안) | 그 달 모든 날짜 | FE `calendar.html`이 `days`를 그대로 달력 칸으로 그림 |
| 오류 응답 | `{code, message, fieldErrors}` (제안) | `{code, message, field}` | FE는 첫 오류의 `field`만 사용 |
| 415 | 없음 | `UNSUPPORTED_MEDIA_TYPE` | JSON이 아닌 요청 구분 |
| `requestNumber` | 미확정 | 없음 | 완료 화면은 날짜별 목록에서 순번 계산 |
