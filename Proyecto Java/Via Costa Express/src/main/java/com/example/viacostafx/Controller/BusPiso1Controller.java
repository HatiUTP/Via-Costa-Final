package com.example.viacostafx.Controller;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.json.JSONObject;

import com.example.viacostafx.Auxiliar.PasajeroData;
import com.example.viacostafx.Auxiliar.Session;
import com.example.viacostafx.Modelo.AsientoModel;
import com.example.viacostafx.Modelo.PasajeroModel;
import com.example.viacostafx.Modelo.ViajeBusModel;
import com.example.viacostafx.dao.AsientoDao;
import com.example.viacostafx.dao.BusDao;
import com.example.viacostafx.dao.PasajeroDao;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.TableCell;
import javafx.stage.Window;
import javafx.util.Callback;

public class BusPiso1Controller implements Initializable {

    // Campos de texto la información del pasajero
    @FXML
    private TextArea txtServicios;
    @FXML
    private TextField txtDNI;
    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtApellido;
    @FXML
    private TextField txtTelefono;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtAsientoPasajero;
    @FXML
    private TextField txtPrecioAsiento;

    // Elementos de los asientos y la tabla de pasajeros
    @FXML
    private GridPane gridAsientos;
    @FXML
    private TableView<PasajeroData> tblPasajeros;
    @FXML
    private TableColumn<PasajeroData, Void> colAcciones;
    @FXML
    private TableColumn<PasajeroData, String> colNombre;
    @FXML
    private TableColumn<PasajeroData, String> colDNI;
    @FXML
    private TableColumn<PasajeroData, String> colTelefono;
    @FXML
    private TableColumn<PasajeroData, String> colEmail;
    @FXML
    private TableColumn<PasajeroData, String> colAsiento;
    @FXML
    private TableColumn<PasajeroData, String> colPrecio;

    // Botones de la interfaz
    @FXML
    private Button btnSiguiente;
    @FXML
    private Button btnRegistrar;
    @FXML
    private Button btnBuscar;
    @FXML
    private Button btnAtras;

    // DAOs y modelos
    private final PasajeroDao pasajeroDao;
    private final AsientoDao asientoDAO;
    private ViajeBusModel viajeBusDAO;
    private BusDao busDAO;
    private AsientoModel asientoSeleccionado;

    // Variables de registro de pasajeros
    private int busId;
    private int viajeId;
    private String origen;
    private String destino;
    private String fecha;
    private int asientoId;
    private double precio;

    // Mapa de botones de asientos y pasajeros
    private final Map<Integer, Button> botonesAsientos;
    private Button botonSeleccionado;
    private Set<Integer> asientosRegistrados = new HashSet<>();
    private List<PasajeroData> listaPasajeros;

    // Controlador principal
    private InterfazPrincipalController mainController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        
        // Agregar estilos CSS a la tabla de pasajeros
        gridAsientos.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            }
        });

        // Validar el campo txtDNI para que solo acepte números y tenga un máximo de 8 dígitos
        txtDNI.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtDNI.setText(newValue.replaceAll("[^\\d]", ""));
            }
            if (newValue.length() > 8) {
                txtDNI.setText(newValue.substring(0, 8));
            }
        });

        // Validar el campo txtTelefono para que solo acepte números y tenga un máximo de 9 dígitos
        txtTelefono.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtTelefono.setText(newValue.replaceAll("[^\\d]", ""));
            }
            if (newValue.length() > 9) {
                txtTelefono.setText(newValue.substring(0, 9));
            }
        });

        // Validar el campo txtNombre para que solo acepte letras y tenga un máximo de 50 caracteres
        txtNombre.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[a-zA-Z]*")) {
                txtNombre.setText(newValue.replaceAll("[^a-zA-Z]", ""));
            }
            if (newValue.length() > 50) {
                txtNombre.setText(newValue.substring(0, 50));
            }
        });

        // Validar el campo txtApellido para que solo acepte letras y tenga un máximo de 50 caracteres
        txtApellido.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[a-zA-Z]*")) {
                txtApellido.setText(newValue.replaceAll("[^a-zA-Z]", ""));
            }
            if (newValue.length() > 50) {
                txtApellido.setText(newValue.substring(0, 50));
            }
        });

        // Asignacion de eventos a los botones
        btnSiguiente.setOnAction(event -> continuarCompra());
        btnBuscar.setOnAction(event -> buscarPasajero());
        btnRegistrar.setOnAction(event -> registrarPasajero());
        btnAtras.setOnAction(event -> volverAInterfazPrincipal());

        // Inicializar la tabla de pasajeros
        colNombre.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        colDNI.setCellValueFactory(cellData -> cellData.getValue().dniProperty());
        colTelefono.setCellValueFactory(cellData -> cellData.getValue().telefonoProperty());
        colEmail.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
        colAsiento.setCellValueFactory(cellData -> cellData.getValue().asientoProperty());
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));

        // Agregar botón de eliminar a la tabla de pasajeros
        agregarBotonEliminar();
    }

    public BusPiso1Controller() {
        // Inicializar los DAOs
        asientoDAO = new AsientoDao();
        busDAO = new BusDao();
        pasajeroDao = new PasajeroDao();
        botonesAsientos = new HashMap<>();
    }

    // Setters para los datos de la interfaz al abrir la ventana o regresar a ella
    public void setAsientosRegistrados(Set<Integer> asientosRegistrados) {
        this.asientosRegistrados = asientosRegistrados;
    }

    public void setListaPasajeros(List<PasajeroData> listaPasajeros) {
        this.listaPasajeros = listaPasajeros;
        tblPasajeros.getItems().addAll(listaPasajeros);
    }

    public void setDatosViaje(int viajeId, String origen, String destino, String fecha) {
        this.viajeId = viajeId;
        this.origen = origen;
        this.destino = destino;
        this.fecha = fecha;
    }

    public void setBusId(int busId) {
        this.busId = busId;
        inicializarAsientos();
    }

    public void setMainController(InterfazPrincipalController mainController) {
        this.mainController = mainController;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public void mostrarDescripcionServicios(String descripcionServicios) {
        txtServicios.setText(descripcionServicios);
    }

    // Método para limpiar los campos de texto
    private void limpiarCampos() {
        txtDNI.clear();
        txtNombre.clear();
        txtApellido.clear();
        txtTelefono.clear();
        txtEmail.clear();
        txtAsientoPasajero.clear();
        txtPrecioAsiento.clear();
    }

    // Método para mostrar una alerta de información
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String cabecera, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(cabecera);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // Método para inicializar los asientos del bus
    private void inicializarAsientos() {
        // Limpiar el grid de asientos
        gridAsientos.getChildren().clear();

        // Obtener los asientos del bus
        List<AsientoModel> asientos = asientoDAO.obtenerAsientosPorBus(busId);

        // Crear los botones de los asientos
        int columna = 1;
        int fila = 3;
        for (AsientoModel asiento : asientos) {
            // Crear un botón para el asiento
            Button btn = new Button(asiento.getNumero());
            btn.setMinSize(50, 50);
            btn.setMaxSize(50, 50);
            botonesAsientos.put(asiento.getId(), btn);

            // Establecer el estilo del botón según el estado del asiento
            if (asiento.getEstado().equals("OCUPADO")) {
                btn.getStyleClass().add("asiento-ocupado");
                btn.setDisable(true); // No permitir seleccionar asientos ocupados
            } else {
                btn.getStyleClass().add("asiento-disponible");
                btn.setOnAction(e -> handleAsientoClick(asiento));
            }

            // Agregar el botón al grid de asientos
            gridAsientos.add(btn, columna, fila);

            // Actualizar la fila y columna
            fila--;
            if (fila < 0) {
                fila = 3;
                columna++;
            }
        }

        // Agregar el botón del conductor
        Button btnConductor = new Button("Conductor");
        btnConductor.setMinSize(50, 50);
        btnConductor.getStyleClass().add("asiento-conductor");
        gridAsientos.add(btnConductor, 0, 3);
    }

    // Método para manejar el evento de clic en un asiento
    private void handleAsientoClick(AsientoModel asiento) {
        if (asiento.getEstado().equals("OCUPADO")) {
            mostrarAlerta(Alert.AlertType.WARNING, "Error", "Asiento no disponible", "Este asiento ya está ocupado");
            return;
        }

        // Restaurar el estilo del asiento previamente seleccionado si no está registrado
        if (botonSeleccionado != null && !asientosRegistrados.contains(asientoSeleccionado.getId())) {
            botonSeleccionado.getStyleClass().remove("asiento-seleccionado");
            botonSeleccionado.getStyleClass().add("asiento-disponible");
        }

        // Actualizar el asiento y botón seleccionados
        botonSeleccionado = botonesAsientos.get(asiento.getId());
        botonSeleccionado.getStyleClass().remove("asiento-disponible");
        botonSeleccionado.getStyleClass().add("asiento-seleccionado");

        asientoSeleccionado = asiento;
        this.asientoId = asiento.getId();

        // Mostrar información del asiento seleccionado
        txtAsientoPasajero.setText(asiento.getNumero());
        txtPrecioAsiento.setText(String.format("S/ %.2f", precio));
    }

    // Método para agregar el botón de eliminar a la tabla de pasajeros
    private void agregarBotonEliminar() {

        // Crear la columna de acciones
        Callback<TableColumn<PasajeroData, Void>, TableCell<PasajeroData, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<PasajeroData, Void> call(final TableColumn<PasajeroData, Void> param) {
                final TableCell<PasajeroData, Void> cell = new TableCell<>() {

                    private final Button btn = new Button("ELIMINAR");

                    {
                        btn.setOnAction((event) -> {
                            // Obtener el pasajero seleccionado
                            PasajeroData pasajeroData = getTableView().getItems().get(getIndex());

                            // Mostrar una alerta de confirmación
                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.setTitle("Confirmación de eliminación");
                            alert.setHeaderText("Eliminar pasajero");
                            alert.setContentText("¿Está seguro de que desea eliminar este pasajero?");

                            // Obtener la respuesta del usuario
                            alert.showAndWait().ifPresent(response -> {
                                if (response == ButtonType.OK) {
                                    // Eliminar el pasajero de la tabla
                                    getTableView().getItems().remove(pasajeroData);

                                    // Habilitar el asiento correspondiente
                                    AsientoModel asiento = asientoDAO.obtenerAsientoPorId(pasajeroData.getAsientoId());
                                    asiento.setEstado("DISPONIBLE");
                                    asientoDAO.actualizarAsiento(asiento);

                                    // Cambiar el estilo del botón del asiento a 'asiento-disponible'
                                    Button botonAsiento = botonesAsientos.get(pasajeroData.getAsientoId());
                                    if (botonAsiento != null) {
                                        botonAsiento.getStyleClass().remove("asiento-registrado");
                                        botonAsiento.getStyleClass().add("asiento-disponible");
                                    }
                                }
                            });
                        });
                    }

                    // Actualizar la celda con el botón de eliminar
                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        };

        colAcciones.setCellFactory(cellFactory);
    }

    // Método para buscar un pasajero por DNI
    @FXML
    private void buscarPasajero() {
        // Validar el campo DNI
        String dni = txtDNI.getText().trim();
        if (dni.isEmpty()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Campo incompleto", "El campo DNI no puede estar vacío");
            return;
        }

        // Buscar el pasajero en la base de datos
        PasajeroModel pasajero = pasajeroDao.obtenerPasajeroPorDNI(dni);
        if (pasajero != null) {
            // Establecer los valores en los campos de texto
            String[] nombreCompleto = pasajero.getNombre().split(" ", 2);
            txtNombre.setText(nombreCompleto[0]);
            txtApellido.setText(nombreCompleto.length > 1 ? nombreCompleto[1] : "");
            txtTelefono.setText(pasajero.getTelefono());
            txtEmail.setText(pasajero.getEmail());

            // Mostrar mensaje de éxito indicando que el pasajero fue encontrado en la base de datos
            mostrarAlerta(Alert.AlertType.INFORMATION,
                    "Búsqueda",
                    "Pasajero encontrado en la base de datos",
                    "El pasajero ha sido encontrado en la base de datos");
        } else {
            // Intentar buscar en la API
            try {
                // Configura el número de DNI
                // Crea el objeto JSON con el DNI
                JSONObject jsonInput = new JSONObject();
                jsonInput.put("dni", dni);

                // Crear el cliente HTTP
                HttpClient client = HttpClient.newHttpClient();

                // Crear la solicitud HTTP
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://apiperu.dev/api/dni"))
                        .header("Accept", "application/json")
                        .header("Content-Type", "application/json")
                        .header("Authorization",
                                "Bearer 8ed569d7ad7d912fc1d248c883ef4a752c4522e1f5e7e6534cbf9fe05c29a4b2")
                        .POST(HttpRequest.BodyPublishers.ofString(jsonInput.toString()))
                        .build();

                // Enviar la solicitud y obtener la respuesta
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                // Parsear la respuesta JSON
                JSONObject jsonResponse = new JSONObject(response.body());

                // Verificar si la solicitud fue exitosa
                if (jsonResponse.getBoolean("success")) {
                    JSONObject data = jsonResponse.getJSONObject("data");

                    // Obtener solo nombres y apellido paterno
                    String nombres = data.getString("nombres");
                    String apellidoPaterno = data.getString("apellido_paterno");

                    // Obtener solo el primer nombre
                    String primerNombre = nombres.split(" ")[0];

                    // Establecer los valores en los campos de texto
                    txtNombre.setText(primerNombre);
                    txtApellido.setText(apellidoPaterno);
                    txtTelefono.clear(); // Limpiar el campo de teléfono ya que no se obtiene de la API
                    txtEmail.clear(); // Limpiar el campo de email ya que no se obtiene de la API

                    // Mostrar mensaje de éxito indicando que el pasajero fue encontrado en la API
                    mostrarAlerta(Alert.AlertType.INFORMATION,
                            "Búsqueda",
                            "Pasajero encontrado en la API",
                            "El pasajero ha sido encontrado en la API");
                } else {
                    // Mostrar mensaje de error indicando que el pasajero no fue encontrado
                    mostrarAlerta(Alert.AlertType.INFORMATION,
                            "Búsqueda",
                            "Pasajero no encontrado",
                            "No se encontró un pasajero con el DNI especificado en la base de datos ni en la API");
                    limpiarCampos();
                }
            } catch (IOException | InterruptedException e) {
                // Mostrar mensaje de error indicando que ocurrió un error al consultar la API
                e.printStackTrace();
                mostrarAlerta(Alert.AlertType.ERROR,
                        "Error",
                        "Error en la consulta a la API",
                        "Ocurrió un error al intentar consultar la API");
            }
        }
    }

    // Método para registrar un pasajero en la tabla
    @FXML
    private void registrarPasajero() {
        // Obtener valores de los campos
        String dni = txtDNI.getText().trim();
        String nombre = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String email = txtEmail.getText().trim();
        String asiento = txtAsientoPasajero.getText().trim();
        String precioTexto = txtPrecioAsiento.getText().trim().replace("S/ ", "").replace(",", ".");

        // Validar campos obligatorios
        if (dni.isEmpty() || nombre.isEmpty() || apellido.isEmpty() || asiento.isEmpty() || precioTexto.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Error", "Campos incompletos",
                    "Todos los campos obligatorios deben estar completos");
            return;
        }

        // Validar el precio
        double precio;
        try {
            precio = Double.parseDouble(precioTexto);
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Error", "Precio inválido", "El precio debe ser un número válido");
            return;
        }

        // Verificar si el pasajero ya está registrado en la tabla
        for (PasajeroData pasajero : tblPasajeros.getItems()) {
            if (pasajero.getDni().equals(dni)) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Informacion", "Registro de Pasajero",
                        "El pasajero con DNI " + dni + " ya está registrado");
                limpiarCampos();
                return;
            }
        }

        // Verificar si el asiento ya está registrado en la tabla
        for (PasajeroData pasajero : tblPasajeros.getItems()) {
            if (pasajero.getAsientoId() == asientoSeleccionado.getId()) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Informacion", "Registro de Pasajero",
                        "El asiento " + asiento + " ya está registrado");
                limpiarCampos();
                return;
            }
        }

        // Crear y agregar el pasajero a la tabla
        PasajeroData pasajeroData = new PasajeroData(nombre + " " + apellido, dni, telefono, email, asiento, precio,
                asientoSeleccionado.getId());
        tblPasajeros.getItems().add(pasajeroData);

        // Agregar el ID del asiento a la lista de asientos registrados
        asientosRegistrados.add(asientoSeleccionado.getId());

        // Cambiar el estilo del botón a 'asiento-registrado'
        botonSeleccionado.getStyleClass().remove("asiento-seleccionado");
        botonSeleccionado.getStyleClass().add("asiento-registrado");

        // Limpiar selección actual
        asientoSeleccionado = null;
        botonSeleccionado = null;

        limpiarCampos();
    }

    // Método para continuar con la compra
    @FXML
    private void continuarCompra() {
        if (tblPasajeros.getItems().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Error", "Registro de pasajeros",
                    "Debe registrar al menos un pasajero para continuar");
            return;
        }
        // Si hay pasajeros, abrir la siguiente vista
        abrirRegistroCliente();
    }

    // Método para abrir la ventana de RegistroCliente
    private void abrirRegistroCliente() {
        try {
            // Cargar la vista de RegistroCliente
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/RegistroCliente.fxml"));
            Parent root = loader.load();

            // Obtener el controlador de RegistroCliente y establecer los datos
            RegistroClienteController registroController = loader.getController();
            registroController.setDatosAdicionales(true, new ArrayList<>(asientosRegistrados));
            registroController.setDatosViaje(origen, destino, fecha, viajeId);
            registroController.setEmpleadoId(Session.getInstance().getEmpleadoId());
            registroController.setListaPasajeros(new ArrayList<>(tblPasajeros.getItems()));
            registroController.setPrecio(precio);

            // Mostrar la ventana de RegistroCliente
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Hace la ventana modal
            stage.setTitle("Via Costa - Registro de Compra");
            stage.show();

            // Cerrar la ventana actual
            Stage currentStage = (Stage) btnSiguiente.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.WARNING, "Error", "Carga de interfaz",
                    "No se pudo cargar la vista de RegistroCliente");
        }
    }

    // Método para volver a la InterfazPrincipal
    private void volverAInterfazPrincipal() {
        // Obtener la ventana actual
        Stage currentStage = (Stage) btnAtras.getScene().getWindow();

        // Cerrar todas las ventanas abiertas
        for (Window window : Stage.getWindows()) {
            if (window instanceof Stage) {
                ((Stage) window).close();
            }
        }

        // Cargar la InterfazPrincipal
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/InterfazPrincipal.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Interfaz Principal");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error de Interfaz",
                    "No se pudo cargar la Interfaz Principal");
        }

        // Cerrar la ventana actual
        currentStage.close();
    }

}