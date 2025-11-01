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
---
# 동시성 해결 방법
## Database Level
### 1. Lock
여러 커넥션에서 동시에 동일한 자원을 요청할 경우, 순서대로 하나의 커넥션만 변경할 수 있게 해주는 기능
#### 1-1. Shared Lock
: 읽기 작업을 위한 Lock

##### 특징
- 읽기 작업은 데이터의 정합성에 영향을 주지 않으므로, 다른 세션의 Shared Lock을 막을 이유가 없다.
- 한 세션이 Read 할 때 다른 세션이 Write 한다면 데이터의 정합성이 깨지므로 다른 세션의 Exclusive Lock 획득은 막는다.
- 다른 세션에서 공유 Lock을 걸고 접근할 수 있다.
- **다른 세션에서 배타 Lock을 걸고 접근할 수 는 없다.**

##### 결론
Shared Lock을 건다 = "내가 이 데이터 읽을거니까, Exclusinve Lock 걸고 쓸 생각 하지마라. 근데 읽기만 할 애들은 각자 Shared Lock 걸어도 돼~"

#### 1-2. Exclusive Lock
: 쓰기 작업을 위한 Lock

##### 특징
- 쓰기 작업은 데이터의 정합성에 영향을 주므로, 다른 세션의 Shared Lock 획득을 막는다.
- 마찬가지로, 다른 세션의 Exclusive Lock 획득도 막는다.
- 다른 세션에서 공유 Lock을 걸고 접근할 수 없다.
- 다른 세션에서 배타 Lock을 걸고 접근할 수 없다.

##### 결론
Exclusive Lock을 건다 = "내가 이 데이터 변경할거니까, 다른 애들은 읽거나 쓸수 없어~"

### 2. Blocking
Lock은 하나의 트랜잭션 안에서 걸리고 해제되는데, 이때 L**ock 간의 경합이 발생해서 특정 트랜잭션이 작업을 진행하지 못하고 대기하는 상태**를 의미
- 데이터에 공유 Lock이 걸린 상태에서 배타 Lock을 걸려고 할 때
- 데이터에 배타 Lock에 걸린 상태에서 공유 Lock을 걸려고 할 때
- 데이터에 배타 Lock에 걸린 상태에서 배타 Lock을 걸려고 할 때

#### 2-1. Blocking 상태 해결하기
Blocking은 문제 상황이 아니라, 데이터의 정합성을 보장하는 과정에서 발생하는 필연적이고 자연스러운 현상임. 하지만 Blocking 상태가 지나치게 길어지면 해당 데이터를 사용하는 작업이 모두 지연될 것이므로 **Lock을 설정할 때 블로킹 상태를 고려하여 설정**해야 함.

##### 2-1-1. 트랜잭션 작업 단위를 최대한 적게 구성하기
- JPA 사용 시 생명주기가 다른 객체 간의 직접 참조을 간접 참조로 끊음

##### 2-1-2. 동일한 데이터를 동시에 변경하지 않기
- 비관적 락, 낙관적 락

##### 2-1-3. 트랜잭션이 활발한 주간에는 대용량 데이터 작업 수행 지양하기


### 3. Dead Lock
두 트랜잭션 모두 블로킹 상태에 진입하여 서로의 블로킹을 해결할 수 없는 상태
<img width="1195" height="629" alt="image" src="https://github.com/user-attachments/assets/c5c8a518-ec8b-4ed0-ab5c-5e65cb4b9fc3" />
출처: https://ksh-coding.tistory.com/121

## Application Level
### 1. synchronized 키워드
#### 코드 예시
```java
@Transactional
public synchronized void decrease(final Long id, final Long quantity) {
	final Stock stock = stockRepository.findById(id)
			.orElseThrow();
	stock.decrease(quantity);

	stockRepository.saveAndFlush(stock);
}
```
critical section(위에서는 재고 감소 로직)에 해당 키워드를 붙여주면 됨.

#### 문제점
단순히 synchronized 키워드를 붙이는 것만으로 동시성 문제를 해결할 수 없음.
##### 1. @Transactional
- @Transactional이 붙은 메소드는 **Proxy 객체를 생성**해서 트랜잭션을 처리함.
- 이때, **재고 감소가 DB에 반영되는 시점은 트랜잭션 커닛 이후 종료 시점**임.
- synchronized 키워드는 메소드 선언부에 사용되어, 해당 메소드가 종료되면 다른 스레드에서 해당 메소드를 실행할 수 있게 됨.
- 따라서 재고 감소 로직이 실행되고 트랜잭션이 종료되기 전까지의 시점에서 다른 스래드가 재고 감소 로직을 실행할 수 있음.
- 이때 다른 스레드에서 재고를 조회할 경우 감소되지 않은 재고 수량을 Read 하게 되어 재고 감소가 누락됨.

##### 2. 확장성
Synchronized는 '하나의 프로세스'에 대해서만 특정 데이터에 동시에 하나의 스레드만 접근하는 것을 보장함. → **여러 대의 서버로 확장할 경우 동시성이 보장되지 않음.**

따라서 실무에서  synchronized는 거의 사용하지 않음.

### 2. DB(MySQL) 레벨의 Lock 사용하기
#### 1. 비관적 락
실제 DB 단에 Exclusive Lock을 설정해서 동시성을 제어하는 방법
```java
public interface StockRepository extends JpaRepository<Stock, Long> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select s from Stock s where s.id = :id")
	Stock findByIdWithPessimisticLock(Long id);
}
```
- @LockModeType.PESSIMISTIC_WRITE: 배타 락 쿼리 수행
	- SELECT FOR WRITE
	- 읽는 순간부터 이 데이터는 내가 수정 전용으로 홀딩함. 읽기도, 수정도 다른 트랜잭션은 못 함
- @LockModeType.PESSIMISTIC_READ: 공유 락 쿼리 수행
	- SELECT FOR SHARE
	- 읽기 전용으로 락 걸었음. 다른 사람도 읽기는 가능하지만 수정은 못 해

#### 2. 낙관적 락
DB 단에 실제 Lock을 설정하지 않고, Version을 관리하는 컬럼을 테이블에 추가해서 데이터 수정 시마다 올바른 버전의 데이터를 수정하는지 판단하는 방식
<img width="1184" height="618" alt="image" src="https://github.com/user-attachments/assets/69f3d8c0-6561-43ee-930f-8c050b0d1148" />
```java
public interface StockRepository extends JpaRepository<Stock, Long> {

	@Lock(LockModeType.OPTIMISTIC)
	@Query("select s from Stock s where s.id = :id")
	Stock findByIdWithOptimisticLock(Long id);
}
```

이후 Entity에 수동으로 version 컬럼을 추가해주어야 함.
```java
@Entity
public class Stock {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long productId;

	private Long quantity;

	@Version
	private Long version;

		...

}
```

버전이 일치하지 않을 경우 기본적으로 예외가 발생하게 됨. 
따라서 while & try-catch 구문을 활용해서 **재고 감소 로직이 성공할 때까지 수행**하도록 다음과 같이 구현함.
```java
public void decrease(Long id, Long quantity) throws InterruptedException {
	while (true) {
		try {
			optimisticLockStockService.decrease(id, quantity);
			break;
		} catch (Exception e) {
			Thread.sleep(50);
		}
	}
}
```

#### 3. 네임드 락
임의로 락의 이름을 설정하고, 해당 락을 사용하여 동시성 문제를 해결하는 방법
주로 **분산 락**을 사용하려고 할 때 많이 사용하는 방식

##### vs 비관적 락
- 트랜잭션이 종료되더라도 Lock이 자동으로 해제되지 않음.
	- 별도의 Lock 해제 로직 구현
	- Lock Timeout 이후 해제
- Lock 설정 대상
	- 비관적 락
		- 해당 테이블의 인덱스 레코드에 Lock 설정
	- 네임드 락
		- 해당 테이블이 **아닌** 별도의 MySQL 공간에 지정한 이름의 Lock 설정

##### 분산 락 구현
- https://techblog.woowahan.com/17416/
- https://techblog.woowahan.com/2631/

```java
public interface LockRepository extends JpaRepository<Stock, Long> {

	@Query(value = "select get_lock(:key, 3000)", nativeQuery = true)
	void getLock(String key);

	@Query(value = "select release_lock(:key)", nativeQuery = true)
	void releaseLock(String key);
}
```

```java
@Transactional
public void decrease(final Long id, final Long quantity) {
	try {
		lockRepository.getLock(id.toString());
		stockService.decrease(id, quantity);
	} finally {
		lockRepository.releaseLock(id.toString());
	}
}
```
releasLock 메소드로 네임드 락을 수동으로 해제해 주어야 함.

##### 네임드 락 주의할 점
네임드 락 설정 부분과 비즈니스 로직의 트랜잭션을 분리해야 함.

- 비즈니스 로직
```java
@Service
public class StockService {
		
		...

		// 비즈니스 로직
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void decrease(final Long id, final Long quantity) throws SQLException {
		final Stock stock = stockRepository.findById(id)
				.orElseThrow();
		stock.decrease(quantity);

		stockRepository.saveAndFlush(stock);
	}
}
```

- 네임드 락 설정 부분
```java
@Component
public class NamedLockStockFacade {

	...

		// Lock 설정, 해제 로직
	@Transactional
	public void decrease(final Long id, final Long quantity) throws SQLException {
		try {
			lockRepository.getLock(id.toString());
			stockService.decrease(id, quantity);
		} finally {
			lockRepository.releaseLock(id.toString());
		}
	}
}
```

호출 흐름
```java
[외부 호출자]
        │
        ▼
NamedLockStockFacade.decrease()
        │
        ├─① lockRepository.getLock()   ← Named Lock 획득
        │
        ├─② stockService.decrease()    ← 실제 재고 감소
        │       ├─ stockRepository.findById()
        │       ├─ stock.decrease(quantity)
        │       └─ stockRepository.saveAndFlush()
        │
        └─③ lockRepository.releaseLock() ← Named Lock 해제
```

### 3. 장단점
#### 1. 비관적 락
- 장점
	- Race Condition이 잦은 경우 낙관적 락보다 성능이 좋음.
	- DB 단의 Lock이므로 데이터 정합성 보장
- 단점
	- DB 단의 Lock을 설정하는 이유로, 한 트랜잭션 작업이 정상적으로 끝나지 않으면 다른 트랜잭션 작업들이 대기해야 하므로 성능이 감소할 우려가 있음. (Blocking)

#### 2. 낙관적 락
- 장점
	- DB 단에서 별도의 Lock을 설정하지 않기 때문에 하나의 트랜잭션 작업이 길어질 때 다른 작업이 영향받지 않아서 성능이 좋을 수 있다.
- 단점
	- 버전이 맞지 않아서 예외가 발생하는 경우 별도의 재시도 로직을 구현해야 하는 부담이 있음.
	- 버전이 맞지 않는 상황이 잦은 경우, 즉 경합이 잦은 경우 재시도를 계속 할 것이므로 성능이 좋지 않을 수 있음.

#### 4. 네임드 락
- 장점
	- Lock을 위한 별도의 공간에 Lock을 설정하기 때문에 같은 Named Lock을 사용하는 작업 이외의 작업은 영향 받지 않음.
	- INSERT 작업의 경우에는 기준을 잡을 레코드가 존재하지 않아 비관적 락을 사용할 수 없는데, 이때 Named Lock을 사용할 수 있음.
	- 분산 락 구현 가능.
- 단점
	- 트랜잭션 종료 시에 Lock 해제, 세션 관리 등을 수동으로 처리해야 하는 이유로 구현 복잡도가 높음.

---
# Spring에서 외부 API 호출하기
- HttpURLConnection/URL Connection
- RestTemplate
- HttpClient
- WebClient
- OpenFeign

## HttpURLConnection / URLConnection
### 특징
- 순수 Java로 HTTP 통신 가능
- URL을 이용하여 외부 API에 연결하고 데이터를 전송할 수 있음.

### 단점
- **동기적인 통신**
- URLConnection은 저수준 API로서, 기본적인 요청/응답 기능이 제공되지만 추가적인 기능은 직접 구현해야 함.

## HttpClient
### 특징
- Apache HTTP 컴포넌트
- 객체 생성이 쉽다.
```java
HttpClient client = HttpClientBuilder.create().build(); // HttpClient 생성
...
HttpResponse response = client.execute(getRequest); // request 수행
```
- 비동기 방식도 지원함.

### 단점
- 반복적이고 긴 코드
- 응답 컨테츠 타입에 따라 별도의 로직이 필요함.

## Spring RestTemplate
### 특징
- Spring 3에서 추가
- HttpClient를 추상화해서 제공
- 사용하기 편하고 직관적임

### 단점
- **동기적인 통신**
- 커넥션 풀을 사용하지 않아서 연결할 때마다 로컬 포트를 열고, tcp 연결을 맺는다 → 이를 해결하기 위해 커넥션 풀을 별도로 사용해야 한다.

## WebClient
### 특징
- Spring 5부터 도입
- 비동기/논블로킹 방식으로 외부 API 호출 가능 → 높은 처리량과 확장성 지원
- 리액티브 프로그래밍 가능
	- 리액티브 프로그래밍 = 데이터 스트림 + 비동기 이벤트 흐름
	- 데이터 스트림을 효과적으로 처리할 수 있음.
- 가독성이 좋다.

### 단점
- WebFlux 학습곡선 존재
- WebFlux 모듈 전체를 의존성에 포함해야 하므로, 작은 규모의 프로젝트에서 해당 모듈을 도입하는 것에 대해 고민해볼 여지가 있음.

## OpenFeign
### 특징
- 선언적(어노테이션 사용) 클라이언트
- Spring Data JPA와 유사하게 **인터페이스에 어노테이션을 붙여서** 구현
```java
@FeignClient(name = "user-service", url = "https://api.ceos22.com/users")
public interface UserClient {

    @GetMapping("/{id}")
    User getUserById(@PathVariable("id") Long id);

    @PostMapping
    User createUser(@RequestBody User user);
}
```
- 코드 작성이 쉬움.
- 다른 Spring Cloud 기술과의 통합이 쉬움.

### 단점
- spring cloud 의존성이 발생함.
- 기본 설정에서는 HTTP/2를 지원하지 않지만, OkHttpClient를 설정하면 HTTP/2 통신이 가능함.

---
# 외부 결제 시스템 연동
## API
- [x] 결제
POST payments/{paymentId}/instant
<img width="1200" height="964" alt="image" src="https://github.com/user-attachments/assets/9e9552cf-0329-41fd-9a68-36da64a75aae" />
PENDING → RESERVED 상태 변경 → 예매 성공

- [x] 결제 취소
POST payments/{paymentId}/cancel
<img width="1169" height="1315" alt="image" src="https://github.com/user-attachments/assets/df2a579b-28f0-4e15-ba94-93334f926fb0" />


- [x] 단건 결제 조회
GET payments/{paymentId}
<img width="1169" height="1151" alt="image" src="https://github.com/user-attachments/assets/d2d81174-94d0-4c6c-820e-66cf21820f72" />


# 로깅 전략
- 핵심 전략: 스프링 프로필(local vs prod) 기반 환경 분리
- 개발/기본 환경 (local, dev, default):
    - 레벨: DEBUG (애플리케이션, SQL, Web 상세 로깅)
    - 출력: 콘솔 + 파일 (동기)
- 운영 환경 (prod):
    - 레벨: INFO (주요 정보만)
    - 출력: 파일 전용 (비동기)
		- AsyncAppender를 통한 비동기 로깅으로 I/O 병목 방지
- 파일 관리: 일자별 로그 분리 (RollingFileAppender), 7일간 보관

---

# (Week 5) Github Action + Docker 기반 CI/CD 파이프라인
# 수동 배포
## 디버깅
### Docker 환경 변수 설정
- application.yml에 `${DB_URL}`과 같이 민감 정보 마스킹 (spring boot에서 .env는 표준이 아닌듯..?)
- 로컬 환경
	- IntelliJ → 상단 CgvcloneApplication → Edit Configuration → Environment Variable 설정
- 배포 환경
	- `docker container run -e DB_URL="jdbc:RDS endpoint" -e DB_URSERNAME="admin" -e DB_PASSWORD="password" (...)`
	- Github에서 통합 관리하는 게 편함.

### RDS → DB 이름 '-' 미설정 관련
1. 로컬 mysql 클라이언트로 접속
	- 인바운드 소스를 내 IP로 설정
```cmd
ubuntu$> -h {RDS endpoint} -P 3306 -u admin -p
```
2. 스키마 생성
```mysql
CREATE DATABASE cgv_clone CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
SHOW DATABASES;
USE cgv_clone;
```

### RDS <~> EC2 연결 문제
RDS 보안그룹의 인바운드 규칙에 EC2의 보안그룹 ID를 추가하여 해결

### 브라우저 → 서버 접속 불가
```
sudo ufw allow 80/tcp
```
방화벽 rule 추가

## 배포 결과
<img width="1139" height="210" alt="image" src="https://github.com/user-attachments/assets/36de283c-9945-4838-ace8-fd4558613c71" />

---
# CI/CD
## 디버깅
### Github Action - Error: Unable to access jarfile /app/gradle/wrapper/gradle-wrapper.jar
```
#15 [builder 6/8] RUN ./gradlew bootJar

[](https://github.com/ba2slk/spring-cgv-22nd/actions/runs/18964949556/job/54159635638#step:4:149)#15 0.127 Error: Unable to access jarfile /app/gradle/wrapper/gradle-wrapper.jar

[](https://github.com/ba2slk/spring-cgv-22nd/actions/runs/18964949556/job/54159635638#step:4:150)#15 ERROR: process "/bin/sh -c ./gradlew bootJar" did not complete successfully: exit code: 1

...

[](https://github.com/ba2slk/spring-cgv-22nd/actions/runs/18964949556/job/54159635638#step:4:152)> [builder 6/8] RUN ./gradlew bootJar:

[](https://github.com/ba2slk/spring-cgv-22nd/actions/runs/18964949556/job/54159635638#step:4:153)0.127 Error: Unable to access jarfile /app/gradle/wrapper/gradle-wrapper.jar

...

[](https://github.com/ba2slk/spring-cgv-22nd/actions/runs/18964949556/job/54159635638#step:4:163)ERROR: failed to build: failed to solve: process "/bin/sh -c ./gradlew bootJar" did not complete successfully: exit code: 1
```
#### 원인
: 내 레포지토리의 prod 브랜치로 체크아웃해서 빌드를 수행하는데, .gitignore에서 빌드 과정에 필요한 graddle-wrapper.jar가 함께 무시되어 있어서 Dockerfile에서 COPY 한 결과에 포함되지 않았음

#### 해결
: .gitignore 파일에 `!gradle/wrapper/gradle-wrapper.jar` 추가

### workflow .yml 
```
- name: [1] Build something
```
- yml에서 대괄호는 리스트 표기용 예약 구문이기 때문에 name이라도 `[1] ~~` 처럼 사용하면 indentation 오류 발생

### 배포 환경에서 컨테이너가 환경변수를 못 읽음
#### 상황
`docker container logs <container name>`으로 컨테이너 내부 로그에서 환경 변수를 읽지 못하는 것과 관련한 에러 발생

#### 해결
- 결국 다시 .gitignore 문제
- application.yml 민감 정보를 전부 환경변수로 치환해서 푸쉬 → runner 에서 빌드 
- 이전에는 application.yml이 빌드 과정에 존재하지 않았기 때문에 DB 관련 환경변수를 읽어들일 수 없었던 것

## 배포 결과
<img width="1197" height="115" alt="image" src="https://github.com/user-attachments/assets/223b37b9-253f-4b5d-99f5-374fef9a565d" />

<img width="1148" height="289" alt="image" src="https://github.com/user-attachments/assets/cefffaa3-8257-4e50-ac15-3d9ab2416be4" />


# 배포 과정 복기
## AWS
- 같은 gmail 계정으로 프리티어 개설하기
	- 기존 gmail 주소에 '+' 트릭: '0730bss+25.3@gmail.com'
- '기존 고객'이 어디까지 해당하는지
	- 25.7 이전에 가입한 유저는 legacy free tier 혜택을 받음
	- 이후에 가입한 유저는 $200 크레딧 + 6개월 + 비용 안 나감
- swap 메모리 설정
### EC2
- pem 보관 잘 하기
- 사용하지 않을 때는 인스턴스 꼭 꺼두기
	- 영구 삭제할 경우 Elastic IP도 함께 삭제 확인하기
- 유동 IP
	- [ ] Github Actions `AWS_EC2_HOST` 변경
	- [ ] MobaXterm(putty 같은 클라이언트) Session 재설정

### RDS
- MySQL
- 스냅샷, 복구 → 사전에 비활성화 해서 과금 방지하기
- 인바운드 규칙 설정
	- 소스
		- 로컬: 내 IP
		- EC2: EC2 보안 그룹 ID
	- DB는 모든 소스(0.0.0.0/0) 허용하면 절대 안 됨.

## Docker
### Dockerfile
- 컨테이너 이미지 생성
- 프로젝트 루트에 Dockerfile 생성
```Dockerfile
# Dockerfile

# Stage 1: jar 빌드하기  
FROM gradle:8.3-jdk17 AS builder  
  
WORKDIR /app  
  
COPY . .  
  
RUN ls -al gradle/wrapper  
  
RUN chmod +x gradlew  
RUN ./gradlew bootJar  
RUN echo "Checking whether *.jar created..."  
RUN ls -l build/libs  
  
# Stage 2: app 실행  
FROM openjdk:17-jdk-slim  
WORKDIR /app  
  
EXPOSE 8080  
  
COPY --from=builder app/build/libs/*.jar app.jar  
  
ENTRYPOINT ["java","-jar","app.jar"]
```
- 멀티스테이지 이미지 빌드 가능

### docker-compose.yml
- 컨테이너 다중 제어
- 작성 관련
	- version 명시하지 않는 것이 권장
	- 환경변수
		- env_file: .env
	- 포트
		- ports: "호스트:컨테이너"
```yml
# docker-compose.yml
services:
  server:
    image: "${DOCKER_REGISTRY_URL}/${DOCKER_IMAGE_NAME}:${IMAGE_TAG}"
    ports:
      - "80:8080"
    env_file:
      - .env
    restart: always

```

## Github Actions
- 프로젝트 루트에 .github/workflows/NAME.yml 생성
```yml
# docker-deploy-prod.yml

name: CI/CD Pipeline with Docker - Push based  
on:  
  push:  
    branches:  
      - prod  
  
jobs:  
  build:  
    name: Build and Push Docker Image  
    runs-on: ubuntu-latest  
    outputs:  
      image_tag: ${{ github.sha }}  
  
    steps:  
      - name: Checkout Source Code  
        # uses: 기존 marketplace 상에 등록된 action 사용하기  
        uses: actions/checkout@v4  
  
      - name: Authenticate with Docker Registry  
        # run: 직접 shell 명령어 실행하기  
        run: echo "${{ secrets.DOCKER_REGISTRY_PASSWORD }}" | docker login ${{ secrets.DOCKER_REGISTRY_URL }} --username ${{ secrets.DOCKER_REGISTRY_USER_NAME }} --password-stdin  
      - name: Build Docker Image  
        run: docker build -t ${{ secrets.DOCKER_REGISTRY_URL }}/${{ secrets.DOCKER_IMAGE_NAME }}:${{ github.sha }} .  
  
      - name: Push Docker Image  
        run: docker push ${{ secrets.DOCKER_REGISTRY_URL }}/${{ secrets.DOCKER_IMAGE_NAME}}:${{ github.sha }}  
  
  deploy:  
    name: Deploy to EC2 Server  
    runs-on: ubuntu-latest  
    needs: build  
  
    steps:  
      - name: Deploy to EC2  
        uses: appleboy/ssh-action@v1.2.0  
        with:  
          host: ${{ secrets.AWS_EC2_HOST }}  
          username: ubuntu  
          key: ${{ secrets.AWS_EC2_SSH_PRIVATE_KEY }}  
          port: ${{ secrets.AWS_EC2_SSH_PORT }}  
          script: |  
            cd ~  
              
            echo "Pulling new image..."  
            docker pull ${{ secrets.DOCKER_REGISTRY_URL }}/${{ secrets.DOCKER_IMAGE_NAME }}:${{ needs.build.outputs.image_tag }}  
              
            echo "Exporting new image tag..."  
            export IMAGE_TAG=${{ needs.build.outputs.image_tag }}  
              
            # 환경변수 주입  
            export JWT_SECRET=${{ secrets.JWT_SECRET }}  
            export JWT_ACCESS_EXPIRATION=${{ secrets.JWT_ACCESS_EXPIRATION }}  
            export JWT_REFRESH_EXPIRATION=${{ secrets.JWT_REFRESH_EXPIRATION }}  
            export PORTONE_TOKEN=${{ secrets.PORTONE_TOKEN }}  
            export PORTONE_STORE_ID=${{ secrets.PORTONE_STORE_ID }}  
            export PORTONE_HOST=${{ secrets.PORTONE_HOST }}  
            export HIBERNATE_DDL_AUTO=${{ secrets.HIBERNATE_DDL_AUTO }}  
              
            echo "Running containers..."  
            docker compose up -d  
              
            echo "Pruning old images..."  
            docker image prune -f  
              
            echo "Done. Check container status below."  
            docker ps
```
- secret → Settings > secrets and variables → actions
	- workflow상에서 사용할 환경변수와 일반변수를 구분해서 github에서 통합 관리 가능
- on
	- 이벤트 트리거 (push, pr, …)
- jobs
	- output = 특정 job의 결과물을 다음 job이 사용할 수 있도록 해줌
	- needs = job 의존성 명시
	- runs-on = ubuntu-latest 또는 self-hosted (실행 환경)
	- steps
		- 각각의 job은 여러 step으로 구분
			- - name: 무슨 단계인지
				- uses: marketplace에 있는 action 사용 (repo/action이름@버전)
					- actions/checkout@v4 : Github 레포지토리 체크아웃
					- appleboy/ssh-action@v1.2.0: SSH 연결 (위 스크립트에서는 docker-compose에 사용)
				- run: shell 커맨드 입력
- Githuc Action 구성 Workflow 도식화
<img width="1191" height="682" alt="image" src="https://github.com/user-attachments/assets/96488f83-7e71-4466-b9a8-5c589a1298d2" />


## 배포 흐름 도식화
<img width="1668" height="444" alt="image" src="https://github.com/user-attachments/assets/78d5cf6c-b2a9-4ecb-bd38-9db47edbe296" />

