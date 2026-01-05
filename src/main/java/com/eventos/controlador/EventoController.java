package com.eventos.controlador;

import com.eventos.enums.Modalidad;
import com.eventos.enums.TipoEntrada;
import com.eventos.modelo.*; // Importamos todas las entidades
import com.eventos.repo.EventoRepository;
import com.eventos.repo.EventoRepositoryImpl;
import com.eventos.repo.PersonaRepository;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.util.List;

public class EventoController {

    // Controles Comunes
    @FXML private ComboBox<String> comboTipo;
    @FXML private TextField txtNombre;
    @FXML private DatePicker dpFecha;
    @FXML private TextField txtDuracion;
    @FXML private ComboBox<Persona> comboOrganizador;

    // Paneles Específicos
    @FXML private VBox panelTaller;
    @FXML private VBox panelConcierto;
    @FXML private VBox panelFeria;

    // Controles Taller
    @FXML private TextField txtCupo;
    @FXML private ComboBox<Modalidad> comboModalidad;
    @FXML private ComboBox<Persona> comboInstructor;

    // Controles Concierto
    @FXML private ComboBox<TipoEntrada> comboTipoEntrada;
    @FXML private ComboBox<Persona> comboArtista;

    // Controles Feria
    @FXML private TextField txtStands;
    @FXML private CheckBox chkAireLibre;

    private EventoRepository eventoRepo = new EventoRepositoryImpl();
    private PersonaRepository personaRepo = new PersonaRepository(); // Para llenar los combos de gente

    @FXML
    public void initialize() {
        // 1. Cargar Tipos de Evento
        comboTipo.getItems().addAll("Taller", "Concierto", "Feria");
        
        // 2. Cargar Personas (Fito Paez, etc) en los combos
        // (En un sistema real filtraríamos por rol, acá cargamos todos)
        try {
            // Nota: Como PersonaRepository no tiene "listarTodos" en el código que te pasé antes,
            // vamos a usar un truco rápido o agregalo al Repo. 
            // Asumo que agregás un metodo listarTodos() al PersonaRepository similar al de Evento.
            // Si no lo tenés, el combo aparecerá vacío.
            
            // List<Persona> personas = personaRepo.listarTodos(); 
            // comboOrganizador.getItems().addAll(personas);
            // comboInstructor.getItems().addAll(personas);
            // comboArtista.getItems().addAll(personas);
            
            // AGREGADO TEMPORAL: Cargamos a mano a Fito si existe para probar
            Persona p = personaRepo.buscarPorDni("11223344"); 
            if(p != null) {
                comboOrganizador.getItems().add(p);
                comboInstructor.getItems().add(p);
                comboArtista.getItems().add(p);
            }
            
        } catch (Exception e) {
            System.err.println("Error cargando personas: " + e.getMessage());
        }

        // 3. Cargar Enums
        comboModalidad.getItems().addAll(Modalidad.values());
        comboTipoEntrada.getItems().addAll(TipoEntrada.values());
    }

    @FXML
    public void onTipoChange() {
        String tipo = comboTipo.getValue();
        
        // Ocultar todo primero
        ocultarPanel(panelTaller);
        ocultarPanel(panelConcierto);
        ocultarPanel(panelFeria);

        if (tipo == null) return;

        // Mostrar según selección
        switch (tipo) {
            case "Taller": mostrarPanel(panelTaller); break;
            case "Concierto": mostrarPanel(panelConcierto); break;
            case "Feria": mostrarPanel(panelFeria); break;
        }
    }

    private void mostrarPanel(VBox panel) {
        panel.setVisible(true);
        panel.setManaged(true); // Ocupa espacio
    }

    private void ocultarPanel(VBox panel) {
        panel.setVisible(false);
        panel.setManaged(false); // No ocupa espacio
    }

    @FXML
    public void guardarEvento() {
        try {
            String tipo = comboTipo.getValue();
            if (tipo == null) throw new RuntimeException("Seleccioná un tipo de evento");

            // Datos comunes
            String nombre = txtNombre.getText();
            if (nombre.isEmpty()) throw new RuntimeException("El nombre es obligatorio");
            
            if (dpFecha.getValue() == null) throw new RuntimeException("La fecha es obligatoria");
            LocalDateTime fecha = dpFecha.getValue().atStartOfDay();
            
            int duracion = Integer.parseInt(txtDuracion.getText());
            Persona organizador = comboOrganizador.getValue();
            
            Evento nuevoEvento = null;

            // --- POLIMORFISMO: INSTANCIACIÓN DINÁMICA ---
            switch (tipo) {
                case "Taller":
                    Taller t = new Taller();
                    t.setCupoMaximo(Integer.parseInt(txtCupo.getText()));
                    t.setModalidad(comboModalidad.getValue());
                    t.setInstructor(comboInstructor.getValue()); // Puede ser null
                    nuevoEvento = t;
                    break;

                case "Concierto":
                    Concierto c = new Concierto();
                    c.setTipoEntrada(comboTipoEntrada.getValue());
                    if (comboArtista.getValue() != null) {
                        c.agregarArtista(comboArtista.getValue());
                    }
                    nuevoEvento = c;
                    break;

                case "Feria":
                    Feria f = new Feria();
                    f.setCantidadStands(Integer.parseInt(txtStands.getText()));
                    f.setAlAireLibre(chkAireLibre.isSelected());
                    nuevoEvento = f;
                    break;
            }

            // Seteamos lo común
            nuevoEvento.setNombre(nombre);
            nuevoEvento.setFechaInicio(fecha);
            nuevoEvento.setDuracionEstimada(duracion);
            if (organizador != null) {
                nuevoEvento.agregarOrganizador(organizador);
            }

            // GUARDAR EN BD
            eventoRepo.guardar(nuevoEvento);

            // Cerrar ventana
            cerrarVentana();
            
            System.out.println("Evento guardado: " + tipo);

        } catch (NumberFormatException e) {
            mostrarAlerta("Error de datos", "Por favor ingresá números válidos en cupo/duración/stands.");
        } catch (Exception e) {
            mostrarAlerta("Error", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void cerrarVentana() {
        Stage stage = (Stage) txtNombre.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}