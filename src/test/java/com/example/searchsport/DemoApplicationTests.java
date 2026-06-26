package com.example.searchsport;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mockStatic;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

class DemoApplicationTest {

    @Test
    void main_debeIniciarAplicacionSinErrores() {
        try (MockedStatic<SpringApplication> springApplicationMock = mockStatic(SpringApplication.class)) {
            assertDoesNotThrow(() -> DemoApplication.main(new String[]{}));

            springApplicationMock.verify(() ->
                    SpringApplication.run(DemoApplication.class, new String[]{})
            );
        }
    }
}