package com.redko.lab2.service;

import com.redko.lab2.model.Book;
import com.redko.lab2.repository.BookRepository;
import com.redko.lab2.request.BookCreateRequest;
import com.redko.lab2.request.BookUpdateRequest;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
  @author User
  @project lab7
  @class BookService
  @version 1.0.0
  @since 24.04.2025 - 22.25
*/
///  check CI pipeline with PR
/// attempt 2

@RequiredArgsConstructor
@Service
public class BookService {

    private final BookRepository bookRepository;

    private List<Book> books = new ArrayList<>(
            Arrays.asList(new Book("name","00001","description1"),
                    new Book("2","name2","00002","description2"),
            new Book("3","namwe3","00003","description3"))
    );

    @PostConstruct
    void init() {
        if (bookRepository.count() == 0) {
            bookRepository.saveAll(books);
        }
    }

    public List<Book> getAll(){
        return bookRepository.findAll();
    }

    public Book getById(String id) {
        return bookRepository.findById(id).orElse(null);
    }

    public Book create(Book book) {
        return bookRepository.save(book);
    }
    public Book create(BookCreateRequest request) {
       if (bookRepository.existsByCode(request.code())){
           return null;
       }

        Book book = mapToBook(request);
        book.setCreateDate(LocalDateTime.now());
        book.setUpdateDate(new ArrayList<LocalDateTime>());
        return bookRepository.save(book);
    }
    public Book update(Book book) {
        return bookRepository.save(book);
    }


    public void delById(String id) {
         bookRepository.deleteById(id);
    }
    private Book mapToBook(BookCreateRequest request) {
        Book book = new Book(request.name(), request.code(), request.description());
        return book;
    }

    public Book update(BookUpdateRequest request) {
        Book bookPersisted = bookRepository.findById(request.id()).orElse(null);
        if (bookPersisted != null) {
            List<LocalDateTime> updateDates = bookPersisted.getUpdateDate();
            if (updateDates == null) {
                updateDates = new ArrayList<>();
            }
            updateDates.add(LocalDateTime.now());

            Book bookToUpdate = Book.builder()
                    .id(request.id())
                    .name(request.name())
                    .code(request.code())
                    .description(request.description())
                    .createDate(bookPersisted.getCreateDate())
                    .updateDate(updateDates)
                    .build();
            return bookRepository.save(bookToUpdate);
        }
        return null;
    }

}
