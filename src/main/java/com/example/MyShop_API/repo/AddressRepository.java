package com.example.MyShop_API.repo;

import com.example.MyShop_API.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    Address findAddressesByAddressId(Long addressId);


    Optional<Address> findById(Long addressId);
}
