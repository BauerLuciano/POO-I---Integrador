package com.eventos.modelo;

import jakarta.persistence.*;

@Entity
@Table(name = "ferias")
public class Feria extends Evento {

    @Column(name = "cantidad_stands")
    private int cantidadStands;

    @Column(name = "al_aire_libre")
    private boolean alAireLibre;

    public Feria() {
        super();
    }

    // Getters y Setters
    public int getCantidadStands() { return cantidadStands; }
    public void setCantidadStands(int cantidadStands) { this.cantidadStands = cantidadStands; }

    public boolean isAlAireLibre() { return alAireLibre; }
    public void setAlAireLibre(boolean alAireLibre) { this.alAireLibre = alAireLibre; }
}