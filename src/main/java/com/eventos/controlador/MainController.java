package com.eventos.controlador;

import com.eventos.modelo.CicloCine;
import com.eventos.modelo.Evento;
import com.eventos.modelo.Taller;
import com.eventos.repo.EventoRepository;
import com.eventos.repo.EventoRepositoryImpl;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox; // Importante para agrupar botones
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class MainController {

    @FXML private TableView<Evento> tablaEventos;
    @FXML private TableColumn<Evento, Long> colId;
    @FXML private TableColumn<Evento, String> colTipo;
    @FXML private TableColumn<Evento, String> colNombre;
    @FXML private TableColumn<Evento, LocalDateTime> colFecha;
    @FXML private TableColumn<Evento, String> colEstado;
    
    // Nueva Columna para los botones dinámicos
    @FXML private TableColumn<Evento, Void> colAcciones;

    private EventoRepository repo = new EventoRepositoryImpl();

    @FXML
    public void initialize() {
        configurarColumnas();
        // Configuramos la columna mágica
        configurarColumnaAcciones(); 
        cargarEventos();
    }

    private void configurarColumnas() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        colTipo.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getClass().getSimpleName())
        );

        // Formateo de fecha (Tu código original)
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaInicio"));
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        colFecha.setCellFactory(columna -> new TableCell<Evento, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.format(formato));
                }
            }
        });
    }

    // --- LA MAGIA: BOTONES DINÁMICOS ---
    private void configurarColumnaAcciones() {
        colAcciones.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                
                if (empty) {
                    setGraphic(null);
                } else {
                    // 1. Obtenemos el evento de esta fila específica
                    Evento evento = getTableView().getItems().get(getIndex());
                    
                    // 2. Contenedor para poner los botones uno al lado del otro
                    HBox box = new HBox(5); // 5px de separación
                    box.setStyle("-fx-alignment: CENTER_LEFT;");
                    
                    // --- BOTONES ESPECÍFICOS (Polimorfismo Visual) ---
                    
                    // A. Si es TALLER -> Botón Inscribir
                    if (evento instanceof Taller) {
                        Button btnInscribir = new Button("Inscribir");
                        btnInscribir.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 11px;");
                        btnInscribir.setOnAction(e -> abrirInscripcion((Taller) evento));
                        box.getChildren().add(btnInscribir);
                    }
                    
                    // B. Si es CINE -> Botón Películas
                    else if (evento instanceof CicloCine) {
                        Button btnCine = new Button("Listado de Películas");
                        btnCine.setStyle("-fx-background-color: #006f29ff; -fx-text-fill: white; -fx-font-size: 11px;");
                        btnCine.setOnAction(e -> abrirGestionPeliculas((CicloCine) evento));
                        box.getChildren().add(btnCine);
                    }

                    // --- BOTONES COMUNES (Para todos) ---
                    
                    Button btnEditar = new Button("Editar");
                    btnEditar.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-size: 11px;");
                    btnEditar.setOnAction(e -> editarEvento(evento));

                    Button btnEliminar = new Button("Eliminar");
                    btnEliminar.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold;");
                    btnEliminar.setOnAction(e -> eliminarEvento(evento));

                    // Agregamos los comunes al final
                    box.getChildren().addAll(btnEditar, btnEliminar);
                    
                    setGraphic(box);
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
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cargar la lista: " + e.getMessage());
        }
    }

    // --- ACCIONES (Refactorizadas para recibir parámetros) ---

    @FXML
    public void abrirNuevoEvento(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/evento.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Alta de Nuevo Evento");
            stage.setScene(new Scene(root, 400, 600));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            cargarEventos();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "Error al abrir ventana: " + e.getMessage());
        }
    }

    // Ahora recibe el Taller directo desde el botón de la fila
    private void abrirInscripcion(Taller taller) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/inscripcion.fxml"));
            Parent root = loader.load();

            InscripcionController controller = loader.getController();
            controller.initData(taller); // <-- Pasamos el taller directo

            Stage stage = new Stage();
            stage.setTitle("Inscripción: " + taller.getNombre());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
            cargarEventos(); // Refrescamos por si cambió el cupo

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir la inscripción: " + e.getMessage());
        }
    }

    private void abrirGestionPeliculas(CicloCine cine) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/peliculas.fxml"));
            Parent root = loader.load();

            PeliculasController controller = loader.getController();
            controller.initData(cine); // <-- Pasamos el cine directo

            Stage stage = new Stage();
            stage.setTitle("Gestión de Películas");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir cine: " + e.getMessage());
        }
    }

    private void editarEvento(Evento evento) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/evento.fxml"));
            Parent root = loader.load();
            
            EventoController controller = loader.getController();
            controller.initData(evento); 

            Stage stage = new Stage();
            stage.setTitle("Editar Evento");
            stage.setScene(new Scene(root, 400, 600));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            cargarEventos();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo editar: " + e.getMessage());
        }
    }

    private void eliminarEvento(Evento evento) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Eliminar");
        alert.setHeaderText(null);
        alert.setContentText("¿Eliminar evento: " + evento.getNombre() + "?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                repo.eliminar(evento.getId());
                cargarEventos(); 
                mostrarAlerta("Éxito", "Evento eliminado.");
            } catch (Exception e) {
                mostrarAlerta("Error", "No se pudo eliminar: " + e.getMessage());
            }
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