package coderhood.service;

import coderhood.dto.*;
import coderhood.exception.ResourceNotFoundException;
import coderhood.exception.BusinessRuleException;
import coderhood.model.User;
import coderhood.model.User.TipoAcesso;
import coderhood.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public UserResponseDto getUserById(Long id) { // Mudou para Long
        return userRepository.findById(id)
            .map(this::convertToDto)
            .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
    }

    @Transactional(readOnly = true)
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }

    @Transactional
    public UserResponseDto createUser(UserCreateDto userCreateDto) {
        if (userRepository.existsByEmail(userCreateDto.getEmail())) {
            throw new BusinessRuleException("E-mail já cadastrado");
        }

        User user = new User();
        user.setNome(userCreateDto.getNome());
        user.setEmail(userCreateDto.getEmail());
        user.setSenha(passwordEncoder.encode(userCreateDto.getSenha()));
        user.setTipoAcesso(userCreateDto.getTipoAcesso());

        return convertToDto(userRepository.save(user));
    }

    @Transactional
    public UserResponseDto updateUser(Long id, UserUpdateDto userUpdateDto) { // Mudou para Long
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + id));

        // Verifica se o novo email já existe em outro usuário
        if (!user.getEmail().equals(userUpdateDto.getEmail()) && 
            userRepository.existsByEmail(userUpdateDto.getEmail())) {
            throw new BusinessRuleException("E-mail já está em uso por outro usuário");
        }

        user.setNome(userUpdateDto.getNome());
        user.setEmail(userUpdateDto.getEmail());
        user.setTipoAcesso(userUpdateDto.getTipoAcesso());

        // Atualiza senha apenas se for fornecida
        if (userUpdateDto.getSenha() != null && !userUpdateDto.getSenha().isBlank()) {
            user.setSenha(passwordEncoder.encode(userUpdateDto.getSenha()));
        }

        return convertToDto(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(Long id) { // Mudou para Long
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuário não encontrado");
        }
        userRepository.deleteById(id);
    }

    private UserResponseDto convertToDto(User user) {
        return new UserResponseDto(
            user.getId(),
            user.getNome(),
            user.getEmail(),
            user.getTipoAcesso().toString()
        );
    }
}
