package com.example.viacostafx.Controller;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.example.viacostafx.Modelo.BusModel;
import com.example.viacostafx.Modelo.ViajeBusModel;
import com.example.viacostafx.Modelo.ViajeModel;
import com.example.viacostafx.dao.AgenciaDao;
import com.example.viacostafx.dao.ViajeDao;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class InterfazPrincipalController implements Initializable {

    // Datos para buscar viajes
    @FXML
    private ComboBox<String> destinoCombo;
    @FXML
    private DatePicker viajeDate;
    @FXML
    private ComboBox<String> origenCombo;

    // Tabla de viajes
    @FXML
    private GridPane Tabla1;
    @FXML
    private TableView<ViajeModel> tablaViajes;
    @FXML
    private TableColumn<ViajeModel, String> origenColumn;
    @FXML
    private TableColumn<ViajeModel, String> destinoColumn;
    @FXML
    private TableColumn<ViajeModel, String> horaSalidaColumn;
    @FXML
    private TableColumn<ViajeModel, String> tipoBusColumn;
    @FXML
    private TableColumn<ViajeModel, String> disponibilidadColumn;
    @FXML
    private TableColumn<ViajeModel, String> precioColumn;
    @FXML
    private AnchorPane mainContent;
    @FXML
    private Pane contentArea;

    // Botones
    @FXML
    private Button btnBuscar;
    @FXML
    private Button btnSalir;

    // Panel de registro de usuarios
    private Node registroUsuariosPanel;
    private List<Node> elementosOriginalesTabla1;

    // Lista de distritos
    private List<String> todosLosDistritos;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Guardar los elementos originales de la tabla
        elementosOriginalesTabla1 = new ArrayList<>(Tabla1.getChildren());

        // Configurar los eventos de los botones
        btnBuscar.setOnAction(event -> cargarPanel());
        btnSalir.setOnAction(event -> salirPrograma());

        cargarDistritosEnComboBox();

        // Restringir la selección de fechas pasadas
        restrictPastDates(viajeDate);

        // Configurar las columnas de la tabla
        origenColumn.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getAgenciaOrigen().getUbigeo().getDistrito()));
        destinoColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getAgenciaDestino().getUbigeo().getDistrito()));
        horaSalidaColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getFechaHoraSalida().toLocalTime().toString()));
        tipoBusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(obtenerTipoBus(cellData.getValue())));
        disponibilidadColumn
                .setCellValueFactory(cellData -> new SimpleStringProperty(obtenerDisponibilidad(cellData.getValue())));
        precioColumn.setCellValueFactory(
                cellData -> new SimpleStringProperty(String.format("S/ %.2f", calcularPrecio(cellData.getValue()))));
    }

    // Restringir la selección de fechas pasadas
    private void restrictPastDates(DatePicker datePicker) {
        datePicker.setDayCellFactory(param -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);

                if (item.isBefore(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;");
                }
            }
        });
    }

    // Cargar el panel de búsqueda de viajes
    @FXML
    private void cargarPanel() {
        Tabla1.getChildren().removeIf(node -> GridPane.getRowIndex(node) != null && GridPane.getRowIndex(node) == 0);
        cargarTablaPrincipall();

        buscarYCargarViajesEnTabla();
    }

    // Cargar los elementos originales de la tabla
    private void cargarTablaPrincipall() {
        Tabla1.getChildren().clear();
        Tabla1.getChildren().addAll(elementosOriginalesTabla1);
    }

    // Mostrar un mensaje de alerta
    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // Cargar los distritos en los ComboBox
    private void cargarDistritosEnComboBox() {
        todosLosDistritos = AgenciaDao.obtenerDistritosConAgencias();

        // Crear un ObservableList a partir de la lista de distritos
        ObservableList<String> distritosList = FXCollections.observableArrayList(todosLosDistritos);

        // Asignar la lista de distritos a los ComboBox
        origenCombo.setItems(FXCollections.observableArrayList(distritosList));
        destinoCombo.setItems(FXCollections.observableArrayList(distritosList));

        origenCombo.setPromptText("Seleccione origen");
        destinoCombo.setPromptText("Seleccione destino");

        // Agregar listeners para actualizar los ComboBox al seleccionar un ítem
        origenCombo.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            actualizarDestinoCombo(newValue);
        });

        destinoCombo.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            actualizarOrigenCombo(newValue);
        });

        // Configurar la tabla para manejar doble clic en un viaje
        tablaViajes.setRowFactory(tv -> {
            TableRow<ViajeModel> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    ViajeModel viajeSeleccionado = row.getItem();
                    manejarDobleClicEnViaje(viajeSeleccionado);
                }
            });
            return row;
        });
    }

    // Método para actualizar destinoCombo al cambiar origenCombo
    private void actualizarDestinoCombo(String origenSeleccionado) {
        if (origenSeleccionado != null) {
            // Crear una nueva lista para destinoCombo excluyendo el origen seleccionado
            List<String> destinosActualizados = new ArrayList<>(todosLosDistritos);
            destinosActualizados.remove(origenSeleccionado);
            destinoCombo.setItems(FXCollections.observableArrayList(destinosActualizados));

            // Si el destino seleccionado previamente fue el mismo que el nuevo origen,
            // limpiarlo
            if (origenSeleccionado.equals(destinoCombo.getValue())) {
                destinoCombo.setValue(null);
            }
        } else {
            // Si no hay selección en origenCombo, restaurar la lista completa en
            // destinoCombo
            destinoCombo.setItems(FXCollections.observableArrayList(todosLosDistritos));
        }
    }

    // Método para actualizar origenCombo al cambiar destinoCombo
    private void actualizarOrigenCombo(String destinoSeleccionado) {
        if (destinoSeleccionado != null) {
            // Crear una nueva lista para origenCombo excluyendo el destino seleccionado
            List<String> origenesActualizados = new ArrayList<>(todosLosDistritos);
            origenesActualizados.remove(destinoSeleccionado);
            origenCombo.setItems(FXCollections.observableArrayList(origenesActualizados));

            // Si el origen seleccionado previamente fue el mismo que el nuevo destino,
            // limpiarlo
            if (destinoSeleccionado.equals(origenCombo.getValue())) {
                origenCombo.setValue(null);
            }
        } else {
            // Si no hay selección en destinoCombo, restaurar la lista completa en
            // origenCombo
            origenCombo.setItems(FXCollections.observableArrayList(todosLosDistritos));
        }
    }

    // Obtiene la disponibilidad de asientos de un viaje
    private String obtenerDisponibilidad(ViajeModel viaje) {
        // Obtener el bus del viaje
        BusModel bus = obtenerBusDelViaje(viaje);
        if (bus != null) {
            if (bus.getAsientos() != null) {
                // Contar los asientos disponibles
                long asientosDisponibles = bus.getAsientos().stream()
                        .filter(asiento -> "DESOCUPADO".equalsIgnoreCase(asiento.getEstado()))
                        .count();
                return asientosDisponibles > 0 ? "Disponible (" + asientosDisponibles + " asientos)" : "Bus Lleno";
            } else {
                System.out.println("Asientos es null");
            }
        } else {
            System.out.println("Bus es null");
        }
        return "Desconocido";
    }

    // Cargar los viajes en la tabla
    private void cargarViajesEnTabla(List<ViajeModel> viajesDisponibles) {
        tablaViajes.setItems(FXCollections.observableArrayList(viajesDisponibles));
    }

    // Calcular el precio de un viaje
    private double calcularPrecio(ViajeModel viaje) {

        // Precio base
        double precioBase = 35.0;
        BusModel bus = obtenerBusDelViaje(viaje);

        // Si el bus tiene una categoría y un costo extra, sumarlo al precio base
        if (bus != null && bus.getCategoria() != null && bus.getCategoria().getCostoExtra() != null) {
            precioBase += bus.getCategoria().getCostoExtra().doubleValue();
        }

        return precioBase;
    }

    // Obtener el tipo de bus de un viaje
    private String obtenerTipoBus(ViajeModel viaje) {
        // Obtener el bus del viaje
        BusModel bus = obtenerBusDelViaje(viaje);

        // Si se obtiene el bus, mostrar la categoría
        if (bus != null) {
            System.out.println("Bus ID: " + bus.getId());
            if (bus.getCategoria() != null) {
                return bus.getCategoria().getNombre();
            } else {
                System.out.println("bus.getCategoria() es null");
            }
        } else {
            System.out.println("Bus es null");
        }
        return "Desconocido";
    }

    // Obtener el bus de un viaje
    private BusModel obtenerBusDelViaje(ViajeModel viaje) {
        // Si el viaje tiene buses, obtener el primer bus
        if (viaje.getViajeBuses() != null) {
            System.out.println("viaje.getViajeBuses().size(): " + viaje.getViajeBuses().size());
            if (!viaje.getViajeBuses().isEmpty()) {
                ViajeBusModel viajeBus = viaje.getViajeBuses().iterator().next();
                if (viajeBus != null) {
                    System.out.println("viajeBus ID: " + viajeBus.getId());
                    BusModel bus = viajeBus.getBus();
                    if (bus != null) {
                        System.out.println("Bus ID: " + bus.getId());
                        return bus;
                    } else {
                        System.out.println("viajeBus.getBus() es null");
                    }
                } else {
                    System.out.println("viajeBus es null");
                }
            } else {
                System.out.println("viaje.getViajeBuses() está vacío");
            }
        } else {
            System.out.println("viaje.getViajeBuses() es null");
        }
        return null;
    }

    // Buscar y cargar los viajes en la tabla
    private void buscarYCargarViajesEnTabla() {
        // Obtener los valores seleccionados en los ComboBox y DatePicker
        String origen = origenCombo.getSelectionModel().getSelectedItem();
        String destino = destinoCombo.getSelectionModel().getSelectedItem();
        LocalDate fechaSeleccionada = viajeDate.getValue();

        if (origen == null || destino == null || fechaSeleccionada == null) {
            // Mostrar mensaje de error indicando que deben seleccionar origen, destino y
            // fecha
            mostrarAlerta("Debe seleccionar origen, destino y fecha de viaje.");
            return;
        }

        // Obtener los viajes disponibles
        List<ViajeModel> viajesDisponibles = ViajeDao.obtenerViajesDisponibles(origen, destino, fechaSeleccionada);

        if (viajesDisponibles == null || viajesDisponibles.isEmpty()) {
            // Mostrar mensaje indicando que no hay viajes disponibles
            mostrarAlerta("No hay viajes disponibles para los criterios seleccionados.");
            return;
        }

        // Cargar los viajes en la tabla
        cargarViajesEnTabla(viajesDisponibles);
    }

    // Manejar el doble clic en un viaje
    private void manejarDobleClicEnViaje(ViajeModel viajeSeleccionado) {
        // Obtener el bus del viaje seleccionado
        BusModel bus = obtenerBusDelViaje(viajeSeleccionado);
        if (bus != null) {
            // Obtener la capacidad del bus, la descripción de los servicios y el precio
            int capacidad = bus.getCapacidad();
            String descripcionServicios = bus.getCategoria().getDescripcion();
            double precio = calcularPrecio(viajeSeleccionado);

            // Abrir la interfaz correspondiente al tipo de bus
            if (capacidad == 36) {
                abrirInterfaz("/GUI/BusPiso1.fxml", descripcionServicios, bus, precio, viajeSeleccionado);
            } else if (capacidad == 51) {
                abrirInterfaz("/GUI/BusPiso1y2.fxml", descripcionServicios, bus, precio, viajeSeleccionado);
            } else {
                mostrarAlerta("No se encontró una interfaz para la capacidad de " + capacidad + " asientos.");
            }
        } else {
            mostrarAlerta("No se pudo obtener la información del bus para el viaje seleccionado.");
        }
    }

    // Abrir la interfaz correspondiente al tipo de bus
    private void abrirInterfaz(String fxmlPath, String descripcionServicios, BusModel bus, double precio,
            ViajeModel viajeSeleccionado) {

        try {
            // Cargar la interfaz correspondiente al tipo de bus
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Pane root = loader.load();

            Object controller = loader.getController();

            // Configurar el controlador de la interfaz
            if (controller instanceof BusPiso1Controller) {
                BusPiso1Controller busController = (BusPiso1Controller) controller;
                // Establecer los datos del bus y del viaje
                busController.mostrarDescripcionServicios(descripcionServicios);
                busController.setBusId(bus.getId());
                busController.setPrecio(precio);
                busController.setDatosViaje(viajeSeleccionado.getId(), origenCombo.getValue(), destinoCombo.getValue(),
                        viajeDate.getValue().toString());
                busController.setMainController(this);
            } else if (controller instanceof BusPiso1y2Controller) {
                BusPiso1y2Controller busController = (BusPiso1y2Controller) controller;
                // Establecer los datos del bus y del viaje
                busController.mostrarDescripcionServicios(descripcionServicios);
                busController.setBusId(bus.getId());
                busController.setPrecio(precio);
                busController.setDatosViaje(viajeSeleccionado.getId(), origenCombo.getValue(), destinoCombo.getValue(),
                        viajeDate.getValue().toString());
                busController.setMainController(this);
            } else {
                mostrarAlerta("Controlador desconocido.");
                return;
            }

            // Crear una nueva escena y mostrarla en una nueva ventana
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Via Costa - Selección de Asiento");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error al cargar la interfaz.");
        }
    }

    // Cargar el panel de registro de usuarios
    @FXML
    private void onRegistroUsuariosClick() {
        try {
            if (registroUsuariosPanel == null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/RegistroEmpleado.fxml"));
                registroUsuariosPanel = (AnchorPane) loader.load();

                // Configurar el tamaño usando propiedades directamente
                AnchorPane.setTopAnchor(registroUsuariosPanel, 0.0);
                AnchorPane.setBottomAnchor(registroUsuariosPanel, 0.0);
                AnchorPane.setLeftAnchor(registroUsuariosPanel, 0.0);
                AnchorPane.setRightAnchor(registroUsuariosPanel, 0.0);
            }

            // Limpiar el área de contenido
            contentArea.getChildren().clear();

            contentArea.getChildren().add(registroUsuariosPanel);

        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error al cargar la vista");
            alert.setContentText("No se pudo cargar la vista de registro de usuarios: " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    // Cargar el panel de registro de choferes
    @FXML
    private void onRegistroChoferesClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/Chofer.fxml"));
            Pane choferPane = loader.load();

            // Obtener el controlador de Chofer
            ChoferController choferController = loader.getController();

            contentArea.getChildren().clear();
            contentArea.getChildren().add(choferPane);

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error al cargar la interfaz de Choferes: " + e.getMessage());
        }
    }

    // Cargar el panel de informe de pasajeros
    @FXML
    private void onRegistroPasajerosClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/InformePasajeros.fxml"));
            Pane choferPane = loader.load();

            // Obtener el controlador de Informe Pasajeros
            InformePasajeroController informePasajerosController = loader.getController();

            contentArea.getChildren().clear();
            contentArea.getChildren().add(choferPane);

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error al cargar la interfaz de Choferes: " + e.getMessage());
        }
    }

    // Cargar el panel del Menu Principal
    @FXML
    private void onMenuPrincipalClick() {
        try {
            // Limpiar el contenido actual
            mainContent.getChildren().clear();

            // Cargar la interfaz principal
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/InterfazPrincipal.fxml"));
            AnchorPane principalView = loader.load();

            // Configurar el nuevo contenido para que ocupe todo el espacio
            AnchorPane.setTopAnchor(principalView, 0.0);
            AnchorPane.setBottomAnchor(principalView, 0.0);
            AnchorPane.setLeftAnchor(principalView, 0.0);
            AnchorPane.setRightAnchor(principalView, 0.0);

            // Añadir la vista principal al mainContent
            mainContent.getChildren().add(principalView);

        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error al cargar la interfaz principal");
            alert.setContentText("No se pudo cargar la interfaz principal: " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    // Salir del programa
    private void salirPrograma() {
        Platform.exit();
        System.exit(0);
    }

}