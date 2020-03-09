package com.example.demo.es;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DocumentCrudTest {
    @Autowired
    private DocumentCrud documentCrud;

    @Test
    void getIndice() {
        documentCrud.getIndice();
    }
}