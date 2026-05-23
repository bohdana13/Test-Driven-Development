package com.redko.lab2;

/*
@author   User
@project   lab2
@class  ItemServicePagingTest
@version  1.0.0
@since 26.04.2026 - 21.53
*/

import com.redko.lab2.repository.BookRepository;
import com.redko.lab2.model.Book;
import com.redko.lab2.request.BookPageRequest;
import com.redko.lab2.response.ApiResponse;
import com.redko.lab2.response.PaginationMetaData;
import com.redko.lab2.service.BookService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class BookServicePagingTest {


    @Autowired
    private BookService  underTest;

    @Autowired
    private BookRepository bookRepository;

    List<Book> books = new ArrayList<>();

    @BeforeAll
    static void beforeAll() {
    }

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();

        List<Book> manyBooks = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            manyBooks.add(new Book("name" + i, "code" + i, "description" + i));
        }
        bookRepository.saveAll(manyBooks);
    }

    @AfterEach
    void tearsDown(){
    }

    @Test
    void whenHappyPathThenOk() {
        // given
        BookPageRequest request = new BookPageRequest(0, 5);
        // when
        ApiResponse<PaginationMetaData, Book> response = underTest.getBooksPage(request);
        //then
        assertNotNull(response);
        assertNotNull(response.getMeta());

        assertEquals(200, response.getMeta().getCode());
        assertTrue(response.getMeta().isSuccess());
        assertNull(response.getMeta().getErrorMessage());

        assertEquals(0, response.getMeta().getNumber());
        assertEquals(5, response.getMeta().getSize());
        assertEquals(30, response.getMeta().getTotalElements());
        assertEquals(6, response.getMeta().getTotalPages());
        assertTrue(response.getMeta().isFirst());
        assertFalse(response.getMeta().isLast());

        assertNotNull(response.getData());
        assertFalse(response.getData().isEmpty());
        assertEquals(5, response.getData().size());
        assertEquals("name29", response.getData().get(0).getName());
    }

    @Test
    void whenSizeIs_7_AndPageIs_4_ThenIsLast_TrueAndSizeEquals_2() {
        // given
        BookPageRequest request = new BookPageRequest(4, 7);

        // when
        ApiResponse<PaginationMetaData, Book> response = underTest.getBooksPage(request);

        // then
        assertNotNull(response);
        assertNotNull(response.getMeta());
        assertNotNull(response.getData());

        assertTrue(response.getMeta().isLast());
        assertEquals(2, response.getData().size());
        assertEquals(30, response.getMeta().getTotalElements());
        assertEquals(5, response.getMeta().getTotalPages());
    }

    @Test
    void whenTheListIsEmptyThenErrorMessageHasTheWarning() {
        // given
        bookRepository.deleteAll();
        BookPageRequest request = new BookPageRequest(0, 5);

        // when
        ApiResponse<PaginationMetaData, Book> response = underTest.getBooksPage(request);

        // then
        assertNotNull(response);
        assertNotNull(response.getMeta());

        assertNotNull(response.getMeta().getErrorMessage(), "Error message should not be null when list is empty");
        assertFalse(response.getMeta().getErrorMessage().isBlank(), "Error message should contain a warning");
    }

    @Test
    void whenTheListIsEmptyThenMetadataAndDataAreNotNull() {
        // given
        bookRepository.deleteAll();
        BookPageRequest request = new BookPageRequest(0, 5);

        // when
        ApiResponse<PaginationMetaData, Book> response = underTest.getBooksPage(request);

        // then
        assertNotNull(response, "Response object should not be null");
        assertNotNull(response.getMeta(), "Metadata should not be null");
        assertNotNull(response.getData(), "Data list should not be null, it should be an empty list");

        assertTrue(response.getData().isEmpty(), "Data list should be empty");
        assertEquals(0, response.getMeta().getTotalElements(), "Total elements should be 0");
    }

    @Test
    void whenPageValueIsOutOfRangeThenErrorMessageHasTheWarning() {
        // given
        BookPageRequest request = new BookPageRequest(10, 5);

        // when
        ApiResponse<PaginationMetaData, Book> response = underTest.getBooksPage(request);

        // then
        assertNotNull(response);
        assertNotNull(response.getMeta());
        assertNotNull(response.getData());

        assertTrue(response.getData().isEmpty(), "Data should be empty for out of range page");

        assertNotNull(response.getMeta().getErrorMessage(), "Error message should not be null when page is out of range");
    }


    @Test
    void whenPageSizeIsLargerThanTotalElements_ThenReturnAllElementsOnOnePage() {
        // given
        BookPageRequest request = new BookPageRequest(0, 100);

        // when
        ApiResponse<PaginationMetaData, Book> response = underTest.getBooksPage(request);

        // then
        assertNotNull(response);
        assertEquals(30, response.getData().size());
        assertEquals(1, response.getMeta().getTotalPages());
        assertTrue(response.getMeta().isFirst());
        assertTrue(response.getMeta().isLast());
    }

    @Test
    void whenRequestingMiddlePage_ThenIsFirstAndIsLastAreFalse() {
        // given
        BookPageRequest request = new BookPageRequest(2, 5);

        // when
        ApiResponse<PaginationMetaData, Book> response = underTest.getBooksPage(request);

        // then
        assertNotNull(response);
        assertFalse(response.getMeta().isFirst());
        assertFalse(response.getMeta().isLast());
        assertEquals(2, response.getMeta().getNumber());
    }

    @Test
    void whenPageSizeExactlyDividesTotalElements_ThenLastPageIsFull() {
        // given
        BookPageRequest request = new BookPageRequest(2, 10);

        // when
        ApiResponse<PaginationMetaData, Book> response = underTest.getBooksPage(request);

        // then
        assertNotNull(response);
        assertTrue(response.getMeta().isLast());
        assertEquals(10, response.getData().size());
    }

    @Test
    void whenDatabaseHasOnlyOneElement_ThenPaginationWorksCorrectly() {
        // given
        bookRepository.deleteAll();
        bookRepository.save(new Book("SingleBook", "001", "Desc"));
        BookPageRequest request = new BookPageRequest(0, 5);

        // when
        ApiResponse<PaginationMetaData, Book> response = underTest.getBooksPage(request);

        // then
        assertEquals(1, response.getMeta().getTotalElements());
        assertEquals(1, response.getMeta().getTotalPages());
        assertTrue(response.getMeta().isFirst());
        assertTrue(response.getMeta().isLast());
        assertEquals(1, response.getData().size());
        assertEquals("SingleBook", response.getData().get(0).getName());
    }

    @Test
    void whenPageSizeIsOne_ThenTotalPagesEqualsTotalElements() {
        // given
        BookPageRequest request = new BookPageRequest(0, 1);

        // when
        ApiResponse<PaginationMetaData, Book> response = underTest.getBooksPage(request);

        // then
        assertEquals(30, response.getMeta().getTotalElements());
        assertEquals(30, response.getMeta().getTotalPages());
        assertEquals(1, response.getData().size());
    }

    @Test
    void whenPageNumberIsNegative_ThenThrowIllegalArgumentException() {
        // given
        BookPageRequest request = new BookPageRequest(-1, 5);

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            underTest.getBooksPage(request);
        });

        assertTrue(exception.getMessage().contains("Page index must not be less than zero"));
    }

    @Test
    void whenPageSizeIsZero_ThenThrowIllegalArgumentException() {
        // given
        BookPageRequest request = new BookPageRequest(0, 0);

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            underTest.getBooksPage(request);
        });

        assertTrue(exception.getMessage().contains("Page size must not be less than one"));
    }

    @Test
    void whenPageSizeIsNegative_ThenThrowIllegalArgumentException() {
        // given
        BookPageRequest request = new BookPageRequest(0, -5);

        // when & then
        assertThrows(IllegalArgumentException.class, () -> {
            underTest.getBooksPage(request);
        });
    }


    @Test
    void whenFetchingFirstPage_ThenReturnCorrectDataMapping() {
        bookRepository.deleteAll();
        Book savedBook = bookRepository.save(new Book("ExactName", "ExactCode", "ExactDesc"));
        BookPageRequest request = new BookPageRequest(0, 5);

        // when
        ApiResponse<PaginationMetaData, Book> response = underTest.getBooksPage(request);

        // then
        Book retrievedBook = response.getData().get(0);
        assertEquals(savedBook.getId(), retrievedBook.getId());
        assertEquals("ExactName", retrievedBook.getName());
        assertEquals("ExactCode", retrievedBook.getCode());
        assertEquals("ExactDesc", retrievedBook.getDescription());
    }

    @Test
    void whenDeletingItem_ThenTotalElementsDecreases() {
        // given
        List<Book> allBooks = bookRepository.findAll();
        String idToDelete = allBooks.get(0).getId();

        underTest.delById(idToDelete);

        // when
        BookPageRequest request = new BookPageRequest(0, 5);
        ApiResponse<PaginationMetaData, Book> response = underTest.getBooksPage(request);

        // then
        assertEquals(29, response.getMeta().getTotalElements());
    }

    @Test
    void whenAddingNewItem_ThenTotalElementsIncreases() {
        // given
        underTest.create(new Book("New Book", "NEW-001", "New Desc"));

        // when
        BookPageRequest request = new BookPageRequest(0, 5);
        ApiResponse<PaginationMetaData, Book> response = underTest.getBooksPage(request);

        // then
        assertEquals(31, response.getMeta().getTotalElements());
    }

    @Test
    void whenFetchingData_ThenDataIsSortedByIdDescending() {
        // given
        bookRepository.deleteAll();
        Book book1 = bookRepository.save(new Book("Book A", "Code A", "Desc A"));
        Book book2 = bookRepository.save(new Book("Book B", "Code B", "Desc B"));
        Book book3 = bookRepository.save(new Book("Book C", "Code C", "Desc C"));

        BookPageRequest request = new BookPageRequest(0, 5);

        // when
        ApiResponse<PaginationMetaData, Book> response = underTest.getBooksPage(request);

        // then
        List<Book> content = response.getData();
        assertEquals(3, content.size());

        assertEquals(book3.getId(), content.get(0).getId());
        assertEquals(book2.getId(), content.get(1).getId());
        assertEquals(book1.getId(), content.get(2).getId());
    }


    @Test
    void whenHappyPath_ThenMetaHasSuccessCodeAndFlags() {
        // given
        BookPageRequest request = new BookPageRequest(0, 5);

        // when
        ApiResponse<PaginationMetaData, Book> response = underTest.getBooksPage(request);

        // then
        assertNotNull(response.getMeta());
        assertEquals(200, response.getMeta().getCode());
        assertTrue(response.getMeta().isSuccess());
        assertNull(response.getMeta().getErrorMessage());
    }

    @Test
    void whenExtremelyLargePageRequested_ThenMetaValuesAreCorrectlyCalculated() {
        // given
        BookPageRequest request = new BookPageRequest(999, 10);

        // when
        ApiResponse<PaginationMetaData, Book> response = underTest.getBooksPage(request);

        // then
        assertNotNull(response);
        assertEquals(999, response.getMeta().getNumber());
        assertEquals(10, response.getMeta().getSize());
        assertEquals(30, response.getMeta().getTotalElements());
        assertEquals(3, response.getMeta().getTotalPages());
        assertFalse(response.getMeta().isFirst());
    }

    @Test
    void whenRequestingEmptyPage_ThenDataListIsNotNullButEmpty() {
        // given
        BookPageRequest request = new BookPageRequest(5, 10);

        // when
        ApiResponse<PaginationMetaData, Book> response = underTest.getBooksPage(request);

        // then
        assertNotNull(response.getData());
        assertTrue(response.getData().isEmpty(), "Лист має бути порожнім, а не null");
        assertEquals(0, response.getData().size());
    }

    @Test
    void whenRequestIsIncorrectThenGiveTheLastPage() {
        // given
        BookPageRequest request = new BookPageRequest(9, 4);

        // when
        ApiResponse<PaginationMetaData, Book> response = underTest.getBooksPage(request);

        // then
        assertNotNull(response);
        assertNotNull(response.getMeta());

        assertEquals(404, response.getMeta().getCode());
        assertFalse(response.getMeta().isSuccess());
        assertNotNull(response.getMeta().getErrorMessage());
        assertTrue(response.getMeta().getErrorMessage()
                .contains("Maximal page for the size is " + response.getMeta().getTotalPages()));

        assertEquals(7, response.getMeta().getNumber());
        assertEquals(4, response.getMeta().getSize());
        assertEquals(30, response.getMeta().getTotalElements());
        assertEquals(8, response.getMeta().getTotalPages());

        assertFalse(response.getMeta().isFirst());
        assertTrue(response.getMeta().isLast());

        assertNotNull(response.getData());
        assertFalse(response.getData().isEmpty());

        assertEquals(2, response.getData().size());

        assertEquals("name0", response.getData().get(1).getName());
    }

    @Test
    void testLogging(CapturedOutput output){

        assertTrue(output.toString().contains("Out of range"));
    }

}
