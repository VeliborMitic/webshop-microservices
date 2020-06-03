package product.service.web.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import product.service.domain.Category;
import product.service.domain.Product;
import product.service.domain.ProductInfo;
import product.service.services.ProductService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/products", produces = "application/json")
public class ProductController {

    private final RestTemplate restTemplate;

    private final ProductService productService;

    public ProductController(RestTemplate restTemplate, ProductService productService) {
        this.restTemplate = restTemplate;
        this.productService = productService;
    }

    @GetMapping
    public List<Product> findAll() {
        return productService.findAll()
                .stream()
                .map(e -> new ProductInfo(e.getId(), e.getName(), e.getCategoryId(), restTemplate.getForObject("http://CATEGORY-SERVICE/category/" + e.getCategoryId(), Category.class)))
                .collect(Collectors.toList());
    }

    @PostMapping
    public Product create(@RequestBody Product product) {
        Product created = productService.create(product);
        return new ProductInfo(created.getId(), created.getName(), created.getCategoryId(),
                restTemplate.getForObject("http://CATEGORY-SERVICE/category/" + created.getCategoryId(), Category.class)
        );
    }

    @PutMapping("/{id}")
    public Product update(@RequestBody Product product,
                          @PathVariable("id") Long id) {
        return productService.update(id, product);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        productService.delete(id);
    }

}
