package com.app.productservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.app.productservice.model.Product;
import com.app.productservice.repository.ProductRepository;

public class ProductServiceTest {

	@Test
    void updateStockShouldReduceAvailableStock() {

        // Arrange: preparar datos y simular el repositorio
        ProductRepository repository = mock(ProductRepository.class);
        ProductService service = new ProductService(repository);

        Product product = new Product();
        product.setStock(10);

        when(repository.findById(1L))
                .thenReturn(Optional.of(product));

        when(repository.save(product))
                .thenReturn(product);

        // Act: ejecutar la operación
        Product result = service.updateStock(1L, 3);

        // Assert: comprobar el resultado
        assertEquals(7, result.getStock());

        verify(repository).save(product);
    }
}
