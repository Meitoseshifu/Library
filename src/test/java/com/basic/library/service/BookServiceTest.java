package com.basic.library.service;

import com.basic.library.domain.Book;
import com.basic.library.domain.Image;
import com.basic.library.repository.BookRepository;
import com.basic.library.repository.ImageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.mockito.Mockito.when;

class BookServiceTest {
    @Mock
    private BookRepository bookRepository;

    @Mock
    private ImageRepository imageRepository;

    @InjectMocks
    private BookService bookService;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this).close();
    }

    @Test
    void getAllBooks() {
        Book book = new Book();
        when(bookRepository.findAll()).thenReturn(List.of(book));

        List<Book> books = bookService.getAllBooksByLogin("");

        assertThat(books).hasSize(1).contains(book);
    }

    @Test
    void getImage() {
        Image image = new Image();
        when(imageRepository.findById(anyLong())).thenReturn(Optional.of(image));

        Image foundImage = bookService.getImage(1L);

        assertThat(foundImage).isEqualTo(image);
    }

    @Test
    void getImage_notFound() {
        when(imageRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.getImage(1L)).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void saveBook() {
        Book book = new Book();

        bookService.save(book);

        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void saveBookWithFile() throws IOException {
        Book book = new Book();
        book.setId(1L);
        MockMultipartFile file = new MockMultipartFile("file", "orig.png", "image/png", "image data".getBytes());

        when(bookRepository.save(any(Book.class))).thenReturn(book);

        bookService.save(book, file);

        verify(bookRepository, times(1)).save(book);
        verify(imageRepository, times(1)).save(any(Image.class));
    }

    @Test
    void findByTitle() {
        Book book = new Book();
        when(bookRepository.findByTitle(anyString())).thenReturn(Optional.of(book));

        Book foundBook = bookService.findByTitle("title");

        assertThat(foundBook).isEqualTo(book);
    }

    @Test
    void findByTitle_notFound() {
        when(bookRepository.findByTitle(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.findByTitle("title")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void editBook() throws IOException {
        Book book = new Book();
        MockMultipartFile file = new MockMultipartFile("file", "orig.png", "image/png", "image data".getBytes());

        when(bookRepository.save(any(Book.class))).thenReturn(book);

        bookService.edit(book, file);

        verify(bookRepository, times(1)).save(book);
        verify(imageRepository, times(1)).save(any(Image.class));
    }

    @Test
    void deleteBook() {
        bookService.delete(1L);

        verify(imageRepository, times(1)).deleteById(1L);
        verify(bookRepository, times(1)).deleteById(1L);
    }
}