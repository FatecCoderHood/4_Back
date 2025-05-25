package coderhood.controller;

import coderhood.dto.LoginRequestDto;
import coderhood.repository.UserRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.http.HttpStatus;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final SecurityContextRepository securityContextRepository;
    
    public AuthController(AuthenticationManager authenticationManager, 
                         UserRepository userRepository,
                         SecurityContextRepository securityContextRepository) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.securityContextRepository = securityContextRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> authenticateUser(
            @RequestBody LoginRequestDto loginRequest,
            HttpServletRequest request,
            HttpServletResponse response) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.senha())
            );
            
            // Criar o contexto de segurança e definir a autenticação
            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
            securityContext.setAuthentication(authentication);
            SecurityContextHolder.setContext(securityContext);
            
            // Salvar o contexto de segurança na sessão
            securityContextRepository.saveContext(securityContext, request, response);
            
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("ROLE_USER");
            
            coderhood.model.User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userDetails.getUsername()));
            
            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("name", user.getName());
            responseBody.put("role", adjustRoleString(role));
            responseBody.put("message", "Login realizado com sucesso");
            
            return ResponseEntity.ok(responseBody);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid credentials"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(
            HttpServletRequest request, 
            HttpServletResponse response) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        
        return ResponseEntity.ok(Map.of("message", "Logout realizado com sucesso"));
    }

    private String adjustRoleString(String input) {
        // Split the string by underscore
        String[] parts = input.split("_");
        
        // Take the second part (after the underscore) and adjust it
        if (parts.length > 1) {
            String role = parts[1];  // Get the part after the underscore
            // Capitalize the first letter and make the rest lowercase
            return role.substring(0, 1).toUpperCase() + role.substring(1).toLowerCase();
        }
        
        // If the input doesn't contain an underscore, return the input as is
        return input;
    }
}
