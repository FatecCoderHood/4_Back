package coderhood.service;

import coderhood.dto.UserRequestDto;
import coderhood.dto.UserResponseDto;
import coderhood.exception.MessageException;
import coderhood.model.TipoAcesso;
import coderhood.model.User;
import coderhood.repository.UserRepository;
import coderhood.validator.EmailValidator;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
public class UserService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public UserResponseDto userCreate(UserRequestDto userRequestDto) {
        if (!EmailValidator.isValidEmail(userRequestDto.getEmail())) {
            throw new MessageException("E-mail inválido");
        }

        Optional<User> existingUser = userRepository.findByEmail(userRequestDto.getEmail());
        if (existingUser.isPresent()) {
            throw new MessageException("E-mail já cadastrado.");
        }

        User user = new User();
        user.setNome(userRequestDto.getNome());
        user.setEmail(userRequestDto.getEmail());
        user.setSenha(passwordEncoder.encode(userRequestDto.getSenha()));
        user.setTipoAcesso(TipoAcesso.valueOf(userRequestDto.getTipoAcesso()));

        user = userRepository.save(user);

        return new UserResponseDto(user.getId(), user.getNome(), user.getEmail(), user.getTipoAcesso().name());
    }

    @Transactional
    public String userUpdate(UUID id, UserRequestDto userRequestDto) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new MessageException("Usuário não encontrado."));

        // Atualiza apenas os campos que não são nulos ou vazios
        if (userRequestDto.getNome() != null && !userRequestDto.getNome().trim().isEmpty()) {
            user.setNome(userRequestDto.getNome());
        }

        if (userRequestDto.getEmail() != null && !userRequestDto.getEmail().trim().isEmpty()) {
            user.setEmail(userRequestDto.getEmail());
        }

        if (userRequestDto.getSenha() != null && !userRequestDto.getSenha().trim().isEmpty()) {
            user.setSenha(passwordEncoder.encode(userRequestDto.getSenha()));
        }

        if (userRequestDto.getTipoAcesso() != null) {
            user.setTipoAcesso(TipoAcesso.valueOf(userRequestDto.getTipoAcesso()));
        }

        userRepository.save(user);

        return "Usuário atualizado com sucesso.";
    }

    @Transactional
    public String userDelete(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new MessageException("Usuário não encontrado."));

        user.setAtivo(false);

        userRepository.save(user);
        return "Usuário excluído logicamente com sucesso.";
    }

    public Optional<User> findByIdAndAtivoTrue(UUID id) {
        return userRepository.findByIdAndAtivoTrue(id);
    }

    public List<User> findAllActiveUsers() {
        return userRepository.findByAtivoTrue();
    }

}
