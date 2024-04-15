package com.app.gizmophile.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer rating;
    private String content;
    private LocalDateTime posted;
    private LocalDateTime lastUpdated;
    private Integer likes;

    @ElementCollection
    @Column(name = "image")
    private List<String> images = new ArrayList<>();

    @ElementCollection
    @Column(name = "highlight")
    private List<String> highlights = new ArrayList<>();

    @OneToOne
    private OrderItem orderItem;

    @ManyToOne
    private User user;

    @ManyToOne
    private Product product;

    @ManyToOne
    private Review parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Review> replies = new ArrayList<>();
}
