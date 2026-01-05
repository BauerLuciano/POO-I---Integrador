package com.eventos;

// --- IMPORTS OBLIGATORIOS (Sin esto Java no encuentra las clases) ---
import com.eventos.modelo.Taller;
import com.eventos.modelo.Concierto;
import com.eventos.modelo.Persona;
import com.eventos.modelo.Evento;
import com.eventos.enums.Modalidad;
import com.eventos.enums.TipoEntrada;
import com.eventos.repo.EventoRepository;
import com.eventos.repo.EventoRepositoryImpl;
import com.eventos.repo.PersonaRepository;

import java.time.LocalDateTime;

public class MainTest {
    public static void main(String[] args) {
        System.out.println("--- PRUEBA INTEGRAL DE BACKEND ---");

        // 1. Instanciamos los repositorios
        PersonaRepository personaRepo = new PersonaRepository();
        EventoRepository repo = new EventoRepositoryImpl();

        // 2. Prueba con TALLER
        System.out.println("\n>>> Creando Taller...");
        Taller taller = new Taller(
            "Java para Principiantes",
            LocalDateTime.now().plusDays(5),
            120, // duracion
            50,  // cupo
            Modalidad.VIRTUAL
        );
        repo.guardar(taller);
        System.out.println("Taller guardado: " + taller.getNombre());

        // 3. Prueba con CONCIERTO (y Fito)
        System.out.println("\n>>> Buscando a Fito...");
        Persona fito = personaRepo.buscarPorDni("11223344");
        
        if (fito == null) {
            System.out.println("Fito no existe, lo creamos.");
            fito = new Persona("Fito Paez", "11223344", "fito@rock.com", "11-1234-5678");
            personaRepo.guardar(fito);
        } else {
            System.out.println("Fito encontrado: " + fito.getNombreCompleto());
        }

        System.out.println("\n>>> Creando Concierto...");
        Concierto concierto = new Concierto();
        concierto.setNombre("Recital en el Estadio");
        concierto.setFechaInicio(LocalDateTime.now().plusMonths(1));
        concierto.setTipoEntrada(TipoEntrada.PAGA);
        concierto.agregarArtista(fito);
        
        repo.guardar(concierto);
        System.out.println("Concierto guardado. ID: " + concierto.getId());

        System.out.println("\n¡TODO FUNCIONA FIERA!");
    }
}