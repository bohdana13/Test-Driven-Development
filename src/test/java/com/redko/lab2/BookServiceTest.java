package com.redko.lab2;


import com.redko.lab2.model.Book;
import com.redko.lab2.service.BookService;
import org.junit.jupiter.api.*;

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
    void whenGetAllBooksListThenSizeIs30() {
        int size = underTest.getAll().size();
        assertEquals(30, size);
    }
}
