package com.example.viacostafx.Controller;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mindrot.jbcrypt.BCrypt;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.viacostafx.Auxiliar.Session;
import com.example.viacostafx.Modelo.EmpleadosModel;
import com.example.viacostafx.dao.EmpleadosDao;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

@ExtendWith(MockitoExtension.class)
class LoginControllerTest {

    @Mock
    private EmpleadosDao empleadosDao;

    @InjectMocks
    private LoginController loginController;

    private static Stage stage;

    @BeforeAll
    public static void initJavaFX() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        Platform.startup(() -> {
            // Initialize JavaFX toolkit
            stage = new Stage();
            latch.countDown();
        });
        if (!latch.await(5, TimeUnit.SECONDS)) {
            throw new RuntimeException("JavaFX inicialización timeout");
        }
    }

    @BeforeEach
    void setUp() throws InterruptedException {
        // Inicializar mocks
        MockitoAnnotations.openMocks(this);

        // Resetear la instancia de Session
        Session.getInstance().setEmpleadoId(0);

        // Inicializar componentes JavaFX en el hilo FX
        final CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            loginController.txtUsername = new TextField();
            loginController.txtPassword = new PasswordField();
            loginController.txtLabel = new Label();
            loginController.btnLogin = new Button();

            Pane root = new Pane();
            root.getChildren().addAll(loginController.txtUsername, loginController.txtPassword, loginController.txtLabel, loginController.btnLogin);

            Scene scene = new Scene(root, 400, 300);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();

            latch.countDown();
        });
        if (!latch.await(5, TimeUnit.SECONDS)) {
            throw new RuntimeException("Timeout en la inicialización de componentes JavaFX");
        }

        // Inicializar intentos a 0 antes de cada prueba
        loginController.intentos = 0;
    }

    @Test
    void testIniciarSesion_CamposVacios() throws InterruptedException {
        // Arrange
        loginController.txtUsername.setText("");
        loginController.txtPassword.setText("");

        // Act
        final CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                loginController.iniciarSesion(loginController.txtUsername.getText(), loginController.txtPassword.getText());
            } finally {
                latch.countDown();
            }
        });
        if (!latch.await(5, TimeUnit.SECONDS)) {
            throw new RuntimeException("Timeout en la prueba");
        }

        // Assert
        // Verificar que obtenerEmpleadoPorUsername nunca es llamado
        verify(empleadosDao, never()).obtenerEmpleadoPorUsername(anyString());
        assertEquals(0, loginController.intentos, "El contador de intentos no debería incrementarse.");

        // Mensaje de éxito
        System.out.println("testIniciarSesion_CamposVacios completado correctamente.");
    }

    @Test
    void testIniciarSesion_UsuarioNoEncontrado() throws InterruptedException {
        // Arrange
        loginController.txtUsername.setText("usuarioInexistente");
        loginController.txtPassword.setText("adminpass");

        when(empleadosDao.obtenerEmpleadoPorUsername("usuarioInexistente")).thenReturn(null);

        // Act & Assert dentro del hilo de JavaFX
        final CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                loginController.iniciarSesion(loginController.txtUsername.getText(), loginController.txtPassword.getText());

                // Assert dentro del hilo de JavaFX
                verify(empleadosDao).obtenerEmpleadoPorUsername("usuarioInexistente");
                assertEquals(1, loginController.intentos, "El contador de intentos debería ser 1.");
                assertEquals("Usuario no encontrado. Te quedan 2 intentos.", loginController.txtLabel.getText());

                // Mensaje de éxito
                System.out.println("testIniciarSesion_UsuarioNoEncontrado completado correctamente.");
            } finally {
                latch.countDown();
            }
        });
        if (!latch.await(5, TimeUnit.SECONDS)) {
            throw new RuntimeException("Timeout en la prueba");
        }
    }

    @Test
    void testIniciarSesion_ContraseniaIncorrecta() throws InterruptedException {
        // Arrange
        loginController.txtUsername.setText("admin");
        loginController.txtPassword.setText("contraseniaIncorrecta");

        EmpleadosModel empleado = new EmpleadosModel();
        empleado.setId(1);
        empleado.setUsuario("admin");
        // Contraseña hasheada para "adminpass"
        empleado.setContrasenia(BCrypt.hashpw("adminpass", BCrypt.gensalt()));

        when(empleadosDao.obtenerEmpleadoPorUsername("admin")).thenReturn(empleado);

        // Act
        final CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            loginController.iniciarSesion(loginController.txtUsername.getText(), loginController.txtPassword.getText());
            latch.countDown();
        });
        if (!latch.await(5, TimeUnit.SECONDS)) {
            throw new RuntimeException("Timeout en la prueba");
        }

        // Assert
        verify(empleadosDao).obtenerEmpleadoPorUsername("admin");
        assertEquals(1, loginController.intentos, "El contador de intentos debería ser 1.");
        assertEquals("Contraseña incorrecta. Te quedan 2 intentos.", loginController.txtLabel.getText());

        // Mensaje de éxito
        System.out.println("testIniciarSesion_ContraseniaIncorrecta completado correctamente.");
    }



    @Test
    void testIniciarSesion_Exito() throws InterruptedException {
        // Arrange
        loginController.txtUsername.setText("EmpleadoPrincipal");
        loginController.txtPassword.setText("adminpass");

        EmpleadosModel empleado = new EmpleadosModel();
        empleado.setId(1); // Asegurarse de que el ID sea 1
        empleado.setUsuario("EmpleadoPrincipal");
        empleado.setContrasenia(BCrypt.hashpw("adminpass", BCrypt.gensalt()));

        when(empleadosDao.obtenerEmpleadoPorUsername("EmpleadoPrincipal")).thenReturn(empleado);

        // Act
        final CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            loginController.iniciarSesion(loginController.txtUsername.getText(), loginController.txtPassword.getText());
            latch.countDown();
        });
        if (!latch.await(5, TimeUnit.SECONDS)) {
            throw new RuntimeException("Timeout en la prueba");
        }

        // Assert
        assertEquals(1, Session.getInstance().getEmpleadoId(), "La sesión debería tener el ID del empleado.");
        assertEquals(0, loginController.intentos, "El contador de intentos debería resetearse a 0.");
        // Verificar que la interfaz principal se haya cargado
        // Esto podría requerir una verificación adicional dependiendo de cómo se implemente 'cargarInterfazPrincipal'

        // Mensaje de éxito
        System.out.println("testIniciarSesion_Exito completado correctamente.");
    }
}