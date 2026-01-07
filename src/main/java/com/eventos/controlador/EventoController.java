package com.eventos.controlador;

import com.eventos.enums.EstadoEvento;
import com.eventos.enums.Modalidad;
import com.eventos.enums.TipoEntrada;
import com.eventos.modelo.*;
import com.eventos.repo.EventoRepository;
import com.eventos.repo.EventoRepositoryImpl;
import com.eventos.repo.PersonaRepository;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;

public class EventoController {

    // --- CONTROLES COMUNES ---
    @FXML private ComboBox<String> comboTipo;
    @FXML private ComboBox<EstadoEvento> comboEstado;
    @FXML private TextField txtNombre;
    
    // FECHA Y HORA
    @FXML private DatePicker dpFecha;
    @FXML private TextField txtHora; // <--- NUEVO CAMPO
    
    @FXML private TextField txtDuracion;
    @FXML private ComboBox<Persona> comboOrganizador;

    // --- PANELES ESPECÍFICOS ---
    @FXML private VBox panelTaller;
    @FXML private VBox panelConcierto;
    @FXML private VBox panelFeria;
    @FXML private VBox panelExposicion;
    @FXML private VBox panelCicloCine;

    // --- CONTROLES ESPECÍFICOS ---
    // Taller
    @FXML private TextField txtCupo;
    @FXML private ComboBox<Modalidad> comboModalidad;
    @FXML private ComboBox<Persona> comboInstructor;
    
    // Concierto
    @FXML private ComboBox<TipoEntrada> comboTipoEntrada;
    @FXML private ComboBox<Persona> comboArtista;
    
    // Feria
    @FXML private TextField txtStands;
    @FXML private ComboBox<String> comboUbicacionFeria;
    
    // Exposicion
    @FXML private TextField txtTipoArte;
    @FXML private ComboBox<Persona> comboCurador;
    
    // Ciclo Cine
    @FXML private CheckBox chkHayCharlas;

    // --- REPOSITORIOS ---
    private EventoRepository eventoRepo = new EventoRepositoryImpl();
    private PersonaRepository personaRepo = new PersonaRepository();
    
    private Evento eventoEnEdicion;

    @FXML
    public void initialize() {
        // 1. Cargar Combos
        comboTipo.getItems().addAll("Taller", "Concierto", "Feria", "Exposicion", "CicloCine");
        comboUbicacionFeria.getItems().addAll("Es al aire libre", "Techado");
        comboModalidad.getItems().addAll(Modalidad.values());
        comboTipoEntrada.getItems().addAll(TipoEntrada.values());
        comboEstado.getItems().addAll(EstadoEvento.values());

        // 2. Cargar Personas
        try {
            var personas = personaRepo.listarTodos();
            comboOrganizador.getItems().addAll(personas);
            comboInstructor.getItems().addAll(personas);
            comboArtista.getItems().addAll(personas);
            comboCurador.getItems().addAll(personas);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --- MOSTRAR PANELES SEGÚN TIPO ---
    @FXML
    public void onTipoChange() {
        if (eventoEnEdicion != null) return; // Bloquear si editamos
        
        ocultarPaneles();
        String tipo = comboTipo.getValue();
        
        if ("Taller".equals(tipo)) mostrarPanel(panelTaller);
        if ("Concierto".equals(tipo)) mostrarPanel(panelConcierto);
        if ("Feria".equals(tipo)) mostrarPanel(panelFeria);
        if ("Exposicion".equals(tipo)) mostrarPanel(panelExposicion);
        if ("CicloCine".equals(tipo)) mostrarPanel(panelCicloCine);
    }

    private void ocultarPaneles() {
        panelTaller.setVisible(false); panelTaller.setManaged(false);
        panelConcierto.setVisible(false); panelConcierto.setManaged(false);
        panelFeria.setVisible(false); panelFeria.setManaged(false);
        panelExposicion.setVisible(false); panelExposicion.setManaged(false);
        panelCicloCine.setVisible(false); panelCicloCine.setManaged(false);
    }
    
    private void mostrarPanel(VBox p) {
        p.setVisible(true); p.setManaged(true);
    }

    // --- CARGAR DATOS PARA EDITAR ---
    public void initData(Evento evento) {
        this.eventoEnEdicion = evento;
        
        // Datos comunes
        txtNombre.setText(evento.getNombre());
        
        // Cargar Fecha y Hora separadas
        if (evento.getFechaInicio() != null) {
            dpFecha.setValue(evento.getFechaInicio().toLocalDate());
            txtHora.setText(evento.getFechaInicio().toLocalTime().toString()); // Mostramos la hora
        }
        
        txtDuracion.setText(String.valueOf(evento.getDuracionEstimada()));
        comboEstado.setValue(evento.getEstado());
        
        if (!evento.getOrganizadores().isEmpty()) {
            comboOrganizador.setValue(evento.getOrganizadores().get(0));
        }

        comboTipo.setDisable(true); // Bloquear tipo

        // Cargar específicos
        if (evento instanceof Taller) {
            comboTipo.setValue("Taller");
            Taller t = (Taller) evento;
            txtCupo.setText(String.valueOf(t.getCupoMaximo()));
            comboModalidad.setValue(t.getModalidad());
            comboInstructor.setValue(t.getInstructor());
            mostrarPanel(panelTaller);
        } 
        else if (evento instanceof Concierto) {
            comboTipo.setValue("Concierto");
            Concierto c = (Concierto) evento;
            comboTipoEntrada.setValue(c.getTipoEntrada());
            if (!c.getArtistas().isEmpty()) comboArtista.setValue(c.getArtistas().get(0));
            mostrarPanel(panelConcierto);
        }
        else if (evento instanceof Feria) {
            comboTipo.setValue("Feria");
            Feria f = (Feria) evento;
            txtStands.setText(String.valueOf(f.getCantidadStands()));
            if (f.isAlAireLibre()) {
                comboUbicacionFeria.setValue("Es al aire libre");
            } else {
                comboUbicacionFeria.setValue("Techado");
            }
            mostrarPanel(panelFeria);
        }
        else if (evento instanceof Exposicion) {
            comboTipo.setValue("Exposicion");
            Exposicion e = (Exposicion) evento;
            txtTipoArte.setText(e.getTipoArte());
            comboCurador.setValue(e.getCurador());
            mostrarPanel(panelExposicion);
        }
        else if (evento instanceof CicloCine) {
            comboTipo.setValue("CicloCine");
            CicloCine cc = (CicloCine) evento;
            chkHayCharlas.setSelected(cc.isHayCharlas());
            mostrarPanel(panelCicloCine);
        }
    }

    // --- GUARDAR EVENTO ---
    @FXML
    public void guardarEvento() {
        try {
            // Validaciones básicas
            if (txtNombre.getText().isEmpty()) throw new RuntimeException("El nombre es obligatorio");
            if (dpFecha.getValue() == null) throw new RuntimeException("La fecha es obligatoria");
            if (txtHora.getText().isEmpty()) throw new RuntimeException("La hora es obligatoria");

            Evento eventoFinal;

            // 1. Instanciación
            if (eventoEnEdicion != null) {
                eventoFinal = eventoEnEdicion;
                eventoFinal.setEstado(comboEstado.getValue());
            } else {
                String tipo = comboTipo.getValue();
                if (tipo == null) throw new RuntimeException("Seleccione tipo");
                
                switch (tipo) {
                    case "Taller": eventoFinal = new Taller(); break;
                    case "Concierto": eventoFinal = new Concierto(); break;
                    case "Feria": eventoFinal = new Feria(); break;
                    case "Exposicion": eventoFinal = new Exposicion(); break;
                    case "CicloCine": eventoFinal = new CicloCine(); break;
                    default: return;
                }
                eventoFinal.setEstado(comboEstado.getValue() != null ? comboEstado.getValue() : EstadoEvento.EN_PLANIFICACION);
            }

            // 2. Setear Datos Comunes
            eventoFinal.setNombre(txtNombre.getText());

            // --- MANEJO DE FECHA Y HORA ---
            try {
                // Parseamos la hora (ej: "18:30" o "18:30:00")
                LocalTime hora = LocalTime.parse(txtHora.getText());
                // Unimos Fecha + Hora
                eventoFinal.setFechaInicio(dpFecha.getValue().atTime(hora));
            } catch (DateTimeParseException e) {
                throw new RuntimeException("Formato de hora inválido. Use HH:mm (Ej: 18:30)");
            }

            try {
                eventoFinal.setDuracionEstimada(Integer.parseInt(txtDuracion.getText()));
            } catch (Exception e) { eventoFinal.setDuracionEstimada(60); }
            
            eventoFinal.getOrganizadores().clear();
            if (comboOrganizador.getValue() != null) eventoFinal.agregarOrganizador(comboOrganizador.getValue());

            // 3. Setear Datos Específicos
            if (eventoFinal instanceof Taller) {
                Taller t = (Taller) eventoFinal;
                t.setCupoMaximo(Integer.parseInt(txtCupo.getText()));
                t.setModalidad(comboModalidad.getValue());
                t.setInstructor(comboInstructor.getValue());
            } else if (eventoFinal instanceof Concierto) {
                Concierto c = (Concierto) eventoFinal;
                c.setTipoEntrada(comboTipoEntrada.getValue());
                c.getArtistas().clear();
                if (comboArtista.getValue() != null) c.agregarArtista(comboArtista.getValue());
            } else if (eventoFinal instanceof Feria) {
                Feria f = (Feria) eventoFinal;
                f.setCantidadStands(Integer.parseInt(txtStands.getText()));
                String ubicacion = comboUbicacionFeria.getValue();
                if (ubicacion == null) throw new RuntimeException("Seleccione la ubicación de la feria");
                f.setAlAireLibre("Es al aire libre".equals(ubicacion));
            } else if (eventoFinal instanceof Exposicion) {
                Exposicion e = (Exposicion) eventoFinal;
                e.setTipoArte(txtTipoArte.getText());
                e.setCurador(comboCurador.getValue());
            } else if (eventoFinal instanceof CicloCine) {
                CicloCine cc = (CicloCine) eventoFinal;
                cc.setHayCharlas(chkHayCharlas.isSelected());
            }

            // 4. Guardar
            if (eventoEnEdicion != null) {
                eventoRepo.actualizar(eventoFinal);
            } else {
                eventoRepo.guardar(eventoFinal);
            }

            cerrarVentana();

        } catch (NumberFormatException e) {
            mostrarAlerta("Error de formato", "Revisá los números (Cupo, Duración, Stands).");
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo guardar: " + e.getMessage());
        }
    }

    @FXML public void cerrarVentana() {
        Stage s = (Stage) txtNombre.getScene().getWindow(); 
        s.close();
    }
    
    private void mostrarAlerta(String t, String m) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(t); a.setContentText(m); a.showAndWait();
    }
}