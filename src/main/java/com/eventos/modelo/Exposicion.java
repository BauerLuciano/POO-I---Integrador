package com.eventos.modelo;

import jakarta.persistence.*;

@Entity
@Table(name = "exposiciones")
public class Exposicion extends Evento {

    @Column(name = "tipo_arte")
    private String tipoArte; // Ej: "Pintura", "Fotografía"

    // Relación Muchos-a-Uno: Una expo tiene 1 curador, 
    // pero una persona puede ser curadora de muchas expos.
    @ManyToOne
    @JoinColumn(name = "curador_id")
    private Persona curador;

    public Exposicion() {
        super();
    }

    // Getters y Setters
    public String getTipoArte() { return tipoArte; }
    public void setTipoArte(String tipoArte) { this.tipoArte = tipoArte; }

    public Persona getCurador() { return curador; }
    public void setCurador(Persona curador) { this.curador = curador; }
}