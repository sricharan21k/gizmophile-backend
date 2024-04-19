package com.app.gizmophile.service;

import com.app.gizmophile.dto.*;
import com.app.gizmophile.model.Product;
import com.app.gizmophile.model.ProductColor;
import com.app.gizmophile.model.ProductVariant;
import com.app.gizmophile.repository.ProductColorRepository;
import com.app.gizmophile.repository.ProductRepository;
import com.app.gizmophile.repository.ProductVariantRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService {
    @Value("${app.image.path}")
    private String imagePath;

    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ProductColorRepository productColorRepository;

    public ProductService(
            ProductRepository productRepository,
            ProductVariantRepository productVariantRepository,
            ProductColorRepository productColorRepository) {
        this.productRepository = productRepository;
        this.productVariantRepository = productVariantRepository;
        this.productColorRepository = productColorRepository;
    }

    public ProductPage getProductsByType(String productType, Pageable pageable) {
        Page<Product> productPage = productRepository.findByType(productType, pageable);
        List<Product> products = productPage.getContent();
        List<ProductData> productDataList = new ArrayList<>();

        products.forEach(product -> {
            ProductData productData = ProductData.builder().
                    id(product.getId())
                    .type(product.getType())
                    .brand(product.getBrand())
                    .model(product.getModel())
                    .description(product.getDescription())
                    .baseVariant(product.getBaseVariant())
                    .specs(product.getSpecs())
                    .rating(product.getRating())
                    .stock(product.getStock())
                    .build();

            List<Long> colors = product.getColors().stream().map(ProductColor::getId).toList();
            List<Long> variants = product.getVariants().stream().map(ProductVariant::getId).toList();
            productData.setColors(colors);
            productData.setVariants(variants);

            productDataList.add(productData);
        });
        return ProductPage.builder()
                .products(productDataList)
                .totalProducts(productPage.getTotalElements())
                .totalPages(productPage.getTotalPages())
                .build();
    }

    public ProductData getProduct(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow();

        ProductData productData = ProductData.builder().
                id(product.getId())
                .type(product.getType())
                .brand(product.getBrand())
                .model(product.getModel())
                .description(product.getDescription())
                .baseVariant(product.getBaseVariant())
                .specs(product.getSpecs())
                .rating(product.getRating())
                .stock(product.getStock())
                .build();

        List<Long> colors = product.getColors().stream().map(ProductColor::getId).toList();
        List<Long> variants = product.getVariants().stream().map(ProductVariant::getId).toList();
        productData.setColors(colors);
        productData.setVariants(variants);

        return productData;
    }

    public SearchProduct getBaseProduct(Long productId){
        Product product = productRepository.findById(productId).orElseThrow();
        ProductColor productColor = productColorRepository.findById(product.getColors().getFirst().getId()).orElseThrow();
        return SearchProduct.builder()
                .productId(product.getId())
                .productName(product.getBrand() + " " + product.getModel())
                .colorId(product.getColors().getFirst().getId())
                .variantId(product.getVariants().getFirst().getId())
                .imageUrl(productColor.getImage())
                .build();
    }

    public List<ProductData> mapProductsToProductData(List<Product> products) {
        List<ProductData> productDataList = new ArrayList<>();

        products.forEach(product -> {
            ProductData productData = ProductData.builder().
                    id(product.getId())
                    .type(product.getType())
                    .brand(product.getBrand())
                    .model(product.getModel())
                    .description(product.getDescription())
                    .baseVariant(product.getBaseVariant())
                    .specs(product.getSpecs())
                    .rating(product.getRating())
                    .stock(product.getStock())
                    .build();

            List<Long> colors = product.getColors().stream().map(ProductColor::getId).toList();
            List<Long> variants = product.getVariants().stream().map(ProductVariant::getId).toList();
            productData.setColors(colors);
            productData.setVariants(variants);

            productDataList.add(productData);
        });
        return productDataList;
    }

    public List<SearchProduct> mapProductToSearchProduct(List<Product> products) {
        List<SearchProduct> searchProducts = new ArrayList<>();
        products.forEach(product -> {
            ProductColor productColor = productColorRepository.findById(product.getColors().getFirst().getId()).orElseThrow();
            SearchProduct searchProduct = SearchProduct.builder()
                    .productId(product.getId())
                    .productName(product.getBrand() + " " + product.getModel())
                    .colorId(product.getColors().getFirst().getId())
                    .variantId(product.getVariants().getFirst().getId())
                    .imageUrl(productColor.getImage())
                    .build();
            searchProducts.add(searchProduct);
        });
        return searchProducts;
    }

    public ProductVariantData getVariant(Long variantId) {
        ProductVariant productVariant = productVariantRepository.findById(variantId).orElseThrow();
        return ProductVariantData.builder().id(productVariant.getId())
                .productId(productVariant.getProduct().getId())
                .variant(productVariant.getVariant())
                .price(productVariant.getPrice())
                .build();
    }

    public ProductColorData getColor(Long colorId) {
        ProductColor productColor = productColorRepository.findById(colorId).orElseThrow();
        return ProductColorData.builder().id(productColor.getId())
                .productId(productColor.getProduct().getId())
                .color(productColor.getColor())
                .image(productColor.getImage())
                .build();
    }

    public Resource getProductImage(Long colorId) throws MalformedURLException {
        ProductColor productColor = productColorRepository.findById(colorId).orElseThrow();
        Path filePath = Paths.get(imagePath, "product/" + productColor.getImage());
        return new UrlResource(filePath.toUri());
    }

    public List<SearchProduct> getNewLaunches() {
        List<ProductData> productDataList = new ArrayList<>();
        List<Product> products = productRepository.findAll().stream()
                .sorted(Comparator.comparing(Product::getId).reversed())
                .limit(5).toList();
        return mapProductToSearchProduct(products);

    }

    public Page<Product> getProductsOfType(String type, boolean inStock, List<String> brands, Pageable pageable) {
        if (inStock) {
            return brands == null
                    ? productRepository.findByTypeAndStockGreaterThan(type, 0, pageable)
                    : productRepository.findByTypeAndStockGreaterThanAndBrandIn(type, 0, brands, pageable);
        }
        return brands == null
                ? productRepository.findByType(type, pageable)
                : productRepository.findByTypeAndBrandIn(type, brands, pageable);
    }

    public Page<Product> getAllProducts(boolean inStock, List<String> brands, Pageable pageable) {
        if (inStock) {
            return brands == null
                    ? productRepository.findByStockGreaterThan(0, pageable)
                    : productRepository.findByStockGreaterThanAndBrandIn(0, brands, pageable);
        }
        return brands == null
                ? productRepository.findAll(pageable)
                : productRepository.findByBrandIn(brands, pageable);
    }

    public Page<Product> getProductsBySearch(String keyword, boolean inStock, List<String> brands, Pageable pageable) {
        if (inStock) {
            return brands == null
                    ? productRepository.searchByKeywordAndStock(keyword, 0, pageable)
                    : productRepository.searchByKeywordStockAndBrand(keyword, 0, brands, pageable);
        } else {
            return brands == null
                    ? productRepository.searchByKeyword(keyword, pageable)
                    : productRepository.searchByKeywordAndBrand(keyword, brands, pageable);
        }
    }

//    public Product getProduct(Long id) {
//        return productRepository.findById(id).orElseThrow(ProductNotFoundException::new);
//    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public List<SearchProduct> searchProducts(String keyword) {

        var products = productRepository.searchByKeyword(keyword).stream().limit(10).toList();
        return mapProductToSearchProduct(products);

    }

    public Map<String, Set<String>> getBrands() {
        return productRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(Product::getType,
                        Collectors.mapping(Product::getBrand, Collectors.toSet())));

    }

    public Set<String> getAllBrands() {
        return productRepository.findAll().stream().map(Product::getBrand).collect(Collectors.toSet());
    }


}
