package com.example.searchsport.dto;

public class PerfilUsuarioResponse {

    private Long id;
    private String rut;
    private String nombre;
    private String segundoNombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String email;
    private Boolean activo;
    private Long rolId;
    private String rolNombre;

    public PerfilUsuarioResponse() {
    }

    public PerfilUsuarioResponse(
            Long id,
            String rut,
            String nombre,
            String segundoNombre,
            String apellidoPaterno,
            String apellidoMaterno,
            String email,
            Boolean activo,
            Long rolId,
            String rolNombre
    ) {
        this.id = id;
        this.rut = rut;
        this.nombre = nombre;
        this.segundoNombre = segundoNombre;
        this.apellidoPaterno = apellidoPaterno;
        this.apellidoMaterno = apellidoMaterno;
        this.email = email;
        this.activo = activo;
        this.rolId = rolId;
        this.rolNombre = rolNombre;
    }

    public Long getId() {
        return id;
    }

    public String getRut() {
        return rut;
    }

    public String getNombre() {
        return nombre;
    }

    public String getSegundoNombre() {
        return segundoNombre;
    }

    public String getApellidoPaterno() {
        return apellidoPaterno;
    }

    public String getApellidoMaterno() {
        return apellidoMaterno;
    }

    public String getEmail() {
        return email;
    }

    public Boolean getActivo() {
        return activo;
    }

    public Long getRolId() {
        return rolId;
    }

    public String getRolNombre() {
        return rolNombre;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setRut(String rut) {
        this.rut = rut;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setSegundoNombre(String segundoNombre) {
        this.segundoNombre = segundoNombre;
    }

    public void setApellidoPaterno(String apellidoPaterno) {
        this.apellidoPaterno = apellidoPaterno;
    }

    public void setApellidoMaterno(String apellidoMaterno) {
        this.apellidoMaterno = apellidoMaterno;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public void setRolId(Long rolId) {
        this.rolId = rolId;
    }

    public void setRolNombre(String rolNombre) {
        this.rolNombre = rolNombre;
    }
}