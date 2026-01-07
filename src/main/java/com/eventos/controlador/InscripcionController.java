package com.eventos.controlador;

import com.eventos.modelo.Persona;
import com.eventos.modelo.Taller;
import com.eventos.repo.EventoRepository;
import com.eventos.repo.EventoRepositoryImpl;
import com.eventos.repo.PersonaRepository;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;
import javafx.util.StringConverter;
import java.util.List;
import java.util.Optional;

public class InscripcionController {

    @FXML private Label lblEvento;
    @FXML private Label lblCupo;
    @FXML private ComboBox<Persona> comboPersonas;
    @FXML private TableView<Persona> tablaInscriptos;
    
    @FXML private TableColumn<Persona, String> colNombre;
    @FXML private TableColumn<Persona, String> colDni;
    @FXML private TableColumn<Persona, String> colEmail;
    @FXML private TableColumn<Persona, Void> ColAcciones; 

    private final EventoRepository eventoRepo = new EventoRepositoryImpl();
    private final PersonaRepository personaRepo = new PersonaRepository(); 
    
    private Taller tallerActual; 

    @FXML
    public void initialize() {
        // Configuración de columnas
        colNombre.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNombreCompleto()));
        colDni.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDni()));
        colEmail.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));

        agregarBotonEliminar();
        cargarComboPersonas();
    }

    public void initData(Taller taller) {
        this.tallerActual = taller;
        lblEvento.setText("Inscripción al: " + taller.getNombre());
        actualizarTabla();
        actualizarCupo();
    }

    private void cargarComboPersonas() {
        // Cargamos todas las personas disponibles para el combo
        List<Persona> listaPersonas = personaRepo.listarTodos(); 
        comboPersonas.setItems(FXCollections.observableArrayList(listaPersonas));

        comboPersonas.setConverter(new StringConverter<Persona>() {
            @Override
            public String toString(Persona p) {
                return (p == null) ? null : p.getNombreCompleto() + " (DNI: " + p.getDni() + ")";
            }

            @Override
            public Persona fromString(String string) {
                return null; // No necesario para este caso
            }
        });
    }

    @FXML 
    public void inscribir() { 
        Persona seleccionada = comboPersonas.getValue();
        
        if (seleccionada == null) {
            mostrarAlerta("Atención", "Debés seleccionar una persona.");
            return;
        }

        try {
            // USAMOS LA LÓGICA DEL MODELO (Taller.java)
            // Esto valida automáticamente: Cupo, Estado CONFIRMADO y Duplicados.
            // Si algo falla, el modelo tira una RuntimeException que capturamos abajo.
            tallerActual.inscribir(seleccionada);
            
            // Si pasó la línea anterior, guardamos en BD
            eventoRepo.actualizar(tallerActual); 
            
            // Actualizamos la vista
            tablaInscriptos.getItems().add(seleccionada);
            comboPersonas.getSelectionModel().clearSelection();
            actualizarCupo(); 
            
            mostrarNotificacion("¡Inscripto correctamente!");

        } catch (RuntimeException e) {
            // Acá caen los errores de "No hay cupo", "Ya inscripto" o "No confirmado"
            mostrarAlerta("No se pudo inscribir", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error Crítico", "Error en base de datos: " + e.getMessage());
        }
    }

    private void darDeBaja(Persona persona) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText(null);
        alert.setContentText("¿Quitar a " + persona.getNombreCompleto() + "?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // CORRECCIÓN: Usamos el nombre real del método
                tallerActual.getInscripciones().remove(persona);
                
                // Actualizar DB
                eventoRepo.actualizar(tallerActual);
                
                // Actualizar Vista
                tablaInscriptos.getItems().remove(persona);
                actualizarCupo();
                
                mostrarNotificacion("Persona removida.");
            } catch (Exception e) {
                e.printStackTrace();
                mostrarAlerta("Error", "No se pudo eliminar: " + e.getMessage());
            }
        }
    }

    private void actualizarTabla() {
        if (tallerActual != null) {
            // CORRECCIÓN: Usamos getInscripciones() y no hace falta castear
            tablaInscriptos.setItems(FXCollections.observableArrayList(tallerActual.getInscripciones()));
        }
    }

    private void actualizarCupo() {
        if (tallerActual != null) {
            int cupoMaximo = tallerActual.getCupoMaximo();
            // CORRECCIÓN: Usamos getInscripciones()
            int ocupados = tallerActual.getInscripciones().size();

            lblCupo.setText("Cupo: " + ocupados + " / " + cupoMaximo + " ocupados");

            if (ocupados >= cupoMaximo) {
                lblCupo.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            } else {
                lblCupo.setStyle("-fx-text-fill: #666666; -fx-font-weight: bold;");
            }
        }
    }

    // --- Helpers de UI ---
    
    private void agregarBotonEliminar() {
        ColAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnBaja = new Button("Eliminar"); 
            {
                btnBaja.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-min-width: 30px;");
                btnBaja.setOnAction(event -> {
                    if (getIndex() < getTableView().getItems().size()) {
                        darDeBaja(getTableView().getItems().get(getIndex()));
                    }
                });
            }
            @Override
            public void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnBaja);
            }
        });
    }
    
    private void mostrarNotificacion(String msj) {
        Alert a = new Alert(Alert.AlertType.INFORMATION); 
        a.setTitle("Éxito"); a.setHeaderText(null); a.setContentText(msj); a.show();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert a = new Alert(Alert.AlertType.WARNING); // Warning queda mejor para validaciones
        a.setTitle(titulo); a.setHeaderText(null); a.setContentText(mensaje); a.show();
    }
}