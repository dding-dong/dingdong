## 🤔 개요

- 이 프로젝트는 스프링부트와 JPA를 기반으로 **음식 주문 관리 플랫폼**을 구현한 백엔드 애플리케이션입니다.
- 기능: 회원 관리(CUSTOMER, OWNER, MANAGER), 음식점 & 메뉴 관리, 주문 처리, 결제 기록, 댓글/리뷰 관리, AI 기반 메뉴 설명 자동 생성

## 👥 팀원

| **프로젝트 기간** | 2025.09.29(월) ~ 2025.10.16(목) |
|-------------|-------------------------------|
| **포지션**     | 백엔드 개발자 5명                    |

| 이름         | 역할           |
|------------|--------------|
| 팀원 김믿음     | 회원, 인증       |
| 팀장 **윤혜지** | 리뷰, 결제       |
| 팀원 이가현     | 장바구니, 결제     |
| 팀원 이수민     | 매니저, 주문      |
| 팀원 차초희     | 가게, 메뉴, 카테고리 |

## 🌳 개발환경

언어 : ![Static Badge](https://img.shields.io/badge/Java-red?style=flat-square)  
JDK : ![Static Badge](https://img.shields.io/badge/JDK-17-yellow?style=flat-square)  
프레임워크 : ![Static Badge](https://img.shields.io/badge/SpringBoot-%23FFFF00?logo=springboot)  
DB : ![Static Badge](https://img.shields.io/badge/Postgres-%23FFFFFF?style=flat&logo=mysql), ![Static Badge](https://img.shields.io/badge/Redis-%23FFFFFF?style=flat&logo=mysql)    
ORM : ![Static Badge](https://img.shields.io/badge/JPA-FFA500?style=flat)

## 🔠 목차

1. [📄 API 명세서](#-api-명세서)
2. [💿 ERD](#-erd)
3. [❓ DTO 흐름도](#-패키지-설명)
4. [▶️ 실행방법](#️-실행방법)
5. [🛠 기능 요약](#-기능-요약)

# 📄 API 명세서

- https://documenter.getpostman.com/view/49210641/2sB3QMKoX2

# 💿 ERD

- ERD그림이 들어갈 공간입니다.

# ❓ DTO 흐름도

![sequence1.png](sequence1.png)

## Entity

- **BaseEntity**: 생성일, 수정일, 삭제일, Soft Delete 관리
- **User**: 회원정보 및 역할(Customer, Owner, Manager, Master)
- **Cart**: 장바구니
- **Store**: 음식점 정보
- **Menu**: 음식 메뉴
- **Order / OrderItem**: 주문 및 주문 상세
- **Payment**: 결제 처리
- **Address**: 주소 관리(시, 구, 동)
- **Review**: 리뷰, 댓글
- **AIHistory**: AI API 요청 기록

# ▶️ 실행방법

## 1. **레포지토리 클론**

```bash
git clone <레포지토리 URL>
cd <프로젝트 디렉토리>
```

## 2. **Docker 컨테이너 실행**

- docker-compose.yml 파일로 PostgreSQL과 Redis 컨테이너 실행

```bash
docker-compose up -d
```

## 3. **Jasypt 복호화 키 설정**

- 환경 변수로 복호화 키 지정

```bash
export JASYPT_ENCRYPTOR_PASSWORD=<복호화 키>
```

## 4. **Spring Boot 서버 실행**

- IDE에서 FoodOrderPlatformApplication.java 실행
  또는 터미널에서

```bash
./gradlew bootRun   # Gradle
# 또는
mvn spring-boot:run # Maven
```

## 5. **API 테스트**

- 서버 실행 후 Postman 또는 Swagger를 통해 테스트

# 🛠 기능 요약

- **회원 관리:** 회원가입, 역할별 권한
- **음식점/메뉴 관리:** 메뉴 등록, 수정, 삭제, 숨김 처리, AI 기반 메뉴 설명
- **주문 관리:** 주문 생성, 승인/거절, 조리 완료, 배달 완료
- **결제 관리:** 카드 결제 기록 저장
- **리뷰 관리:** 댓글 및 사장님 댓글 CRUD
- **지역 관리:** 시/구/동 구조 기반 음식점 필터링
- **Soft Delete 처리:** 모든 데이터 삭제는 soft delete 처리
