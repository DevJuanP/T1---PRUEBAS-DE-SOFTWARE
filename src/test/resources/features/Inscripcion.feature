@inscripciones
Feature: Inscripción a cursos

  El sistema de matrícula permite inscribir a un estudiante en un curso
  y rechaza la inscripción cuando el estudiante ya está inscrito.
  La base H2 carga automáticamente la inscripción del estudiante I2024001
  en el curso 1.

  Background:
    Given la aplicación de matrícula está disponible en el puerto 8080

  @inscripcion-correcta
  Scenario: Inscripción correcta de un estudiante nuevo
    Given un estudiante nuevo con un código distinto en cada ejecución
    When el estudiante se inscribe en el curso 2
    Then el sistema responde con el código 200
    And la inscripción queda registrada con el código del estudiante nuevo

  @inscripcion-duplicada
  Scenario: Inscripción duplicada de un estudiante ya inscrito
    Given el estudiante I2024001 ya está inscrito en el curso 1
    When el estudiante I2024001 intenta inscribirse de nuevo en el curso 1
    Then el sistema rechaza la inscripción con un código de error 4xx o 5xx
    And el mensaje de error indica que el estudiante ya está inscrito
