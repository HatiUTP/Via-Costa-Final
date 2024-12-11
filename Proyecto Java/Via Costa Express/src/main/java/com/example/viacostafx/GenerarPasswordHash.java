package com.example.viacostafx;

import org.mindrot.jbcrypt.BCrypt;

public class GenerarPasswordHash {
    public static void main(String[] args) {
        String password = "adminpass";
        String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
        System.out.println("Contraseña Hasheada: " + hashed);
    }
}