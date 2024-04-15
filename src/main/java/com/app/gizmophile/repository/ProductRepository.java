package com.app.gizmophile.repository;

import com.app.gizmophile.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByType(String type);
    Page<Product> findByType(String productType, Pageable pageable);
    Page<Product> findByTypeAndStockGreaterThan(String type, Integer stock, Pageable pageable);

    Page<Product> findByTypeAndBrandIn(String type, List<String> brands, Pageable pageable);

    Page<Product> findByTypeAndStockGreaterThanAndBrandIn(String type, Integer stock, List<String> brands, Pageable pageable);


    Page<Product> findByStockGreaterThan(Integer stock, Pageable pageable);

    Page<Product> findByBrandIn(List<String> brands, Pageable pageable);

    Page<Product> findByStockGreaterThanAndBrandIn(Integer stock, List<String> brands, Pageable pageable);

    @Query("from Product p where p.type like %:keyword%" +
            " or p.brand like %:keyword%" +
            " or p.model like %:keyword%"
    )
    List<Product> searchByKeyword(String keyword);

    @Query("from Product p where p.type like %:keyword%" +
            " or p.brand like %:keyword%" +
            " or p.model like %:keyword%"
    )
    Page<Product> searchByKeyword(String keyword, Pageable pageable);

    @Query("from Product p where ( p.type like %:keyword%" +
            " or p.brand like %:keyword%" +
            " or p.model like %:keyword% )" +
            " and p.stock > :stock"
    )
    Page<Product> searchByKeywordAndStock(String keyword,Integer stock, Pageable pageable);

    @Query("from Product p where ( p.type like %:keyword%" +
            " or p.brand like %:keyword%" +
            " or p.model like %:keyword% )" +
            " and p.brand IN :brands"
    )
    Page<Product> searchByKeywordAndBrand(String keyword, List<String> brands, Pageable pageable);

    @Query("from Product p where ( p.type like %:keyword%" +
            " or p.brand like %:keyword%" +
            " or p.model like %:keyword% )" +
            " and p.stock > :stock" +
            " and p.brand IN :brands"
    )
    Page<Product> searchByKeywordStockAndBrand(String keyword,Integer stock, List<String> brands, Pageable pageable);

}
