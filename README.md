# 16-Hot6-be
카테부 2기 16팀 be 레포지토리입니다.


📦 구조
├── 📁 presentation             // 외부 요청과 응답 처리
│   └── controller
│   └── dto
│       └── response
│       └── request
├── 📁 application              // 유스케이스 조합
│   └── service                 // 인터페이스
│   └── serviceImpl             // 구현체
├── 📁 domain                   // 핵심 비즈니스 로직
│   └── model
│   └── repository              // 인터페이스
│   └── service                 // 도메인 로직 조정자
├── 📁 infrastructure           // 실제 저장/통신 등 기술 구현
│   └── repository              // Spring Data JPA, 도메인 Repository 구현체
│   └── entity                  // entity 클래스