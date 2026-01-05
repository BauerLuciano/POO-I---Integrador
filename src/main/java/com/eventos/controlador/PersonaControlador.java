package com.eventos.controlador;

import com.eventos.modelo.Persona;
import com.eventos.repo.PersonaRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;

public class PersonaControlador {

    @FXML private TextField txtNombre;
    @FXML private TextField txtDni;
    @FXML private TextField txtEmail;
    @FXML private TextField txtTelefono;
    @FXML private Label lblTitulo;

    private final PersonaRepository personaRepo = new PersonaRepository();
    private Persona personaActual; // Objeto temporal para saber si editamos

    // Método para recibir datos desde el Listado
    public void setPersona(Persona p) {
        this.personaActual = p;
        lblTitulo.setText("Editar Persona: " + p.getNombreCompleto());
        txtNombre.setText(p.getNombreCompleto());
        txtDni.setText(p.getDni());
        txtEmail.setText(p.getEmail());
        txtTelefono.setText(p.getTelefono());
    }

    @FXML
    public void guardarPersona(ActionEvent event) {
        String nombre = txtNombre.getText();
        String dni = txtDni.getText();

        if (nombre == null || nombre.trim().isEmpty() || dni == null || dni.trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Faltan datos", "Nombre y DNI son obligatorios.");
            return;
        }

        try {
            if (personaActual == null) {
                personaActual = new Persona(); // Es NUEVO
            }
            
            // Actualizamos el objeto con lo que haya en los textfields
            personaActual.setNombreCompleto(nombre);
            personaActual.setDni(dni);
            personaActual.setEmail(txtEmail.getText());
            personaActual.setTelefono(txtTelefono.getText());

            personaRepo.guardar(personaActual); // El repo decide si INSERT o UPDATE

            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Guardado correctamente.");
            volverALista(event);

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al guardar: " + e.getMessage());
        }
    }

    @FXML
    public void volverALista(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/lista_personas.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void limpiarFormulario() {
        txtNombre.clear();
        txtDni.clear();
        txtEmail.clear();
        txtTelefono.clear();
        personaActual = null;
        lblTitulo.setText("Alta de Nueva Persona");
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}