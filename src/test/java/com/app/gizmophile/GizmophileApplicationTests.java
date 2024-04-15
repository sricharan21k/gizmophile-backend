package com.app.gizmophile;

import com.app.gizmophile.model.Product;
import com.app.gizmophile.model.ProductColor;
import com.app.gizmophile.model.ProductVariant;
import com.app.gizmophile.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class GizmophileApplicationTests {

    @Autowired
    ProductRepository productRepository;

    @Test
    void contextLoads() {
    }

    @Test
    void add(){
        List<String> specs = List.of(
                "MediaTek Dimensity 7200-Ultra",
                "120Hz 1.5K Curved AMOLED display",
                "200MP Ultra-High Res Camera",
                "120W HyperCharge"
        );
        String relativePath = "phone/redmi/note 13 pro+/";

        Product redmiNote13Pro = Product.builder().
                brand("Redmi")
                .model("Note 13 Pro+ 5G")
                .type("phone")
                .description("Super Power. Super Note")
                .stock(20)
                .specs(specs).build();

        List<ProductVariant> variants = List.of(
                ProductVariant.builder()
                        .product(redmiNote13Pro)
                        .baseVariant(true)
                        .variant("8GB+256GB")
                        .price(31999D)
                        .build(),

                ProductVariant.builder()
                        .product(redmiNote13Pro)
                        .baseVariant(false)
                        .variant("12GB+256GB")
                        .price(33999D)
                        .build(),

                ProductVariant.builder()
                        .product(redmiNote13Pro)
                        .baseVariant(false)
                        .variant("12GB+512GB")
                        .price(35999D)
                        .build()
        );

        List<ProductColor> colors = List.of(
                ProductColor.builder()
                        .product(redmiNote13Pro)
                        .color("Fusion Purple")
                        .image(relativePath+ "redmi_note_13_pro_plus_purple.webp")
                        .specialEdition(false)
                        .extraCost(0D)
                        .build(),

                ProductColor.builder()
                        .product(redmiNote13Pro)
                        .color("Fusion White")
                        .image(relativePath + "redmi_note_13_pro_plus_white.webp")
                        .specialEdition(false)
                        .extraCost(0D)
                        .build(),

                ProductColor.builder()
                        .product(redmiNote13Pro)
                        .color("Fusion Black")
                        .image(relativePath + "redmi_note_13_pro_plus_black.webp")
                        .specialEdition(false)
                        .extraCost(0D)
                        .build()
        );

        redmiNote13Pro.setColors(colors);
        redmiNote13Pro.setVariants(variants);
        redmiNote13Pro.setBaseVariant();

        productRepository.save(redmiNote13Pro);
    }

}
