# 16-Hot6-be

카테부 2기 16팀 BE 레포지토리입니다.



```bash
📦 src
├── 📁 presentation             # 외부 요청과 응답 처리 (Controller 계층)
│   ├── 📁 controller           # API 진입점
│   └── 📁 dto
│       ├── 📁 request          # 요청 DTO
│       └── 📁 response         # 응답 DTO
│
├── 📁 application              # 유스케이스 조합 및 흐름 조정
│   ├── 📁 service              # 서비스 인터페이스
│   └── 📁 serviceImpl          # 서비스 구현체
│
├── 📁 domain                   # 핵심 도메인 로직
│   ├── 📁 model                # 도메인 모델 (VO, Entity 등)
│   ├── 📁 repository           # 도메인 레벨 repository 인터페이스
│   └── 📁 service              # 도메인 서비스 (도메인 규칙 조정자)
│
├── 📁 infrastructure           # 외부 자원 접근 (DB, API 등)
│   ├── 📁 repository           # JPA 기반의 Repository 구현체
│   └── 📁 entity               # 영속화 대상 Entity 클래스
```

## 🔍 계층 책임 요약

| 계층 | 역할 |
|------|------|
| **presentation** | 클라이언트 요청을 받고 응답을 반환 (REST API Controller, DTO) |
| **application** | 유스케이스 조합, 서비스 흐름 제어 (`Service`, `ServiceImpl`) |
| **domain** | 비즈니스 규칙, 도메인 모델, 도메인 서비스 |
| **infrastructure** | 외부 시스템과의 통신 구현 (DB, 외부 API, 파일 등) |

---

> ✅ 참고: `application.service`는 인터페이스, `application.serviceImpl`은 해당 인터페이스 구현체입니다. 도메인 단의 로직이 필요한 경우 `domain.service`에 도메인 서비스로 정의합니다.
