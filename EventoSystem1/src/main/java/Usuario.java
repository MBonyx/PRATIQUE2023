package main.java;

import java.io.Serializable;

public class Usuario implements Serializable {
    private String nome;
    private String cidade;
    private String email;

    public Usuario(String nome, String cidade, String email) {
        this.nome = nome;
        this.cidade = cidade;
        this.email = email;
    }

    public String getNome() {
        return nome;
    }

    public String getCidade() {
        return cidade;
    }

    public String getEmail() {
        return email;
    }
}
