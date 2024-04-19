package com.app.gizmophile.controller;


import com.app.gizmophile.dto.*;
import com.app.gizmophile.model.Review;
import com.app.gizmophile.service.ReviewService;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.net.Socket;
import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/product/{productId}")
    public List<ReviewData> getReviews(@PathVariable Long productId) {
        return reviewService.getProductReviews(productId);
    }

    @GetMapping("/review/{reviewId}/replies")
    public List<ReplyData> getReplies(@PathVariable Long reviewId) {
        return reviewService.getReplies(reviewId);
    }

    @GetMapping("/user/{username}")
    public List<ReviewData> getUserReviews(@PathVariable String username) {
        return reviewService.getUserReviews(username);
    }

    @GetMapping("/review/{reviewId}")
    public ReviewData getReview(@PathVariable Long reviewId) {
        return reviewService.getReview(reviewId);
    }

    @GetMapping("/review/{reviewId}/image/{filename:.+}")
    public Resource getImage(@PathVariable Long reviewId, @PathVariable String filename) {

        try {
            Resource image = reviewService.getImage(filename, reviewId);
            if (image.exists() || image.isReadable()) {
                return image;
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
    @PostMapping("/rating")
    public void addRating(@RequestBody AddRating request) {
        reviewService.addRating(request);
    }

    @PostMapping("/review")
    public Long addReview(@RequestBody AddReview request) {
        return reviewService.addReview(request);
    }


    @PatchMapping("/review/{reviewId}")
    public Review updateReview(@RequestBody UpdateReviewData updateReviewData, @PathVariable Long reviewId) {
        return reviewService.updateReview(updateReviewData, reviewId);
    }

    @PutMapping("/upload-images/{reviewId}")
    public List<String> addPhotos(@RequestParam("imageFiles") MultipartFile[] files, @PathVariable Long reviewId) {
        return reviewService.addImages(files, reviewId);
    }

    @PatchMapping("/{reviewId}/like")
    public Integer likeReview(@PathVariable Long reviewId) {
        System.out.println("revId "+reviewId);
        return reviewService.likeReview(reviewId);
    }

    @PatchMapping("rating/{reviewId}")
    public Integer updateRating(@RequestBody Integer rating, @PathVariable Long reviewId) {
        return reviewService.updateRating(rating, reviewId);

    }

    @PostMapping("reply")
    public ReplyData addReply(@RequestBody AddReply replyData) {
        return reviewService.addReplyToReview(replyData);
    }

    @DeleteMapping("/review/{reviewId}")
    public boolean deleteRating(@PathVariable Long reviewId) {
        return reviewService.deleteReview(reviewId);
    }

    @DeleteMapping("/review/{reviewId}/image/{filename:.+}")
    public boolean deleteImage(@PathVariable Long reviewId, @PathVariable String filename){
        return reviewService.deleteImage(filename, reviewId);
    }
}
