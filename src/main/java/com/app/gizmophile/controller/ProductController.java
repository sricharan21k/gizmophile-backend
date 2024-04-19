package com.app.gizmophile.controller;

import com.app.gizmophile.dto.*;
import com.app.gizmophile.model.Product;
import com.app.gizmophile.service.ProductService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{type}")
    public ProductPage getProductsByType(
            @PathVariable String type,
            Pageable pageable,
            @RequestParam boolean inStock,
            @RequestParam(required = false) String brands,
            @RequestParam(required = false) String keyword
    ) {
        List<String> brandList = null;
        Page<Product> page = null;
        List<ProductData> products = new ArrayList<>();

        if (brands != null && !brands.isEmpty()) {
            try {
                brandList = new ObjectMapper().readValue(brands, new TypeReference<List<String>>() {});
                System.out.println(brandList);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        if (type.equals("search")) {
            page = productService.getProductsBySearch(keyword, inStock, brandList, pageable);
        } else if (type.equals("all")) {
            page = productService.getAllProducts(inStock,brandList, pageable);
        } else {
            page = productService.getProductsOfType(type, inStock, brandList, pageable);
        }


        return ProductPage.builder().products(productService.mapProductsToProductData(page.getContent()))
                .totalProducts(page.getTotalElements())
                .totalPages(page.getTotalPages()).build();
    }

    @GetMapping("/new-launches")
    public List<SearchProduct> getNewLaunches() {
        return productService.getNewLaunches();
    }

    @GetMapping("/search-all")
    public List<SearchProduct> searchProducts(@RequestParam String keyword) {
        return productService.searchProducts(keyword);
    }

    @GetMapping("/brands")
    public Map<String, Set<String>> getBrands() {
        return productService.getBrands();
    }

    @GetMapping("/all-brands")
    public Set<String> getAllBrands() {
        return productService.getAllBrands();
    }


    @GetMapping("product/{productId}")
    public ProductData getProduct(@PathVariable Long productId) {
        return productService.getProduct(productId);
    }

    @GetMapping("base-product/{productId}")
    public SearchProduct getBaseProduct(@PathVariable Long productId) {
        return productService.getBaseProduct(productId);
    }

    @GetMapping("/product-variant/{variantId}")
    public ProductVariantData getProductVariant(@PathVariable Long variantId){
        return productService.getVariant(variantId);
    }

    @GetMapping("/product-color/{colorId}")
    public ProductColorData getProductColor(@PathVariable Long colorId){
        return productService.getColor(colorId);
    }

    @GetMapping("/product-color/{colorId}/image-url")
    public String getProductImageUrl(@PathVariable Long colorId){
        return productService.getColor(colorId).getImage();
    }

    @GetMapping("/product-image/{colorId}")
    public ResponseEntity<Resource> getProductImage(@PathVariable Long colorId){
        try {
            Resource productImage = productService.getProductImage(colorId);
            return ResponseEntity.ok(productImage);
        } catch (MalformedURLException e) {
           return ResponseEntity.notFound().build();
        }
    }

}
