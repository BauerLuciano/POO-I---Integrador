package main.java.com.eventos.controlador;

import com.eventos.modelo.Evento;
import com.eventos.repo.EventoRepository;
import com.eventos.repo.EventoRepositoryImpl;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader; // Necesario para cargar la otra ventana
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality; // Para que la ventana bloquee a la de atrás
import javafx.stage.Stage;

import java.time.LocalDateTime;

public class MainController {

    // --- TABLA Y COLUMNAS ---
    @FXML private TableView<Evento> tablaEventos;
    @FXML private TableColumn<Evento, Long> colId;
    @FXML private TableColumn<Evento, String> colNombre;
    @FXML private TableColumn<Evento, LocalDateTime> colFecha;
    @FXML private TableColumn<Evento, String> colEstado;
    @FXML private TableColumn<Evento, String> colTipo;

    private EventoRepository repo = new EventoRepositoryImpl();

    @FXML
    public void initialize() {
        configurarColumnas();
        cargarEventos();
    }

    private void configurarColumnas() {
        // Enlazamos las columnas con los atributos de la clase Evento
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaInicio"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        // Truco para mostrar el TIPO de clase (Taller, Concierto, etc)
        colTipo.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getClass().getSimpleName())
        );
    }

    @FXML
    public void cargarEventos() {
        try {
            tablaEventos.getItems().clear();
            // Traemos todo de la base de datos
            ObservableList<Evento> lista = FXCollections.observableArrayList(repo.listarTodos());
            tablaEventos.setItems(lista);
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo cargar la lista: " + e.getMessage());
        }
    }

    // --- ACÁ ESTÁ LA MAGIA: ABRIR LA VENTANA DE ALTA ---
    @FXML
    public void abrirNuevoEvento() {
        try {
            // 1. Cargamos el archivo FXML de la ventana nueva
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/evento.fxml"));
            Parent root = loader.load();

            // 2. Creamos el escenario (Stage)
            Stage stage = new Stage();
            stage.setTitle("Alta de Nuevo Evento");
            stage.setScene(new Scene(root));
            
            // 3. Configuración Modal (No podés tocar la ventana de atrás hasta cerrar esta)
            stage.initModality(Modality.APPLICATION_MODAL);
            
            // 4. Mostramos y esperamos a que cierre
            stage.showAndWait();

            // 5. Cuando se cierra, refrescamos la tabla para ver el nuevo evento
            cargarEventos();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir la ventana de alta: " + e.getMessage());
        }
    }

    @FXML
    public void eliminarEvento() {
        Evento seleccionado = tablaEventos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Atención", "Seleccioná un evento para borrar.");
            return;
        }
        try {
            repo.eliminar(seleccionado.getId());
            cargarEventos(); 
            mostrarAlerta("Éxito", "Evento eliminado.");
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo eliminar: " + e.getMessage());
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}