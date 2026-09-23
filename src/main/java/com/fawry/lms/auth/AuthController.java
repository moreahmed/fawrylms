package com.fawry.lms.auth;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping ("/register")
    public String register(@RequestBody RegisterRequest request) {
        return authService.register(request, Role.STUDENT);
    }

    @PreAuthorize ("hasRole('ADMIN')")
    @PostMapping("/admin/register")
    public String registerByAdmin(@RequestBody RegisterByAdminRequest request) {
        return authService.register(new RegisterRequest(request.username(), request.password()), request.role());
    }
    

    @PostMapping ("/login")
    public TokenResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
