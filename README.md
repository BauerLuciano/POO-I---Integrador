# Sistema de Gestión de Eventos Culturales 🎭

**Trabajo Integrador 2025 - Programación Orientada a Objetos I**
*Universidad Nacional de Misiones*

Este proyecto implementa una aplicación de escritorio para la gestión integral de eventos municipales (ferias, conciertos, exposiciones, talleres y ciclos de cine), permitiendo la administración de recursos, responsables y participantes.

## 📋 Descripción
El sistema resuelve la necesidad del municipio de organizar su agenda cultural. Permite administrar el ciclo de vida completo de un evento (desde su planificación hasta su finalización), gestionando inscripciones, cupos y roles específicos (organizadores, artistas, curadores, instructores).

### Características Principales
**Gestión de Eventos:** Alta, baja y modificación de distintos tipos de eventos con atributos específicos (ej. stands para ferias, artistas para conciertos)[cite: 6, 7].
**Control de Estados:** Manejo de flujo de estados (En planificación, Confirmado, En ejecución, Finalizado) con validaciones lógicas[cite: 14, 15].
**Roles y Personas:** Registro de personas y asignación de roles (Organizador, Artista, Curador, Instructor)[cite: 11].
**Inscripciones:** Gestión de asistentes, validación de cupos y control de fechas de inscripción

## 🛠️ Tecnologías
* **Lenguaje:** Java (JDK 17+ recomendado).
* **Interfaz Gráfica (GUI):** JavaFX.
* **Persistencia:** JPA (Java Persistence API) / Hibernate.
* **Base de Datos:** (Especificar aquí: MySQL / H2 / PostgreSQL).

## 🚀 Arquitectura
El proyecto sigue un diseño orientado a objetos utilizando **herencia y polimorfismo** para los tipos de eventos, y una arquitectura en capas (Modelo-Vista-Controlador) para separar la lógica de negocio de la interfaz gráfica[cite: 25, 27].

---
**Autores:**
* [Bauer Luciano Agustín]
* [Olivieri Ricardo]
