package com.redko.lab2;


import com.redko.lab2.model.Book;
import com.redko.lab2.request.BookCreateRequest;
import com.redko.lab2.request.BookUpdateRequest;
import com.redko.lab2.response.ApiResponse;
import com.redko.lab2.response.BaseMetaData;
import com.redko.lab2.service.BookService;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BookServiceTest {


    @Autowired
    private BookService underTest;

    List<Book> books = new ArrayList<>();

    @BeforeAll
    static void beforeAll() {
    }

    @BeforeEach
    void setUp() {
    }
   @AfterEach
    void tearsDown(){
    }

    @Test
    void whenCreateBookThenBooksCountIncreasesByOne() {
        // given
        int before = underTest.getAll().size();
        String uniqueCode = "code_" + System.nanoTime();
        BookCreateRequest request = new BookCreateRequest("Count Book", uniqueCode, "Count description");

        // when
        Book created = underTest.create(request);
        int after = underTest.getAll().size();

        // then
        assertNotNull(created);
        assertEquals(before + 1, after);
    }


    @Test
    void whenBookIsPresentThenReturnBookById() {
        // given
        List<Book> allBooks = underTest.getAll();
        assertFalse(allBooks.isEmpty());

        Book expectedBook = allBooks.stream()
                .filter(book -> book.getId() != null)
                .findFirst()
                .orElse(null);

        assertNotNull(expectedBook);

        String id = expectedBook.getId();

        // when
        Book actualBook = underTest.getById(id);

        // then
        assertNotNull(actualBook);
        assertEquals(expectedBook.getId(), actualBook.getId());
        assertEquals(expectedBook.getName(), actualBook.getName());
        assertEquals(expectedBook.getCode(), actualBook.getCode());
        assertEquals(expectedBook.getDescription(), actualBook.getDescription());
    }

    @Test
    void whenBookIsPresentThenReturnAsOkApiResponse() {
        // given
        List<Book> allBooks = underTest.getAll();
        assertFalse(allBooks.isEmpty());

        Book expectedBook = allBooks.get(0);
        String id = expectedBook.getId();

        // when
        ApiResponse<BaseMetaData, Book> response = underTest.getByIdAsApiResponse(id);

        // then
        assertNotNull(response);
        assertNotNull(response.getMeta());
        assertNotNull(response.getData());
        assertFalse(response.getData().isEmpty());

        Book actualBook = response.getData().get(0);

        assertNotNull(actualBook);
        assertEquals(expectedBook.getId(), actualBook.getId());
        assertEquals(expectedBook.getName(), actualBook.getName());
        assertEquals(expectedBook.getCode(), actualBook.getCode());
        assertEquals(expectedBook.getDescription(), actualBook.getDescription());

        assertTrue(response.getMeta().isSuccess());
        assertEquals(200, response.getMeta().getCode());
        assertNull(response.getMeta().getErrorMessage());
    }

    @Test
    void whenBookIsNotPresentThenReturnNull() {
        // given
        String id = "wrong_id_123";

        // when
        Book book = underTest.getById(id);

        // then
        assertNull(book);
    }

    @Test
    void whenBookIsNotPresentThenReturnNullApiResponse() {
        // given
        String id = "wrong_id_123";

        // when
        ApiResponse<BaseMetaData, Book> response = underTest.getByIdAsApiResponse(id);

        // then
        assertNull(response);
    }

    @Test
    void whenGetAllBooksThenListIsNotNull() {
        // given

        // when
        List<Book> result = underTest.getAll();

        // then
        assertNotNull(result);
    }

    @Test
    void whenGetAllBooksThenListIsNotEmpty() {
        // given

        // when
        List<Book> result = underTest.getAll();

        // then
        assertFalse(result.isEmpty());
    }

    @Test
    void whenBookWithWrongIdThenReturnNull() {
        // given
        String wrongId = "wrong_id_123";

        // when
        Book result = underTest.getById(wrongId);

        // then
        assertNull(result);
    }

    @Test
    void whenBookWithWrongIdThenApiResponseIsNull() {
        // given
        String wrongId = "wrong_id_123";

        // when
        ApiResponse<BaseMetaData, Book> response = underTest.getByIdAsApiResponse(wrongId);

        // then
        assertNull(response);
    }

    @Test
    void whenCreateBookDirectlyThenReturnCreatedBook() {
        // given
        Book book = new Book("Test Book", "11111", "Test description");

        // when
        Book created = underTest.create(book);

        // then
        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals("Test Book", created.getName());
        assertEquals("11111", created.getCode());
        assertEquals("Test description", created.getDescription());
    }

    @Test
    void whenCreateBookDirectlyThenBookAppearsInDatabase() {
        // given
        Book book = new Book("Direct Create", "22222", "Created directly");

        // when
        Book created = underTest.create(book);
        Book persisted = underTest.getById(created.getId());

        // then
        assertNotNull(persisted);
        assertEquals(created.getId(), persisted.getId());
        assertEquals("Direct Create", persisted.getName());
    }

    @Test
    void whenCreateBookByRequestWithUniqueCodeThenReturnCreatedBook() {
        // given
        String uniqueCode = "code_" + System.nanoTime();
        BookCreateRequest request = new BookCreateRequest("Request Book", uniqueCode, "Request description");

        // when
        Book created = underTest.create(request);

        // then
        assertNotNull(created);
        assertEquals("Request Book", created.getName());
        assertEquals(uniqueCode, created.getCode());
        assertEquals("Request description", created.getDescription());
    }

    @Test
    void whenCreateBookByRequestThenCreateDateIsSet() {
        // given
        String uniqueCode = "code_" + System.nanoTime();
        BookCreateRequest request = new BookCreateRequest("Date Book", uniqueCode, "Date description");

        // when
        Book created = underTest.create(request);

        // then
        assertNotNull(created);
        assertNotNull(created.getCreateDate());
    }

    @Test
    void whenCreateBookByRequestThenUpdateDateIsInitialized() {
        // given
        String uniqueCode = "code_" + System.nanoTime();
        BookCreateRequest request = new BookCreateRequest("Update List Book", uniqueCode, "Update list description");

        // when
        Book created = underTest.create(request);

        // then
        assertNotNull(created);
        assertNotNull(created.getUpdateDate());
        assertTrue(created.getUpdateDate().isEmpty());
    }

    @Test
    void whenCreateBookByRequestWithDuplicateCodeThenReturnNull() {
        // given
        Book existingBook = underTest.getAll().stream()
                .filter(book -> book.getCode() != null)
                .findFirst()
                .orElse(null);

        BookCreateRequest request = new BookCreateRequest(
                "Duplicate Book",
                existingBook.getCode(),
                "desc"
        );

        // when
        Book created = underTest.create(request);

        // then
        assertNull(created);
    }

    @Test
    void whenUpdateExistingBookThenReturnUpdatedBook() {
        // given
        Book existingBook = underTest.getAll().stream()
                .filter(book -> book.getId() != null)
                .findFirst()
                .orElse(null);

        BookUpdateRequest request = new BookUpdateRequest(
                existingBook.getId(),
                "Updated Name",
                "99991",
                "Updated Description"
        );

        // when
        Book updated = underTest.update(request);

        // then
        assertNotNull(updated);
        assertEquals("Updated Name", updated.getName());
        assertEquals("99991", updated.getCode());
    }

    @Test
    void whenUpdateExistingBookThenCreateDateIsPreserved() {
        // given
        Book existingBook = underTest.getAll().stream()
                .filter(book -> book.getId() != null)
                .findFirst()
                .orElse(null);

        LocalDateTime originalCreateDate = existingBook.getCreateDate();

        BookUpdateRequest request = new BookUpdateRequest(
                existingBook.getId(),
                "Name",
                "99992",
                "Desc"
        );

        // when
        Book updated = underTest.update(request);

        // then
        assertEquals(originalCreateDate, updated.getCreateDate());
    }

    @Test
    void whenUpdateExistingBookThenUpdateDateListGetsNewEntry() {
        // given
        Book created = underTest.create(
                new BookCreateRequest("Book", "66666", "Before")
        );

        BookUpdateRequest request = new BookUpdateRequest(
                created.getId(),
                "After",
                "66667",
                "After"
        );

        // when
        Book updated = underTest.update(request);

        // then
        assertEquals(1, updated.getUpdateDate().size());
    }

    @Test
    void whenUpdateBookWithWrongIdThenReturnNull() {
        // given
        BookUpdateRequest request = new BookUpdateRequest(
                "wrong_id",
                "Name",
                "77777",
                "Desc"
        );

        // when
        Book updated = underTest.update(request);

        // then
        assertNull(updated);
    }

    @Test
    void whenDeleteBookByIdThenBookIsRemoved() {
        // given
        Book created = underTest.create(
                new Book("Delete Book", "88888", "Delete description")
        );

        // when
        underTest.delById(created.getId());
        Book deleted = underTest.getById(created.getId());

        // then
        assertNull(deleted);
    }

}
