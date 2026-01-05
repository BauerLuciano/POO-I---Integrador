package com.eventos.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JPAUtil {
    
    // La fábrica es estática y única para toda la app
    private static final EntityManagerFactory FACTORY = 
            Persistence.createEntityManagerFactory("Eventos"); // Mismo nombre que en el XML

    // Método para pedir un EntityManager (el obrero que hace las consultas)
    public static EntityManager getEntityManager() {
        return FACTORY.createEntityManager();
    }
    
    // Cerrar la fábrica al apagar la app
    public static void close() {
        FACTORY.close();
    }
}