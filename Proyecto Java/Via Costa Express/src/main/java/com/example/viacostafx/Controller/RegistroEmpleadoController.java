package com.example.viacostafx.Controller;

import java.security.SecureRandom;
import java.util.List;

import org.mindrot.jbcrypt.BCrypt;

import com.example.viacostafx.Modelo.EmpleadosModel;
import com.example.viacostafx.Servicio.ExcelEmpleado;
import com.example.viacostafx.dao.EmpleadosDao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class RegistroEmpleadoController {
    // Variables de los empleados
    @FXML
    TextField txtNombre;
    @FXML
    TextField txtApellido;
    @FXML
    TextField txtDNI;
    @FXML
    TextField txtTelefono;
    @FXML
    TextField txtBuscar;

    @FXML
    PasswordField txtContrasenia; // Asegúrate de que sea PasswordField


    // Tabla de empleados
    @FXML
    private TableView<EmpleadosModel> tablaEmpleados;
    @FXML
    private TableColumn<EmpleadosModel, String> colNombre;
    @FXML
    private TableColumn<EmpleadosModel, String> colApellido;
    @FXML
    private TableColumn<EmpleadosModel, Integer> colDNI;
    @FXML
    private TableColumn<EmpleadosModel, Integer> colTelefono;
    @FXML
    private TableColumn<EmpleadosModel, String> colUsuario;
    @FXML
    private TableColumn<EmpleadosModel, String> colContrasenia;
    @FXML
    private TableColumn<EmpleadosModel, Void> colAcciones;


    // Botones
    @FXML
    private Button btnAgregar;
    @FXML
    private Button btnEditar;
    @FXML
    private Button btnEliminar;
    @FXML
    private Button btnExportar;
    @FXML
    private Button btnLimpiar;

    // Variables de control
    private boolean modoEdicion = false;
    private EmpleadosModel empleadoEnEdicion = null;

    // Instancias de EmpleadosDao y ObservableList
    EmpleadosDao empleadosDao;
    private ObservableList<EmpleadosModel> listaEmpleados;

    @FXML
    public void initialize() {
        // Inicializar instancias
        empleadosDao = new EmpleadosDao();
        listaEmpleados = FXCollections.observableArrayList();

        // Configurar columnas
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        colDNI.setCellValueFactory(new PropertyValueFactory<>("DNI"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colUsuario.setCellValueFactory(new PropertyValueFactory<>("usuario"));
        colContrasenia.setCellValueFactory(new PropertyValueFactory<>("contrasenia"));
        btnAgregar.setOnAction(e -> {
            if (modoEdicion) {
                guardarEdicion();
            } else {
                handleAgregar();
            }
        });

        // Cargar datos
        cargarEmpleados();
        btnExportar.setOnAction(event -> exportarEmpleados());
        btnLimpiar.setOnAction(event -> limpiarCampos());
        btnEditar.setOnAction(event -> handleEditar());
        btnEliminar.setOnAction(event -> handleEliminar());

        // Validar el campo txtDNI para que solo acepte números y tenga un máximo de 8
        // dígitos
        txtDNI.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtDNI.setText(newValue.replaceAll("[^\\d]", ""));
            }
            if (newValue.length() > 8) {
                txtDNI.setText(newValue.substring(0, 8));
            }
        });

        // Validar el campo txtTelefono para que solo acepte números y tenga un máximo
        // de 9 dígitos
        txtTelefono.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtTelefono.setText(newValue.replaceAll("[^\\d]", ""));
            }
            if (newValue.length() > 9) {
                txtTelefono.setText(newValue.substring(0, 9));
            }
        });

        // Validar el campo txtNombre para que solo acepte letras y tenga un máximo de
        // 50 caracteres
        txtNombre.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[a-zA-Z]*")) {
                txtNombre.setText(newValue.replaceAll("[^a-zA-Z]", ""));
            }
            if (newValue.length() > 50) {
                txtNombre.setText(newValue.substring(0, 50));
            }
        });

        // Validar el campo txtApellido para que solo acepte letras y tenga un máximo de
        // 50 caracteres
        txtApellido.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[a-zA-Z]*")) {
                txtApellido.setText(newValue.replaceAll("[^a-zA-Z]", ""));
            }
            if (newValue.length() > 50) {
                txtApellido.setText(newValue.substring(0, 50));
            }
        });

        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            buscarEmpleados(null);
        });
    }

    // Método para verificar campos vacíos
    boolean camposVacios() {
        return txtNombre.getText().isEmpty() || txtApellido.getText().isEmpty()
                || txtDNI.getText().isEmpty() || txtTelefono.getText().isEmpty();
    }

    @FXML
    private void handleAgregar() {
        try {
            if (camposVacios()) {
                mostrarAlerta("Campos vacíos", "Por favor, completa todos los campos.", Alert.AlertType.WARNING);
                return;
            }

            String nombre = txtNombre.getText();
            String apellido = txtApellido.getText();
            int dni = Integer.parseInt(txtDNI.getText());
            int telefono = Integer.parseInt(txtTelefono.getText());
            String contrasenia = txtContrasenia.getText();

            if (contrasenia == null || contrasenia.isEmpty()) {
                mostrarAlerta("Contraseña vacía", "Por favor, ingresa una contraseña.", Alert.AlertType.WARNING);
                return;
            }

            String usuario = generarUsuario(nombre, apellido);
            String contraseniaHasheada = BCrypt.hashpw(contrasenia, BCrypt.gensalt());

            EmpleadosModel nuevoEmpleado = new EmpleadosModel(0, nombre, apellido, dni, telefono, usuario, contraseniaHasheada);
            
            if (modoEdicion) {
                empleadoEnEdicion.setNombre(nombre);
                empleadoEnEdicion.setApellido(apellido);
                empleadoEnEdicion.setDNI(dni);
                empleadoEnEdicion.setTelefono(telefono);
                empleadoEnEdicion.setUsuario(usuario);
                empleadoEnEdicion.setContrasenia(contraseniaHasheada);

                empleadosDao.actualizarEmpleado(empleadoEnEdicion);
                volverAModoAgregar();
            } else {
                empleadosDao.crearEmpleado(nuevoEmpleado);
            }

            cargarEmpleados();
            limpiarCampos();
        } catch (NumberFormatException e) {
            mostrarAlerta("Error de formato", "DNI y teléfono deben ser números.", Alert.AlertType.ERROR);
        }
    }

    // Método para manejar la edición de un empleado
    @FXML
    private void handleEditar() {
        // Obtener el empleado seleccionado
        EmpleadosModel empleadoSeleccionado = tablaEmpleados.getSelectionModel().getSelectedItem();

        if (empleadoSeleccionado != null) {
            editarEmpleado(empleadoSeleccionado);
        } else {
            mostrarAlerta("Selección necesaria",
                    "Por favor, selecciona un empleado de la tabla para editar.", Alert.AlertType.WARNING);
        }
    }

    // Método para manejar la eliminación de un empleado
    @FXML
    private void handleEliminar() {
        // Obtener el empleado seleccionado
        EmpleadosModel empleadoSeleccionado = tablaEmpleados.getSelectionModel().getSelectedItem();

        if (empleadoSeleccionado != null) {
            eliminarEmpleado(empleadoSeleccionado);
        } else {
            mostrarAlerta("Selección necesaria",
                    "Por favor, selecciona un empleado de la tabla para eliminar.", Alert.AlertType.WARNING);
        }
    }

    String generarUsuario(String nombre, String apellido) {
        // Generar usuario con primera letra del nombre + apellido en minúsculas
        String usuario = (nombre.charAt(0) + apellido).toLowerCase()
                .replaceAll("[áéíóúñ]", "a")
                .replaceAll("\\s+", "");

        // Verificar si ya existe y agregar número si es necesario
        int contador = 1;
        String usuarioBase = usuario;
        while (empleadosDao.obtenerEmpleadoPorUsername(usuario) != null) {
            usuario = usuarioBase + contador++;
        }
        return usuario;
    }

    String generarContrasenia() {
        // Generar contraseña segura de 8 caracteres
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        SecureRandom random = new SecureRandom();
        StringBuilder contrasenia = new StringBuilder(8);

        for (int i = 0; i < 8; i++) {
            contrasenia.append(caracteres.charAt(random.nextInt(caracteres.length())));
        }

        return contrasenia.toString();
    }

    private void eliminarEmpleado(EmpleadosModel empleado) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText("¿Está seguro de eliminar este empleado?");
        alert.setContentText("Esta acción no se puede deshacer");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (empleadosDao.eliminarEmpleado(empleado.getId())) {
                    mostrarAlerta("Éxito", "Empleado eliminado correctamente", Alert.AlertType.INFORMATION);
                    cargarEmpleados();
                }
            }
        });
    }

    private void editarEmpleado(EmpleadosModel empleado) {
        // Cambiar a modo edición
        modoEdicion = true;
        empleadoEnEdicion = empleado;

        // Cargar datos en los campos
        txtNombre.setText(empleado.getNombre());
        txtApellido.setText(empleado.getApellido());
        txtDNI.setText(String.valueOf(empleado.getDNI()));
        txtTelefono.setText(String.valueOf(empleado.getTelefono()));

        // Cambiar el texto del botón
        btnAgregar.setText("Guardar Cambios");
    }

    private void guardarEdicion() {
        if (empleadoEnEdicion == null)
            return;

        boolean hayModificaciones = false;
        boolean todoCorrecto = true;
        StringBuilder mensaje = new StringBuilder("Se actualizaron los siguientes campos:\n");

        // Verificar y actualizar nombre si cambió
        String nuevoNombre = txtNombre.getText().trim();
        if (!nuevoNombre.equals(empleadoEnEdicion.getNombre())) {
            if (!nuevoNombre.isEmpty()) {
                empleadoEnEdicion.setNombre(nuevoNombre);
                mensaje.append("- Nombre\n");
                hayModificaciones = true;
            } else {
                mostrarAlerta("Error", "El nombre no puede estar vacío", Alert.AlertType.ERROR);
                todoCorrecto = false;
            }
        }

        // Verificar y actualizar apellido si cambió
        String nuevoApellido = txtApellido.getText().trim();
        if (!nuevoApellido.equals(empleadoEnEdicion.getApellido())) {
            if (!nuevoApellido.isEmpty()) {
                empleadoEnEdicion.setApellido(nuevoApellido);
                mensaje.append("- Apellido\n");
                hayModificaciones = true;
            } else {
                mostrarAlerta("Error", "El apellido no puede estar vacío", Alert.AlertType.ERROR);
                todoCorrecto = false;
            }
        }

        // Verificar y actualizar DNI si cambió
        try {
            int nuevoDNI = Integer.parseInt(txtDNI.getText().trim());
            if (nuevoDNI != empleadoEnEdicion.getDNI()) {
                empleadoEnEdicion.setDNI(nuevoDNI);
                mensaje.append("- DNI\n");
                hayModificaciones = true;
            }
        } catch (NumberFormatException ex) {
            mostrarAlerta("Error", "El DNI debe ser un número válido", Alert.AlertType.ERROR);
            todoCorrecto = false;
        }

        // Verificar y actualizar teléfono si cambió
        try {
            int nuevoTelefono = Integer.parseInt(txtTelefono.getText().trim());
            if (nuevoTelefono != empleadoEnEdicion.getTelefono()) {
                empleadoEnEdicion.setTelefono(nuevoTelefono);
                mensaje.append("- Teléfono\n");
                hayModificaciones = true;
            }
        } catch (NumberFormatException ex) {
            mostrarAlerta("Error", "El teléfono debe ser un número válido", Alert.AlertType.ERROR);
            todoCorrecto = false;
        }

        // Si hay modificaciones y todo es correcto, actualizar en la base de datos
        if (hayModificaciones && todoCorrecto) {
            if (empleadosDao.actualizarEmpleado(empleadoEnEdicion)) {
                mostrarAlerta("Éxito", mensaje.toString(), Alert.AlertType.INFORMATION);
                limpiarCampos();
                cargarEmpleados();
                volverAModoAgregar();
            } else {
                mostrarAlerta("Error", "No se pudieron guardar los cambios", Alert.AlertType.ERROR);
            }
        } else if (!hayModificaciones && todoCorrecto) {
            mostrarAlerta("Información", "No se detectaron cambios", Alert.AlertType.INFORMATION);
        }
    }

    @FXML
    public void buscarEmpleados(Event event) {
        // Obtener el usuario buscado del campo de texto
        String usuarioBuscado = txtBuscar.getText().trim();

        if (usuarioBuscado.isEmpty()) {
            // Si el campo de búsqueda está vacío, cargar todos los empleados
            cargarEmpleados();
        } else {
            // Obtener la lista filtrada de empleados
            List<EmpleadosModel> empleadosFiltrados = empleadosDao.buscarEmpleadoPorUsuario(usuarioBuscado);

            // Actualizar la lista observable y la tabla
            listaEmpleados.clear();
            listaEmpleados.addAll(empleadosFiltrados);
            tablaEmpleados.setItems(listaEmpleados);
        }
    }

    private void volverAModoAgregar() {
        modoEdicion = false;
        empleadoEnEdicion = null;
        btnAgregar.setText("Agregar Empleado");
        limpiarCampos();
    }

    private void cargarEmpleados() {
        List<EmpleadosModel> empleados = empleadosDao.obtenerTodosEmpleados();
        listaEmpleados.clear();
        listaEmpleados.addAll(empleados);
        tablaEmpleados.setItems(listaEmpleados);
    }

    private void limpiarCampos() {
        txtNombre.clear();
        txtApellido.clear();
        txtDNI.clear();
        txtTelefono.clear();

        modoEdicion = false;
        empleadoEnEdicion = null;
        btnAgregar.setText("Agregar Empleado");
    }

    private void exportarEmpleados() {
        // Aquí puedes elegir la ubicación donde deseas guardar el archivo
        String filePath = "empleados.xlsx"; // Cambia la ruta si es necesario

        // Obtener la lista de empleados
        List<EmpleadosModel> listaEmpleados = empleadosDao.obtenerTodosEmpleados();

        // Crear una instancia de ExcelEmpleado
        ExcelEmpleado excelEmpleado = new ExcelEmpleado();

        // Llamar al método de exportación
        excelEmpleado.exportEmpleados(listaEmpleados, filePath);

        // Mostrar mensaje de éxito utilizando el método mostrarAlerta
        mostrarAlerta("Exportación Exitosa", "Los empleados se han exportado a " + filePath,
                Alert.AlertType.INFORMATION);
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

}