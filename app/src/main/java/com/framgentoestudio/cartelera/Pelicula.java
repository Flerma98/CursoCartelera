package com.framgentoestudio.cartelera;

public class Pelicula {
    private String Titulo;
    private String Director;
    private String UID;

    public Pelicula() {
    }

    public Pelicula(String titulo, String director, String uid) {
        Titulo = titulo;
        Director = director;
        UID = uid;
    }

    public String getTitulo() {
        return Titulo;
    }

    public void setTitulo(String titulo) {
        Titulo = titulo;
    }

    public String getDirector() {
        return Director;
    }

    public void setDirector(String director) {
        Director = director;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }
}
