package com.eventos.modelo;

import com.eventos.enums.EstadoEvento;
import com.eventos.enums.Modalidad;
import com.eventos.interfaz.Inscribible;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "talleres")
// Al extender de Evento, hereda ID, nombre, fecha y estado.
// JPA hace un JOIN automático con la tabla 'eventos' usando el ID.
public class Taller extends Evento implements Inscribible {

    @Column(name = "cupo_maximo", nullable = false)
    private int cupoMaximo;

    @Enumerated(EnumType.STRING)
    private Modalidad modalidad;

    // Un taller tiene 1 instructor (Persona).
    @ManyToOne
    @JoinColumn(name = "instructor_id")
    private Persona instructor;

    // Usamos Set (Conjunto) en lugar de List para evitar duplicados automáticamente
    // (JPA no dejará insertar dos veces a la misma persona en este taller).
    @ManyToMany
    @JoinTable(
        name = "taller_inscripciones",
        joinColumns = @JoinColumn(name = "taller_id"),
        inverseJoinColumns = @JoinColumn(name = "persona_id")
    )
    private Set<Persona> inscritos = new HashSet<>();

    // --- CONSTRUCTORES ---

    public Taller() {
        super(); // Llama al constructor de Evento
    }

    public Taller(String nombre, LocalDateTime fechaInicio, int duracion, int cupoMaximo, Modalidad modalidad) { 
        super(nombre, fechaInicio, duracion);
        this.cupoMaximo = cupoMaximo;
        this.modalidad = modalidad;
    }

    
    // Aquí implementamos los métodos de la interfaz Inscribible

    @Override
    public boolean hayCupo() {
        return this.inscritos.size() < this.cupoMaximo;
    }

    @Override
    public void inscribir(Persona participante) {
        // Validación 1: El participante no puede ser nulo
        if (participante == null) {
            throw new IllegalArgumentException("El participante no puede ser nulo");
        }

        // Validación 2: Estado del evento (Requerimiento )
        // "El sistema debe evitar que se inscriban si el evento no está confirmado..."
        if (this.getEstado() != EstadoEvento.CONFIRMADO) {
            throw new IllegalStateException("No se puede inscribir: El taller no está CONFIRMADO.");
        }

        // Validación 3: Cupo (Requerimiento )
        if (!hayCupo()) {
            throw new IllegalStateException("No se puede inscribir: Cupo lleno.");
        }

        // Validación 4: Duplicados (Lógica defensiva extra)
        if (this.inscritos.contains(participante)) {
            throw new IllegalStateException("Esta persona ya está inscrita en el taller.");
        }

        // Si pasa todas las validaciones, guardamos
        this.inscritos.add(participante);
    }

    // --- GETTERS Y SETTERS ---

    public int getCupoMaximo() {
        return cupoMaximo;
    }

    public void setCupoMaximo(int cupoMaximo) {
        this.cupoMaximo = cupoMaximo;
    }

    public Modalidad getModalidad() {
        return modalidad;
    }

    public void setModalidad(Modalidad modalidad) {
        this.modalidad = modalidad;
    }

    public Persona getInstructor() {
        return instructor;
    }

    public void setInstructor(Persona instructor) {
        this.instructor = instructor;
    }

    public Set<Persona> getInscritos() {
        return inscritos;
    }

}