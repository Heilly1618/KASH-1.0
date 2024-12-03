package com.proyecto.KASH.entidad;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import java.time.LocalDateTime;

@Entity
@Table(name = "comentario")

public class Comentario {

    public static void setIdForo(Long idForo) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "IDforo", nullable = false, length = 100)
    private int idForo;

    @Column(name = "nombre", nullable = false, length = 100)
    public String nombre;

    @Column(name = "contenido", nullable = false, length = 100)
    public String contenido;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getIDforo() {
        return idForo;
    }

    public void setIDforo(int IDforo) {
        this.idForo = IDforo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public Comentario(Long id, int IDforo, String nombre, String contenido) {
        this.id = id;
        this.idForo = IDforo;
        this.nombre = nombre;
        this.contenido = contenido;
    }

    public Comentario(int idForo, String nombre, String contenido) {
        this.idForo = idForo;
        this.nombre = nombre;
        this.contenido = contenido;
    }
    
    public Comentario(){
        
    }
    
    @Override
    public String toString() {
        return "Comentario{" + "id=" + id + ", IDforo=" + idForo + ", nombre=" + nombre + ", contenido=" + contenido + '}';
    }

}
