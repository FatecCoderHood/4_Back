package coderhood.service;

import coderhood.dto.UserRequestDto;
import coderhood.dto.UserResponseDto;
import coderhood.model.TipoAcesso;
import coderhood.model.User;
import coderhood.repository.UserRepository;
import coderhood.validator.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Transactional
    public UserResponseDto criarUsuario(UserRequestDto userRequestDto) {
        if (!EmailValidator.isValidEmail(userRequestDto.getEmail())) {
            throw new IllegalArgumentException("O e-mail fornecido é inválido.");
        }

        Optional<User> existingUser = userRepository.findByEmail(userRequestDto.getEmail());
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("Email já cadastrado.");
        }

        User user = new User();
        user.setNome(userRequestDto.getNome());
        user.setEmail(userRequestDto.getEmail());
        user.setSenha(passwordEncoder.encode(userRequestDto.getSenha()));
        user.setTipoAcesso(TipoAcesso.valueOf(userRequestDto.getTipoAcesso()));

        user = userRepository.save(user);

        return new UserResponseDto(user.getId(), user.getNome(), user.getEmail(), user.getTipoAcesso().name());
    }
}