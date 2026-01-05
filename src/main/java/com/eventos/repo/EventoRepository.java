package com.eventos.repo;

import com.eventos.modelo.Evento;
import com.eventos.enums.EstadoEvento;
import java.util.List;

public interface EventoRepository {
    void guardar(Evento evento);
    void actualizar(Evento evento);
    void eliminar(Long id);
    Evento buscarPorId(Long id);
    List<Evento> listarTodos();
    List<Evento> buscarPorEstado(EstadoEvento estado);
}