package com.eventos.modelo;

import com.eventos.enums.TipoEntrada;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "conciertos")
public class Concierto extends Evento {

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_entrada")
    private TipoEntrada tipoEntrada;

    // Relación Muchos-a-Muchos para los ARTISTAS
    @ManyToMany
    @JoinTable(
        name = "concierto_artistas",
        joinColumns = @JoinColumn(name = "concierto_id"),
        inverseJoinColumns = @JoinColumn(name = "persona_id")
    )
    private List<Persona> artistas = new ArrayList<>();

    public Concierto() {
        super();
    }

    public void agregarArtista(Persona artista) {
        this.artistas.add(artista);
    }

    // Getters y Setters
    public TipoEntrada getTipoEntrada() { return tipoEntrada; }
    public void setTipoEntrada(TipoEntrada tipoEntrada) { this.tipoEntrada = tipoEntrada; }

    public List<Persona> getArtistas() { return artistas; }
    public void setArtistas(List<Persona> artistas) { this.artistas = artistas; }
}