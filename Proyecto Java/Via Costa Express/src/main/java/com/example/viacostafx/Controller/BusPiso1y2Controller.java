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
import com.example.viacostafx.dao.AsientoDao;
import com.example.viacostafx.dao.BusDao;
import com.example.viacostafx.dao.PasajeroDao;


import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Callback;

public class BusPiso1y2Controller implements Initializable {

    // Campos de texto para ingresar los datos del pasajero
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
    private TextArea txtServicios;
    @FXML
    private TextField txtAsientoPasajero;
    @FXML
    private TextField txtPrecioAsiento;

    // Elementos de los asientos y la tabla de pasajeros
    @FXML
    private GridPane gridAsientosPiso1;
    @FXML
    private GridPane gridAsientosPiso2;
    @FXML
    private TableView<PasajeroData> tblPasajeros;
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
    private TableColumn<PasajeroData, Double> colPrecio;
    @FXML
    private TableColumn<PasajeroData, Void> colAcciones;

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
    private PasajeroDao pasajeroDao;
    private AsientoDao asientoDAO;
    private BusDao busDAO;
    private AsientoModel asientoSeleccionado;

    // Variables de registro de pasajeros
    private int busId;
    private int viajeId;
    private String origen;
    private String destino;
    private String fecha;
    private double precio;
    private int asientoId;

    // Mapa de botones de asientos y pasajeros
    private Map<Integer, Button> botonesAsientos;
    private Button botonSeleccionado;
    private Set<Integer> asientosRegistrados;

    // Controlador principal
    private InterfazPrincipalController mainController;
    private List<PasajeroData> listaPasajeros;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

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

        // Inicializar DAOs y variables
        asientoDAO = new AsientoDao();
        busDAO = new BusDao();
        pasajeroDao = new PasajeroDao();
        botonesAsientos = new HashMap<>();
        asientosRegistrados = new HashSet<>();

        configurarTabla();
        configurarBotones();
    }

    // Setters para los datos de la interfaz al abrir la ventana o regresar a ella
    public void setBusId(int busId) {
        this.busId = busId;
        inicializarAsientos();
    }

    public void setDatosViaje(int viajeId, String origen, String destino, String fecha) {
        this.viajeId = viajeId;
        this.origen = origen;
        this.destino = destino;
        this.fecha = fecha;
    }

    public void setListaPasajeros(List<PasajeroData> listaPasajeros) {
        this.listaPasajeros = listaPasajeros;
        tblPasajeros.getItems().addAll(listaPasajeros);
    }

    public void mostrarDescripcionServicios(String descripcionServicios) {
        txtServicios.setText(descripcionServicios);
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public void setMainController(InterfazPrincipalController mainController) {
        this.mainController = mainController;
    }

    public void setAsientosRegistrados(Set<Integer> asientosRegistrados) {
        this.asientosRegistrados = asientosRegistrados;
    }

    // Método para mostrar una alerta de información
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String cabecera, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(cabecera);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // Metodo para limpiar los campos de texto
    private void limpiarCampos() {
        txtDNI.clear();
        txtNombre.clear();
        txtApellido.clear();
        txtTelefono.clear();
        txtEmail.clear();
        txtAsientoPasajero.clear();
        txtPrecioAsiento.clear();
    }

    // Método para inicializar los asientos del bus
    private void inicializarAsientos() {
        // Limpiar los GridPane y el mapa de botones
        gridAsientosPiso1.getChildren().clear();
        gridAsientosPiso2.getChildren().clear();
        botonesAsientos.clear(); // Limpiar el mapa de botones

        List<AsientoModel> asientos = asientoDAO.obtenerAsientosPorBus(busId);
        System.out.println("Cantidad de asientos: " + asientos.size());

        // Inicializar primer piso (asientos 1-42)
        int columna = 1;
        int fila = 3;
        for (AsientoModel asiento : asientos) {
            if (Integer.parseInt(asiento.getNumero()) <= 42) {
                // Crear un botón para el asiento
                Button btn = new Button(asiento.getNumero());
                btn.setMinSize(50, 50);
                btn.setMaxSize(50, 50);
                botonesAsientos.put(asiento.getId(), btn);

                if (asiento.getEstado().equals("OCUPADO")) {
                    btn.getStyleClass().add("asiento-ocupado");
                    btn.setDisable(true);
                } else if (asientosRegistrados.contains(asiento.getId())) {
                    btn.getStyleClass().add("asiento-registrado");
                } else {
                    btn.getStyleClass().add("asiento-disponible");
                    btn.setOnAction(e -> handleAsientoClick(asiento));
                }

                // Agregar el botón al GridPane
                gridAsientosPiso1.add(btn, columna, fila);

                // Actualizar las coordenadas del GridPane
                fila--;
                if (fila < 0) {
                    fila = 3;
                    columna++;
                }
            }
        }

        // Agregar conductor en primer piso
        Button btnConductor = new Button("C");
        btnConductor.setMinSize(50, 50);
        btnConductor.setMaxSize(50, 50);
        btnConductor.setDisable(true);
        btnConductor.getStyleClass().add("asiento-conductor");
        gridAsientosPiso1.add(btnConductor, 0, 3);

        // Inicializar segundo piso (asientos 43-51)
        columna = 1;
        fila = 2;
        for (AsientoModel asiento : asientos) {
            if (Integer.parseInt(asiento.getNumero()) > 42) {
                // Crear un botón para el asiento
                Button btn = new Button(asiento.getNumero());
                btn.setMinSize(50, 50);
                btn.setMaxSize(50, 50);
                botonesAsientos.put(asiento.getId(), btn);

                if (asiento.getEstado().equals("OCUPADO")) {
                    btn.getStyleClass().add("asiento-ocupado");
                    btn.setDisable(true);
                } else if (asientosRegistrados.contains(asiento.getId())) {
                    btn.getStyleClass().add("asiento-registrado");
                } else {
                    btn.getStyleClass().add("asiento-disponible");
                    btn.setOnAction(e -> handleAsientoClick(asiento));
                }

                // Agregar el botón al GridPane
                gridAsientosPiso2.add(btn, columna, fila);

                // Actualizar las coordenadas del GridPane
                fila--;
                if (fila < 0) {
                    fila = 2;
                    columna++;
                }
            }
        }
    }

    // Configurar los botones de la interfaz
    private void configurarBotones() {
        // Configurar estilos CSS para el primer piso
        gridAsientosPiso1.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            }
        });

        // Configurar estilos CSS para el segundo piso
        gridAsientosPiso2.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            }
        });

        // Configurar los eventos de los botones
        btnSiguiente.setOnAction(event -> continuarCompra());
        btnBuscar.setOnAction(event -> buscarPasajero());
        btnRegistrar.setOnAction(event -> registrarPasajero());
        btnAtras.setOnAction(event -> volverAInterfazPrincipal());
    }

    // Manejar el evento de clic en un asiento
    private void handleAsientoClick(AsientoModel asiento) {
        if (asiento.getEstado().equals("OCUPADO")) {
            mostrarAlerta(Alert.AlertType.WARNING, "Error", "Asiento no disponible", "Este asiento ya está ocupado");
            return;
        }

        // Restaurar el estilo del asiento previamente seleccionado si no está
        // registrado
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

        // Calcular el precio del asiento
        double precioFinal = precio;
        if (Integer.parseInt(asiento.getNumero()) >= 43 && Integer.parseInt(asiento.getNumero()) <= 51) {
            precioFinal += 25; // Aumentar 25 unidades al precio si el asiento está en el rango de 43 a 51
        }

        // Mostrar información del asiento seleccionado
        txtAsientoPasajero.setText(asiento.getNumero());
        txtPrecioAsiento.setText(String.format("S/ %.2f", precioFinal));
    }

    // Configurar la tabla de pasajeros
    private void configurarTabla() {
        // Configurar las columnas de la tabla
        colNombre.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        colDNI.setCellValueFactory(cellData -> cellData.getValue().dniProperty());
        colTelefono.setCellValueFactory(cellData -> cellData.getValue().telefonoProperty());
        colEmail.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
        // Configurar la columna de asiento para que muestre el número de asiento
        colAsiento.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<PasajeroData, String>, ObservableValue<String>>() {
                    @Override
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<PasajeroData, String> cellData) {
                        return cellData.getValue().asientoProperty();
                    }
                });
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        agregarBotonEliminar();
    }

    // Agregar botón de eliminar a la tabla de pasajeros
    private void agregarBotonEliminar() {
        // Crear una celda personalizada para la columna de acciones
        Callback<TableColumn<PasajeroData, Void>, TableCell<PasajeroData, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<PasajeroData, Void> call(final TableColumn<PasajeroData, Void> param) {
                final TableCell<PasajeroData, Void> cell = new TableCell<>() {

                    private final Button btn = new Button("ELIMINAR");

                    {
                        btn.setOnAction((event) -> {
                            // Obtener el pasajero seleccionado
                            PasajeroData pasajeroData = getTableView().getItems().get(getIndex());

                            // Mostrar un mensaje de confirmación
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

                    // Configurar el botón en la celda
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

    // Metodo para buscar un pasajero por DNI
    @FXML
    private void buscarPasajero() {
        // Obtener el DNI ingresado
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
                    mostrarAlerta(Alert.AlertType.INFORMATION,
                            "Búsqueda",
                            "Pasajero no encontrado",
                            "No se encontró un pasajero con el DNI especificado en la base de datos ni en la API");
                    limpiarCampos();
                }

            } catch (IOException | InterruptedException e) {
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
    private void continuarCompra() {
        // Verificar si hay pasajeros registrados
        if (tblPasajeros.getItems().isEmpty()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Sin registros", "Debe registrar al menos un pasajero");
            return;
        }
        abrirRegistroCliente();
    }

    // Método para abrir la ventana de RegistroCliente
    private void abrirRegistroCliente() {
        try {
            // Cargar la vista de RegistroCliente
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/RegistroCliente.fxml"));
            Parent root = loader.load();

            // Obtener el controlador de la vista cargada
            RegistroClienteController registroController = loader.getController();

            // Establecer los datos necesarios en el controlador
            registroController.setDatosAdicionales(false, new ArrayList<>(asientosRegistrados));
            registroController.setDatosViaje(origen, destino, fecha, viajeId);
            registroController.setEmpleadoId(Session.getInstance().getEmpleadoId());
            registroController.setListaPasajeros(new ArrayList<>(tblPasajeros.getItems()));
            registroController.setPrecio(precio);

            // Mostrar la ventana de RegistroCliente
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Hace la ventana modal
            stage.setTitle("Via Costa Registro de Compra"); // Añade un título a la ventana
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