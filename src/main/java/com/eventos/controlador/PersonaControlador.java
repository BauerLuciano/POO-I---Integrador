package com.eventos.controlador;

import com.eventos.modelo.Persona;
import com.eventos.repo.PersonaRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*; // Importamos todo para DialogPane
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.util.function.UnaryOperator;

public class PersonaControlador {

    @FXML private TextField txtNombre;
    @FXML private TextField txtDni;
    @FXML private TextField txtEmail;
    @FXML private TextField txtTelefono;
    @FXML private Label lblTitulo;

    private final PersonaRepository personaRepo = new PersonaRepository();
    private Persona personaActual;

    @FXML
    public void initialize() {
        configurarInput(txtNombre, 30, "[a-zA-ZñÑáéíóúÁÉÍÓÚ\\s]*");
        configurarInput(txtDni, 8, "[0-9]*");
        configurarInput(txtTelefono, 20, "[0-9]*");
    }

    private void configurarInput(TextField textField, int maxLength, String regex) {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String text = change.getControlNewText();
            if (text.matches(regex) && text.length() <= maxLength) return change;
            return null;
        };
        textField.setTextFormatter(new TextFormatter<>(filter));
    }

    public void setPersona(Persona p) {
        this.personaActual = p;
        if(lblTitulo != null) lblTitulo.setText("Editar: " + p.getNombreCompleto());
        txtNombre.setText(p.getNombreCompleto());
        txtDni.setText(p.getDni());
        txtEmail.setText(p.getEmail());
        txtTelefono.setText(p.getTelefono());
    }

    @FXML
    public void guardarPersona(ActionEvent event) {
        String nombre = txtNombre.getText().trim();
        String dni = txtDni.getText().trim();
        String email = txtEmail.getText().trim();
        String telefono = txtTelefono.getText().trim();

        // 1. Validaciones básicas
        if (nombre.isEmpty() || dni.isEmpty()) {
            mostrarAlertaNashe("Datos Incompletos", "Por favor, ingresá Nombre y DNI.", true);
            return;
        }

        if (dni.length() != 8) {
            mostrarAlertaNashe("DNI Inválido", "El DNI debe tener exactamente 8 números.", true);
            return;
        }

        // 2. VALIDACIÓN DE DUPLICADO (Mejorada)
        // Solo buscamos si es nuevo, O si estamos editando y cambiamos el DNI
        Persona existente = personaRepo.buscarPorDni(dni);
        if (existente != null) {
            // Si es nuevo (personaActual == null) O si es otro usuario (id diferente)
            if (personaActual == null || !existente.getId().equals(personaActual.getId())) {
                mostrarAlertaNashe("¡DNI Duplicado!", 
                        "La persona con DNI " + dni + " ya existe en el sistema.\nNombre: " + existente.getNombreCompleto(), 
                        true); // TRUE = Es un error (Rojo)
                return;
            }
        }

        try {
            if (personaActual == null) personaActual = new Persona();
            
            personaActual.setNombreCompleto(nombre);
            personaActual.setDni(dni);
            personaActual.setEmail(email);
            personaActual.setTelefono(telefono);

            personaRepo.guardar(personaActual);

            mostrarAlertaNashe("Éxito", "Persona guardada correctamente.", false); // FALSE = Es éxito (Verde/Azul)
            
            // Cerrar ventana
            Node source = (Node) event.getSource();
            Stage stage = (Stage) source.getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlertaNashe("Error Crítico", "No se pudo guardar: " + e.getMessage(), true);
        }
    }

    // --- EL MÉTODO DE LA ALERTA FACHERA (FULL NASHE) ---
    private void mostrarAlertaNashe(String titulo, String mensaje, boolean esError) {
        Alert alert = new Alert(esError ? Alert.AlertType.ERROR : Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null); // Sin cabecera fea por defecto
        alert.setContentText(mensaje);

        // Estilizando el DialogPane (El fondo de la alerta)
        DialogPane dialogPane = alert.getDialogPane();
        
        // CSS en línea para no depender de archivos externos
        String estiloBase = "-fx-font-family: 'Segoe UI', sans-serif; -fx-font-size: 14px;";
        
        if (esError) {
            // Estilo ERROR: Borde Rojo y fondo claro
            dialogPane.setStyle(estiloBase + "-fx-border-color: #e74c3c; -fx-border-width: 2px; -fx-background-color: #fff5f5;");
            // Buscamos el botón OK y lo ponemos rojo
            /* Nota: Acceder a los botones internos requiere lookup, para simplificar usamos estilo general */
        } else {
            // Estilo ÉXITO: Borde Verde y fondo claro
            dialogPane.setStyle(estiloBase + "-fx-border-color: #2ecc71; -fx-border-width: 2px; -fx-background-color: #f0fff4;");
        }

        // Sacar el ícono feo por defecto si querés (opcional)
        // alert.setGraphic(null); 

        alert.showAndWait();
    }
    
    @FXML public void volverALista(ActionEvent event) {
        ((Stage)((Node)event.getSource()).getScene().getWindow()).close();
    }
}