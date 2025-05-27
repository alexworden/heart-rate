package com.heartrate.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.heartrate.model.Item;
import com.heartrate.model.Rating;
import com.heartrate.model.User;
import com.heartrate.repository.ItemRepository;
import com.heartrate.repository.RatingRepository;
import com.heartrate.repository.UserRepository;

@Service
public class RatingService {

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    /**
     * Saves or updates a user's interaction status with an item.
     *
     * @param userId The UUID of the user.
     * @param itemId The UUID of the item.
     * @param ratingValue The rating value (0-5) if status is RATED, otherwise null.
     * @param status The status of the interaction (e.g., "RATED", "DONT_CARE", "DELETED", "DONT_KNOW").
     * @return The saved or updated Rating object.
     */
    public Rating saveRating(UUID userId, UUID itemId, Integer ratingValue, String status) {
        Optional<User> userOptional = userRepository.findById(userId);
        Optional<Item> itemOptional = itemRepository.findById(itemId);

        if (userOptional.isPresent() && itemOptional.isPresent()) {
            User user = userOptional.get();
            Item item = itemOptional.get();

            // Check if a rating already exists for this user and item
            Optional<Rating> existingRatingOptional = ratingRepository.findByUserAndItem(user, item);

            Rating ratingToSave;
            if (existingRatingOptional.isPresent()) {
                // Update the existing rating
                ratingToSave = existingRatingOptional.get();
                ratingToSave.setRating(status.equals("RATED") ? ratingValue : null); // Set rating only if status is RATED
                ratingToSave.setStatus(status);
                ratingToSave.setTimestamp(LocalDateTime.now()); // Update timestamp on interaction
            } else {
                // Create a new rating
                ratingToSave = new Rating(user, item, status.equals("RATED") ? ratingValue : null, status); // Set rating only if status is RATED
            }

            return ratingRepository.save(ratingToSave);
        } else {
            // Handle case where user or item is not found
            throw new RuntimeException("User or Item not found");
        }
    }
} 