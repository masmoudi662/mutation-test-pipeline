java
package pl.finsys.example.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.finsys.example.domain.Book;
import pl.finsys.example.repository.BookRepository;
import pl.finsys.example.service.exception.BookAlreadyExistsException;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.junit.Assert.assertEquals;

public class BookServiceImplTest {

    @InjectMocks
    private BookServiceImpl bookService;

    @Mock
    private BookRepository repository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test(expected = BookAlreadyExistsException.class)
    public void testSaveBook_alreadyExists() {
        Book book = new Book();
        book.setId(1L);

        when(repository.findOne(1L)).thenReturn(book);

        bookService.saveBook(book);
    }

    @Test
    public void testSaveBook_success() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");

        when(repository.findOne(1L)).thenReturn(null);
        when(repository.save(any(Book.class))).thenReturn(book);

        Book savedBook = bookService.saveBook(book);

        assertEquals(book, savedBook);
    }
}