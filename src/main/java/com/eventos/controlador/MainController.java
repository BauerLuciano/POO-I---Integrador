package com.eventos.controlador;

import com.eventos.modelo.CicloCine;
import com.eventos.modelo.Pelicula;
import com.eventos.modelo.Evento;
import com.eventos.modelo.Taller; // Necesario para validar inscripción
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
// --- IMPORTS CRÍTICOS PARA LA TABLA (JAVAFX) ---
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
// -----------------------------------------------
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MainController {

    // --- TABLA Y COLUMNAS ---
    @FXML private TableView<Evento> tablaEventos;
    @FXML private TableColumn<Evento, Long> colId;
    @FXML private TableColumn<Evento, String> colTipo;
    @FXML private TableColumn<Evento, String> colNombre;
    @FXML private TableColumn<Evento, LocalDateTime> colFecha; // Ojo: es LocalDateTime
    @FXML private TableColumn<Evento, String> colEstado;

    private EventoRepository repo = new EventoRepositoryImpl();

    @FXML
    public void initialize() {
        configurarColumnas();
        cargarEventos();
    }

    private void configurarColumnas() {
        // 1. Enlazamos las columnas con los atributos del modelo
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        // 2. Truco para mostrar el TIPO de evento (Nombre de la Clase)
        colTipo.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getClass().getSimpleName())
        );

        // 3. CONFIGURACIÓN ESPECIAL DE FECHA (Para que se vea linda)
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaInicio"));
        
        // Formateador: Día/Mes/Año Hora:Minutos
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        colFecha.setCellFactory(columna -> new TableCell<Evento, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    // Acá convertimos la fecha fea a texto lindo
                    setText(item.format(formato));
                }
            }
        });
    }

    @FXML
    public void cargarEventos() {
        try {
            tablaEventos.getItems().clear();
            ObservableList<Evento> lista = FXCollections.observableArrayList(repo.listarTodos());
            tablaEventos.setItems(lista);
        } catch (Exception e) {
            e.printStackTrace(); // Para ver el error en la consola
            mostrarAlerta("Error", "No se pudo cargar la lista: " + e.getMessage());
        }
    }

    // --- ABRIR ALTA DE EVENTO ---
    @FXML
    public void abrirNuevoEvento() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/evento.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Alta de Nuevo Evento");
            stage.setScene(new Scene(root, 400, 600));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            cargarEventos(); // Refrescar al volver

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir la ventana de alta: " + e.getMessage());
        }
    }

    // --- ABRIR EDICIÓN DE EVENTO ---
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

            // Pasamos el evento seleccionado al controlador
            EventoController controller = loader.getController();
            controller.initData(seleccionado); 

            Stage stage = new Stage();
            stage.setTitle("Editar Evento");
            stage.setScene(new Scene(root, 400, 600));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            cargarEventos(); // Refrescar

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir la edición: " + e.getMessage());
        }
    }

    // --- ABRIR INSCRIPCIÓN (Solo Talleres) ---
    @FXML
    public void abrirInscripcion() {
        Evento seleccionado = tablaEventos.getSelectionModel().getSelectedItem();
        
        if (seleccionado == null) {
            mostrarAlerta("Atención", "Seleccioná un evento primero.");
            return;
        }

        if (!(seleccionado instanceof Taller)) {
            mostrarAlerta("Acción no válida", "La inscripción solo está disponible para Talleres.\nEl evento seleccionado es un: " + seleccionado.getClass().getSimpleName());
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/inscripcion.fxml"));
            Parent root = loader.load();

            InscripcionController controller = loader.getController();
            controller.initData((Taller) seleccionado);

            Stage stage = new Stage();
            stage.setTitle("Inscripción a Taller");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
            cargarEventos();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir la inscripción: " + e.getMessage());
        }
    }

    // --- ELIMINAR EVENTO ---
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

    // Importar CicloCine arriba
@FXML
public void abrirPeliculas() {
    Evento seleccionado = tablaEventos.getSelectionModel().getSelectedItem();

    if (seleccionado == null || !(seleccionado instanceof CicloCine)) {
        mostrarAlerta("Atención", "Seleccioná un evento de tipo 'Ciclo de Cine'.");
        return;
    }

    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/peliculas.fxml"));
        Parent root = loader.load();

        PeliculasController controller = loader.getController();
        controller.initData((CicloCine) seleccionado);

        Stage stage = new Stage();
        stage.setTitle("Gestión de Películas");
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();

    } catch (Exception e) {
        e.printStackTrace();
        mostrarAlerta("Error", e.getMessage());
    }
}
}