package com.school.grades.controller;

import com.school.grades.dto.AuthRequest;
import com.school.grades.dto.AuthResponse;
import com.school.grades.dto.RegisterRequest;
import com.school.grades.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Авторизация", description = "Вход в систему и регистрация пользователей")
public class AuthController {

    private final AuthService authService;

    @Operation(
        summary = "Войти в систему",
        description = "Принимает логин и пароль, возвращает JWT-токен. " +
                      "Скопируйте значение поля `token` и вставьте его в кнопку **Authorize** (вверху справа)."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Успешный вход, токен в ответе"),
        @ApiResponse(responseCode = "401", description = "Неверный логин или пароль")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @Operation(
        summary = "Зарегистрировать нового пользователя (только ADMIN)",
        description = "Создаёт учётную запись с ролью ROLE_ADMIN или ROLE_TEACHER. " +
                      "Доступно только администратору (требует JWT-токен с ролью ROLE_ADMIN)."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Пользователь создан, возвращает его токен"),
        @ApiResponse(responseCode = "400", description = "Логин уже занят или данные невалидны"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещён — нужна роль ROLE_ADMIN")
    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }
}
