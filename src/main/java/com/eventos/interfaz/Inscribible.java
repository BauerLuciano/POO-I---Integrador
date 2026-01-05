package com.eventos.interfaz;

import com.eventos.modelo.Persona; 

public interface Inscribible {
    // Método que intenta inscribir a alguien. Puede fallar (lanzar excepción).
    void inscribir(Persona participante);
    
    // Método de consulta para saber si entra alguien más.
    boolean hayCupo();
}