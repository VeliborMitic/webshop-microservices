package product.service.persistence.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import product.service.web.filters.ProductFilter;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("select p from Product p " +
            "where (:#{#productFilter.name} is null or p.name like cast(concat('%',:#{#productFilter.name},'%') as text)) " +
            "and (:#{#productFilter.priceFrom} is null or p.price > :#{#productFilter.priceFrom}) " +
            "and (:#{#productFilter.priceTo} is null or p.price < :#{#productFilter.priceTo}) " +
            "and (:#{#productFilter.categoryId} is null or p.category.id = :#{#productFilter.categoryId})")
    Page<Product> findAllByFilter(Pageable pageable, @Param("productFilter") ProductFilter productFilter);
}
