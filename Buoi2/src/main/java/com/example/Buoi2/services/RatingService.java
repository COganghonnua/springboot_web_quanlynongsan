package com.example.Buoi2.services;

import com.example.Buoi2.entity.Rating;
import com.example.Buoi2.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RatingService {
    private final RatingRepository ratingRepository;

    public List<Rating> getRatingsByProductId(Long productId) {
        return ratingRepository.findByProductId(productId);
    }

    public Rating saveRating(Rating rating) {
        return ratingRepository.save(rating);
    }
}
