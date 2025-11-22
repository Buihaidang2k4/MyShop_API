package com.example.MyShop_API.service.user;

import com.example.MyShop_API.anotation.AdminOnly;
import com.example.MyShop_API.anotation.AllAccess;
import com.example.MyShop_API.constant.PredefinedRole;
import com.example.MyShop_API.dto.request.ChangePasswordRequest;
import com.example.MyShop_API.dto.request.UserCreationRequest;
import com.example.MyShop_API.dto.request.UserUpdateRequest;
import com.example.MyShop_API.dto.response.UserResponse;
import com.example.MyShop_API.entity.*;
import com.example.MyShop_API.exception.AppException;
import com.example.MyShop_API.exception.ErrorCode;
import com.example.MyShop_API.mapper.UserMapper;
import com.example.MyShop_API.repo.*;
import com.example.MyShop_API.service.userprofie.UserProfileService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
public class UserService implements IUserService {
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;
    UserProfileService profileService;

    @AdminOnly
    public List<UserResponse> getUsers() {
        log.info("getUsers().........");
        return userRepository.findAll().stream().map(userMapper::toResponse).collect(Collectors.toList());
    }

    @AdminOnly
    public UserResponse getUserById(Long id) {
        log.info("getUserById().........");
        User user = userRepository.findById(id).orElse(null);
        return userMapper.toResponse(user);
    }

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

        // Tạo profile mặc định
        UserProfile profile = profileService.createEmptyUserProfile();
        profile.setUser(user);
        user.setProfile(profile);

        try {
            user = userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        return userMapper.toResponse(user);
    }

    @AllAccess
    public void changePassword(ChangePasswordRequest request, Long id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new AppException(ErrorCode.USER_NOT_EXISTED));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.PASSWORD_NOT_MATCHES);
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new AppException(ErrorCode.PASSWORD_CONFIRM_NOT_MATCHES);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        try {
            userRepository.save(user);
            log.info("================== CHANGE PASSWORD ===================");
        } catch (DataIntegrityViolationException e) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
    }

    @AllAccess
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

    public void deleteUserById(Long id) {
        log.info("deleteUserById().........");
        userRepository.deleteById(id);
    }

    @AllAccess // Tất cả role đều truy cập được
    @PostAuthorize("returnObject.email.toLowerCase() == authentication.name.toLowerCase()")
    public UserResponse getMyInfor() {
        var auth = SecurityContextHolder.getContext().getAuthentication();

        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        log.info("user = {}", user.getProfile());
        UserResponse response = userMapper.toResponse(user);
        log.info("=========== Get Info ===============");
        return response;
    }
}
