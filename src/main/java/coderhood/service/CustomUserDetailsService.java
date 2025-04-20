package coderhood.service;

import coderhood.controller.AuthController;
import coderhood.model.User;
import coderhood.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Fetch the user from the database using email
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        
        logger.info("Creating UserDetails for email: " + user.getEmail());
        logger.info("Creating UserDetails: Email = " + user.getEmail() + ", Role = " + user.getRole());

        // Return an instance of UserDetails (Spring Security's User or a custom implementation)
        return org.springframework.security.core.userdetails.User
            .withUsername(user.getEmail()) // Use email as username
            .password(user.getSenha())
            .authorities("ROLE_" + user.getRole().toUpperCase())         // Map role (you might need a custom role mapping here)
            .build();
    }
}
