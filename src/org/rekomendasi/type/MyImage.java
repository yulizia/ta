/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.rekomendasi.type;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author YuliArnezia
 */
public class MyImage 
{
    private int idImage;
    private String pemilikImage;
    private BufferedImage buffImage;
    private String namaImage;
    private String deskripsiImage;
    private List<String> tagImagePemilik;
    private HashMap<String,List<String>> tagOrangLain;
    private List<String> likeUsers;

    public MyImage()
    {
        tagImagePemilik = new ArrayList<>();
        tagOrangLain = new HashMap<>();
        likeUsers = new ArrayList<>();
    }

    public MyImage(int idImage, String pemilikImage, BufferedImage buffImage, String namaImage, String deskripsiImage, List<String> tagImagePemilik, HashMap<String, List<String>> tagOrangLain, List<String> likeUsers) {
        this.idImage = idImage;
        this.pemilikImage = pemilikImage;
        this.buffImage = buffImage;
        this.namaImage = namaImage;
        this.deskripsiImage = deskripsiImage;
        this.tagImagePemilik = tagImagePemilik;
        this.tagOrangLain = tagOrangLain;
        this.likeUsers = likeUsers;
        
    }

    public int getIdImage() {
        return idImage;
    }

    public void setIdImage(int idImage) {
        this.idImage = idImage;
    }

    public String getNamaImage() {
        return namaImage;
    }

    public void setNamaImage(String namaImage) {
        this.namaImage = namaImage;
    }

    public String getDeskripsiImage() {
        return deskripsiImage;
    }

    public void setDeskripsiImage(String deskripsiImage) {
        this.deskripsiImage = deskripsiImage;
    }

    
    
  

    public String getPemilikImage() {
        return pemilikImage;
    }

    public void setPemilikImage(String pemilikImage) {
        this.pemilikImage = pemilikImage;
    }

    public BufferedImage getBuffImage() {
        return buffImage;
    }

    public void setBuffImage(BufferedImage buffImage) {
        this.buffImage = buffImage;
    }

    public List<String> getTagImagePemilik() {
        return tagImagePemilik;
    }

    public void setTagImagePemilik(List<String> tagImagePemilik) {
        this.tagImagePemilik = tagImagePemilik;
    }

    public HashMap<String, List<String>> getTagOrangLain() {
        return tagOrangLain;
    }

    public void setTagOrangLain(HashMap<String, List<String>> tagOrangLain) {
        this.tagOrangLain = tagOrangLain;
    }
    
    public String toString()
    {
        return idImage+"";
    }

    public List<String> getLikeUsers() {
        return likeUsers;
    }

    public void setLikeUsers(List<String> likeUsers) {
        this.likeUsers = likeUsers;
    }
    
    
}
