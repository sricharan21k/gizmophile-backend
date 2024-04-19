package com.app.gizmophile.service;


import com.app.gizmophile.dto.*;
import com.app.gizmophile.model.OrderItem;
import com.app.gizmophile.model.Product;
import com.app.gizmophile.model.Review;
import com.app.gizmophile.model.User;
import com.app.gizmophile.repository.ProductRepository;
import com.app.gizmophile.repository.ReviewRepository;
import com.app.gizmophile.repository.UserRepository;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
public class ReviewService {

    @Value("${app.image.path}")
    private String imagePath;
    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public ReviewService(
            ReviewRepository reviewRepository,
            ProductRepository productRepository,
            UserRepository userRepository
    ) {
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public List<ReviewData> getProductReviews(Long productId) {
        List<Review> reviews = productRepository.findById(productId).orElseThrow()
                .getReviews().stream().filter(review -> review.getParent() == null).toList();
        return getReviews(reviews);
    }

    public List<ReviewData> getUserReviews(String username) {
        User user = userRepository.findByUsername(username).orElseThrow();
        List<Review> reviews = user.getReviews().stream()
                .filter(review ->
                        review.getParent() == null
                                && ((review.getContent() != null && !review.getContent().isBlank())
                                || !review.getHighlights().isEmpty()
                                || !review.getImages().isEmpty())
                ).toList();
        return getReviews(reviews);
    }

    private List<ReviewData> getReviews(List<Review> reviews) {
        List<ReviewData> reviewInfoList = new ArrayList<>();
        reviews.forEach(review -> {
            UserProfile userProfile = UserProfile.builder()
                    .username(review.getUser().getUsername())
                    .profilePicture(review.getUser().getProfileImage()).build();

            String productName = review.getProduct().getBrand()
                    + " " + review.getProduct().getModel();
            String productImage = review.getProduct().getColors().stream()
                    .filter(productColor -> Objects.equals(productColor.getColor(), review.getOrderItem().getItemColor()))
                    .findFirst().orElseThrow().getImage();

            ProductInfo product = ProductInfo.builder()
                    .productId(review.getProduct().getId())
                    .productType(review.getProduct().getType())
                    .productName(productName)
                    .productColor(review.getOrderItem().getItemColor())
                    .productVariant(review.getOrderItem().getItemVariant())
                    .productImage(productImage)
                    .build();

            ReviewData reviewData = ReviewData.builder()
                    .id(review.getId())
                    .rating(review.getRating())

                    .content(review.getContent())
                    .likes(review.getLikes())
                    .replies(review.getReplies().size())
                    .posted(review.getPosted())
                    .lastUpdated(review.getLastUpdated())
                    .images(review.getImages())
                    .profile(userProfile)
                    .highlights(review.getHighlights())
                    .product(product).build();

            reviewInfoList.add(reviewData);

        });
        return reviewInfoList;
    }

    public ReviewData getReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId).orElseThrow();

        String productName = review.getProduct().getBrand()
                + " " + review.getProduct().getModel();

        ProductInfo product = ProductInfo.builder()
                .productId(review.getProduct().getId())
                .productType(review.getProduct().getType())
                .productName(productName)
                .productColor(review.getOrderItem().getItemColor())
                .productVariant(review.getOrderItem().getItemVariant())
                .productImage(review.getProduct().getColors().stream()
                        .filter(productColor -> Objects.equals(productColor.getColor(), review.getOrderItem().getItemColor()))
                        .findFirst().orElseThrow().getImage()).build();


        return ReviewData.builder()
                .id(review.getId())
                .rating(review.getRating())
                .content(review.getContent())
                .likes(review.getLikes())
                .replies(review.getReplies().size())
                .posted(review.getPosted())
                .lastUpdated(review.getLastUpdated())
                .images(review.getImages())
                .highlights(review.getHighlights())
                .product(product).build();

    }

    public Double getRating(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow();
        boolean productRated = (product.getRating() == null) || (product.getRating() == 0);
        return product.getReviews().stream()
                .filter(review -> review != null && review.getRating() != null)
                .mapToDouble(Review::getRating)
                .sum();
    }

    public List<ReplyData> getReplies(Long reviewId) {
        var replies = reviewRepository.findById(reviewId).orElseThrow().getReplies();
        List<ReplyData> reviewReplies = new ArrayList<>();
        replies.forEach(reply -> {
            UserProfile userProfile = UserProfile.builder()
                    .username(reply.getUser().getUsername())
                    .profilePicture(reply.getUser().getProfileImage()).build();

            ReplyData replyData = ReplyData.builder()
                    .profile(userProfile)
                    .content(reply.getContent())
                    .lastUpdated(reply.getLastUpdated())
                    .build();

            reviewReplies.add(replyData);
        });
        return reviewReplies;
    }

    public Resource getImage(String filename, Long reviewId) throws MalformedURLException {
        Path filePath = Paths.get(imagePath, "review-" + reviewId + "/" + filename);
        System.out.println(filePath.getFileName());
        return new UrlResource(filePath.toUri());
    }

    @Transactional
    public Long addReview(AddReview request) {

        User user = userRepository.findByUsername(request.getUsername()).orElseThrow();

        OrderItem orderItem = user.getOrders().stream()
                .filter(order -> Objects.equals(order.getId(), request.getOrderId()))
                .findFirst().orElseThrow().getItems().stream()
                .filter(item -> Objects.equals(item.getId(), request.getOrderItemId()))
                .findFirst().orElseThrow();


        Review review = orderItem.getReview();
        if (review == null) {
            Product product = productRepository.findById(orderItem.getItem().getId()).orElseThrow();
            review = Review.builder()
                    .content(request.getContent())
                    .posted(LocalDateTime.now())
                    .user(user)
                    .highlights(request.getHighlights())
                    .images(request.getImages())
                    .product(product)
                    .orderItem(orderItem)
                    .build();
        } else {
            if (review.getContent() != null && !review.getContent().isBlank()) {
                review.setContent(request.getContent());
            }
            review.setHighlights(request.getHighlights());
            review.setImages(request.getImages());
        }

        return reviewRepository.save(review).getId();
    }

    public void addRating(AddRating request) {
        User user = userRepository.findByUsername(request.getUsername()).orElseThrow();

        OrderItem orderItem = user.getOrders().stream()
                .filter(order -> Objects.equals(order.getId(), request.getOrderId()))
                .findFirst().orElseThrow(() -> new RuntimeException("Order item not found ")).getItems().stream()
                .filter(item -> Objects.equals(item.getId(), request.getOrderItemId()))
                .findFirst().orElseThrow();

        Product product = productRepository.findById(orderItem.getItem().getId()).orElseThrow();
        int reviewCount = !product.getReviews().isEmpty() ? product.getReviews().size() : 0;
        System.out.println("review count: "+reviewCount);
        double averageRating = (getRating(product.getId()) + request.getRating()) / (reviewCount + 1);
        System.out.println("avg rating: "+averageRating);
        var ratingCorrectedToSingleDecimalPlace = new BigDecimal(averageRating)
                .setScale(1, RoundingMode.HALF_UP).doubleValue();
        System.out.println("rating corrected: "+ratingCorrectedToSingleDecimalPlace);

        product.setRating(ratingCorrectedToSingleDecimalPlace);
        productRepository.save(product);

        Review review = orderItem.getReview();
        if (review == null) {
            review = Review.builder()
                    .rating(request.getRating())
                    .user(user)
                    .product(product)
                    .orderItem(orderItem)
                    .posted(LocalDateTime.now())
                    .build();
        } else {
            review.setRating(request.getRating());
        }
        reviewRepository.save(review);
    }

    public Review updateReview(UpdateReviewData request, Long reviewId) {
        Review review = reviewRepository.findById(reviewId).orElseThrow();
        if (request.getContent() != null) {
            review.setContent(request.getContent());
        }
        review.setHighlights(request.getHighlights());
        review.setLastUpdated(LocalDateTime.now());
        return reviewRepository.save(review);
    }

    public List<String> addImages(MultipartFile[] images, Long reviewId) {
        Review review = reviewRepository.findById(reviewId).orElseThrow();
        for (var image : images) {
            if (!image.isEmpty()) {
                try {
                    String originalFilename = image.getOriginalFilename();
                    String uniqueFilename = generateUniqueFilename(originalFilename);
                    Path reviewDir = Paths.get(imagePath, "review-" + reviewId.toString());
                    Path filePath = reviewDir.resolve(uniqueFilename);

                    Files.createDirectories(reviewDir);
                    Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                    review.getImages().add(uniqueFilename);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to save image", e);
                }
            }
        }
        return reviewRepository.save(review).getImages().reversed();
    }

    public boolean deleteImage(String filename, Long reviewId) {
        try {
            Review review = reviewRepository.findById(reviewId).orElseThrow();
            Path filePath = Paths.get(imagePath, "review-" + reviewId.toString()).resolve(filename);
            review.getImages().remove(filename);
            try {
                Files.delete(filePath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            reviewRepository.save(review);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String generateUniqueFilename(String originalFilename) {
        String baseName = FilenameUtils.getBaseName(originalFilename);
        String extension = FilenameUtils.getExtension(originalFilename);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return baseName + "_" + timestamp + "." + extension;
    }


    public Integer likeReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId).orElseThrow();
        int likes = review.getLikes() == null ? 1 : review.getLikes() + 1;
        review.setLikes(likes);
        System.out.println("likes " + review.getLikes());
        reviewRepository.save(review);
        return review.getLikes();
    }

    public Integer updateRating(Integer rating, Long reviewId) {
        Review review = reviewRepository.findById(reviewId).get();

        if (rating != null) {
            review.setRating(rating);
        }
        reviewRepository.save(review);
        return review.getRating();
    }

    public ReplyData addReplyToReview(AddReply request) {
        Review parent = reviewRepository.findById(request.getReviewId()).orElseThrow();
        User user = userRepository.findById(request.getUserId()).orElseThrow();
        Review reviewReply = Review.builder().content(request.getContent())
                .posted(LocalDateTime.now())
                .user(user)
                .parent(parent)
                .build();
        parent.getReplies().add(reviewReply);
        Review reply = reviewRepository.save(parent);
        UserProfile userProfile = UserProfile.builder().username(reply.getUser().getUsername())
                .profilePicture(reply.getUser().getProfileImage()).build();
        return ReplyData.builder().lastUpdated(reply.getLastUpdated())
                .profile(userProfile)
                .content(reply.getContent()).build();

    }


    public boolean deleteReview(Long reviewId) {
        try {
            Path reviewDir = Paths.get(imagePath, "review-" + reviewId.toString());
            try (Stream<Path> paths = Files.walk(reviewDir)) {
                paths.sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            }
            reviewRepository.deleteReviewById(reviewId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


}
