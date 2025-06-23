package com.proyecto.KASH.servicio;

import com.proyecto.KASH.Repository.PQRSRepository;
import com.proyecto.KASH.entidad.PQRS;
import jakarta.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PQRSServicioImpl implements PQRSServicio {

    @Autowired
    private PQRSRepository pqrsRepositorio;

    @Override
    @Transactional
    public PQRS guardarPQRS(PQRS pqrs) {
        return pqrsRepositorio.save(pqrs);
    }

    @Override
    public List<PQRS> obtenerTodasPQRS() {
        return pqrsRepositorio.findAll();
    }

    @Override
    public PQRS obtenerPQRSPorId(Long id) {
        Optional<PQRS> pqrs = pqrsRepositorio.findById(id);
        return pqrs.orElse(null);
    }

    @Override
    @Transactional
    public void eliminarPQRS(Long id) {
        pqrsRepositorio.deleteById(id);
    }

    public List<PQRS> obtenerTodasLasPQRS() {
        return pqrsRepositorio.findAll();
    }

    @Override
    public List<PQRS> listarTodas() {
        return pqrsRepositorio.findAll();
    }

    @Override
    public List<PQRS> buscarPorCriterio(String filtro) {
        return pqrsRepositorio.buscarPorCriterio(filtro.toLowerCase());// Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }


}
