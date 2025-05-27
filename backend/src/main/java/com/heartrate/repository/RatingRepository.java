package com.heartrate.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.heartrate.model.Item;
import com.heartrate.model.Rating;
import com.heartrate.model.User;

@Repository
public interface RatingRepository extends JpaRepository<Rating, UUID> {
    Optional<Rating> findByUserAndItem(User user, Item item);
} 