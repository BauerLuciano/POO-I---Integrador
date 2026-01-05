package com.eventos.modelo;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ciclos_cine")
public class CicloCine extends Evento {

    @Column(name = "hay_charlas")
    private boolean hayCharlas;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "ciclo_cine_id") 
    @OrderColumn(name = "orden_proyeccion") 
    private List<Pelicula> peliculas = new ArrayList<>();

    public CicloCine() {
        super();
    }
    
    public void agregarPelicula(Pelicula p) {
        this.peliculas.add(p);
    }

    // Getters y Setters
    public boolean isHayCharlas() { return hayCharlas; }
    public void setHayCharlas(boolean hayCharlas) { this.hayCharlas = hayCharlas; }
    
    public List<Pelicula> getPeliculas() { return peliculas; }
}