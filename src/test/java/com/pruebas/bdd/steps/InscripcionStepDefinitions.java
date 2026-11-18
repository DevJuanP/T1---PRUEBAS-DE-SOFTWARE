package com.pruebas.bdd.steps;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pruebas.bdd.InscripcionAPI;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

public class InscripcionStepDefinitions {

    private static final String ESTUDIANTE_YA_INSCRITO = "I2024001";
    private static final Long CURSO_DUPLICADO = 1L;
    private static final Long CURSO_NUEVO = 2L;

    private final InscripcionAPI inscripcionAPI = new InscripcionAPI();

    private String codigoEstudiante;
    private Response response;

    @Given("la aplicación de matrícula está disponible en el puerto 8080")
    public void laAplicacionEstaDisponible() {
        System.out.println("Verificando que la aplicación responde en " + InscripcionAPI.BASE_URL);

        InscripcionAPI.configurar();

        Response estado = given()
                .when()
                .get("/api/cursos");

        assertEquals(200, estado.statusCode(),
                "La aplicación debe estar levantada en el puerto 8080 antes de ejecutar el escenario");
    }

    // Escenario 1: inscripcion correcta

    @Given("un estudiante nuevo con un código distinto en cada ejecución")
    public void unEstudianteNuevo() {
        codigoEstudiante = "I" + System.currentTimeMillis();

        System.out.println("Estudiante nuevo generado: " + codigoEstudiante);
    }

    @When("el estudiante se inscribe en el curso 2")
    public void elEstudianteSeInscribeEnElCurso2() {
        response = inscripcionAPI.inscribir(codigoEstudiante, CURSO_NUEVO);
    }

    @Then("el sistema responde con el código 200")
    public void elSistemaRespondeConCodigo200() {
        assertEquals(200, response.statusCode(),
                "Se esperaba un 200 al inscribir al estudiante " + codigoEstudiante
                        + " en el curso " + CURSO_NUEVO);
    }

    @And("la inscripción queda registrada con el código del estudiante nuevo")
    public void laInscripcionQuedaRegistrada() {
        Long id = response.jsonPath().getLong("id");
        String codigoRegistrado = response.jsonPath().getString("codigoEstudiante");
        Integer cursoRegistrado = response.jsonPath().getInt("cursoId");

        assertNotNull(id, "La inscripción registrada debe tener id");
        assertEquals(codigoEstudiante, codigoRegistrado,
                "El código registrado debe coincidir con el estudiante nuevo");
        assertEquals(CURSO_NUEVO.intValue(), cursoRegistrado,
                "La inscripción debe quedar en el curso " + CURSO_NUEVO);
    }

    // Escenario 2: inscripcion duplicada

    @Given("el estudiante I2024001 ya está inscrito en el curso 1")
    public void elEstudianteYaEstaInscrito() {
        System.out.println("Dato precargado por el DataLoader: "
                + ESTUDIANTE_YA_INSCRITO + " en el curso " + CURSO_DUPLICADO);

        codigoEstudiante = ESTUDIANTE_YA_INSCRITO;
    }

    @When("el estudiante I2024001 intenta inscribirse de nuevo en el curso 1")
    public void elEstudianteIntentaInscribirseDeNuevo() {
        response = inscripcionAPI.inscribir(ESTUDIANTE_YA_INSCRITO, CURSO_DUPLICADO);
    }

    @Then("el sistema rechaza la inscripción con un código de error 4xx o 5xx")
    public void elSistemaRechazaLaInscripcion() {
        int statusCode = response.statusCode();

        assertTrue(statusCode >= 400 && statusCode < 600,
                "Se esperaba un código de error 4xx o 5xx, pero se obtuvo " + statusCode);
    }

    @And("el mensaje de error indica que el estudiante ya está inscrito")
    public void elMensajeDeErrorIndicaInscripcionDuplicada() {
        String mensaje = response.jsonPath().getString("message");

        assertNotNull(mensaje, "La respuesta de error debe incluir el campo message");
        assertEquals("Estudiante ya inscrito", mensaje);
    }
}
