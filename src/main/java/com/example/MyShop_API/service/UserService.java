package com.example.MyShop_API.service;

import com.example.MyShop_API.constant.PredefinedRole;
import com.example.MyShop_API.dto.UserCreationRequest;
import com.example.MyShop_API.dto.UserUpdateRequest;
import com.example.MyShop_API.dto.UserResponse;
import com.example.MyShop_API.entity.*;
import com.example.MyShop_API.exception.AppException;
import com.example.MyShop_API.exception.ErrorCode;
import com.example.MyShop_API.mapper.UserMapper;
import com.example.MyShop_API.repo.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;
    CartRepository cartRepository;
    UserProfileRepository userProfileRepository;

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getUsers() {
        log.info("getUsers().........");
        return userRepository.findAll().stream().map(userMapper::toResponse).collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse getUserById(Long id) {
        log.info("getUserById().........");
        User user = userRepository.findById(id).orElse(null);
        return userMapper.toResponse(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public UserResponse createUser(UserCreationRequest request) {
        log.info("createUser().........");
        User user = userMapper.toEntity(request);

        // convert pass
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // set role defautlt nếu không có tạo mới
        HashSet<Role> roles = new HashSet<>();
        roleRepository.findById(PredefinedRole.USER_ROLE).ifPresentOrElse(roles::add,
                () -> {
                    Role defaultRole = Role.builder()
                            .roleName("USER")
                            .description("Role User default")
                            .build();
                    roleRepository.save(defaultRole);
                    roles.add(defaultRole);
                });

        user.setRoles(roles);
        try {
            user = userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        return userMapper.toResponse(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse updateUser(UserUpdateRequest request, Long id) {
        log.info("updateUser().........");
        User findUser = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        userMapper.update(request, findUser);
        findUser.setPassword(passwordEncoder.encode(findUser.getPassword()));
        var roles = roleRepository.findAllById(request.getRoles());

        findUser.setRoles(new HashSet<>(roles));

        try {
            findUser = userRepository.save(findUser);

        } catch (DataIntegrityViolationException e) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        return userMapper.toResponse(findUser);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUserById(Long id) {
        log.info("deleteUserById().........");
        userRepository.deleteById(id);
    }

    @PostAuthorize("returnObject.username == authentication.name")
    public UserResponse getMyInfor() {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        User user = userRepository.findByUsername(name).orElseThrow(()
                -> new AppException(ErrorCode.USER_NOT_EXISTED));

        return userMapper.toResponse(user);
    }
}
