package com.app.gizmophile.repository;

import com.app.gizmophile.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
