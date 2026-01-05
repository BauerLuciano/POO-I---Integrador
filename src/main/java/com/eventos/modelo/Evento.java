package com.eventos.modelo;

// Importamos el Enum (Ajustá el paquete si lo pusiste en otro lado)
import com.eventos.enums.EstadoEvento; 
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "eventos")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime fechaInicio;

    @Column(name = "duracion_minutos")
    private int duracionEstimada; // En minutos (más fácil de manejar que Duration en DBs simples)

    // Guardamos el estado como TEXTO ("CONFIRMADO") en lugar de números (0, 1)
    // para evitar problemas si cambiamos el orden del Enum en el futuro.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoEvento estado;

    // RELACIÓN: Un evento tiene varios responsables, y una persona puede organizar varios eventos.
    @ManyToMany
    @JoinTable(
        name = "evento_organizadores", // Nombre de la tabla intermedia
        joinColumns = @JoinColumn(name = "evento_id"),
        inverseJoinColumns = @JoinColumn(name = "persona_id")
    )
    private List<Persona> organizadores = new ArrayList<>();

    // --- CONSTRUCTORES ---

    public Evento() {
        // Por defecto, al crear un evento nace en "PLANIFICACIÓN"
        this.estado = EstadoEvento.EN_PLANIFICACION;
    }

    public Evento(String nombre, LocalDateTime fechaInicio, int duracionEstimada) {
        this(); // Llama al constructor vacío para setear el estado inicial
        this.nombre = nombre;
        this.fechaInicio = fechaInicio;
        this.duracionEstimada = duracionEstimada;
    }

    // --- MÉTODOS DE NEGOCIO (MODELO RICO) ---

    // Permite cambiar el estado (útil para la máquina de estados)
    public void cambiarEstado(EstadoEvento nuevoEstado) {
        if (nuevoEstado == null) {
            throw new IllegalArgumentException("El nuevo estado no puede ser nulo");
        }
        // Acá podrías agregar validaciones extra (ej: no volver a borrador si ya finalizó)
        this.estado = nuevoEstado;
    }

    public void agregarOrganizador(Persona responsable) {
        if (responsable == null) {
            throw new IllegalArgumentException("El organizador no puede ser nulo");
        }
        this.organizadores.add(responsable);
    }
    
    // Método abstracto (opcional): Obliga a los hijos a decir qué tipo son
    // public abstract String getTipo(); 

    // --- GETTERS Y SETTERS ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public int getDuracionEstimada() {
        return duracionEstimada;
    }

    public void setDuracionEstimada(int duracionEstimada) {
        this.duracionEstimada = duracionEstimada;
    }

    public EstadoEvento getEstado() {
        return estado;
    }

    public void setEstado(EstadoEvento estado) {
        this.estado = estado;
    }

    public List<Persona> getOrganizadores() {
        return organizadores;
    }

    public void setOrganizadores(List<Persona> organizadores) {
        this.organizadores = organizadores;
    }

    // --- IDENTIDAD DEL OBJETO ---

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Evento evento = (Evento) o;
        // Si el ID es nulo, no son iguales (a menos que sean la misma instancia en memoria)
        return id != null && Objects.equals(id, evento.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
    
    @Override
    public String toString() {
        return "Evento{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", estado=" + estado +
                '}';
    }
}