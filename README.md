# outfit-of-the-weather
[#OOTW] 그날의 날씨를 보고 오늘의 코디를 공유하는 커뮤니티 By 빽엔두리

## 프로젝트 주제

### 클론 타겟

- [꾸온꾸](https://korean.visitkorea.or.kr/detail/rem_detail.do?cotid=14a13909-134a-4a0e-a581-fc1ace95d925)
- 현재 날씨를 확인하고 날씨에 맞춰 아바타를 코디한 후 전국의 여행지를 배경으로 사진을 찍어 업로드하는 게시판
- 레퍼런스 이미지

![image](https://github.com/backendoori/ootw-backend/assets/85275893/011843d2-5827-4886-8c91-df8fc9143b78)

### 개발 목표

- 현재의 날씨를 보고 그에 맞는 아바타 코디를 선택해 이미지를 저장할 수 있고 저장한 이미지를 공유할 수 있는 게시판을 만드는 것을 1차 프로젝트 mvp로 삼고 진행
- 이후 프로젝트 고도화에 따라 여러 기능을 추가적으로 구현 예정
- 최종적으로 해당 콘텐츠를 메인으로 아바타 아이템 상거래가 가능한 커머스 시스템 도입을 목표로 진행 예정

<details>
<summary>moscow</summary>

- Must Have: 날씨 API 기반 게시글, 사용자 인증, 이미지 서비스
- Should Have: 댓글, 무한 스크롤, 사용자 인증/인가, 소셜 로그인, 좋아요, 해시태그, 히스토리
- Could Have: 검색, 서비스 구독, 사용자 권한, 사용자 인증/인가
- Won't Have: 게시글 북마크, 알림, 이메일, 아바타 아이템 커머스, 통합회원, 휴대폰 인증

</details>

### 기술 스택

- java 17
- springboot 3.2.0
- spring security
- jsonwebtoken jjwt 0.12.3
- openfeign 4.1.0
- spring data jpa
- spring data redis
- spring mail
- green mail
- minio 8.5.7

### 팀 역할(SM, PO, Dev)

<table>
<tr align="center">
<td><B>SM</B></td>
<td><B>PO</B></td>
<td><B>Dev</B></td>
</tr>
<tr align="center">
<td><a href="https://github.com/Sehee-Lee-01">이세희</a></td>
<td><a href="https://github.com/shoeone96">이중원</a></td>
<td><a href="https://github.com/ASak1104">김현우</a></td>
</tr>
<tr align="center">
<td>
<img src="https://github.com/Sehee-Lee-01.png?size=100">
</td>
<td>
<img src="https://github.com/shoeone96.png?size=100">
</td>
<td>
<img src="https://github.com/ASak1104.png?size=100">
</td>
</tr>
</table>

1. 그라운드 룰
    - **트러블 슈팅** 경험은 **2주 내**에 기록하고 공유
        - [Git Wiki](https://github.com/spring-comes-to-us/outfit-of-the-weather/wiki)에 작성
    - **가독성**을 중요하게 생각하여 코드 작성.
    - 서로의 지식을 **공유**하고 코드와 의견을 **존중**하자
2. 스크럼 규칙
    
    
    | 진행 시간 | 내용 |
    | --- | --- |
    | 매일 오전 9시, 15~30분  <br>(QR 찍고 진행) | - 어제 한 일(전날 커밋 기록) 공유  <br>- 오늘 할 일(체크 리스트) 공유 |
    | 금요일 오전 9시  <br>(QR 찍자마자 진행) | - 스프린트(1주) 회고, 피드백 |
3. 트러블 슈팅
4. 프로젝트 관리

## 주요 기능

### 유저 기능

- 회원가입
    - Email 검증
        - [RFC 5322](https://www.rfc-editor.org/rfc/rfc5322#section-3.4.1) 기준의 정규 표현식 검증
        - 최대 길이 255
    - Password 검증
        - 최소 한 개 이상의 영문자, 숫자, 특수문자가 각각 필요
        - 최소 길이 8, 최대 길이 30
        - DTO에서 `@Password` 커스텀 어노테이션 기반으로 진행
        - Entity 생성자를 통한 2차 검증
- 인증 코드 발송
    - 회원가입 시 회원가입한 Email로 인증 코드 날리는 시스템 도입 (재전송 가능)
    - Spring `MailSender`로 회원 가입자 이메일로 회원 가입 인증 코드 전송
    - mail을 보내는 메서드는 `@Async`를 이용하여 비동기 처리
    - 회원 정보를 저장하되 회원 인증 여부(`certified`)를 `false`로 저장하고 `true`가 될 때까지 로그인 서비스를 이용할 수 없게 설정
    - `GreenMail` 라이브러리를 통한 인증 코드 전송 테스트 진행
- 회원가입 코드 인증
    - 가입한 이메일과 코드를 body에 담아 회원 가입 인증 진행
    - 회원 테이블의 `certified` column을 `true`로 변경하고 로그인 하여 사용할 수 있게 만듬
    - 인증 코드를 `Redis`에서 관리
    - Redis TTL 설정으로 10분이 지난 인증 코드는 만료 처리
- 로그인
    - jwtToken 기반의 인증 시스템 도입
    - 로그인 정보를 받은 후 유저 검증 후 `TokenProvider`를 통해 토큰 발급 및 검증
    - 토큰 프로세스는 `jsonwebtoken jjwt` 라이브러리를 활용
- Token 인증 필터
    - SecurityFilter 내부에 `JwtAuthenticationFilter`를 추가
    - `JwtAuthenticationFilter`에서 로그인 시 사용한 토큰을 파싱 후 `userId`를 기반으로 `UsernameAuthenticationToken`을 생성 후 `SecurityContext`에 저장
    - 로그인이 필요한 API에서 `Authentication` 객체를 받거나 직접 `principal`을 호출하는 메서드로 user 정보 호출

### 현재 날씨 조회 기능

- 기상청 OpenAPI 호출
    - `OpenFeign`을 이용하여 기상청 API를 호출
    - 기상청 API 응답 중 현재 날씨 정보(현재 기온, 강수 형태, 하늘 상태) 정보 조회
    - 복잡한 OpenAPI 응답을 처리하기 위해 `@JsonDeserialize` 를 이용하여 API 응답 비직렬화 구현
    - 요청이 제한된 기상청 API를 반복적으로 호출하는 문제 상황
        - 추후 날씨 요청 위치를 Enum(시.도.동 등)으로 관리하고 Redis 기능을 이용하여 캐싱 기능 추가하여 외부 API 중복 호출을 줄일 예정
- `Coordinate` 클래스 유효성 검사
    - `@Grid` 커스텀 어노테이션 기반으로 Controller에서 유효성 검사 진행

### 게시판 기능

- 게시판 CRUD 기능 작성
    - 로그인 여부에 따라 보여주는 정보 분기문 작성
    - jmeter 부하 테스트 중 게시글 목록 조회 성능이 가장 안 좋은 것을 확인
        - 추후 조회 성능을 더 분석해보고 개선 예정
        - 목록 조회는 필터 조회 기능을 추가할 예정
    - 게시글 생성 시 `OpenFeign`을 이용하여 기상청 OpenAPI 호출
        - 게시글 작성 시 일교차(최저/최고 기온) 정보 반영하여 저장
    - 레이어별 요청 값 유효성 검사
        - 요청 dto에서 annotation으로 1차 유효성 검사
        - Entity 생성시 2차 유효성 검사
- 게시판 좋아요 기능
    - 좋아요 여부(`isLike`) 바꿔주는 Patch API로 좋아요 기능 처리
    - 게시글에 좋아요가 눌러진 숫자를 게시판 테이블에 추가
    - 좋아요 동시성 문제 해결
        - 한 사람이 한 게시물에 대해 여러 번 좋아요를 눌러서 생기는 문제
        - 여러 사람이 한 게시물에 대하여 동시에 좋아요를 누르는 문제
        - 비관적 락(`Pessimistic Lock`)을 사용하여 동시성 문제 해결
            - jmeter 부하 테스트 중 두 번째로 성능이 안 좋은 것을 확인
            (평균 시간이 전체 조회를 제외하고 가장 오래 걸림)
            - 추후 성능 테스트 결과 확인 후 낙관적 락(`Optimistic Lock`) or `Redis`를 이용한 성능 개선 예정

### 아바타 이미지 기능

- 아바타 이미지 등록 및 조회 기능
- Enum Validation annotation 생성 및 적용
    - 모든 Enum 타입을 String으로 받고 그 Enum에 해당하는 값이 존재하지 않을 시 예외를 반환하는 annotation 생성
- AvatarItem 전용 validation 클래스로 Entity validation 진행
- 파일 저장 오픈 소스 `MinIO` 도입
    - 추후 배포 시 S3와의 호환성을 위해 로컬 작업에서 MinIO를 도입
        
        → S3 설정과 MinIO 설정 및 메서드가 동일하여 interface로 작성, 배포 버전에서는 S3 도입 예정
        
    - Docker를 이용해 컨테이너로 이미지 저장소 생성
    - `MinIO` 설정 후 이미지 저장 및 이미지 URL 반환 서비스 생성
    - 게시판, 아바타 이미지에 로직 진행 중 이미지 저장 후 실패하면 이미지 삭제하는 로직 추가
    - 대용량 이미지, 이미지 이외의 타입을 필터링 해주는 annotation 기반 validation 적용
    - Post, AvatarItem 등 이미지를 사용하는 테이블이 많아 이미지를 관리할 수 있는 별도의 테이블 생성
    - 추후 아바타 이미지/게시글의 수정 삭제 로직 후 발생 가능한 이미지 정합성 문제 확인 및 scheduling을 도입, 일정 시간 마다 `@Async` 를 이용하여 비동기적으로 처리 작업중
    - filename의 unique 조건을 위해 UUID를 이용한 filename 생성 도입

### CI 도입(Github Action)

- pull request 시 테스트 코드를 모두 돌려보는 CI 환경 생성
    - 로컬환경과 동일하게 Docker-compose 기반의 컨테이너 구동 후 test, build를 진행하는 방식 적용
- github secret 기반으로 환경 변수 지정
- Jacoco 테스트 리포트 자동화

### Jacoco 도입

- test coverage를 설정하여 기능 개발 시 test code를 작성하지 못한 케이스 확인
- 메서드 개행 제한 및 분기문에 따른 test 기준 추가
    - Class Line Coverage → 70% 이상
    - Branch Coverage → 70% 이상
    - Method Line Count → 20줄
- 전체 프로젝트 테스트 커버리지 **✨98.48%✨** (2024.01.18 15:00 기준)

### Check style 도입

- google checkstyle 파일을 기반으로 팀 프로젝트에 적합한 코드 스타일 컨벤션 정의
    - import 문, 여백 등 코드 스타일 컨벤션을 build 시 확인할 수 있도록 gradle 설정
- 정의한 checkstyle 파일을 통해 Intelij 코드 스타일 자동 정렬 설정

### JMeter 부하 테스트 실행

- 수치로 나타난 객관적인 성능을 파악하기 위해 일부 API 부하 테스트 진행
- 동시 접속이 많아지면 일부 요청이 예외를 반환하는 것을 확인
    - 최대 DB Connection Pool을 조절함으로써 해결
