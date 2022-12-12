package com.gachon.fishbowl.repository;

import com.gachon.fishbowl.entity.UserId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserIdRepository extends JpaRepository<UserId, String> {
    Optional<List<UserId>> findAllById(String userId);
}
