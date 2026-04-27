java
package top.maplefix.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import top.maplefix.entity.Book;
import top.maplefix.service.IMongoDbService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MongoDbControllerTest {

    @InjectMocks
    private MongoDbController mongoDbController;

    @Mock
    private IMongoDbService mongoDbService;

    @Test
    void findByLikes() {
        String search = "test";
        List<Book> expectedBooks = new ArrayList<>();
        Book book1 = new Book();
        book1.setId("1");
        book1.setName("Test Book 1");
        expectedBooks.add(book1);
        when(mongoDbService.findByLikes(search)).thenReturn(expectedBooks);
        List<Book> actualBooks = mongoDbController.findByLikes(search);
        assertEquals(expectedBooks.size(), actualBooks.size());
        assertEquals(expectedBooks.get(0).getName(), actualBooks.get(0).getName());
    }
}