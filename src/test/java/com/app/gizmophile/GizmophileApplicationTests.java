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

//    @Autowired
//    ProductRepository productRepository;

    @Test
    void contextLoads() {
    }

//    @Test
//    void add() {
//        List<String> specs = List.of(
//                "10bit Liquid Retina IPS Display",
//                "Apple M3 Chip 8-Core GPU",
//                "8GB Unified Memory",
//                "2 x Thunderbolt 4 + MagSafe 3 Port + 3.5mm Jack",
//                "52Wh Battery + 30W USB-C Power Adapter",
//                "1080p FaceTime HD Video Calls",
//                "Four-speaker sound system, Spatial Audio",
//                "macOS Sonoma"
//        );
//
//        Product macbookAir3 = Product.builder().
//                brand("Apple")
//                .model("Macbook Air 3")
//                .type("laptop")
//                .description("Supercharged by M3.")
//                .stock(20)
//                .specs(specs).build();
//
//        List<ProductVariant> variants = List.of(
//                ProductVariant.builder()
//                        .product(macbookAir3)
//                        .baseVariant(true)
//                        .variant("13\"-256GB")
//                        .price(110890D)
//                        .build(),
//                ProductVariant.builder()
//                        .product(macbookAir3)
//                        .baseVariant(true)
//                        .variant("13\"-512GB")
//                        .price(129990D)
//                        .build(),
//                ProductVariant.builder()
//                        .product(macbookAir3)
//                        .baseVariant(true)
//                        .variant("15\"-256GB")
//                        .price(129990D)
//                        .build(),
//                ProductVariant.builder()
//                        .product(macbookAir3)
//                        .baseVariant(true)
//                        .variant("15\"-512GB")
//                        .price(149490D)
//                        .build()
//
//        );
//
//        List<ProductColor> colors = List.of(
//
//                ProductColor.builder()
//                        .product(macbookAir3)
//                        .color("Midnight Black")
//                        .image("https://i.postimg.cc/nzKztYw2/original-imagypv6zpfu8kzh.webp")
//                        .specialEdition(false)
//                        .extraCost(0D)
//                        .build(),
//                ProductColor.builder()
//                        .product(macbookAir3)
//                        .color("Silver")
//                        .image("https://i.postimg.cc/HLQjyZJw/original-imagypv6x6zqbu7t.webp")
//                        .specialEdition(false)
//                        .extraCost(0D)
//                        .build(),
//                ProductColor.builder()
//                        .product(macbookAir3)
//                        .color("Space Grey")
//                        .image("https://i.postimg.cc/kgG5TM2g/original-imagypv6yyg96khh.webp")
//                        .specialEdition(false)
//                        .extraCost(0D)
//                        .build(),
//                ProductColor.builder()
//                        .product(macbookAir3)
//                        .color("Starlight Gold")
//                        .image("https://i.postimg.cc/J7FZNF6J/original-imagypv6prbgkfzg.webp")
//                        .specialEdition(false)
//                        .extraCost(0D)
//                        .build()
//        );
//
//        macbookAir3.setColors(colors);
//        macbookAir3.setVariants(variants);
//        macbookAir3.setBaseVariant();
//
//        productRepository.save(macbookAir3);
//    }
//
//    @Test
//    void deleteProduct() {
//        productRepository.deleteById(39L);
//    }
//
//    @Test
//    void updateProduct() {
//        Product product = productRepository.findById(32L).get();
//        ProductVariant remove = product.getVariants().get(0);
//        remove.setVariant("Over Ear");
//        product.getVariants().remove(0);
//        product.getVariants().add(remove);
//        productRepository.save(product);
//    }

}
