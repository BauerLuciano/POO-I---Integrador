package com.eventos.repo;

import com.eventos.modelo.Persona;
import com.eventos.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import java.util.List;

public class PersonaRepository {

    // 1. GUARDAR (Sirve para Crear y para Editar)
    public void guardar(Persona p) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            if (p.getId() == null) {
                em.persist(p); // Es nuevo -> INSERT
            } else {
                em.merge(p);   // Ya existe -> UPDATE
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    // 2. ELIMINAR
    public void eliminar(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Persona p = em.find(Persona.class, id);
            if (p != null) {
                em.remove(p);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    // 3. LISTAR TODOS
    public List<Persona> listarTodos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT p FROM Persona p ORDER BY p.id", Persona.class).getResultList();
        } finally {
            em.close();
        }
    }

    // 4. BUSCAR POR DNI (¡ESTE ERA EL QUE FALTABA Y ROMPÍA TODO!)
    public Persona buscarPorDni(String dni) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT p FROM Persona p WHERE p.dni = :dni", Persona.class)
                     .setParameter("dni", dni)
                     .getSingleResult();
        } catch (NoResultException e) {
            return null; 
        } finally {
            em.close();
        }
    }
}