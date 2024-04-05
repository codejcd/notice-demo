# 공지사항 REST API
공지사항 등록, 수정, 삭제, 조회 API
<br>
# 개발 환경
- SpringBoot 2.7.8
- H2 2.1.212
- Gradle
- JPA 2.7.7 / Hibernate 
- QueryDsl 5.0.0

# 실행 방법
### 1. Project Download & Import
- Download & import 진행.
### 2. QueryDSL Q객체 생성
- Gradle IntelliJ
   - Gradle → Tasks → build → clean

   - Gradle → Tasks → other → compileQueryDsl

   - build → generated → querydsl 에 Q 객체 생성됐으면 정상.
  
- Gradle 콘솔
   - ./gradlew clean compileQuerydsl

### 3. h2 설치
- 다운로드 및 설치
     - https://www.h2database.com
     - 스프링부트 2.x 는 1.4.200 버전 다운로드.
     - 스프링부트 3.x 는 2.1.214 버전 이상.
- 권한 부여 : chmod 755 h2.sh
- 데이터베이스 파일 생성 방법
     - jdbc:h2:~/querydsl (최소 한번)
     -  \~/querydsl.mv.db 파일 생성 됐으면
       jdbc:h2:tcp://localhost/~/querydsl 로 접속

- Windows 기준
     - 시작프로그램 or 찾기 > H2 Console 실행.
     - 연결 (연결시험 X)
       ![image](https://github.com/codejcd/notice-demo/assets/14977984/0c4719f9-25ca-41af-a094-f842b9b5faf0)

### 4. DemoAppliaction > Run

# 설명
1. REST API
   공지사항 목록/단건 조회(GET), 등록(POST), 수정(PATCH), 삭제(DELETE)
   
3. 대용량 트래픽 처리
   Ehcache 3 사용하여 단건 조회 시 600초 캐시 설정.
   
   초당 1000건 이상 트래픽 처리 시간 캐시 설정으로 최초 캐시 설정 조회를 제외한 건 당 평균 약 10 ms 내로 처리 가능.
   
   단, 실제 운영 서버에서 테스트 환경을 분리한 것이 아니라 로컬 환경에서 테스트했으므로 차이는 있을 수 있음.
   
5. 단위/통합테스트
   
   Mockito, Junit5 사용하여 테스트.
   
7. 예외 처리
   
   @RestControllerAdvice 전역 예외 처리 핸들러 사용.
   
   

   
