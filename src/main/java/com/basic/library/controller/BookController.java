package com.basic.library.controller;

import com.basic.library.domain.Book;
import com.basic.library.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    public static final String USER_LOGIN = "userLogin";
    private final BookService bookService;

    @GetMapping
    public String getBookPage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        model.addAttribute(USER_LOGIN, username);

        List<Book> books = bookService.getAllBooksByLogin(username);
        model.addAttribute("userBooks", books);
        return "book_page";
    }

    @GetMapping("/create")
    public String getCreateBookPage(Model model) {
        model.addAttribute("newBook", new Book());
        return "create_book_page";
    }

    @PostMapping("/createBook")
    public String createBook(@ModelAttribute Book book,
                             @RequestPart("image") MultipartFile file) {
        bookService.save(book, file);
        return "redirect:/books";
    }

    @GetMapping("/edit/{title}")
    public String getEditBookPage(Model model, @PathVariable String title) {
        Book byTitle = bookService.findByTitle(title);
        model.addAttribute("bookToEdit", byTitle);
        return "edit_book_page";
    }

    @PostMapping("/editBook")
    public String editBook(@ModelAttribute Book book,
                           @RequestParam("imageFile") MultipartFile imageFile) {
        bookService.edit(book, imageFile);
        return "redirect:/books";
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String delete(@PathVariable Long id) {
        bookService.delete(id);
        return "redirect:/books";
    }
}
