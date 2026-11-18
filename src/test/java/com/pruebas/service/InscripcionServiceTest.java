package com.pruebas.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pruebas.model.Inscripcion;
import com.pruebas.model.services.InscripcionService;
import com.pruebas.repository.InscripcionRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pregunta 1 - InscripcionService con JUnit 5 y Mockito")
class InscripcionServiceTest {

    @InjectMocks
    InscripcionService inscripcionService;

    @Mock
    InscripcionRepository inscripcionRepository;

    @BeforeEach
    void setUp() {
        System.out.println("Ejecutando setUp");
    }

    @AfterEach
    void tearDown() {
        System.out.println("Ejecutando tearDown");
    }

    @Test
    @DisplayName("Caso 1 - Inscribe correctamente al estudiante I202500545 en el curso 2")
    void inscribirEstudianteCorrectamente() {
        System.out.println("Caso 1: inscribirEstudianteCorrectamente");

        Inscripcion inscripcion = generarInscripcion("I202500545", 2L);

        when(inscripcionRepository.existsByCodigoEstudianteAndCursoId(anyString(), anyLong()))
                .thenReturn(false);
        when(inscripcionRepository.save(any(Inscripcion.class)))
                .thenReturn(generarInscripcionConId(inscripcion, 10L));

        Inscripcion resultado = inscripcionService.inscribir(inscripcion);

        assertNotNull(resultado, "El resultado no debe ser nulo");
        assertEquals(10L, resultado.getId(), "La inscripcion debe quedar registrada con id");
        assertEquals("I202500545", resultado.getCodigoEstudiante());
        assertEquals(2L, resultado.getCursoId());

        verify(inscripcionRepository).save(inscripcion);
    }

    @Test
    @DisplayName("Caso 2 - Rechaza la inscripcion duplicada y nunca llama a save()")
    void inscribirEstudianteDuplicado() {
        System.out.println("Caso 2: inscribirEstudianteDuplicado");

        Inscripcion inscripcion = generarInscripcion("I2024001", 1L);

        when(inscripcionRepository.existsByCodigoEstudianteAndCursoId("I2024001", 1L))
                .thenReturn(true);

        RuntimeException excepcion = assertThrows(RuntimeException.class,
                () -> inscripcionService.inscribir(inscripcion),
                "Debe lanzar RuntimeException si el estudiante ya esta inscrito");

        assertEquals("Estudiante ya inscrito", excepcion.getMessage());

        verify(inscripcionRepository, never()).save(any(Inscripcion.class));
    }

    // datos de apoyo

    Inscripcion generarInscripcion(String codigoEstudiante, Long cursoId) {
        Inscripcion inscripcion = new Inscripcion();
        inscripcion.setCodigoEstudiante(codigoEstudiante);
        inscripcion.setCursoId(cursoId);
        return inscripcion;
    }

    Inscripcion generarInscripcionConId(Inscripcion base, Long id) {
        Inscripcion inscripcion = new Inscripcion();
        inscripcion.setId(id);
        inscripcion.setCodigoEstudiante(base.getCodigoEstudiante());
        inscripcion.setCursoId(base.getCursoId());
        return inscripcion;
    }
}
