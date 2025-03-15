package coderhood.controller;

import coderhood.dto.UserRequestDto;
import coderhood.dto.UserResponseDto;
import coderhood.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users")
    public ResponseEntity<UserResponseDto> userCreate(@Valid @RequestBody UserRequestDto userRequestDto) {
        UserResponseDto userResponseDTO = userService.userCreate(userRequestDto);
        return new ResponseEntity<>(userResponseDTO, HttpStatus.CREATED);
    }
}