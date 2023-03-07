package com.online.colloboration.services;


import com.online.colloboration.models.User;
import com.online.colloboration.config.JwtService;
import com.online.colloboration.dto.AuthenticationResponse;
import com.online.colloboration.dto.LoginRequest;
import com.online.colloboration.dto.RefreshTokenRequest;
import com.online.colloboration.dto.RegisterRequest;
import com.online.colloboration.exceptions.RhException;
import com.online.colloboration.models.NotificationEmail;
import com.online.colloboration.models.VerificationToken;
import com.online.colloboration.repository.UserRepository;
import com.online.colloboration.repository.VerificationTokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final MailService mailService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public void signup(RegisterRequest registerRequest) {
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setCreated(Instant.now());
        user.setRoles(registerRequest.getRoles());
        user.setEnabled(false);

        userRepository.save(user);

        String token = generateVerificationToken(user);
        mailService.sendMail(new NotificationEmail("Please Activate your Account",
                user.getEmail(), "Thank you for signing up to Gestion RH, " +
                "please click on the below url to activate your account : " +
                "http://localhost:8081/api/auth/accountVerification/" + token+"\n  le groupe SQLI occupe une position centrale dans le marché des NTIC. Cette large base en termes de ressources humaines nécessite une informatisation de l'ensemble des pratiques mises en œuvre pour administrer, gérer et structurer ces ressources impliquées dans l'activité du groupe."));
    }

    @Transactional(readOnly = true)
    public User getCurrentUser() throws UsernameNotFoundException {
        Principal principal = (Principal) SecurityContextHolder.
                getContext().getAuthentication().getPrincipal();
        return userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User name not found - " + principal.getName()));
    }

    private void fetchUserAndEnable(VerificationToken verificationToken) {
        String email = verificationToken.getUser().getEmail();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RhException("User not found with name - " + email));
        user.setEnabled(true);
        userRepository.save(user);
        verificationTokenRepository.delete(verificationToken);
    }

    private String generateVerificationToken(User user) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);

        verificationTokenRepository.save(verificationToken);
        return token;
    }

    public void verifyAccount(String token) {
        Optional<VerificationToken> verificationToken = verificationTokenRepository.findByToken(token);
        fetchUserAndEnable(verificationToken.orElseThrow(() -> new RhException("Invalid Token")));
    }

    public ResponseEntity<?> login(LoginRequest loginRequest) {
        String token=null;
        try {
            Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                    loginRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authenticate);
            if (authenticate.isAuthenticated()) {
                token= jwtService.generateToken(loginRequest.getUsername());
            } else {
                throw new UsernameNotFoundException("invalid user request !");
            }
        } catch (DisabledException e) {
            // Handle disabled user exception
           return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("{\"errorMessage\": \"User account is disabled\"}");
        } catch (AuthenticationException e) {
            // Handle authentication exception
            throw new UsernameNotFoundException("invalid user request !");
        }
        return ResponseEntity.ok(AuthenticationResponse.builder()
                .authenticationToken(token)
                .refreshToken(refreshTokenService.generateRefreshToken().getToken())
                .expiresAt(Instant.now().plusMillis(1000*60*30))
                .username(loginRequest.getUsername())
                .build());
    }


    public AuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        refreshTokenService.validateRefreshToken(refreshTokenRequest.getRefreshToken());
        String token = jwtService.generateToken(refreshTokenRequest.getUsername());
        return AuthenticationResponse.builder()
                .authenticationToken(token)
                .refreshToken(refreshTokenRequest.getRefreshToken())
                .expiresAt(Instant.now().plusMillis(1000*60*30))
                .username(refreshTokenRequest.getUsername())
                .build();
    }

    public boolean isLoggedIn() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return !(authentication instanceof AnonymousAuthenticationToken) && authentication.isAuthenticated();
    }
}