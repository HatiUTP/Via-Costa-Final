package com.example.viacostafx.Controller;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.example.viacostafx.Auxiliar.PasajeroData;
import com.example.viacostafx.Modelo.BoletoModel;
import com.example.viacostafx.Modelo.BusModel;
import com.example.viacostafx.Modelo.ChoferModel;
import com.example.viacostafx.Modelo.DetalleCompraModel;
import com.example.viacostafx.Modelo.PasajeroModel;
import com.example.viacostafx.Modelo.ViajeBusModel;
import com.example.viacostafx.Modelo.ViajeModel;
import com.example.viacostafx.Servicio.ExcelPasajeros;
import com.example.viacostafx.dao.AgenciaDao;
import com.example.viacostafx.dao.BoletoDao;
import com.example.viacostafx.dao.BusDao;
import com.example.viacostafx.dao.ViajeDao;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

public class InformePasajeroController implements Initializable {

    @FXML
    private ComboBox<String> origenCombo;

    @FXML
    private ComboBox<String> destinoCombo;

    @FXML
    private DatePicker viajeDate;

    @FXML
    private TableView<ViajeModel> tablaViajes;

    @FXML
    private TableColumn<ViajeModel, String> colOrigen;

    @FXML
    private TableColumn<ViajeModel, String> colDestino;

    @FXML
    private TableColumn<ViajeModel, String> colHora;

    @FXML
    private TableColumn<ViajeModel, String> colTipoBus;

    @FXML
    private TableColumn<ViajeModel, String> colPlaca;

    @FXML
    private Button btnBuscar;

    @FXML
    private TableView<PasajeroData> tablaPasajeros;

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
    private Button btnExportar;

    private List<String> todosLosDistritos;

    private BoletoDao boletoDao;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        boletoDao = new BoletoDao();
        cargarDistritosEnComboBox();

        // Configurar las columnas de la tabla
        configurarColumnasTabla();

        btnBuscar.setOnAction(event -> buscarYCargarViajesEnTabla());
        btnExportar.setOnAction(event -> exportarAPasajerosExcel());

        // Configurar evento de doble clic en la tabla de viajes
        tablaViajes.setRowFactory(tv -> {
            TableRow<ViajeModel> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    ViajeModel viajeSeleccionado = row.getItem();
                    cargarPasajerosDelViaje(viajeSeleccionado);
                }
            });
            return row;
        });
    }

    // Método para mostrar una alerta
    private void mostrarAlerta(Alert.AlertType alertType, String title, String mensaje) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // Método para configurar las columnas de la tabla
    private void configurarColumnasTabla() {
        colOrigen.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getAgenciaOrigen().getUbigeo().getDistrito()));
        colDestino.setCellValueFactory(
                cellData -> new SimpleStringProperty(
                        cellData.getValue().getAgenciaDestino().getUbigeo().getDistrito()));
        colHora.setCellValueFactory(
                cellData -> new SimpleStringProperty(
                        cellData.getValue().getFechaHoraSalida().toLocalTime().toString()));
        colTipoBus.setCellValueFactory(
                cellData -> new SimpleStringProperty(obtenerTipoBus(cellData.getValue())));
        colPlaca.setCellValueFactory(
                cellData -> new SimpleStringProperty(obtenerPlacaBus(cellData.getValue())));

        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDNI.setCellValueFactory(new PropertyValueFactory<>("dni"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colAsiento.setCellValueFactory(cellData -> new SimpleStringProperty(obtenerNumeroAsiento(cellData.getValue())));
    }

    // Método para cargar los pasajeros del viaje
    private void cargarPasajerosDelViaje(ViajeModel viaje) {
        try {
            // Obtener los boletos del viaje
            List<BoletoModel> boletos = boletoDao.obtenerBoletosPorViajeId(viaje.getId());
            List<PasajeroData> pasajeros = new ArrayList<>();

            // Recorrer los boletos y sus detalles para obtener los pasajeros
            for (BoletoModel boleto : boletos) {
                for (DetalleCompraModel detalle : boleto.getDetalles()) {
                    PasajeroModel pasajero = detalle.getPasajero();
                    pasajeros.add(new PasajeroData(
                            pasajero.getNombre(),
                            pasajero.getDni(),
                            pasajero.getTelefono(),
                            pasajero.getEmail(),
                            boleto.getAsiento().getNumero(),
                            detalle.getPrecio().doubleValue(),
                            boleto.getAsiento().getId()));
                }
            }

            // Cargar los pasajeros en la tabla
            tablaPasajeros.setItems(FXCollections.observableArrayList(pasajeros));

            if (!pasajeros.isEmpty()) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Lista de pasajeros encontrada");
            } else {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Información",
                        "No se encontraron pasajeros para este viaje");
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al cargar los pasajeros del viaje");
        }
    }

    // Método para obtener el tipo de bus
    private String obtenerTipoBus(ViajeModel viaje) {
        BusModel bus = obtenerBusDelViaje(viaje);
        if (bus != null && bus.getCategoria() != null) {
            return bus.getCategoria().getNombre();
        }
        return "Desconocido";
    }

    // Método para obtener la placa del bus
    private String obtenerPlacaBus(ViajeModel viaje) {
        BusModel bus = obtenerBusDelViaje(viaje);
        if (bus != null) {
            return bus.getPlaca();
        }
        return "Desconocida";
    }

    // Método para obtener el número de asiento
    private BusModel obtenerBusDelViaje(ViajeModel viaje) {
        if (viaje != null && viaje.getViajeBuses() != null && !viaje.getViajeBuses().isEmpty()) {
            for (ViajeBusModel viajeBus : viaje.getViajeBuses()) {
                if (viajeBus != null && viajeBus.getBus() != null) {
                    BusDao busDao = new BusDao();
                    return busDao.obtenerBusConChoferesPorId(viajeBus.getBus().getId());
                }
            }
        }
        return null;
    }

    // Método para obtener el número de asiento
    private String obtenerNumeroAsiento(PasajeroData pasajero) {
        return pasajero.getAsiento();
    }

    // Método para cargar los distritos en los ComboBox
    private void cargarDistritosEnComboBox() {
        // Obtener los distritos con agencias
        todosLosDistritos = AgenciaDao.obtenerDistritosConAgencias();

        ObservableList<String> distritosList = FXCollections.observableArrayList(todosLosDistritos);

        // Cargar los distritos en los ComboBox
        origenCombo.setItems(FXCollections.observableArrayList(distritosList));
        destinoCombo.setItems(FXCollections.observableArrayList(distritosList));

        origenCombo.setPromptText("Seleccione origen");
        destinoCombo.setPromptText("Seleccione destino");

        // Actualizar los ComboBox al seleccionar un distrito
        origenCombo.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            actualizarDestinoCombo(newValue);
        });

        // Actualizar los ComboBox al seleccionar un distrito
        destinoCombo.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            actualizarOrigenCombo(newValue);
        });
    }

    // Método para actualizar el ComboBox de destino
    private void actualizarDestinoCombo(String origenSeleccionado) {
        // Obtener los distritos actualizados
        if (origenSeleccionado != null) {
            // Crear una lista con todos los distritos
            List<String> destinosActualizados = new ArrayList<>(todosLosDistritos);
            destinosActualizados.remove(origenSeleccionado);
            destinoCombo.setItems(FXCollections.observableArrayList(destinosActualizados));

            // Limpiar el ComboBox de destino si el origen y destino son iguales
            if (origenSeleccionado.equals(destinoCombo.getValue())) {
                destinoCombo.setValue(null);
            }
        } else {
            destinoCombo.setItems(FXCollections.observableArrayList(todosLosDistritos));
        }
    }

    // Método para actualizar el ComboBox de origen
    private void actualizarOrigenCombo(String destinoSeleccionado) {
        // Obtener los distritos actualizados
        if (destinoSeleccionado != null) {
            // Crear una lista con todos los distritos
            List<String> origenesActualizados = new ArrayList<>(todosLosDistritos);
            origenesActualizados.remove(destinoSeleccionado);
            origenCombo.setItems(FXCollections.observableArrayList(origenesActualizados));

            // Limpiar el ComboBox de origen si el origen y destino son iguales
            if (destinoSeleccionado.equals(origenCombo.getValue())) {
                origenCombo.setValue(null);
            }
        } else {
            origenCombo.setItems(FXCollections.observableArrayList(todosLosDistritos));
        }
    }

    // Método para buscar y cargar los viajes en la tabla
    private void buscarYCargarViajesEnTabla() {
        // Obtener los datos seleccionados
        String origen = origenCombo.getSelectionModel().getSelectedItem();
        String destino = destinoCombo.getSelectionModel().getSelectedItem();
        LocalDate fechaSeleccionada = viajeDate.getValue();

        if (origen == null || destino == null || fechaSeleccionada == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Debe seleccionar origen, destino y fecha de viaje.");
            return;
        }

        // Obtener los viajes disponibles
        List<ViajeModel> viajesDisponibles = ViajeDao.obtenerViajesDisponibles(origen, destino, fechaSeleccionada);

        // Mostrar los viajes en la tabla
        if (viajesDisponibles == null || viajesDisponibles.isEmpty()) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Información",
                    "No hay viajes disponibles para los criterios seleccionados.");
            tablaViajes.setItems(FXCollections.observableArrayList());
        } else {
            tablaViajes.setItems(FXCollections.observableArrayList(viajesDisponibles));
        }
    }

    // Método para exportar los pasajeros a un archivo Excel
    private void exportarAPasajerosExcel() {
        // Verificar si hay datos para exportar
        if (tablaPasajeros.getItems().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "No hay datos para exportar");
            return;
        }

        // Verificar si se ha seleccionado un viaje
        ViajeModel viajeSeleccionado = tablaViajes.getSelectionModel().getSelectedItem();
        if (viajeSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Debe seleccionar un viaje");
            return;
        }

        // Mostrar el cuadro de diálogo para guardar el archivo
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Excel");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        fileChooser.setInitialFileName("Pasajeros_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx");

        File file = fileChooser.showSaveDialog(tablaPasajeros.getScene().getWindow());

        // Exportar los datos a un archivo Excel
        if (file != null) {
            try {
                // Obtener el bus con sus choferes
                BusModel bus = obtenerBusDelViaje(viajeSeleccionado);

                String categoria = "No especificada";
                List<ChoferModel> choferes = new ArrayList<>();

                if (bus != null) {
                    if (bus.getCategoria() != null) {
                        categoria = bus.getCategoria().getNombre();
                    }
                    // Los choferes ya estarán inicializados
                    choferes = bus.getChoferes();
                }

                ExcelPasajeros excelExporter = new ExcelPasajeros();
                excelExporter.exportPasajeros(
                        tablaPasajeros.getItems(),
                        file.getAbsolutePath(),
                        viajeSeleccionado.getAgenciaOrigen().getUbigeo().getDistrito(),
                        viajeSeleccionado.getAgenciaDestino().getUbigeo().getDistrito(),
                        viajeSeleccionado.getFechaHoraSalida(),
                        obtenerPlacaBus(viajeSeleccionado),
                        categoria,
                        choferes);

                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Archivo Excel generado correctamente");
            } catch (Exception e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al generar el archivo Excel");
                e.printStackTrace();
            }
        }
    }
}