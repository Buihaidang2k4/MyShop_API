package com.example.MyShop_API.service.userprofie;

import com.example.MyShop_API.Enum.Role;
import com.example.MyShop_API.dto.request.UserProfileRequest;
import com.example.MyShop_API.dto.response.UserProfileResponse;
import com.example.MyShop_API.entity.Cart;
import com.example.MyShop_API.entity.User;
import com.example.MyShop_API.entity.UserProfile;
import com.example.MyShop_API.exception.AppException;
import com.example.MyShop_API.exception.ErrorCode;
import com.example.MyShop_API.mapper.UserProfileMapper;
import com.example.MyShop_API.repo.UserProfileRepository;
import com.example.MyShop_API.repo.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserProfileService implements IUserProfileService {
    UserProfileRepository userProfileRepository;
    UserProfileMapper userProfileMapper;
    UserRepository userRepository;

    public List<UserProfileResponse> getUserProfile() {
        return userProfileRepository.findAll().stream().map(userProfileMapper::toResponse).collect(Collectors.toList());
    }

    public UserProfileResponse getUserProfileById(Long userProfileId) throws AppException {
        return userProfileMapper.toResponse(userProfileRepository.findById(userProfileId).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        ));
    }

    @Transactional
    public UserProfileResponse createOrUpdateProfile(Long userId, UserProfileRequest userProfileRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        //        check role
        boolean isUser = user.getRoles().stream()
                .anyMatch(role -> "USER".equals(role.getRoleName()));

        if (!isUser) throw new AppException(ErrorCode.ROLE_NOT_ALLOWED);

        UserProfile userProfile = user.getProfile();
        if (userProfile == null) {
            // Nếu user chưa có profile tạo mới
            userProfile = UserProfile.builder()
                    .firstName(userProfileRequest.getFirstName())
                    .lastName(userProfileRequest.getLastName())
                    .mobileNumber(userProfileRequest.getMobileNumber())
                    .user(user)
                    .build();

            // Tạo cart kèm theo
            Cart cart = new Cart();
            cart.setProfile(userProfile);
            userProfile.setCart(cart);
            user.setProfile(userProfile);

            userProfile = userProfileRepository.save(userProfile);
        } else {
            // nếu user đã có profile ->  cập nhật
            userProfileMapper.UpdateUserProfile(userProfileRequest, userProfile);
            userProfile = userProfileRepository.save(userProfile);
        }

        return userProfileMapper.toResponse(userProfile);
    }
}
