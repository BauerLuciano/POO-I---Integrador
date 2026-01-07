package com.eventos.controlador;

import com.eventos.modelo.Persona;
import com.eventos.modelo.Taller;
import com.eventos.repo.EventoRepository;
import com.eventos.repo.EventoRepositoryImpl;
import com.eventos.repo.PersonaRepository;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class InscripcionController {

    @FXML private Label lblEvento;
    @FXML private Label lblCupo;
    @FXML private ComboBox<Persona> comboPersonas;
    
    // TABLA
    @FXML private TableView<Persona> tablaInscriptos;
    @FXML private TableColumn<Persona, String> colNombre;
    @FXML private TableColumn<Persona, String> colDni;
    @FXML private TableColumn<Persona, String> colEmail;

    private EventoRepository eventRepo = new EventoRepositoryImpl();
    private PersonaRepository personaRepo = new PersonaRepository();
    private Taller tallerActual;

    public void initData(Taller taller) {
        this.tallerActual = taller;
        lblEvento.setText("Inscripción: " + taller.getNombre());
        actualizarInfo();
        cargarPersonas();
        
        // Configurar columnas de la tabla
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombreCompleto"));
        colDni.setCellValueFactory(new PropertyValueFactory<>("dni"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        
        // Llenar tabla con los que YA estaban inscriptos
        refrescarTabla();
    }

    private void cargarPersonas() {
        try {
            comboPersonas.getItems().setAll(personaRepo.listarTodos());
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void actualizarInfo() {
        int ocupados = tallerActual.getInscripciones().size();
        int total = tallerActual.getCupoMaximo();
        lblCupo.setText("Cupo: " + ocupados + " / " + total + " ocupados");
    }
    
    private void refrescarTabla() {
        if (tallerActual != null) {
            tablaInscriptos.setItems(FXCollections.observableArrayList(tallerActual.getInscripciones()));
        }
    }

    @FXML
    public void inscribir() {
        Persona p = comboPersonas.getValue();
        if (p == null) {
            mostrarAlerta("Error", "Seleccioná una persona primero.");
            return;
        }

        try {
            // Esto ahora valida Estado, Cupo y Duplicados
            tallerActual.inscribir(p);
            
            // Guardamos en BD
            eventRepo.actualizar(tallerActual);
            
            mostrarAlerta("Éxito", "Inscripción realizada correctamente.");
            
            // Refrescamos la pantalla
            actualizarInfo();
            refrescarTabla();
            
        } catch (Exception e) {
            mostrarAlerta("No se pudo inscribir", e.getMessage());
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