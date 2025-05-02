# 📘 Java로 만들는 BI Tool 기초

## 📌 목표

> Spring Boot와 PostgreSQL을 기반으로 하는 데이터 시각화용 백어드 API 서버 만들기
> (그래프 데이터를 프러티어에 제공하는 BI 도구 백어드)

---

## 📺 1장. 프로젝트 시작하기

### ✅ 사용 기술 스탭

| 구성 요소   | 선택                           |
| ------- | ---------------------------- |
| 언어      | Java 24                      |
| 빌드 도구   | Maven                        |
| 프레임워크   | Spring Boot 3.4.4            |
| DB      | PostgreSQL                   |
| IDE     | IntelliJ IDEA                |
| API 문서화 | (uc120택) Swagger 또는 RestDocs |

---

### 파일 구조 초기화

* IntelliJ → New Project
* Spring Boot 선택
* Maven 설정
* Java 24
* Dependencies:

    * Spring Web
    * Spring Data JPA
    * PostgreSQL Driver
    * Lombok
    * Spring Boot DevTools

---

## 🛠️ 2장. 기본 환경 설정

### 🔧 application.properties

```properties
spring.application.name=bitool
spring.datasource.url=jdbc:postgresql://localhost:5432/bitool
spring.datasource.username=postgres
spring.datasource.password=yourpassword

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

---

## 🧬 3장. 도메인 구성

### 📄 User Entity

```java
@Entity
@Getter @Setter @NoArgsConstructor
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String email;
}
```

---

## 📦 4장. DTO 설계

```java
// UserRequestDTO.java
public record UserRequestDTO(String username, String email) { }

// UserResponseDTO.java
public record UserResponseDTO(Long id, String username, String email) { }
```

---

## 📂 5장. Repository, Service, Controller

### 📄 UserRepository.java

```java
public interface UserRepository extends JpaRepository<User, Long> { }
```

---

### 📄 UserService.java

```java
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserResponseDTO saveUser(UserRequestDTO dto) {
        User user = new User();
        user.setUsername(dto.username());
        user.setEmail(dto.email());
        return toResponse(userRepository.save(user));
    }

    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream().map(this::toResponse).toList();
    }

    public Optional<UserResponseDTO> getUserById(Long id) {
        return userRepository.findById(id).map(this::toResponse);
    }

    public UserResponseDTO updateUser(Long id, UserRequestDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        user.setUsername(dto.username());
        user.setEmail(dto.email());
        return toResponse(userRepository.save(user));
    }

    private UserResponseDTO toResponse(User user) {
        return new UserResponseDTO(user.getId(), user.getUsername(), user.getEmail());
    }
}
```

---

### 🎮 UserController.java

```java
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponseDTO>> createUser(@Valid @RequestBody UserRequestDTO dto) {
        return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "사용자 등록 성공", userService.saveUser(dto)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponseDTO>>> getAllUsers() {
        return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "전체 사용자 조회 성공", userService.getAllUsers()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "조회 성공",
                userService.getUserById(id).orElseThrow(() -> new UserNotFoundException(id))));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> updateUser(@PathVariable Long id, @Valid @RequestBody UserRequestDTO dto) {
        return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "수정 성공", userService.updateUser(id, dto)));
    }
}
```

---

## 🚨 6장. 예외 처리

### 📄 UserNotFoundException.java

```java
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long id) {
        super("사용자 ID " + id + "을(를) 찾을 수 없습니다.");
    }
}
```

---

### 📄 GlobalExceptionHandler.java

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNotFound(UserNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>("ERROR", e.getMessage(), null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));
        return ResponseEntity.badRequest().body(new ApiResponse<>("ERROR", "검증 실패", errors));
    }
}
```

---

## 📆 7장. 공통 응답 형식

### 📄 ApiResponse.java

```java
@Getter
@AllArgsConstructor
public class ApiResponse<T> {
    private String status;
    private String message;
    private T data;
}
```

---

## 🤪 8장. 테스트 예시 (cURL)

```bash
# POST 생성
curl -X POST http://localhost:8080/api/users -H "Content-Type: application/json" -d '{"username": "jinushin", "email": "jinushin@email.com"}'

# GET 전체
curl http://localhost:8080/api/users

# GET 단개
curl http://localhost:8080/api/users/1

# PUT 수정
curl -X PUT http://localhost:8080/api/users/1 -H "Content-Type: application/json" -d '{"username": "new", "email": "new@email.com"}'
```

---

## 📊 9장. 앞으로 가야 할 길

* ❌ 없는 사용자 삭제 DELETE API
* 🚀 Swagger 또는 RestDocs 도입
* 📈 BI 구조에 적합한 구조의 JSON 제공 API
* ⌛ 실시간 검색을 위한 Scheduling + Caching 구성
