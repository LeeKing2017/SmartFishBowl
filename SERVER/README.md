# Spring Boot server description

스마트 어항의 서버로써 아두이노와 앱 사이에서 미들웨어로 동작합니다.

## Folder Structure

- `fishbowl/src/main/java/com/gachon/fishbowl/config`: 프로젝트 설정
- `fishbowl/src/main/java/com/gachon/fishbowl/controller`: 데이터 송수신 API 구현
- `fishbowl/src/main/java/com/gachon/fishbowl/dto`: API에서 사용되는 DTO 구현
- `fishbowl/src/main/java/com/gachon/fishbowl/entity`: DB에서 사용되는 테이블 엔티티 구현 
- `fishbowl/src/main/java/com/gachon/fishbowl/oauth2`: Open Authorization 2.0을 사용하여 google에서 전달받은 사용자 프로필 핸들링
- `fishbowl/src/main/java/com/gachon/fishbowl/repository`: JPA method를 위한 interface 구현
- `fishbowl/src/main/java/com/gachon/fishbowl/security`: Spring security에서 필요한 코드
- `fishbowl/src/main/java/com/gachon/fishbowl/service`: controller에서 사용될 method 구현


## Dependencies

> Lombok   
> H2 Database   
> OAuth2 Client    
> Spring Data Jpa   
> Spring Security   
> Spring Session   
> Spring Web   
> Validation   

