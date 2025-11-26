package com.example.MyShop_API.address_test;

import com.example.MyShop_API.dto.request.AddressRequest;
import com.example.MyShop_API.dto.response.AddressResponse;
import com.example.MyShop_API.entity.Address;
import com.example.MyShop_API.entity.UserProfile;
import com.example.MyShop_API.exception.AppException;
import com.example.MyShop_API.exception.ErrorCode;
import com.example.MyShop_API.mapper.AddressMapper;
import com.example.MyShop_API.repo.AddressRepository;
import com.example.MyShop_API.repo.UserProfileRepository;
import com.example.MyShop_API.service.address.AddressService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Stream;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AddressServiceTest {

    @Mock
    AddressRepository addressRepository;

    @Mock
    UserProfileRepository userProfileRepository;

    @Mock
    AddressMapper addressMapper;

    @InjectMocks
    AddressService addressService;

    UserProfile profile;
    Address address;
    AddressRequest request;

    static Stream<Arguments> provideDeleteAddressCases() {
        UserProfile profile1 = new UserProfile();
        profile1.setProfileId(1L);
        Address addr1 = new Address();
        addr1.setAddressId(10L);
        addr1.setProfile(profile1);

        UserProfile profile2 = new UserProfile();
        profile2.setProfileId(2L);
        Address addr2 = new Address();
        addr2.setAddressId(11L);
        addr2.setProfile(profile2);

        return Stream.of(
                Arguments.of(addr1, 1L, null),                // success
                Arguments.of(addr2, 1L, AppException.class),  // forbidden
                Arguments.of(null, 1L, AppException.class)    // not found
        );
    }

    @BeforeEach
    void setup() {
        profile = new UserProfile();
        profile.setProfileId(1L);
        profile.setAddress(new ArrayList<>());

        address = new Address();
        address.setAddressId(10L);
        address.setProfile(profile);

        profile.getAddress().add(address);

        request = new AddressRequest();
        request.setIsDefault(true);
    }

    @Test
    void createAddress_WhenProfileNotFound_ShouldThrowException() {
        when(userProfileRepository.findById(1L)).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> addressService.createAddress(request, 1L));

        assertEquals(ErrorCode.USER_NOT_EXISTED, ex.getErrorCode());
    }

    @Test
    void createAddress_Success() {
        when(userProfileRepository.findById(1L)).thenReturn(Optional.of(profile));

        when(addressMapper.toEntity(request)).thenReturn(address);
        when(addressRepository.save(address)).thenReturn(address);
        when(addressMapper.toResponse(address)).thenReturn(new AddressResponse());

        AddressResponse response = addressService.createAddress(request, 1L);

        verify(addressRepository).clearDefaultAddressForProfile(1L);
        verify(addressRepository).save(address);
        verify(addressMapper).toResponse(address);

        assertNotNull(response);
        assertTrue(address.isDefault());
        assertEquals(profile, address.getProfile());
    }

    @Test
    void deleteAddress_success() {
        // given
        when(addressRepository.findById(10L)).thenReturn(Optional.of(address));

        // when
        addressService.deleteAddress(10L, 1L);

        // then
        verify(addressRepository, times(1)).delete(address);
    }

    @Test
    void deleteAddress_addressNotFound() {
        when(addressRepository.findById(10L)).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> addressService.deleteAddress(10L, 1L));

        assertEquals(ErrorCode.ADDRESS_NOT_EXISTED, ex.getErrorCode());
    }

    @Test
    void deleteAddress_forbidden() {
        // profileId không khớp
        profile.setProfileId(99L);

        when(addressRepository.findById(10L)).thenReturn(Optional.of(address));

        AppException ex = assertThrows(AppException.class, () -> addressService.deleteAddress(10L, 1L));

        assertEquals(ErrorCode.FORBIDDEN, ex.getErrorCode());

        verify(addressRepository, never()).delete(any());
    }


}
