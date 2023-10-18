package com.mheadmovie.movies;

import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private MongoTemplate mongoTemplate;

    public ReviewModel createReview(String reviewBody, String imdbId) {
        ReviewModel review = reviewRepository.insert(new ReviewModel(reviewBody));

        UpdateResult updateResult = mongoTemplate.update(MovieModel.class)
                .matching(Criteria.where("imdbId").is(imdbId))
                .apply(new Update().push("reviewIds").value(review))
                .first();

        // Check if the update was successful
        if (updateResult.getMatchedCount() == 0) {
            // Handle the error appropriately
            throw new RuntimeException("No matching movie found for imdbId: " + imdbId);
        }

        return review;
    }
}
