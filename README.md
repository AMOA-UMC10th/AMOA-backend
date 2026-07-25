![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/SpringBoot-3.5-6DB33F)
![MySQL](https://img.shields.io/badge/MySQL-8-4479A1)
![Redis](https://img.shields.io/badge/Redis-DC382D)
![AWS](https://img.shields.io/badge/AWS-EC2-FF9900)

# 💅 AMOA Backend

> **네일 디자인 탐색부터 예약까지 한 번에**
>
> AMOA는 다양한 네일 디자인을 탐색하고,
> 원하는 디자인을 비교한 뒤 예약까지 연결하는 네일 큐레이션 플랫폼입니다.

---

# ✨ About

기존에는 인스타그램, 블로그 등 여러 플랫폼에 흩어져 있는 네일 디자인을
사용자가 직접 찾아야 하는 불편함이 있었습니다.

AMOA는 이러한 문제를 해결하기 위해

- 다양한 네일 디자인 탐색
- 가격 및 소요시간 비교
- 지역별 샵 검색
- 원하는 샵 예약

까지 하나의 서비스에서 제공하는 것을 목표로 합니다.

---

# 🚀 Features

## 👤 User

- 카카오 로그인
- JWT 인증
- 온보딩
- 마이페이지

## 💅 Nail

- 디자인 탐색
- 디자인 태그 검색
- 샵 상세 조회
- 관심 샵

## 📅 Reservation

- 예약 가능 시간 조회
- 예약 생성
- 예약 조회
- 예약 취소

## 🔔 Others

- 공지사항
- 이용 약관

---

# 🛠 Tech Stack

## Backend

- Java 21
- Spring Boot 3.5
- Spring Security
- Spring Data JPA
- QueryDSL
- JWT
- Validation

## Database

- MySQL
- Redis

## Infrastructure

- Docker
- GitHub Actions
- AWS EC2

## API Documentation

- Swagger (OpenAPI)

---

# 📂 Project Structure

```text
src
└── main
    └── java
        └── com
            └── amoa
                └── server
                    ├── domain
                    │   ├── auth
                    │   ├── user
                    │   ├── onboarding
                    │   ├── shop
                    │   ├── card
                    │   ├── reservation
                    │   └── ...
                    │
                    ├── global
                    │   ├── apiPayload
                    │   ├── config
                    │   ├── exception
                    │   ├── security
                    │   └── util
                    │
                    └── infra
```

---

# 📖 API Documentation

Swagger

```text
/swagger-ui/index.html
```

---

# 🌳 Git Branch Strategy

| Branch | Description |
|---------|-------------|
| main | Production |
| develop | Integration |
| feat/#issue | Feature |
| fix/#issue | Bug Fix |
| refactor/#issue | Refactoring |
| docs/#issue | Documentation |
| hotfix/#issue | Emergency Fix |

---

# 💬 Commit Convention

```text
feat: 기능 추가
fix: 버그 수정
refactor: 리팩토링
docs: 문서 수정
test: 테스트
chore: 기타 작업
```

---

# 🔀 PR Convention

- develop 브랜치로 PR
- Squash Merge 사용
- PR 작성 후 코드 리뷰 2인 이상

---

# 👥 Backend Team

| Name | Role |
|------|------|
| 케이디 / 김동희 | Backend Lead |
| 단 / 김다은 | Backend |
| 허브 / 조성하 | Backend |
| 루프 / 전경민 | Backend |

---

# 📄 License

This project is for educational purposes.
