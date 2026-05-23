package com.redko.lab2.service;

import com.redko.lab2.model.Book;
import com.redko.lab2.repository.BookRepository;
import com.redko.lab2.request.BookCreateRequest;
import com.redko.lab2.request.BookPageRequest;
import com.redko.lab2.request.BookUpdateRequest;
import com.redko.lab2.response.ApiResponse;
import com.redko.lab2.response.BaseMetaData;
import com.redko.lab2.response.PaginationMetaData;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
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
@Slf4j
@RequiredArgsConstructor
@Service
public class BookService {

    private final BookRepository bookRepository;

    private List<Book> books = new ArrayList<>(
            Arrays.asList(
                    new Book("name1", "00001", "description1"),
                    new Book("name2", "00002", "description2"),
                    new Book("name3", "00003", "description3")
            )
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

    //------------------------- 12 03 response impl ------------------------------
    public ApiResponse<BaseMetaData, Book> getByIdAsApiResponse(String id) {
        Book bookPersisted = bookRepository.findById(id).orElse(null);
        BaseMetaData baseMetaData = new BaseMetaData();
        if (bookPersisted != null) {
            ApiResponse<BaseMetaData, Book> response = new ApiResponse<>(baseMetaData, bookPersisted);
            return response;
        }

        return null;
    }

    public  ApiResponse<BaseMetaData, Book> getAllAsApiResponse() {
        return null;
    }

    public  ApiResponse<BaseMetaData, Book> updateAsApiResponse(Book book) {
        return null;
    }

    public ApiResponse<PaginationMetaData, Book> getBooksPage(BookPageRequest request) {
        int pageNumber = request.page();
        int pageSize = request.size();

        if (pageNumber < 0) throw new IllegalArgumentException("Page index must not be less than zero");
        if (pageSize < 1) throw new IllegalArgumentException("Page size must not be less than one");

        long totalElements = bookRepository.count();
        int totalPages = totalElements == 0 ? 0 : (int) Math.ceil((double) totalElements / pageSize);

        if (pageNumber >= totalPages && totalElements > 0) {
            log.warn("Out of range. Maximal page for the size is {}", totalPages);

            int lastPage = totalPages - 1;
            Pageable fallbackPageable = PageRequest.of(lastPage, pageSize, Sort.by(Sort.Direction.DESC, "id"));
            Page<Book> fallbackPage = bookRepository.findAll(fallbackPageable);

            PaginationMetaData metaData = PaginationMetaData.builder()
                    .code(404)
                    .number(lastPage)
                    .size(pageSize)
                    .totalElements(totalElements)
                    .totalPages(totalPages)
                    .isFirst(lastPage == 0)
                    .isLast(true)
                    .build();

            metaData.setSuccess(false);
            metaData.setErrorMessage("Maximal page for the size is " + totalPages);

            return new ApiResponse<>(metaData, fallbackPage.getContent());
        }

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "id"));
        Page<Book> page = bookRepository.findAll(pageable);

        PaginationMetaData metaData = PaginationMetaData.builder()
                .code(200)
                .number(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .isFirst(page.isFirst())
                .isLast(page.isLast())
                .build();

        metaData.setSuccess(true);

        if (page.isEmpty()) {
            if (page.getTotalElements() == 0) {
                metaData.setErrorMessage("Warning: The list is empty");
            } else {
                metaData.setErrorMessage("Warning: Page value is out of range");
            }
        }

        return new ApiResponse<>(metaData, page.getContent());
    }
}
