java
package com.bluexiii.zenscaffold.service;

import com.bluexiii.zenscaffold.domain.TdBProduct;
import com.bluexiii.zenscaffold.exception.ResourceNotFoundException;
import com.bluexiii.zenscaffold.repository.TdBProductRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private TdBProductRepository tdBProductRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetProductInfo_existingProduct() {
        Long productId = 1L;
        TdBProduct expectedProduct = new TdBProduct();
        expectedProduct.setProductId(productId);
        expectedProduct.setProductName("Test Product");
        when(tdBProductRepository.findOne(productId)).thenReturn(expectedProduct);

        TdBProduct actualProduct = productService.getProductInfo(productId);

        assertEquals(expectedProduct, actualProduct);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testGetProductInfo_nonExistingProduct() {
        Long productId = 2L;
        when(tdBProductRepository.findOne(productId)).thenReturn(null);

        productService.getProductInfo(productId);
    }

    @Test
    public void testGetProductInfo_validProductId() {
        Long productId = 3L;
        TdBProduct expectedProduct = new TdBProduct();
        expectedProduct.setProductId(productId);
        expectedProduct.setProductName("Another Test Product");
        when(tdBProductRepository.findOne(productId)).thenReturn(expectedProduct);

        TdBProduct actualProduct = productService.getProductInfo(productId);

        assertEquals(expectedProduct, actualProduct);
    }
}