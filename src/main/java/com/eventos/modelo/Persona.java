package com.eventos.modelo;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "personas")
public class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombreCompleto;

    // El DNI es obligatorio y único en la base de datos
    @Column(unique = true, nullable = false)
    private String dni;

    private String email;
    
    private String telefono;

    // --- CONSTRUCTORES ---

    // Constructor vacío: OBLIGATORIO para que JPA pueda crear el objeto
    public Persona() {
    }

    // Constructor completo para crear objetos fácil desde el código
    public Persona(String nombreCompleto, String dni, String email, String telefono) {
        this.nombreCompleto = nombreCompleto;
        this.dni = dni;
        this.email = email;
        this.telefono = telefono;
    }

    // --- GETTERS Y SETTERS (Todos los campos) ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    // Definimos que dos personas son iguales si tienen el mismo DNI

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Persona persona = (Persona) o;
        return Objects.equals(dni, persona.dni);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dni);
    }

    // --- REPRESENTACIÓN EN TEXTO ---
    
    // Útil para logs o para ver qué objeto es cuando depurás
    @Override
    public String toString() {
        return "Persona{" +
                "id=" + id +
                ", nombre='" + nombreCompleto + '\'' +
                ", dni='" + dni + '\'' +
                '}';
    }
}