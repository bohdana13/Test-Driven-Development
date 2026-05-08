package com.redko.lab2;

/*
@author   User
@project   lab2
@class  LoggingTest
@version  1.0.0
@since 08.05.2026 - 10.40
*/

import com.redko.lab2.model.Book;
import com.redko.lab2.request.BookCreateRequest;
import com.redko.lab2.request.BookPageRequest;
import com.redko.lab2.request.BookUpdateRequest;
import com.redko.lab2.response.ApiResponse;
import com.redko.lab2.response.BaseMetaData;
import com.redko.lab2.response.PaginationMetaData;
import com.redko.lab2.service.BookService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;



@ExtendWith(OutputCaptureExtension.class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LoggingTest {

    @Autowired
    private BookService underTest;

    private String sampleBookId;

    @BeforeAll
    void setup() {
        List<Book> books = underTest.getAll();
        if (!books.isEmpty()) {
            sampleBookId = books.get(0).getId();
        }
    }


    @Test
    void testLoggingOutputBeforeMethodGetById(CapturedOutput output) {
        // given & when
        Book book = underTest.getById(sampleBookId);

        // then
        assertNotNull(book);
        assertTrue(output.toString().contains("Entering method: BookService.getById"));
        assertTrue(output.toString().contains(sampleBookId));
    }

    @Test
    void testLoggingOutputBeforeMethodGetBooksPage(CapturedOutput output) {
        // given
        BookPageRequest request = new BookPageRequest(0, 5);

        // when
        ApiResponse<PaginationMetaData, Book> page = underTest.getBooksPage(request);

        // then
        assertNotNull(page);
        assertTrue(output.toString().contains("Entering method: BookService.getBooksPage"));
        assertTrue(output.toString().contains("0"));
        assertTrue(output.toString().contains("5"));
    }

    @Test
    void testLoggingOutputAfterMethodGetById(CapturedOutput output) {
        Book book = underTest.getById(sampleBookId);
        assertNotNull(book);
        assertFalse(output.toString().contains("completed successfully"));
    }


    @Test
    void testLoggingOutputGetAll(CapturedOutput output) {
        underTest.getAll();
        assertTrue(output.toString().contains("Entering method: BookService.getAll"));
    }

    @Test
    void testLoggingOutputCreateBook(CapturedOutput output) {
        Book newBook = new Book("New Name", "99999", "New Desc");
        underTest.create(newBook);

        assertTrue(output.toString().contains("Entering method: BookService.create"));
        assertTrue(output.toString().contains("99999"));
    }

    @Test
    void testLoggingOutputCreateBookRequest(CapturedOutput output) {
        BookCreateRequest req = new BookCreateRequest("Req Name", "88888", "Req Desc");
        underTest.create(req);

        assertTrue(output.toString().contains("Entering method: BookService.create"));
        assertTrue(output.toString().contains("88888"));
    }

    @Test
    void testLoggingOutputUpdateBook(CapturedOutput output) {
        Book book = underTest.getById(sampleBookId);
        book.setDescription("Updated description");
        underTest.update(book);

        assertTrue(output.toString().contains("Entering method: BookService.update"));
        assertTrue(output.toString().contains("Updated description"));
    }

    @Test
    void testLoggingOutputUpdateBookRequest(CapturedOutput output) {
        BookUpdateRequest req = new BookUpdateRequest(sampleBookId, "Upd Name", "77777", "Upd Desc");
        underTest.update(req);

        assertTrue(output.toString().contains("Entering method: BookService.update"));
        assertTrue(output.toString().contains("77777"));
    }

    @Test
    void testLoggingOutputDelById(CapturedOutput output) {
        Book bookToDel = underTest.create(new Book("ToDelete", "DEL01", "Desc"));
        String idToDelete = bookToDel.getId();

        underTest.delById(idToDelete);

        assertTrue(output.toString().contains("Entering method: BookService.delById"));
        assertTrue(output.toString().contains(idToDelete));
    }

    @Test
    void testLoggingOutputGetByIdAsApiResponse(CapturedOutput output) {
        underTest.getByIdAsApiResponse(sampleBookId);
        assertTrue(output.toString().contains("Entering method: BookService.getByIdAsApiResponse"));
        assertTrue(output.toString().contains(sampleBookId));
    }

    @Test
    void testLoggingOutputGetAllAsApiResponse(CapturedOutput output) {
        underTest.getAllAsApiResponse();
        assertTrue(output.toString().contains("Entering method: BookService.getAllAsApiResponse"));
    }

    @Test
    void testLoggingOutputUpdateAsApiResponse(CapturedOutput output) {
        Book book = new Book("Temp", "TMP", "Tmp Desc");
        underTest.updateAsApiResponse(book);
        assertTrue(output.toString().contains("Entering method: BookService.updateAsApiResponse"));
    }

    @Test
    void testLoggingOutputCreateDuplicateBookRequest(CapturedOutput output) {
        underTest.create(new BookCreateRequest("Orig", "DUP01", "Orig"));
        underTest.create(new BookCreateRequest("Dup", "DUP01", "Dup"));

        assertTrue(output.toString().contains("Entering method: BookService.create"));
        assertTrue(output.toString().contains("DUP01"));
    }

    @Test
    void testLoggingOutputUpdateBookRequestNotFound(CapturedOutput output) {
        BookUpdateRequest req = new BookUpdateRequest("invalid_id", "Upd Name", "66666", "Upd Desc");
        underTest.update(req); // поверне null

        assertTrue(output.toString().contains("Entering method: BookService.update"));
        assertTrue(output.toString().contains("invalid_id"));
    }

    @Test
    void testLoggingOutputGetBooksPageOutOfRange(CapturedOutput output) {
        BookPageRequest request = new BookPageRequest(1000, 5);
        underTest.getBooksPage(request);

        assertTrue(output.toString().contains("Entering method: BookService.getBooksPage"));
        assertTrue(output.toString().contains("1000"));
    }

    @Test
    void testLoggingOutputGetByIdNotFound(CapturedOutput output) {
        underTest.getById("non_existent_id");

        assertTrue(output.toString().contains("Entering method: BookService.getById"));
        assertTrue(output.toString().contains("non_existent_id"));
    }

    @Test
    void testLoggingOutputGetByIdWithNullId(CapturedOutput output) {

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            underTest.getById(null);
        });

        assertTrue(output.toString().contains("Entering method: BookService.getById"));
        assertTrue(output.toString().contains("[null]"));
    }

    @Test
    void testLoggingOutputCreateWithNullBook(CapturedOutput output) {
        try {
            underTest.create((Book) null);
        } catch (Exception e) {
        }

        assertTrue(output.toString().contains("Entering method: BookService.create"));
        assertTrue(output.toString().contains("[null]"));
    }

    @Test
    void testLoggingOutputUpdateBookNonExistent(CapturedOutput output) {
        Book fakeBook = new Book("fake_id_999", "Fake Book", "11111", "Fake Desc");
        underTest.update(fakeBook);

        assertTrue(output.toString().contains("Entering method: BookService.update"));
        assertTrue(output.toString().contains("fake_id_999"));
    }

    @Test
    void testLoggingOutputGetByIdAsApiResponseNotFound(CapturedOutput output) {
        ApiResponse<BaseMetaData, Book> response = underTest.getByIdAsApiResponse("missing_api_id_404");

        assertNull(response);
        assertTrue(output.toString().contains("Entering method: BookService.getByIdAsApiResponse"));
        assertTrue(output.toString().contains("missing_api_id_404"));
    }

    @Test
    void testLoggingOutputDeleteWithNullId(CapturedOutput output) {
        try {
            underTest.delById(null);
        } catch (IllegalArgumentException e) {
        }

        assertTrue(output.toString().contains("Entering method: BookService.delById"));
        assertTrue(output.toString().contains("[null]"));
    }
}