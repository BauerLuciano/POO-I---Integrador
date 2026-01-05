package com.eventos.controlador;

import com.eventos.modelo.Evento;
import com.eventos.modelo.Taller; // Importante para validar si es taller
import com.eventos.repo.EventoRepository;
import com.eventos.repo.EventoRepositoryImpl;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
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

        // Truco para mostrar el TIPO de clase (Taller, Concierto, etc.)
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

    // --- ABRIR ALTA DE EVENTO (Polimórfica) ---
    @FXML
    public void abrirNuevoEvento() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/evento.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Alta de Nuevo Evento");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Bloquea la ventana de atrás
            stage.showAndWait();

            cargarEventos(); // Refrescar al volver

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir la ventana de alta: " + e.getMessage());
        }
    }

    // --- NUEVO: ABRIR INSCRIPCIÓN (Solo para Talleres) ---
    @FXML
    public void abrirInscripcion() {
        // 1. Obtener el evento seleccionado en la tabla
        Evento seleccionado = tablaEventos.getSelectionModel().getSelectedItem();
        
        if (seleccionado == null) {
            mostrarAlerta("Atención", "Seleccioná un evento de la lista primero.");
            return;
        }

        // 2. Validar que sea un Taller (Polimorfismo: instanceof)
        if (!(seleccionado instanceof Taller)) {
            mostrarAlerta("Acción no válida", "La inscripción solo está disponible para Talleres.\nEl evento seleccionado es un: " + seleccionado.getClass().getSimpleName());
            return;
        }

        try {
            // 3. Cargar el FXML de Inscripción
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/inscripcion.fxml"));
            Parent root = loader.load();

            // 4. Pasar los datos al controlador de inscripción
            InscripcionController controller = loader.getController();
            controller.initData((Taller) seleccionado); // Le pasamos el taller elegido

            // 5. Mostrar ventana modal
            Stage stage = new Stage();
            stage.setTitle("Inscripción a Taller");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
            // Refrescar tabla al volver (por si cambió el estado/cupo)
            cargarEventos();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir la inscripción: " + e.getMessage());
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

	@FXML
    public void editarEvento() {
        Evento seleccionado = tablaEventos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Atención", "Seleccioná un evento para editar.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/evento.fxml"));
            Parent root = loader.load();

            // PASO CLAVE: Obtener el controlador y pasarle el evento
            EventoController controller = loader.getController();
            controller.initData(seleccionado); 

            Stage stage = new Stage();
            stage.setTitle("Editar Evento");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            cargarEventos(); // Refrescar la tabla al volver

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir la edición: " + e.getMessage());
        }
    }
}