package com.rentacar.backend.services;

import com.rentacar.backend.entities.ReservaEntity;
import com.rentacar.backend.entities.UsuarioEntity;
import com.rentacar.backend.entities.ValoracionEntity;
import com.rentacar.backend.entities.VehiculoEntity;
import com.rentacar.backend.repositories.UsuarioRepository;
import com.rentacar.backend.repositories.ValoracionRepository;
import com.rentacar.backend.repositories.VehiculoRepository;

import java.util.List;
import java.util.Optional;

public class ValoracionService {

    private final ValoracionRepository valoracionRepository;
    private final VehiculoRepository vehiculoRepository;
    private final UsuarioRepository usuarioRepository;

    public ValoracionService(ValoracionRepository valoracionRepository, VehiculoRepository vehiculoRepository,
                             UsuarioRepository usuarioRepository) {
        this.valoracionRepository = valoracionRepository;
        this.vehiculoRepository = vehiculoRepository;
        this.usuarioRepository = usuarioRepository;
    }
    public ValoracionEntity encontrarValoracionPorId(Long valoracionID) {
        return valoracionRepository.findById(valoracionID).get();
    }

    List<ValoracionEntity> encontrarPorVehiculo(VehiculoEntity vehiculo) {
        return valoracionRepository.findByVehiculo(vehiculo);
    }

    public ValoracionEntity crearValoracion(Integer puntuacion, String comentario, Long usuarioID, Long vehiculoID){
        ValoracionEntity valoracion = new ValoracionEntity();
        valoracion.setComentario(comentario);
        valoracion.setPuntuacion(puntuacion);
        valoracion.setUsuario(usuarioRepository.findById(usuarioID).get());
        valoracion.setVehiculo(vehiculoRepository.findById(vehiculoID).get());
        return valoracionRepository.save(valoracion);
    }
    public ValoracionEntity editarValoracion(Long valoracionID, String comentario, Integer puntuacion){
        Optional<ValoracionEntity> valoracion = valoracionRepository.findById(valoracionID);
        if(valoracion.isPresent()){
            valoracion.get().setComentario(comentario);
            valoracion.get().setPuntuacion(puntuacion);
            return valoracionRepository.save(valoracion.get());
        }else{
            throw new RuntimeException("Usuario no encontrado con ID: " + valoracionID);
        }
    }
    public void borrarValoracion(Long sucursalID) {
        Optional<ValoracionEntity> sucursal = valoracionRepository.findById(sucursalID);
        if(sucursal.isPresent()){
            valoracionRepository.deleteById(sucursalID);
        } else {
            throw new RuntimeException("Usuario no encontrado con ID: " + sucursalID);
        }
    }

}
