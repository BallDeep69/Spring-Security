package com.example.userverwaltung.persistence;

import com.example.userverwaltung.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, String> {
}
