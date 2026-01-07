package com.eventos.controlador;

import com.eventos.modelo.Persona;
import com.eventos.repo.PersonaRepository;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.net.URL; // Importante
import java.util.Optional;

public class ListadoController {

    @FXML private TableView<Persona> tabla;
    @FXML private TableColumn<Persona, String> colNombre;
    @FXML private TableColumn<Persona, String> colDni;
    @FXML private TableColumn<Persona, String> colEmail;
    @FXML private TableColumn<Persona, Void> colAcciones;

    private final PersonaRepository repo = new PersonaRepository();

    @FXML
    public void initialize() {
        configurarColumnas();
        configurarColumnaAcciones();
        cargarPersonas();
    }

    private void configurarColumnas() {
        colNombre.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNombreCompleto()));
        colDni.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDni()));
        colEmail.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getEmail()));
    }

    private void configurarColumnaAcciones() {
        colAcciones.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Persona persona = getTableView().getItems().get(getIndex());
                    HBox box = new HBox(5);
                    box.setStyle("-fx-alignment: CENTER_LEFT;");
                    
                    Button btnEditar = new Button("Editar");
                    btnEditar.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-size: 11px;");
                    btnEditar.setOnAction(e -> editarPersona(persona));

                    Button btnEliminar = new Button("Eliminar");
                    btnEliminar.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold;");
                    btnEliminar.setOnAction(e -> eliminarPersona(persona));

                    box.getChildren().addAll(btnEditar, btnEliminar);
                    setGraphic(box);
                }
            }
        });
    }

    private void cargarPersonas() {
        tabla.setItems(FXCollections.observableArrayList(repo.listarTodos()));
    }

    @FXML
    public void irAFormulario(ActionEvent event) {
        abrirFormulario(null);
    }

    private void editarPersona(Persona persona) {
        abrirFormulario(persona);
    }

    private void abrirFormulario(Persona personaAEditar) {
        try {
            // --- CHECKEO ANTI-BUGS ---
            URL url = getClass().getResource("/vista/formulario_persona.fxml");
            if (url == null) {
                System.err.println("¡ERROR CRÍTICO! No encuentro el archivo: /vista/formulario_persona.fxml");
                System.err.println("Por favor verificá la carpeta src/main/resources/vista/");
                mostrarAlerta("Error de Archivo", "No se encuentra la vista del formulario.");
                return;
            }
            // -------------------------

            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();

            if (personaAEditar != null) {
                PersonaControlador controller = loader.getController();
                controller.setPersona(personaAEditar);
            }

            Stage stage = new Stage();
            stage.setTitle(personaAEditar == null ? "Nueva Persona" : "Editar Persona");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            cargarPersonas(); 

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "Error al abrir formulario: " + e.getMessage());
        }
    }

    private void eliminarPersona(Persona persona) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Eliminar");
        alert.setHeaderText(null);
        alert.setContentText("¿Eliminar a " + persona.getNombreCompleto() + "?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                repo.eliminar(persona.getId()); 
                cargarPersonas();
            } catch (Exception e) {
                mostrarAlerta("Error", "No se pudo eliminar: " + e.getMessage());
            }
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}