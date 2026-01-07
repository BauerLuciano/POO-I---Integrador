package com.eventos.modelo;

import com.eventos.enums.Modalidad;
import com.eventos.interfaz.Inscribible;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "talleres")
@PrimaryKeyJoinColumn(name = "id")
public class Taller extends Evento implements Inscribible {

    @Column(name = "cupo_maximo", nullable = false)
    private int cupoMaximo;

    @Enumerated(EnumType.STRING)
    private Modalidad modalidad;

    @ManyToOne
    @JoinColumn(name = "instructor_id")
    private Persona instructor;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "taller_inscripciones",
        joinColumns = @JoinColumn(name = "taller_id"),
        inverseJoinColumns = @JoinColumn(name = "persona_id")
    )
    private List<Persona> inscripciones = new ArrayList<>();

    public Taller() {
        super();
    }

    public Taller(String nombre, java.time.LocalDateTime fecha, int duracion, int cupo, Modalidad mod) {
        super();
        this.setNombre(nombre);
        this.setFechaInicio(fecha);
        this.setDuracionEstimada(duracion);
        this.cupoMaximo = cupo;
        this.modalidad = mod;
    }

    
    @Override
    public boolean hayCupo() {
        return inscripciones.size() < cupoMaximo;
    }

  @Override
    public void inscribir(Persona persona) {
        
        // 1. VALIDACIÓN DE ESTADO
        if (this.getEstado() != com.eventos.enums.EstadoEvento.CONFIRMADO) {
            throw new RuntimeException("Solo se pueden inscribir a eventos CONFIRMADOS.\nEstado actual: " + this.getEstado());
        }

        // 2. VALIDACIÓN DE CUPO
        if (!hayCupo()) { 
            throw new RuntimeException("No hay cupo disponible.");
        }

        // 3. VALIDACIÓN DE DUPLICADOS 
        if (inscripciones.contains(persona)) {
            throw new RuntimeException("Esta persona YA está inscripta.");
        }

        inscripciones.add(persona);
    }

    // --- GETTERS Y SETTERS ---
    public int getCupoMaximo() { return cupoMaximo; }
    public void setCupoMaximo(int cupoMaximo) { this.cupoMaximo = cupoMaximo; }

    public Modalidad getModalidad() { return modalidad; }
    public void setModalidad(Modalidad modalidad) { this.modalidad = modalidad; }

    public Persona getInstructor() { return instructor; }
    public void setInstructor(Persona instructor) { this.instructor = instructor; }

    public List<Persona> getInscripciones() {
        return inscripciones;
    }
}