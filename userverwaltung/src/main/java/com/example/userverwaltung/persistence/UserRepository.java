package com.example.userverwaltung.persistence;

import com.example.userverwaltung.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}
