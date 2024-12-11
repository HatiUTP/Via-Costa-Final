package com.example.viacostafx.Controller;

import com.example.viacostafx.Modelo.BusModel;
import com.example.viacostafx.Modelo.ChoferModel;
import com.example.viacostafx.Modelo.EmpleadosModel;
import com.example.viacostafx.Servicio.ExcelChofer;
import com.example.viacostafx.dao.BusDao;
import com.example.viacostafx.dao.ChoferDao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class ChoferController {

    // Campos de texto de la información del chofer
    @FXML
    private TextField txtDni;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtLicencia;
    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtTelefono;
    @FXML
    private TextField txtBuscar;

    // Tabla para mostrar los choferes
    @FXML
    private TableView<ChoferModel> tablaChoferes;
    @FXML
    private TableColumn<ChoferModel, Integer> IdColumn;
    @FXML
    private TableColumn<ChoferModel, String> dniColumn;
    @FXML
    private TableColumn<ChoferModel, String> emailColumn;
    @FXML
    private TableColumn<ChoferModel, Integer> idBusColumn;
    @FXML
    private TableColumn<ChoferModel, String> licenciaColumn;
    @FXML
    private TableColumn<ChoferModel, String> nombreColumn;
    @FXML
    private TableColumn<ChoferModel, String> telefonoColumn;
    @FXML
    private TableColumn<EmpleadosModel, Void> colAcciones;
    @FXML
    private ComboBox<BusModel> cmbBus;

    // Botones para agregar, editar y eliminar choferes
    @FXML
    private Button btnAgregar;
    @FXML
    private Button btnEditar;
    @FXML
    private Button btnEliminar;
    @FXML
    private Button btnLimpiar;

    // Botón para exportar choferes a Excel
    @FXML
    private Button btnExportar;

    // DAO para interactuar con la base de datos
    private ChoferDao choferDao;
    private BusDao busDao;

    // Lista observable para mostrar los choferes en la tabla
    private ObservableList<ChoferModel> listaChoferes;

    public ChoferController() {
        this.choferDao = new ChoferDao();
        this.busDao = new BusDao();
    }

    @FXML
    public void initialize() {
        // Inicializar la lista observable
        listaChoferes = FXCollections.observableArrayList();

        // Configurar columnas
        IdColumn.setCellValueFactory(new PropertyValueFactory<>("id")); // Asegúrate de que el nombre del campo sea
                                                                        // correcto
        nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        dniColumn.setCellValueFactory(new PropertyValueFactory<>("dni"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        licenciaColumn.setCellValueFactory(new PropertyValueFactory<>("numeroLicencia"));
        telefonoColumn.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        idBusColumn.setCellValueFactory(new PropertyValueFactory<>("bus")); // Cambia si necesitas obtener una propiedad
                                                                            // específica

        // Agregar función de botones
        btnAgregar.setOnAction(event -> agregarChofer());
        btnEditar.setOnAction(event -> editarChofer());
        btnLimpiar.setOnAction(event -> limpiarCampos());
        btnEliminar.setOnAction(event -> desactivarChoferSeleccionado());
        btnExportar.setOnAction(event -> exportarChoferes());

        // Cargar datos de choferes
        cargarChoferes();
        List<BusModel> buses = busDao.obtenerTodosLosBuses();
        cmbBus.setItems(FXCollections.observableArrayList(buses));
        tablaChoferes.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                cargarDatosChoferEnCampos(newValue);
            }
        });

        // Validar el campo txtDNI para que solo acepte números y tenga un máximo de 8
        // dígitos
        txtDni.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtDni.setText(newValue.replaceAll("[^\\d]", ""));
            }
            if (newValue.length() > 8) {
                txtDni.setText(newValue.substring(0, 8));
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
        // 100 caracteres
        txtNombre.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[a-zA-Z ]*")) {
                txtNombre.setText(newValue.replaceAll("[^a-zA-Z ]", ""));
            }
            if (newValue.length() > 100) {
                txtNombre.setText(newValue.substring(0, 50));
            }
        });

    }

    // Método para limpiar los campos de texto
    private void limpiarCampos() {
        txtDni.setText("");
        txtEmail.setText("");
        txtLicencia.setText("");
        txtNombre.setText("");
        txtTelefono.setText("");
        cmbBus.getSelectionModel().clearSelection();
        tablaChoferes.getSelectionModel().clearSelection();
    }

    // Método para mostrar alertas
    private void mostrarAlerta(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Método para cargar la lista de choferes
    private void cargarChoferes() {
        // Obtener la lista de choferes del DAO
        List<ChoferModel> choferes = choferDao.obtenerTodosLosChoferes();
        listaChoferes.clear(); // Limpia la lista antes de agregar nuevos elementos
        listaChoferes.addAll(choferes); // Agrega los choferes a la lista observable
        tablaChoferes.setItems(listaChoferes); // Establece la lista en la tabla
    }

    // Método para cargar los datos del chofer seleccionado en los campos de texto
    private void cargarDatosChoferEnCampos(ChoferModel chofer) {
        // Asignar los valores del chofer a los campos de texto
        txtDni.setText(chofer.getDni());
        txtEmail.setText(chofer.getEmail());
        txtLicencia.setText(chofer.getNumeroLicencia());
        txtNombre.setText(chofer.getNombre());
        txtTelefono.setText(chofer.getTelefono());

        // Seleccionar el bus correspondiente en el ComboBox, si está presente
        if (chofer.getBus() != null) {
            cmbBus.getSelectionModel().select(chofer.getBus());
        } else {
            cmbBus.getSelectionModel().clearSelection();
        }
    }

    // Método para agregar un nuevo chofer
    @FXML
    private void agregarChofer() {
        // Crear un nuevo objeto ChoferModel y asignar los valores de los campos de
        // texto
        ChoferModel nuevoChofer = new ChoferModel();
        nuevoChofer.setDni(txtDni.getText());
        nuevoChofer.setEmail(txtEmail.getText());
        nuevoChofer.setNumeroLicencia(txtLicencia.getText());
        nuevoChofer.setNombre(txtNombre.getText());
        nuevoChofer.setTelefono(txtTelefono.getText());
        nuevoChofer.setIsActive(true);

        // Obtener el bus seleccionado en el ComboBox
        BusModel busSeleccionado = cmbBus.getSelectionModel().getSelectedItem();
        if (busSeleccionado != null) {
            nuevoChofer.setBus(busSeleccionado);
        } else {
            System.out.println("Seleccione un bus antes de agregar el chofer.");
        }

        // Llamar al método del DAO para agregar el chofer
        choferDao.agregarChofer(nuevoChofer);
        cargarChoferes(); // Recargar la lista de choferes
        limpiarCampos();
    }

    // Método para editar un chofer existente
    private void editarChofer() {
        // Obtén el chofer seleccionado en la tabla
        ChoferModel choferSeleccionado = tablaChoferes.getSelectionModel().getSelectedItem();

        if (choferSeleccionado != null) {
            // Actualiza los datos del chofer seleccionado
            choferSeleccionado.setDni(txtDni.getText());
            choferSeleccionado.setEmail(txtEmail.getText());
            choferSeleccionado.setNumeroLicencia(txtLicencia.getText());
            choferSeleccionado.setNombre(txtNombre.getText());
            choferSeleccionado.setTelefono(txtTelefono.getText());
            choferSeleccionado.setIsActive(true);

            // Asigna el bus seleccionado
            BusModel busSeleccionado = cmbBus.getSelectionModel().getSelectedItem();
            if (busSeleccionado != null) {
                choferSeleccionado.setBus(busSeleccionado);
            } else {
                System.out.println("Seleccione un bus antes de agregar el chofer.");
            }

            // Llama al método del DAO para actualizar el chofer
            choferDao.editarChofer(choferSeleccionado);

            // Recarga la tabla para mostrar los cambios
            cargarChoferes();
        } else {
            System.out.println("Seleccione un chofer en la tabla para editar.");
        }
    }

    public void desactivarChoferSeleccionado() {
        // Obtén el chofer seleccionado en la tabla
        ChoferModel choferSeleccionado = tablaChoferes.getSelectionModel().getSelectedItem();

        // Verifica que se haya seleccionado un chofer
        if (choferSeleccionado != null) {
            choferDao.desactivarChofer(choferSeleccionado.getId()); // Llama al método en el DAO
            cargarChoferes(); // Actualiza la tabla para reflejar el cambio
        } else {
            System.out.println("Seleccione un chofer en la tabla para desactivar.");
        }
    }

    // Método para buscar choferes por nombre
    @FXML
    public void buscarChoferes(Event event) {
        // Obtener el nombre buscado del campo de texto
        String nombreBuscado = txtBuscar.getText();
        List<ChoferModel> choferesFiltrados = choferDao.buscarChoferPorNombre(nombreBuscado);

        // Actualizar la lista observable y la tabla
        listaChoferes.clear();
        listaChoferes.addAll(choferesFiltrados);
        tablaChoferes.setItems(listaChoferes);
    }

    // Método para exportar choferes a Excel
    private void exportarChoferes() {
        // Ruta del archivo de Excel
        String filePath = "choferes.xlsx";

        try {
            // Obtener la lista de choferes
            List<ChoferModel> listaChoferes = choferDao.obtenerTodosLosChoferes();

            // Crear una instancia de ExcelChofer
            ExcelChofer excelChofer = new ExcelChofer();

            // Llamar al método de exportación
            excelChofer.exportChoferes(listaChoferes, filePath);

            // Mostrar mensaje de éxito
            mostrarAlerta("Exportación Exitosa", "Los choferes se han exportado a " + filePath,
                    Alert.AlertType.INFORMATION);
        } catch (Exception ex) {
            // Mostrar mensaje de error en caso de excepción
            mostrarAlerta("Error de Exportación", "Ocurrió un error al exportar los choferes: " + ex.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }
}
