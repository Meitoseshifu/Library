package com.basic.library.service;

import com.basic.library.domain.Book;
import com.basic.library.domain.Image;
import com.basic.library.repository.BookRepository;
import com.basic.library.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final ImageRepository imageRepository;

    public List<Book> getAllBooksByLogin(String login) {
        return bookRepository.findAll();
    }

    public Image getImage(Long id) {
        return imageRepository.findById(id).orElseThrow();
    }

    public void save(Book book) {
        bookRepository.save(book);
    }

    public void save(Book book, MultipartFile file) {
        Image image = toImage(file, bookRepository.save(book));
        imageRepository.save(image);
    }

    public Book findByTitle(String title) {
        return bookRepository.findByTitle(title).orElseThrow(IllegalArgumentException::new);
    }

    @Transactional
    public void edit(Book book, MultipartFile file) {
        if (!file.isEmpty()) {
            Image image = toImage(file, book);
            image.setId(book.getId());
            imageRepository.save(image);
        }
        save(book);
    }

    @Transactional
    public void delete(Long id) {
        imageRepository.deleteById(id);
        bookRepository.deleteById(id);
    }

    private Image toImage(MultipartFile file, Book book) {
        try {
            var image = new Image();
            image.setFilename(file.getOriginalFilename());
            image.setMimeType(file.getContentType());
            image.setData(file.getBytes());
            image.setBook(book);
            return image;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
