package com.pruebas.bdd;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

// capa de interaccion: sabe como llamar al endpoint de inscripciones

public class InscripcionAPI {

    public static final String BASE_URL = "http://localhost:8080";
    public static final String RUTA_INSCRIPCIONES = "/api/inscripciones";

    public static void configurar() {
        RestAssured.baseURI = BASE_URL;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    public Response inscribir(String codigoEstudiante, Long cursoId) {
        String cuerpo = String.format(
                "{\"codigoEstudiante\":\"%s\",\"cursoId\":%d}", codigoEstudiante, cursoId);

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(cuerpo)
                .when()
                .post(RUTA_INSCRIPCIONES);

        System.out.println("POST " + RUTA_INSCRIPCIONES + " -> " + cuerpo);
        System.out.println("Respuesta [" + response.statusCode() + "]: " + resumen(response));

        return response;
    }

    // evita imprimir el trace completo del error de Spring

    private String resumen(Response response) {
        StringBuilder texto = new StringBuilder("{");

        if (response.jsonPath().get("id") != null) {
            texto.append("\"id\":").append(response.jsonPath().getString("id")).append(", ");
            texto.append("\"codigoEstudiante\":\"")
                    .append(response.jsonPath().getString("codigoEstudiante"))
                    .append("\", ");
            texto.append("\"cursoId\":").append(response.jsonPath().getString("cursoId"));
        }

        String mensaje = response.jsonPath().getString("message");
        if (mensaje != null) {
            texto.append(texto.length() > 1 ? ", " : "")
                    .append("\"message\":\"").append(mensaje).append("\"");
        }

        return texto.append("}").toString();
    }
}
