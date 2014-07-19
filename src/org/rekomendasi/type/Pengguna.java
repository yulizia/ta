/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.rekomendasi.type;

import java.awt.image.BufferedImage;

/**
 *
 * @author YuliArnezia
 */
public class Pengguna 
{
    private String username;
    private String nama;
    private String password;
    private BufferedImage imagePengguna;

    public Pengguna() {
    }

    public Pengguna(String username, String nama, String password, BufferedImage imagePengguna) {
        this.username = username;
        this.nama = nama;
        this.password = password;
        this.imagePengguna = imagePengguna;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public BufferedImage getImagePengguna() {
        return imagePengguna;
    }

    public void setImagePengguna(BufferedImage imagePengguna) {
        this.imagePengguna = imagePengguna;
    }
    
    public String toString()
    {
        return username;
    }
}
