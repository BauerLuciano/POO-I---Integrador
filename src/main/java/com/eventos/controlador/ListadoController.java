package com.eventos.controlador;

import com.eventos.modelo.Persona;
import com.eventos.repo.PersonaRepository;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Optional;

public class ListadoController {

    @FXML private TableView<Persona> tabla;
    @FXML private TableColumn<Persona, String> colNombre;
    @FXML private TableColumn<Persona, String> colDni;
    @FXML private TableColumn<Persona, String> colEmail;

    private final PersonaRepository repo = new PersonaRepository();

    @FXML
    public void initialize() {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombreCompleto"));
        colDni.setCellValueFactory(new PropertyValueFactory<>("dni"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        cargarDatos();
    }

    public void cargarDatos() {
        tabla.setItems(FXCollections.observableArrayList(repo.listarTodos()));
    }

    @FXML
    public void irAFormulario(ActionEvent event) {
        navegarAFormulario(event, null); // Null = Crear Nuevo
    }

    @FXML
    public void editarPersona(ActionEvent event) {
        Persona seleccionada = tabla.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Atención", "Seleccioná a alguien de la lista para editar.");
            return;
        }
        navegarAFormulario(event, seleccionada); // Pasamos la persona para editar
    }

    @FXML
    public void eliminarPersona() {
        Persona seleccionada = tabla.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Atención", "Seleccioná a alguien para eliminar.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar eliminación");
        confirm.setHeaderText(null);
        confirm.setContentText("¿Borrar a " + seleccionada.getNombreCompleto() + "?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            repo.eliminar(seleccionada.getId());
            cargarDatos();
        }
    }

    private void navegarAFormulario(ActionEvent event, Persona p) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/formulario_persona.fxml"));
            Parent root = loader.load();

            if (p != null) {
                // Si estamos editando, le pasamos los datos al otro controlador
                PersonaControlador controller = loader.getController();
                controller.setPersona(p);
            }

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String msj) {
        Alert a = new Alert(tipo);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(msj);
        a.showAndWait();
    }
}