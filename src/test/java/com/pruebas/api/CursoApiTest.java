package com.pruebas.api;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;
import io.restassured.response.Response;

@DisplayName("Pregunta 2 - CursoApiTest con Rest Assured")
class CursoApiTest {

    private static final String BASE_URL = "http://localhost:8080";
    private static final String RUTA_CURSOS = "/api/cursos";

    @BeforeAll
    static void configurar() {
        System.out.println("Configurando Rest Assured contra " + BASE_URL);

        RestAssured.baseURI = BASE_URL;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @BeforeEach
    void setUp() {
        System.out.println("Ejecutando setUp");
    }

    @AfterEach
    void tearDown() {
        System.out.println("Ejecutando tearDown");
    }

    @Test
    @DisplayName("Escenario 1 - Consulta todos los cursos y valida que la lista no esté vacía")
    void listarCursos() {
        System.out.println("Escenario 1: listarCursos");

        Response response = given()
                .when()
                .get(RUTA_CURSOS);

        System.out.println("Respuesta: " + response.asString());

        response.then()
                .statusCode(200)
                .body("$", not(empty()));

        List<Object> cursos = response.jsonPath().getList("$");

        assertNotNull(cursos, "La lista de cursos no debe ser nula");
        assertFalse(cursos.isEmpty(), "La lista de cursos no debe estar vacía");
        assertTrue(cursos.size() >= 3, "Deben existir al menos los 3 cursos cargados por el DataLoader");
    }

    @Test
    @DisplayName("Escenario 2 - Consulta el curso 1 y valida nombre y precio")
    void buscarCursoPorId() {
        System.out.println("Escenario 2: buscarCursoPorId");

        Response response = given()
                .pathParam("id", 1)
                .when()
                .get(RUTA_CURSOS + "/{id}");

        System.out.println("Respuesta: " + response.asString());

        response.then()
                .statusCode(200)
                .body("nombre", equalTo("Pruebas de Software"));

        Double precio = response.jsonPath().getDouble("precio");

        assertNotNull(precio, "El precio del curso no debe ser nulo");
        assertTrue(precio > 0, "El precio del curso debe ser mayor que cero");
    }
}
