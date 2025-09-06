package org.example.stage2module4.user.rest;

import org.example.stage2module4.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@RequestMapping("/rest/users")
public class UserRestController {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    public UserRestController(UserRepository userRepository,
                              UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @GetMapping("/{id}")
    public UserDto getOne(@PathVariable Integer id) {
        Optional<User> userOptional = userRepository.findById(id);
        return userMapper.toUserDto(userOptional.orElseThrow(
                ()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"User with id '%d' not found".formatted(id))
        ));
    }

    @GetMapping
    public PagedModel<UserDto> getAll(@ModelAttribute UserFilter filter, Pageable pageable) {
        Specification<User> spec = filter.toSpecification();
        Page<User> users = userRepository.findAll(spec, pageable);
        Page<UserDto> userDtoPage = users.map(userMapper::toUserDto);
        return new PagedModel<>(userDtoPage);
    }

    @PostMapping
    public UserDto create(@RequestBody UserDto dto) {
        if(dto.getId()!=null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id must be null");
        }
        User user = userMapper.toEntity(dto);
        User resultUser = userRepository.save(user);
        return userMapper.toUserDto(resultUser);
    }

    @PutMapping("/{id}")
    public UserDto update(@PathVariable Integer id, @RequestBody UserDto dto) {
        if(!dto.getId().equals(id)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Id in request path and body must be equal");
        }
        User user = userRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));
        userMapper.updateWithNull(dto, user);
        User resultUser = userRepository.save(user);
        return userMapper.toUserDto(resultUser);
    }

    @DeleteMapping("/{id}")
    public UserDto delete(@PathVariable Integer id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            userRepository.delete(user);
        }
        return userMapper.toUserDto(user);
    }
}

