package com.eventos.modelo;

import jakarta.persistence.*;

@Entity
@Table(name = "peliculas")
public class Pelicula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String director;
    private int duracionMinutos;

    public Pelicula() {}

    public Pelicula(String titulo, String director, int duracion) {
        this.titulo = titulo;
        this.director = director;
        this.duracionMinutos = duracion;
    }
    
    // Getters y Setters (omito por brevedad, agregalos vos)
    public String getTitulo() { return titulo; }
    public void setTitulo(String t) { this.titulo = t; }
}