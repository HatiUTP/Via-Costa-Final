package com.example.viacostafx.Controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import org.json.JSONObject;

import com.example.viacostafx.Auxiliar.PasajeroData;
import com.example.viacostafx.Modelo.AsientoModel;
import com.example.viacostafx.Modelo.BoletoModel;
import com.example.viacostafx.Modelo.ClienteModel;
import com.example.viacostafx.Modelo.CompraModel;
import com.example.viacostafx.Modelo.ComprobantePagoModel;
import com.example.viacostafx.Modelo.DetalleCompraModel;
import com.example.viacostafx.Modelo.EmpleadosModel;
import com.example.viacostafx.Modelo.PasajeroModel;
import com.example.viacostafx.Modelo.ViajeModel;
import com.example.viacostafx.dao.AsientoDao;
import com.example.viacostafx.dao.BoletoDao;
import com.example.viacostafx.dao.ClienteDao;
import com.example.viacostafx.dao.CompraDao;
import com.example.viacostafx.dao.ComprobantePagoDao;
import com.example.viacostafx.dao.DetalleCompraDao;
import com.example.viacostafx.dao.EmpleadosDao;
import com.example.viacostafx.dao.PasajeroDao;
import com.example.viacostafx.dao.ViajeBusDao;
import com.example.viacostafx.dao.ViajeDao;

import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.stage.Window;

public class RegistroClienteController implements Initializable {

    // Campos de texto para los datos del cliente y resumen de la compra
    @FXML
    private TextField txtOrigen;
    @FXML
    private TextField txtDestino;
    @FXML
    private TextField txtFecha;
    @FXML
    private TextField txtTotal;
    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtApellido;
    @FXML
    private TextField txtDNI;
    @FXML
    private TextField txtTelefono;

    // Tabla para mostrar el resumen de los pasajeros
    @FXML
    private TableView<PasajeroData> tblResumen;
    @FXML
    private TableColumn<PasajeroData, String> colNombre;
    @FXML
    private TableColumn<PasajeroData, String> colDNI;
    @FXML
    private TableColumn<PasajeroData, String> colAsiento;
    @FXML
    private TableColumn<PasajeroData, Double> colPrecio;


    // Boton para regresar a la interfaz anterior
    @FXML
    private Button btnAtras;

    // Variables para almacenar los datos de compra
    private int empleadoId;
    private int viajeId;
    private List<Integer> listaAsientosId;
    private String origen;
    private String destino;
    private String fecha;
    private double precio;
    private List<PasajeroData> listaPasajeros;

    // Instancias de los DAO necesarios
    private ClienteDao clienteDao;
    private ComprobantePagoDao comprobantePagoDao;
    private CompraDao compraDao;
    private BoletoDao boletoDao;
    private AsientoDao asientoDao;
    private EmpleadosDao empleadosDao;
    private ViajeDao viajeDao;
    private DetalleCompraDao detalleCompraDao;
    private ViajeBusDao viajeBusDao;

    // Variable para identificar que tipo de bus se está utilizando
    private boolean esBusPiso1;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // Agregar estilos CSS a la tabla de pasajeros
        tblResumen.sceneProperty().addListener((obs, oldScene, newScene) -> {
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

        // Inicializar las instancias de los DAO
        clienteDao = new ClienteDao();
        comprobantePagoDao = new ComprobantePagoDao();
        compraDao = new CompraDao();
        boletoDao = new BoletoDao();
        asientoDao = new AsientoDao();
        empleadosDao = new EmpleadosDao();
        viajeDao = new ViajeDao();
        detalleCompraDao = new DetalleCompraDao();
        viajeBusDao = new ViajeBusDao();

        // Configurar las columnas de la tabla de pasajeros
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDNI.setCellValueFactory(new PropertyValueFactory<>("dni"));
        colAsiento.setCellValueFactory(new PropertyValueFactory<>("asiento"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colPrecio.setCellFactory(tc -> new TableCell<PasajeroData, Double>() {
            @Override
            protected void updateItem(Double precio, boolean empty) {
                super.updateItem(precio, empty);
                if (empty || precio == null) {
                    setText(null);
                } else {
                    setText(String.format("S/ %.2f", precio));
                }
            }
        });

        // Configurar el botón de regresar
        btnAtras.setOnAction(event -> handleAtras());
    }

    // Setters para los datos de la interfaz al abrir la ventana o regresar a ella
    public void setDatosAdicionales(boolean esBusPiso1, List<Integer> asientosId) {
        this.listaAsientosId = asientosId;
        this.esBusPiso1 = esBusPiso1;
        String fechaConHora = obtenerFechaConHora();
        txtFecha.setText(fechaConHora);
    }

    
    public void setDatosViaje(String origen, String destino, String fecha, int viajeId) {
        this.viajeId = viajeId;
        this.origen = origen;
        this.destino = destino;
        this.fecha = fecha;
        txtOrigen.setText(origen);
        txtDestino.setText(destino);
        String fechaConHora = obtenerFechaConHora();
        txtFecha.setText(fechaConHora);
    }

    public void setListaPasajeros(List<PasajeroData> pasajeros) {
        this.listaPasajeros = pasajeros;
        tblResumen.getItems().addAll(pasajeros);
        calcularTotal();
    }

    public void setEmpleadoId(int empleadoId) {
        this.empleadoId = empleadoId;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    private void calcularTotal() {
        double total = 0;
        for (PasajeroData pasajero : listaPasajeros) {
            total += pasajero.getPrecio();
        }
        txtTotal.setText(String.format("S/ %.2f", total));
    }

    private String obtenerFechaConHora() {
        if (viajeId <= 0) {
            return "ID de viaje no válido";
        }

        ViajeDao viajeDao = new ViajeDao();
        ViajeModel viaje = viajeDao.obtenerViajePorId(viajeId);
        if (viaje != null && viaje.getFechaHoraSalida() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            return viaje.getFechaHoraSalida().format(formatter);
        }
        return "Fecha no disponible";
    }

    // Método para limpiar los campos de cliente
    private void limpiarCamposCliente() {
        txtNombre.setText("");
        txtApellido.setText("");
        txtTelefono.setText("");
    }

    // Método para validar los campos obligatorios
    private boolean validarCamposObligatorios() {
        if (txtNombre.getText().trim().isEmpty() ||
                txtApellido.getText().trim().isEmpty() ||
                txtDNI.getText().trim().isEmpty()) {

            mostrarAlerta(Alert.AlertType.ERROR,
                    "Error de validación",
                    "Campos incompletos",
                    "Por favor complete los campos obligatorios (Nombre, Apellido y DNI)");
            return false;
        }
        return true;
    }

    // Método para mostrar una alerta
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String cabecera, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(cabecera);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    // Método para obtener todas las ventanas abiertas
    private List<Stage> getStages() {
        List<Stage> stages = new ArrayList<>();
        for (Window window : Stage.getWindows()) {
            if (window instanceof Stage) {
                stages.add((Stage) window);
            }
        }
        return stages;
    }

    // Método para validar los campos obligatorios
    @FXML
    private void handleBuscarCliente() {
        // Validar que el campo DNI no esté vacío
        if (txtDNI.getText().trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.ERROR,
                    "Error",
                    "DNI vacío",
                    "Por favor ingrese un DNI para buscar");
            return;
        }

        // Buscar el cliente en la base de datos
        String dni = txtDNI.getText().trim();
        ClienteModel cliente = clienteDao.obtenerClientePorDNI(dni);

        if (cliente != null) {
            // Separar el nombre completo
            String nombreCompleto = cliente.getNombre();
            String[] partes = nombreCompleto.split(" ", 2); // Divide en máximo 2 partes

            if (partes.length > 1) {
                txtNombre.setText(partes[0]);
                txtApellido.setText(partes[1]);
            } else {
                txtNombre.setText(nombreCompleto);
                txtApellido.setText("");
            }

            if (cliente.getTelefono() != null) {
                txtTelefono.setText(cliente.getTelefono());
            }

            mostrarAlerta(Alert.AlertType.INFORMATION,
            "Búsqueda",
            "Pasajero encontrado en la base de datos",
            "El cliente ha sido encontrado en la base de datos");
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

                    // Mostrar mensaje de éxito
                    mostrarAlerta(Alert.AlertType.INFORMATION,
                            "Búsqueda",
                            "Cliente encontrado en la API",
                            "El cliente ha sido encontrado en la API");
                } else {
                    mostrarAlerta(Alert.AlertType.INFORMATION,
                            "Búsqueda",
                            "Cliente no encontrado",
                            "No se encontró un cliente con el DNI especificado en la base de datos ni en la API");
                    limpiarCamposCliente();
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

    // Metodo para registrar la compra
    @FXML
    private void handleGenerarBoleta() {
        // Validar campos obligatorios
        if (!validarCamposObligatorios()) {
            return;
        }

        String dni = txtDNI.getText().trim();
        String nombreCompleto = txtNombre.getText().trim() + " " + txtApellido.getText().trim();
        String telefono = txtTelefono.getText().trim();

        // Buscar cliente existente
        ClienteModel clienteExistente = clienteDao.obtenerClientePorDNI(dni);

        int clienteId;
        if (clienteExistente != null) {
            clienteId = clienteExistente.getId();
            System.out.println("Cliente existente encontrado, ID: " + clienteId);
        } else {
            // Crear nuevo cliente
            ClienteModel nuevoCliente = new ClienteModel();
            nuevoCliente.setNombre(nombreCompleto);
            nuevoCliente.setDni(dni);
            nuevoCliente.setTelefono(telefono);

            clienteId = clienteDao.registrarCliente(nuevoCliente);
            System.out.println("Nuevo cliente registrado, ID: " + clienteId);
        }

        if (clienteId <= 0) {
            mostrarAlerta(Alert.AlertType.ERROR,
                    "Error",
                    "Error en el registro",
                    "No se pudo procesar el cliente");
            return;
        }

        PasajeroDao pasajeroDao = new PasajeroDao();

        for (PasajeroData pasajeroData : listaPasajeros) {
            String dniPasajero = pasajeroData.getDni();

            // Corregido: usar dniPasajero en lugar de dni
            PasajeroModel pasajeroExistente = pasajeroDao.obtenerPasajeroPorDNI(dniPasajero);

            if (pasajeroExistente != null) {
                System.out.println("Pasajero existente encontrado - DNI: " + dniPasajero +
                        ", ID: " + pasajeroExistente.getId());
                // Aquí puedes guardar el ID del pasajero existente si lo necesitas
            } else {
                try {
                    PasajeroModel nuevoPasajero = new PasajeroModel();
                    nuevoPasajero.setNombre(pasajeroData.getNombre());
                    nuevoPasajero.setDni(pasajeroData.getDni());
                    nuevoPasajero.setEmail(pasajeroData.getEmail());
                    nuevoPasajero.setTelefono(pasajeroData.getTelefono());

                    int nuevoId = pasajeroDao.registrarPasajero(nuevoPasajero);
                    if (nuevoId > 0) {
                        System.out.println("Nuevo pasajero registrado - DNI: " + dniPasajero +
                                ", ID: " + nuevoId);
                    } else {
                        mostrarAlerta(Alert.AlertType.ERROR,
                                "Error",
                                "Error al registrar pasajero",
                                "No se pudo registrar el pasajero con DNI: " + dniPasajero);
                    }
                } catch (Exception e) {
                    mostrarAlerta(Alert.AlertType.ERROR,
                            "Error",
                            "Error al registrar pasajero",
                            "El DNI " + dniPasajero + " ya existe en la base de datos");
                }
            }
        }

        // Crear registro de Compra
        // Obtener el precio total del campo txtTotal quitando el formato de moneda
        String precioTexto = txtTotal.getText().replace("S/ ", "").replace(",", ".");
        BigDecimal precioTotal = new BigDecimal(precioTexto);
        CompraModel compra = new CompraModel();
        ClienteModel cliente = clienteDao.obtenerClientePorId(clienteId);
        compra.setCliente(cliente);
        EmpleadosModel empleado = empleadosDao.obtenerEmpleadoPorId(empleadoId);
        compra.setEmpleado(empleado);
        compra.setPrecioTotal(precioTotal);
        int compraId = compraDao.registrarCompra(compra);
        System.out.println("Compra registrada, ID: " + compraId);

        if (compraId <= 0) {
            mostrarAlerta(Alert.AlertType.ERROR,
                    "Error",
                    "Error en el registro",
                    "No se pudo procesar la compra");
            return;
        }

        // Crear registro de Comprobante_Pago
        ComprobantePagoModel comprobantePago = new ComprobantePagoModel();
        comprobantePago.setCompra(compra); // Reutilizar la instancia de compra ya definida
        comprobantePago.setFechaEmision(LocalDateTime.now());
        comprobantePagoDao.registrarComprobantePago(comprobantePago);
        System.out.println("Comprobante de pago registrado");

        // Crear registros de Boleto y actualizar estado de asientos
        for (PasajeroData pasajeroData : listaPasajeros) {
            BoletoModel boleto = new BoletoModel();
            boleto.setFechaHoraCompra(LocalDateTime.now());

            AsientoModel asiento = asientoDao.obtenerAsientoPorId(pasajeroData.getAsientoId());
            boleto.setAsiento(asiento);

            ViajeModel viaje = viajeDao.obtenerViajePorId(viajeId);
            boleto.setViaje(viaje);

            boletoDao.registrarBoleto(boleto);
            System.out.println("Boleto registrado para asiento ID: " + pasajeroData.getAsientoId());

            // Actualizar estado del asiento a "OCUPADO"
            asiento.setEstado("OCUPADO");
            asientoDao.actualizarAsiento(asiento);
            System.out.println("Estado del asiento actualizado a OCUPADO para ID: " + pasajeroData.getAsientoId());

            // Crear registro de Detalle_Compra
            DetalleCompraModel detalleCompra = new DetalleCompraModel();
            detalleCompra.setFechaHoraCompra(LocalDateTime.now());
            detalleCompra.setPrecio(BigDecimal.valueOf(pasajeroData.getPrecio()));
            detalleCompra.setBoleto(boleto);
            detalleCompra.setCompra(compra);
            PasajeroModel pasajero = pasajeroDao.obtenerPasajeroPorDNI(pasajeroData.getDni());
            detalleCompra.setPasajero(pasajero);

            detalleCompraDao.registrarDetalleCompra(detalleCompra);
            System.out.println("Detalle de compra registrado para boleto ID: " + boleto.getId());

            // Generar boleto PDF para cada pasajero
            generarBoletoPDF(pasajeroData, pasajeroData.getAsientoId(), compra);
        }

        generarBoletaPDF(compra, listaPasajeros, listaPasajeros.size());
        mostrarAlerta(Alert.AlertType.INFORMATION,
                "Éxito",
                "Registro completado",
                "La compra y los boletos se han registrado correctamente");

        // Cerrar todas las ventanas abiertas
        Stage currentStage = (Stage) txtDNI.getScene().getWindow();
        for (Stage stage : getStages()) {
            if (stage != currentStage) {
                stage.close();
            }
        }
        currentStage.close();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/InterfazPrincipal.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Interfaz Principal");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Interrupcion de Interfaz",
                    "No se pudo cargar la Interfaz Principal");
        }
    }

    // Metodo para generar boleta PDF
    public void generarBoletaPDF(CompraModel compra, List<PasajeroData> listaPasajeros, int cantidadAsientos) {
        Document document = new Document(PageSize.A4.rotate());
        try {
            // Generar un nombre de archivo único usando la fecha y hora actual
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            String fileName = "boleta_venta_" + compra.getCliente().getNombre() + " _ " + timestamp + ".pdf";

            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();

            // Fuentes personalizadas
            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

            // Encabezado
            PdfPTable headerTable = new PdfPTable(3); // 3 columnas
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new float[] { 2, 4, 4 }); // Ancho relativo de cada columna

            // Columna izquierda: Logo
            PdfPCell logoCell = new PdfPCell();
            logoCell.setBorder(Rectangle.NO_BORDER);
            URL imageUrl = getClass().getResource("/Images/ImagenLogin1.jpeg");
            if (imageUrl == null) {
                throw new IOException("No se encontró la imagen en el classpath.");
            }
            Image logo = Image.getInstance(imageUrl);
            logo.scaleToFit(80, 50);
            logoCell.addElement(logo);
            headerTable.addCell(logoCell);

            // Columna central: Información de la empresa (con borde)
            PdfPCell companyInfoCell = new PdfPCell();
            companyInfoCell.setBorder(Rectangle.BOX); // Borde visible
            companyInfoCell.setPadding(5); // Espaciado interno
            companyInfoCell.addElement(new Paragraph("Via Costa Express S.R.L", boldFont));
            companyInfoCell.addElement(new Paragraph("Av. Materiales N° 2215", normalFont));
            companyInfoCell.addElement(new Paragraph("Distrito Huaraz, Provincia Huaraz", normalFont));
            companyInfoCell.addElement(new Paragraph("Teléfono: 01-7168000", normalFont));
            headerTable.addCell(companyInfoCell);

            // Columna derecha: Detalles de la boleta (RUC, Boleta, etc.)
            PdfPCell invoiceDetailsCell = new PdfPCell();
            invoiceDetailsCell.setBorder(Rectangle.NO_BORDER); // Sin borde
            PdfPTable nestedTable = new PdfPTable(1);
            nestedTable.setWidthPercentage(100);
            nestedTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);
            nestedTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            nestedTable.addCell(new Paragraph("R.U.C.: 20602002358", boldFont));
            nestedTable.addCell(new Paragraph("BOLETA ELECTRÓNICA", boldFont));
            nestedTable.addCell(new Paragraph("N°: BB02-00343125", boldFont));
            nestedTable.addCell(new Paragraph(
                    "FECHA EMISIÓN: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    normalFont));
            invoiceDetailsCell.addElement(nestedTable);
            headerTable.addCell(invoiceDetailsCell);

            document.add(headerTable);
            document.add(Chunk.NEWLINE);

            // Datos del cliente
            PdfPTable clientInfoTable = new PdfPTable(2);
            clientInfoTable.setWidthPercentage(100);
            clientInfoTable.setWidths(new float[] { 2, 1 });

            PdfPCell clientInfoCell = new PdfPCell();
            clientInfoCell.setBorder(Rectangle.NO_BORDER);
            clientInfoCell.addElement(new Paragraph("TIPO DOC.: DNI", normalFont));
            clientInfoCell.addElement(new Paragraph("N° DOC.: " + compra.getCliente().getDni(), normalFont));
            clientInfoCell.addElement(new Paragraph("SEÑOR(ES): " + compra.getCliente().getNombre(), normalFont));
            clientInfoCell.addElement(new Paragraph("ORIGEN: " + origen, normalFont));
            clientInfoCell.addElement(new Paragraph("DESTINO: " + destino, normalFont));
            clientInfoTable.addCell(clientInfoCell);

            PdfPCell emptyCell = new PdfPCell();
            emptyCell.setBorder(Rectangle.NO_BORDER);
            clientInfoTable.addCell(emptyCell);

            document.add(clientInfoTable);
            document.add(Chunk.NEWLINE);

            // Detalle del servicio
            PdfPTable serviceTable = new PdfPTable(4);
            serviceTable.setWidthPercentage(100);
            serviceTable.setWidths(new float[] { 4, 1, 1, 1 });

            // Encabezados
            serviceTable.addCell(new PdfPCell(new Phrase("DETALLE DEL SERVICIO", boldFont)));
            serviceTable.addCell(new PdfPCell(new Phrase("CAN", boldFont)));
            serviceTable.addCell(new PdfPCell(new Phrase("TARIFA", boldFont)));
            serviceTable.addCell(new PdfPCell(new Phrase("SUBTOTAL", boldFont)));

            serviceTable.addCell(new PdfPCell(new Phrase(
                    "VTA. PASAJE:\n[PAX: " + compra.getCliente().getNombre() + " (DNI: " + compra.getCliente().getDni()
                            + ")]",
                    normalFont)));
            serviceTable.addCell(new PdfPCell(new Phrase(String.valueOf(cantidadAsientos), normalFont)));
            serviceTable.addCell(new PdfPCell(new Phrase(String.format("S/ %.2f", precio, normalFont))));
            serviceTable
                    .addCell(new PdfPCell(new Phrase(String.format("S/ %.2f", compra.getPrecioTotal()), normalFont)));

            document.add(serviceTable);
            document.add(Chunk.NEWLINE);

            // Totales
            PdfPTable totalTable = new PdfPTable(2);
            totalTable.setWidthPercentage(50);
            totalTable.setHorizontalAlignment(Element.ALIGN_RIGHT);

            totalTable.addCell(new PdfPCell(new Phrase("TOTAL A PAGAR:", boldFont)));
            totalTable.addCell(new PdfPCell(new Phrase(String.format("S/ %.2f", compra.getPrecioTotal()), boldFont)));

            document.add(totalTable);
            document.add(Chunk.NEWLINE);

            // Pie de página
            Paragraph footer = new Paragraph("¡Gracias por viajar con Via Costa Express S.R.L!", boldFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            document.close();
        }
    }

    // Método para generar boleto PDF
    public void generarBoletoPDF(PasajeroData pasajero, int numeroAsiento, CompraModel compra) {
        Document document = new Document(PageSize.A5.rotate()); // Tamaño A5 horizontal
        try {
            // Generar un nombre de archivo único para cada pasajero
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            String fileName = "boleto_" + pasajero.getNombre() + "_" + timestamp + ".pdf";

            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();

            // Fuentes personalizadas
            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

            // Encabezado con logo
            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new float[] { 2, 8 });

            PdfPCell logoCell = new PdfPCell();
            logoCell.setBorder(Rectangle.NO_BORDER);
            URL imageUrl = getClass().getResource("/Images/ImagenLogin1.jpeg");
            if (imageUrl == null) {
                throw new IOException("No se encontró la imagen en el classpath.");
            }
            Image logo = Image.getInstance(imageUrl);
            logo.scaleToFit(80, 50);
            logoCell.addElement(logo);
            headerTable.addCell(logoCell);

            PdfPCell companyInfoCell = new PdfPCell();
            companyInfoCell.setBorder(Rectangle.NO_BORDER);
            companyInfoCell.addElement(new Paragraph("Via Costa Express S.R.L", boldFont));
            companyInfoCell.addElement(new Paragraph("Av. Materiales N° 2215", normalFont));
            companyInfoCell.addElement(new Paragraph("Distrito Huaraz, Provincia Huaraz", normalFont));
            companyInfoCell.addElement(new Paragraph("Teléfono: 01-7168000", normalFont));
            headerTable.addCell(companyInfoCell);

            document.add(headerTable);
            document.add(Chunk.NEWLINE);

            // Información del pasajero
            PdfPTable passengerInfoTable = new PdfPTable(2);
            passengerInfoTable.setWidthPercentage(100);
            passengerInfoTable.setWidths(new float[] { 2, 8 });

            // Obtener el asiento completo
            AsientoModel asiento = asientoDao.obtenerAsientoPorId(numeroAsiento);

            PdfPCell passengerCell = new PdfPCell();
            passengerCell.setBorder(Rectangle.NO_BORDER);
            passengerCell.addElement(new Paragraph("NOMBRE: " + pasajero.getNombre(), normalFont));
            passengerCell.addElement(new Paragraph("N° DOC.: " + pasajero.getDni(), normalFont));

            // Aquí accedemos al número del asiento y lo mostramos
            if (asiento != null) {
                passengerCell.addElement(new Paragraph("N° ASIENTO: " + asiento.getNumero(), normalFont));
            } else {
                passengerCell.addElement(new Paragraph("N° ASIENTO: No disponible", normalFont));
            }

            passengerInfoTable.addCell(passengerCell);

            PdfPCell viajeCell = new PdfPCell();
            viajeCell.setBorder(Rectangle.NO_BORDER);
            viajeCell.addElement(new Paragraph("ORIGEN: " + origen, normalFont));
            viajeCell.addElement(new Paragraph("DESTINO: " + destino, normalFont));
            viajeCell.addElement(new Paragraph("FECHA VIAJE: " + fecha, normalFont));

            // Obtener la hora de salida del viaje
            ViajeModel viaje = viajeDao.obtenerViajePorId(viajeId);
            if (viaje != null && viaje.getFechaHoraSalida() != null) {
                String horaSalida = viaje.getFechaHoraSalida().format(DateTimeFormatter.ofPattern("HH:mm"));
                viajeCell.addElement(new Paragraph("HORA SALIDA: " + horaSalida, normalFont));
            } else {
                viajeCell.addElement(new Paragraph("HORA SALIDA: No disponible", normalFont));
            }

            passengerInfoTable.addCell(viajeCell);

            document.add(passengerInfoTable);
            document.add(Chunk.NEWLINE);

            // Mensaje final
            Paragraph footer = new Paragraph("¡Gracias por viajar con Via Costa Express S.R.L!", boldFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            document.close();
        }
    }

    // Método para regresar a la interfaz anterior
    @FXML
    private void handleAtras() {
        try {
            int busId = viajeBusDao.obtenerBusIdPorViajeId(viajeId);
            if (busId == -1) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error de Interfaz", "No se pudo obtener el ID del bus");
                return;
            }

            // Cargar la interfaz del bus correspondiente
            FXMLLoader loader;
            if (esBusPiso1) {
                loader = new FXMLLoader(getClass().getResource("/GUI/BusPiso1.fxml"));
            } else {
                loader = new FXMLLoader(getClass().getResource("/GUI/BusPiso1y2.fxml"));
            }
            Parent root = loader.load();

            // Pasar los datos necesarios a la interfaz del bus
            if (esBusPiso1) {
                BusPiso1Controller busController = loader.getController();
                busController.setBusId(busId);
                busController.setListaPasajeros(listaPasajeros);
                busController.setDatosViaje(viajeId, origen, destino, fecha);
                busController.setPrecio(precio);
                busController.setAsientosRegistrados(new HashSet<>(listaAsientosId));
            } else {
                BusPiso1y2Controller busController = loader.getController();
                busController.setBusId(busId);
                busController.setListaPasajeros(listaPasajeros);
                busController.setDatosViaje(viajeId, origen, destino, fecha);
                busController.setPrecio(precio);
                busController.setAsientosRegistrados(new HashSet<>(listaAsientosId));
            }

            Stage stage = (Stage) btnAtras.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error de Interfaz", "No se pudo cargar la interfaz del bus");
        }
    }
}