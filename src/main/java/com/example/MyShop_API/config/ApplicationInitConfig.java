package com.example.MyShop_API.config;

import com.example.MyShop_API.entity.Address;
import com.example.MyShop_API.entity.Cart;
import com.example.MyShop_API.entity.Role;
import com.example.MyShop_API.entity.User;
import com.example.MyShop_API.repo.AddressRepository;
import com.example.MyShop_API.repo.RoleRepository;
import com.example.MyShop_API.repo.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Set;

@Configuration
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApplicationInitConfig {
    PasswordEncoder passwordEncoder;

    @Bean
    ApplicationRunner initApplicationRunner(UserRepository userRepository, RoleRepository roleRepository) {

        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                Role role = roleRepository.findById("ADMIN").orElseGet(
                        () -> roleRepository.save(
                                Role.builder()
                                        .roleName("ADMIN")
                                        .description("Default role")
                                        .build()
                        )
                );

                
                roleRepository.save(role);

                User user = User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin"))
                        .email("Admin@gmail.com")
                        .roles((Set.of(role)))
                        .build();


                userRepository.save(user);
                log.info("Default user has been created with password:admin , please change it");
            }
        };
    }
}
