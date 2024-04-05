# 공지사항 REST API
공지사항 등록, 수정, 삭제, 조회 API

# 개발 환경
- SpringBoot 2.7.8
- H2 2.1.214
- Gradle
- JPA 2.7.7 / Hibernate 
- QueryDsl 5.0.0

# 실행 방법
1. Download
2. QueryDSL Q객체 생성

- Gradle → Tasks → build → clean

- Gradle → Tasks → other → compileQueryDsl

- build → generated → querydsl 에 Q 객체 생성됐으면 정상   

3. DemoAppliaction > Run

# 설명
1. REST API
   공지사항 목록/단건 조회(GET), 등록(POST), 수정(PATCH), 삭제(DELETE)
   
3. 대용량 트래픽 처리
   Ehcache 3 사용하여 단건 조회 시 600초 캐시 설정.
   
   초당 1000건 이상 트래픽 처리 시간 캐시 설정으로 최초 캐시 설정 조회를 제외한 평균 약 10 ms 내로 처리 가능.
   
   단, 실제 운영 서버에서 테스트 환경을 분리한 것이 아니라 로컬 환경에서 테스트했으므로 차이는 있을 수 있음.
   
5. 단위/통합테스트
   
   Mockito, Junit5 사용하여 테스트.
   
7. 예외 처리
   
   @RestControllerAdvice 전역 예외 처리 핸들러 사용.
   
   

   
