package coderhood.service;

import coderhood.dto.*;
import coderhood.exception.ResourceNotFoundException;
import coderhood.exception.BusinessRuleException;
import coderhood.model.User;
import coderhood.repository.UserRepository;
import coderhood.repository.TalhaoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TalhaoRepository talhaoRepository;

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

    @Transactional(readOnly = true)
    public List<AnalistaEstatisticasDto> getAnalistasEstatisticas() {
        log.info("Obtendo estatísticas dos analistas");
        
        List<User> analistas = userRepository.findByTipoAcesso(User.TipoAcesso.ANALISTA);
        AtomicInteger numeroSequencial = new AtomicInteger(1);
        
        return analistas.stream()
            .map(analista -> {
                Long quantidadeTalhoes = talhaoRepository.countByAnalistaId(analista.getId());
                
                return AnalistaEstatisticasDto.builder()
                    .id(analista.getId())
                    .nome(analista.getNome())
                    .email(analista.getEmail())
                    .quantidadeTalhoes(quantidadeTalhoes)
                    .horasAnalisadas(0L) // Implementar futuramente
                    .numeroNomeAnalyst(numeroSequencial.getAndIncrement())
                    .build();
            })
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserResponseDto> getAnalistas() {
        log.info("Obtendo lista de analistas");
        return userRepository.findByTipoAcesso(User.TipoAcesso.ANALISTA)
            .stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
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
