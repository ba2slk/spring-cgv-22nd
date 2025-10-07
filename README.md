# spring-cgv-22nd
CEOS 22기 백엔드 스터디 - CGV 클론 코딩 프로젝트
---
# CGV 서비스
## 1. 영화
- 영화 정보를 조회할 수 있다.
	- 하나의 영화는 두 개 이상의 장르를 가질 수 있다.
	- 하나의 장르는 두 개 이상의 영화에 해당할 수 있다.
## 2. 상영관
- 극장 정보를 조회할 수 있다.
- 극장별 상영관 정보를 조회할 수 있다.
	- 상영관 종류는 일반관과 특별관으로 구분된다.
	- 특별관: SCREENX, 4DX, IMAX, …
## 3. 예매
- 관람객은 표를 예매할 수 있다.
	- 관람객은 극장, 영화, 상영관, 상영 시간, 좌석을 선택하여 예매를 진행한다.
	- 한 번의 예매에서 두 개 이상의 좌석을 선택할 수 있다.
- 관람객은 예매한 표를 취소할 수 있다.
	- 동일 예매 건에 대해 좌석 부분 취소는 불가능하다.


# 모델링 결과
## ERD
v1.0
<img width="1154" height="896" alt="CEOS 22nd CGV" src="https://github.com/user-attachments/assets/b5c3e54c-77aa-4692-a84b-84eb5b4d7aff" />

v2.0 (Latest)
<img width="1334" height="1198" alt="cgv-final" src="https://github.com/user-attachments/assets/561fcba1-2fa0-4463-8b5f-2fe54454eacf" />

## 도메인별 Entity 소개
### Movie
1. `Movie` : 영화 (제목, 감독, 관람 등급 정보 등)
2. `Genre`: 영화 장르
3. `MovieGenre`: Movie와 Genre 간 N:M 관계를 해소하기 위해 도입
### Theater
1. `Theater` : 극장 (극장 이름, 위치)
2. `Screen` : 상영관 → 각 상영관은 `theaterId`를 FK로 갖는다.
3. `Showtime` : 상영 시간 → 각 상영 시간은 {`screenId`, `movieId`}를 FK로 갖는다.
4. `Seat` : 좌석 → 각 좌석은 상영관의 screenId를 FK로 갖는다.

### User
1. `User` : 이용자
### Reservation
1. `Reservation`: 예매
	- UUID로 공개용 예매 번호 관리
2. `ReservationSeat`: 예매된 좌석
	- 하나의 예매 건에 두 개 이상의 좌석이 포함될 수 있다.
	- 하나의 좌석이 여러 예매 건에 포함될 수 있다.
		- 같은 좌석이라도 상영 시간에 따라 다른 상품으로 보아야 하기 때문이다.
	- 이러한 N:M 관계를 해소하기 위해 도입
	- 구매/취소의 경우 모두 `ReservationSeat` 엔터티를 통해 관리할 수 있다.
### Snack
1. UserOrder
	- User의 주문 정보
3. Inventory
	- Theater와 Item 간의 N:M 관계를 해소하고 극장 별 재고 관리 확장성을 고려해 추가
4. Item
	- 매점 상품 정보
5. OrderItem
	- 구체적인 주문 명세로, Order와 Item 간의 N:M 관계를 해소하기 위해 도입
---
# API Endpoints
<img width="1438" height="1305" alt="image" src="https://github.com/user-attachments/assets/abed7b23-8e26-462a-a5b9-f73d01868259" />


---
# 인증/인가: 4가지 방법  
## 1. 세션 & 쿠키
### 시나리오
1. 사용자 로그인
2. *서버*가 회원 DB로부터 가입된 사용자인지 확인
3. 해당 회원에게 고유 ID를 부여하여 *세션 저장소*에 저장
4. *세션 저장소*가고유 ID와 연결되는 Session ID 발급
5. 서버가 응답 헤더에 Session ID를 함께 보냄
6. 이후 사용자가 데이터를 요청할 때마다 Session ID가 담긴 쿠키를 실어 보냄
7. 서버는 세션 저장소에서 쿠키를 검증하고 세션 정보를 획득함 (인증 완료)
8. 요청한 데이터와 함께 응답

### 장점
- Session ID가 담긴 쿠키가 탈취되어도, 해당 값에는 의미가 없기 떄문에 쿠키에 사용자 정보를 직접 담는 것보다 안전함.
- 각 사용자가 고유의 Session ID를 발급받기 때문에 회원 정보를 하나하나 확인할 필요가 없으므로 서버 자원에 접근하기 용이함.

### 단점
- 세션 하이재킹 공격
	- 쿠키의 내용은 아무런 뜻도 없지만, 그 자체로 세션 저장소를 통과할 수 있는 출입증 역할을 하기 때문에, 해당 쿠키와 함께 서버에 HTTP 요청을 보낼 경우 인증에 성공할 수 있음
	- <해결> 만료 시간 부여
- 서버 측의 세션 저장소 운영 부담

## 2. Access Token을 이용한 인증
### JWT (JSON Web Token)
#### 구성 요소
1. Header
	- alg: 암호화 방식
	- typ: 토큰 유형
2. Payload : 토큰에 담을 정보(claim)
	- sub: 
	- name: 이름
	- admin: 역할
	- iat:
3. Verify Signature: Payload가 위변조되지 않았다는 사실을 증명하는 문자열
	- Base64 기반 인코딩 헤더, Payload, 임의의 Secret Key 조합
	- SECRET_KEY를 알아야 복호화할 수 있음
	- 공격자가 JWT를 훔쳐서 인증에 이용하려고 해도, Verify Signature가 해커의 정보가 아닌 일반 사용자의 정보 + SECRET_KEY에 기반으로 암호화되었기 때문에 유효하지 않음

### 시나리오
1. 사용자 로그인
2. 회원 DB로부터 사용자 확인
3. JWT 발급
4. 응답 → Access Token 발급
5. 매 데이터 요청마다 요청 헤더에 JWT (Authorization: Bearer (JWT)를 실어 보냄
6. Access Token 검증
7. 응답 + 요청 데이터

### 장점
- 간편하다. 발급 → 인증 프로세스만 존재. 별도의 세션 저장소 운영 부담이 없음.
- 뛰어난 확장성

### 단점
- 한 번 발급되면 유효기간이 만료될 때까지 계속 사용이 가능, 중간에 삭제가 불가능함. 따라서 JWT가 탈취될 경우 대처가 불가능
- <해결> Refresh Token을 추가로 발급
- Payload 정보는 암호화되지 않기 때문에 중요한 정보를 저장할 수 없음.
- JWT 길이가 길기 때문에 요청이 많아지면 서버 자원 낭비 발생

## 3. Access Token + Refresh Token
가장 단순한 형태의 JWT 인증 방식은 
1. 만료 시간 전까지 해당 토큰이 유효하다는 점
2. 발급된 토큰을 수정할 수 없다는 점
3. 이로 인해 공격자에 의해 탈취된 토큰에 대한 대처가 어렵다는 점
과 같은 단점이 있다. 이를 극복하기 위해 Refresh Token 사용

너무 자주 로그인 → UX 악영향
유효 기간 늘리기 → 탈취 취약점

=> Refresh Token을 같이 쓰자!

### 시나리오
1. 사용자 로그인
2. 회원 DB 확인 후 Access Token & Refresh Token 발급
3. 데이터 요청 (+ Access Token)
4. Access Token 검증 → 응답
5. 만약 Access Token이 만료된 경우
	1. 사용자가 만료된 Access Token과 함께 데이터 요청
	2. 서버가 사용자에게 Access Token이 만료되었다는 응답
	3. 사용자가 Access Token 재발급 요청 + Refresh Token 같이 보냄
	4. Refresh Token 유효성 확인 → 만료 기한 내인 경우 새로운 Access Token 발급

### 장점
- 기존 취약점 보완. Access Token의 유효기간이 짧음

### 단점
- 복잡한 구현
- Access Token이 만료될 때마다 새로 발급하는 과정에서 서버 자원 낭비 (짧은 주기)

## OAuth 2.0
### OAuth
외부 서비스의 인증 및 권한부여를 관리하는 범용 프로토콜

### OAuth 2.0
- 현재 범용적으로 사용
- 특징
	- 모바일 사용 용이
	- 반드시 HTTPS 사용 → 보안 강화
	- Access Token 만료 기간 도입

### 인증 시나리오
<img width="820" height="478" alt="Pasted image 20250927222709" src="https://github.com/user-attachments/assets/b01008fa-59f5-4dc7-be40-7313ec237a19" />

1. Resource Owner (사용자)의 인증 요청
2. Client (서버)가 인증 페이지 제공
3. 사용자가 인증 진행
4. 인증 완료 신호로 Authorization Grant를 URL에 실어 Client(=서버)에게 보냄
5. 서버는 해당 권한 증서를 Authorization Server에 보냄
6. Authorization Server가 해당 증서 확인. 유저가 맞다면 Client에게 Access Token, Refresh Token,그리고 유저의 정보를 발급해줌.
7. Client는 해당 Access Token을 DB에 저장 또는 Resource Owner에게 넘김
8. Resource Owner가 Resource Server 자원이 필요하면, Client가 Access Token을 담아 Resource Server에 대신 요청
9. Resource Server는 Access Token의 유효성을 확인 → Client에게 자원을 보냄
	- 만약 Access Token 만료 or 위조 시, Client는 Authorization Server에 Refresh Token을 같이 보내서 Access Token 재발급
	- 다시 Resource Server에 자원 요청
	- Refresh Token도 만료된 경우 Resource Owner는 새로운 Authorization Grant를 Client에게 넘겨야 함. → 즉, Client가 다시 인증 페이지를 제공, 사용자 인증 과정 수행

## 4. SNS 로그인
OAuth 2.0이랑 비슷
→ 단, Authorization Server에서 받은 **고유 ID**를 이용해 DB에서 회원 인증

이후 세션/쿠키 or 토큰 기반 인증 진행

---

# 토큰 기반 API
## 영화 예매/취소
적용 전
<img width="1215" height="811" alt="Pasted image 20250927212357" src="https://github.com/user-attachments/assets/7fad0176-2951-4bae-aa4c-d82ee00b1719" />

적용 후
<img width="1239" height="1282" alt="Pasted image 20250927212809" src="https://github.com/user-attachments/assets/01cb7380-1df1-48ab-a876-cdd1e295deaf" />

---
# 트러블 슈팅
1. 문제 상황
: Swagger UI 접속 시 `Failed to load API definition` 에러 발생.
<img width="591" height="288" alt="Pasted image 20250927000546" src="https://github.com/user-attachments/assets/4b70f13a-46f3-4e17-b931-9edd24cba665" />

2. 문제 원인
`/v3/api-docs` 요청에 대해 GlobalExceptionHandler가 모든 예외를 잡아 `ResponseEntity<String>` 등 JSON이 아닌 텍스트 응답을 반환.
    - Swagger는 OpenAPI JSON 구조를 기대함 → 텍스트가 오면 파싱 실패 → 500 에러 발생.

3. 해결
: SpringDoc에서 Controller의 응답을 분석할 때 Generic Response 형식으로 덮어쓸지 여부를 false로 설정\
```yml
springdoc:  
  api-docs:  
    enabled: true  
  swagger-ui:  
    enabled: true  
    path: /swagger-ui.html  
  override-with-generic-response: false
```


