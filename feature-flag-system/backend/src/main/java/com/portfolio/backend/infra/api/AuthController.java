package com.portfolio.backend.infra.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.portfolio.backend.application.AuthenticateuserUseCase;
import com.portfolio.backend.application.RegisterUserUseCase;
import com.portfolio.backend.domain.PlatformUser;
import com.portfolio.backend.infra.api.response.ApiResponse;
import com.portfolio.backend.infra.api.response.LoginResponse;
import com.portfolio.backend.infra.api.response.RegisterReponse;
import com.portfolio.backend.infra.dto.LoginUserDto;
import com.portfolio.backend.infra.dto.RegisterUserDto;
import com.portfolio.backend.infra.security.JwtService;



@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final JwtService jwtService;

    private final RegisterUserUseCase register;
    
    private final AuthenticateuserUseCase authenticate;

    public AuthController(JwtService jwtService, RegisterUserUseCase register, AuthenticateuserUseCase authenticate) {
        this.jwtService = jwtService;
        this.register = register;
        this.authenticate = authenticate;
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<RegisterReponse>> register(@RequestBody RegisterUserDto user) {
        this.register.run(user);

        return ResponseEntity.ok(new ApiResponse<>(
           new RegisterReponse()
        ));
    }
    

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginUserDto login) {
        PlatformUser user = authenticate.run(login);
        
        String jwtToken = jwtService.generateToken(user.getUsername());

        return ResponseEntity.ok(
            new ApiResponse<>(
                new LoginResponse(jwtToken)
            )
        );
    }    
}
