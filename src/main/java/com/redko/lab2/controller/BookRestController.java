package com.redko.lab2.controller;

import com.redko.lab2.model.Book;
import com.redko.lab2.request.BookCreateRequest;
import com.redko.lab2.request.BookUpdateRequest;
import com.redko.lab2.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
  @author User
  @project lab7
  @class BookRestController
  @version 1.0.0
  @since 24.04.2025 - 22.59
*/
@RestController
@RequestMapping("api/v1/books/")
@RequiredArgsConstructor
public class BookRestController {

    private final BookService bookService;

    @GetMapping
    public List<Book> showAll() {
        return bookService.getAll();
    }

    @GetMapping("{id}")
    public Book showOneById(@PathVariable String id) {
        return bookService.getById(id);
    }

    @PostMapping
    public Book insert(@RequestBody Book book) {
        return bookService.create(book);
    }
    //============== request =====================
    @PostMapping("/dto")
    public Book insert(@RequestBody BookCreateRequest request) {
        return bookService.create(request);
    }
    @PutMapping
    public Book edit(@RequestBody Book book) {
        return bookService.update(book);
    }
    //============== request =====================
    @PutMapping("/dto")
    public Book edit(@RequestBody BookUpdateRequest request) {
        return bookService.update(request);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable String id) {
         bookService.delById(id);
    }
}
