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
<img width="1154" height="896" alt="CEOS 22nd CGV" src="https://github.com/user-attachments/assets/b5c3e54c-77aa-4692-a84b-84eb5b4d7aff" />

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
