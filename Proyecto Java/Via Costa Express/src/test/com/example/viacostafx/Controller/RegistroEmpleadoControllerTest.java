package com.example.viacostafx.Controller;

import static org.junit.jupiter.api.Assertions.*;
import com.example.viacostafx.Modelo.EmpleadosModel;
import com.example.viacostafx.dao.EmpleadosDao;
import javafx.application.Platform;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javafx.scene.control.TextField;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RegistroEmpleadoControllerTest {
    private RegistroEmpleadoController controller;
    private EmpleadosDao empleadosDaoMock;

    @BeforeEach
    public void setUp() {
        // Crear una instancia del controlador y simular el DAO
        controller = new RegistroEmpleadoController();
        empleadosDaoMock = mock(EmpleadosDao.class);
        controller.empleadosDao = empleadosDaoMock;
    }

    @Test
    public void testGenerarContrasenia() {
        // Llamar al método
        String contrasenia = controller.generarContrasenia();

        // Verificar que la contraseña tenga 8 caracteres
        assertNotNull(contrasenia);
        assertEquals(8, contrasenia.length());

        // Mensaje de éxito
        System.out.println("Prueba de generar contraseña realizada con éxito.");
    }

    @Test
    public void testCamposVacios() throws Exception {
        // Asegurar que el entorno gráfico esté inicializado
        Platform.startup(() -> {});

        Platform.runLater(() -> {
            // Configuración de campos vacíos
            controller.txtNombre = new TextField("");
            controller.txtApellido = new TextField("");
            controller.txtDNI = new TextField("");
            controller.txtTelefono = new TextField("");

            // Verificar que detecta los campos vacíos
            assertTrue(controller.camposVacios());

            // Configuración de campos llenos
            controller.txtNombre.setText("Juan");
            controller.txtApellido.setText("Pérez");
            controller.txtDNI.setText("12345678");
            controller.txtTelefono.setText("987654321");

            // Verificar que no hay campos vacíos
            assertFalse(controller.camposVacios());

            // Mensaje de éxito
            System.out.println("Prueba de campos vacíos realizada con éxito.");
        });

        // Esperar a que todas las tareas en el hilo gráfico terminen
        Thread.sleep(1000);
    }
}
