package com.example.viacostafx.Controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.mindrot.jbcrypt.BCrypt;

import com.example.viacostafx.Auxiliar.Session;
import com.example.viacostafx.Modelo.EmpleadosModel;
import com.example.viacostafx.dao.EmpleadosDao;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class LoginController implements Initializable {

    // Se crean los elementos de la interfaz gráfica
    @FXML
    TextField txtUsername;
    @FXML
    Button btnLogin;
    @FXML
    PasswordField txtPassword;
    @FXML
    Label txtLabel;

    // Se crea una instancia de EmpleadosDao para interactuar con la base de datos
    private EmpleadosDao empleadosDao;
    int intentos = 0;
    private final int MAX_INTENTOS = 3;

    public LoginController() {
        // Se inicializa la instancia de EmpleadosDao
        this.empleadosDao = new EmpleadosDao();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnLogin.setOnAction(event -> iniciarSesion(txtUsername.getText(), txtPassword.getText()));
    }

    // Método para iniciar sesión
    void iniciarSesion(String username, String password) {

        // Se verifica que los campos de usuario y contraseña no estén vacíos
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            mostrarAlerta("Por favor, completa ambos campos de usuario y contraseña.");
            return;
        }

        // Se obtiene el empleado con el username proporcionado
        EmpleadosModel empleado = empleadosDao.obtenerEmpleadoPorUsername(username);
        if (empleado != null) {
            // Se verifica que la contraseña proporcionada coincida con la contraseña del empleado
            if (empleado.getContrasenia() != null && BCrypt.checkpw(password, empleado.getContrasenia())) {
                // Resetear el contador de intentos
                intentos = 0;
                // Guardar la ID del empleado en la sesión
                Session.getInstance().setEmpleadoId(empleado.getId());
                cargarInterfazPrincipal();
            } else {
                intentos++;
                if (intentos >= MAX_INTENTOS) {
                    txtLabel.setText("Has agotado los 3 intentos de inicio de sesión. La aplicación se cerrará.");
                    mostrarAlerta("Has agotado los 3 intentos de inicio de sesión. La aplicación se cerrará.");
                    Platform.exit();
                    System.exit(0);
                } else {
                    int intentosRestantes = MAX_INTENTOS - intentos;
                    txtLabel.setText("Contraseña incorrecta. Te quedan " + intentosRestantes + " intentos.");
                    mostrarAlerta("Contraseña incorrecta. Te quedan " + intentosRestantes + " intentos.");
                }
            }
        } else {
            intentos++;
            if (intentos >= MAX_INTENTOS) {
                txtLabel.setText("Has agotado los 3 intentos de inicio de sesión. La aplicación se cerrará.");
                mostrarAlerta("Has agotado los 3 intentos de inicio de sesión. La aplicación se cerrará.");
                Platform.exit();
                System.exit(0);
            } else {
                int intentosRestantes = MAX_INTENTOS - intentos;
                txtLabel.setText("Usuario no encontrado. Te quedan " + intentosRestantes + " intentos.");
                mostrarAlerta("Usuario no encontrado. Te quedan " + intentosRestantes + " intentos.");
            }
        }
    }

    // Método para mostrar una alerta
    private void mostrarAlerta(String mensaje) {
        // Crear una alerta
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Inicio de Sesión");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);

        // Centrar la alerta en la ventana principal
        alert.initOwner((Stage) txtLabel.getScene().getWindow());
        alert.initModality(Modality.WINDOW_MODAL);

        alert.setOnShown(event -> {
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            Stage ownerStage = (Stage) alert.getOwner();
            if (ownerStage != null) {
                stage.setX(ownerStage.getX() + (ownerStage.getWidth() - stage.getWidth()) / 2);
                stage.setY(ownerStage.getY() + (ownerStage.getHeight() - stage.getHeight()) / 2);
            }
        });

        alert.showAndWait();
    }

    // Método para cargar la interfaz principal
    private void cargarInterfazPrincipal() {
        try {
            // Cargar la interfaz principal
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/InterfazPrincipal.fxml"));
            Parent mainInterfaceRoot = loader.load();

            // Obtener el controlador de la interfaz principal
            Stage stage = (Stage) txtLabel.getScene().getWindow();
            Scene scene = new Scene(mainInterfaceRoot);
            stage.setTitle("Via Costa - Interfaz Principal");
            stage.setScene(scene);
            stage.setMaximized(true);

            stage.show();


        } catch (IOException e) {
            e.printStackTrace();
            txtLabel.setText("Error al cargar la interfaz principal");
        }
    }
}