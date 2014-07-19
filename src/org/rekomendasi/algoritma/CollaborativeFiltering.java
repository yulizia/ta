/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.rekomendasi.algoritma;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;
import org.rekomendasi.database.dao.KontrollerDatabase;
import org.rekomendasi.gui.Rekomendasi;
import org.rekomendasi.type.MyImage;
import org.rekomendasi.type.Pengguna;

/**
 *
 * @author YuliArnezia
 */
public class CollaborativeFiltering 
{
    private JTextArea myText;
    private List<MyImage> imagesPengguna;
    private List<MyImage> imageUserLain;
    private Rekomendasi rek;

    public CollaborativeFiltering() 
    {
        
    }

    
    public CollaborativeFiltering(Rekomendasi rek,JTextArea myText, List<MyImage> imagesPengguna, List<MyImage> imageUserLain)
    {
        this.myText = myText;
        this.imagesPengguna = imagesPengguna;
        this.imageUserLain = imageUserLain;
        this.rek = rek;
    }
    
    
    public void updateInfoAwal()
    {
        myText.append("USER AKTIF\t:"+Rekomendasi.pengguna.getNama()+"\n");
        myText.append("Jum Img User\t:"+getJumlahImageUser()+"\n");
        myText.append("Terdiri Dari\t:\n");
        myText.append("-------------------------------------------------------------------------------\n");
        int i = 1;
        for(MyImage ims : imagesPengguna)
        {
            myText.append("Image "+ i++ +"\n");
            myText.append("Nama\t:"+ims.getNamaImage()+"\n");
            myText.append("Deskripsi\t:"+ims.getDeskripsiImage()+"\n");
         //   myText.append("Tagging user\t:"+getTaggingUserAtasImage(ims)+"\n");
            if(ims.getLikeUsers().size()>0)
            {
                myText.append("User Like\t:"+getUSerLikeImage(ims)+"\n");
            }
            else
            {
                myText.append("Tidak Ada User Yang Like Foto Ini \n");
            }
      //      if(ims.getTagOrangLain().size()!=0)
       //     {
         //        myText.append("Tagging USer Lain\t:\n");
        //       myText.append(getTaggingUserAtasImageDariUserLain(ims));
        //    }
           
            myText.append("-------------------------------------------------------------------------------\n");
        }
        myText.append("*****************************************************************************************\n");
        myText.append("Jum Img UserLain\t:"+getJumlahImageUserLain()+"\n");
        myText.append("Terdiri Dari\t:\n");
        myText.append("-------------------------------------------------------------------------------\n");
        i = 1;
        for(MyImage ims : imageUserLain)
        {
            myText.append("Image "+ ims.getIdImage()+"\n");
            myText.append("Pemilik\t:"+ims.getPemilikImage()+"\n");
            myText.append("Nama\t:"+ims.getNamaImage()+"\n");
            myText.append("Deskripsi\t:"+ims.getDeskripsiImage()+"\n");
           // myText.append("Tagging user\t:"+getTaggingUserAtasImage(ims)+"\n");
            System.out.println("------------------------------------------------------------------------------------"+ims.getLikeUsers().size());
            if(ims.getLikeUsers().size()>0)
            {
                myText.append("User Like\t:"+getUSerLikeImage(ims)+"\n");
            }
            else
            {
                myText.append("Tidak Ada User Yang Like Foto Ini \n");
            }
        //    if(ims.getTagOrangLain().size()!=0)
         //   {
         //       myText.append("Tagging USer Lain\t:\n");
         //       myText.append(getTaggingUserAtasImageDariUserLain(ims));
         //   }
           
            myText.append("-------------------------------------------------------------------------------\n");
        }
        double[][] matriksSemuaUser = getMatrikSemuaUser();
//        for(int k = 0; k< matriksSemuaUser.length;k++)
//        {
//            for(int j = 0; j< matriksSemuaUser[k].length;j++)
//            {
//                System.out.print(matriksSemuaUser[k][j]+" - ");
//            }
//            System.out.println("");
//        }
        
        double[][] userSimilaritySemuaUser = getUserSimilaritySemuaUser(matriksSemuaUser);
        printDataUserSimilaritySemuaUser(userSimilaritySemuaUser);
        int indeksPEnggunaAktif = getIndeksPenggunaYangSedangAktif();
        HashMap<String,Integer> mapUser = getMappingUSer();
        HashMap<String,Integer> mapImage = getMappingImage();
        
        
        int[] resPred = AlgoritamaColFiltering.eksekusiTradisionalFiltering(matriksSemuaUser,indeksPEnggunaAktif,mapUser,mapImage,myText);
        System.out.println("dari sini aku mendapatkan urutan ="+Arrays.toString(resPred) );
        rek.tampilkanSemuaImageREkomendasiTraditionalCollaborative(resPred);
    }
    public String getUSerLikeImage(MyImage myImage)
    {
        String like = "";
       
        for(String use : myImage.getLikeUsers())
        {
            like += use+"; ";
        }
        return like;
    }
    public HashMap<String,Integer> getMappingUSer()
    {
        HashMap<String, Integer> mapUser = new HashMap<>();
        int ind = 0;
        try 
        {
            List<Pengguna> pengs = KontrollerDatabase.getSemuaUSer();
            for(Pengguna peng:pengs)
            {
                mapUser.put(peng.getUsername(),ind++);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(CollaborativeFiltering.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CollaborativeFiltering.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(CollaborativeFiltering.class.getName()).log(Level.SEVERE, null, ex);
        }
        return mapUser;
    }
    public HashMap<String,Integer> getMappingImage()
    {
        HashMap<String, Integer> mapUser = new HashMap<>();
        int ind = 0;
        try 
        {
            List<MyImage> pengs = KontrollerDatabase.getSemuaImages();
            for(MyImage peng:pengs)
            {
                mapUser.put(peng.getIdImage()+"",ind++);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(CollaborativeFiltering.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CollaborativeFiltering.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(CollaborativeFiltering.class.getName()).log(Level.SEVERE, null, ex);
        }
        return mapUser;
    }
    
    public int getIndeksPenggunaYangSedangAktif()
    {
        int penggunaAktif = -1;
        int indeks = 0;
        try {
            List<Pengguna> pengs = KontrollerDatabase.getSemuaUSer();
            for(Pengguna pengguna : pengs)
            {
                if(pengguna.getNama().equalsIgnoreCase(Rekomendasi.pengguna.getNama()))
                {
                    penggunaAktif = indeks;
                }
                indeks++;
            }
        } catch (SQLException ex) {
            Logger.getLogger(CollaborativeFiltering.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CollaborativeFiltering.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(CollaborativeFiltering.class.getName()).log(Level.SEVERE, null, ex);
        }
         
        return penggunaAktif;
        
    }
    public double[][] getNeihborhoodUser(double[][] userSimilarity)
    {
        double[][] dataBaru = null;
        try 
        {
            List<Pengguna> pengs = KontrollerDatabase.getSemuaUSer();
            int indeks = 0;
            for(Pengguna peng : pengs)
            {
                
                if(peng.getUsername().equalsIgnoreCase(Rekomendasi.pengguna.getUsername()))
                {
                    break;
                }
                indeks++;
            }
            double[] data = userSimilarity[indeks];
            List<Integer> indeksList = new ArrayList<>();
            int k = 0;
            for(double da : data)
            {
                if(!Double.isNaN(da) && da>0)
                {
                    indeksList.add(k++);
                }
            }
           dataBaru = new double[indeksList.size()][indeksList.size()];
          
        } catch (SQLException ex) {
            Logger.getLogger(CollaborativeFiltering.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CollaborativeFiltering.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(CollaborativeFiltering.class.getName()).log(Level.SEVERE, null, ex);
        }
        return dataBaru;
    }
    private String getTaggingUserAtasImage(MyImage image)
    {
        String tags = "";
        if(image.getTagImagePemilik().size()!=0)
        {
            for(String tag : image.getTagImagePemilik())
            {
                tags +=tag+";";
            }
        }
        
        return tags;
    }
    private String getTaggingUserAtasImageDariUserLain(MyImage image)
    {
        String data = "";
        if(image.getTagOrangLain().size()!=0)
        {
            for(String userLain : image.getTagOrangLain().keySet())
            {
                List<String> listTagUSerLain = image.getTagOrangLain().get(userLain);
                String tags = "";
                for(String tag : listTagUSerLain)
                {
                     tags +=tag+";";
                }
                data += "User : "+userLain+" Dengan Tag : "+tags+"\n";
            }
        }
        
        return data;
    }
    private int getJumlahImageUser()
    {
        return imagesPengguna.size();
    }

     private int getJumlahImageUserLain()
    {
        return imageUserLain.size();
    }
    public List<MyImage> getImagesPengguna() {
        return imagesPengguna;
    }

    public void setImagesPengguna(List<MyImage> imagesPengguna) {
        this.imagesPengguna = imagesPengguna;
    }

    public List<MyImage> getImageUserLain() {
        return imageUserLain;
    }

    public void setImageUserLain(List<MyImage> imageUserLain) {
        this.imageUserLain = imageUserLain;
    }

    public JTextArea getMyText() {
        return myText;
    }

    public void setMyText(JTextArea myText) {
        this.myText = myText;
    }
    
    public double[][] getMatrikSemuaUser()
    {
        double[][] data = null;
        try 
        {
            List<Pengguna> pengs = KontrollerDatabase.getSemuaUSer();
            List<MyImage> ims = KontrollerDatabase.getSemuaImages();
            myText.append("MATRIKS TRADISONAL SEMUA USER :\n");
            for(MyImage im : ims)
            {
                myText.append("\t"+im+"\t");
            }
            myText.append("\n");
            data = new double[pengs.size()][ims.size()];
            for(int i = 0; i< pengs.size();i++)
            {
                data[i] = getSemuaImageYangDisukaiUser(pengs.get(i), ims);
            }
            int i = 0;
            for(double[] dat1 : data)
            {
                myText.append(pengs.get(i++)+"");
                for(double dat2 : dat1)
                {
                   myText.append("\t"+dat2+"\t");
                }
                myText.append("\n");
            }
            myText.append("************************************************************************************************\n");
        } catch (SQLException ex) {
            Logger.getLogger(CollaborativeFiltering.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CollaborativeFiltering.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(CollaborativeFiltering.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return data;
    }
    public double[] getSemuaImageYangDisukaiUser(Pengguna pengguna,List<MyImage> ims) throws SQLException, IOException, ClassNotFoundException
    {
        List<Integer> ins = KontrollerDatabase.getSemuaImageYAngDiSukaiPengguna(pengguna.toString());
        double[] data = new double[ims.size()];
        
        for(int i = 0; i< ims.size();i++)
        {
            for(int ids : ins)
            {
                if(ims.get(i).getIdImage()==ids)
                {
                    data[i] = 1;
                }
            }
        }
        return data;
    }
    public double[][] getUserSimilaritySemuaUser(double[][] data)
    {
        double[][] dataSimilarity  = new double[data.length][data.length];
        
         for(int i = 0; i< data.length;i++)
         {
             for(int j = 0; j< data.length;j++)
             {
                 if(i!=j)
                 {
                     dataSimilarity[i][j] = getDotProduct(data[i],data[j])/(getMAgnitudeUser(data[i])*getMAgnitudeUser(data[j]));
                 }
                 if(Double.isNaN(dataSimilarity[i][j]))
                 {
                     dataSimilarity[i][j] = 0;
                 }
             }
         }
        return dataSimilarity;        
    }
    private void printDataUserSimilaritySemuaUser(double[][] userSim)
    {
        NumberFormat formatter = new DecimalFormat("0.00");
        try {
            List<Pengguna> pengs = KontrollerDatabase.getSemuaUSer();
            myText.append("USER SIMILARITY SEMUA USER ADALAH :\n\t");
            for(Pengguna peng : pengs)
            {
                myText.append(peng.getUsername()+"\t");
            }
            myText.append("\n");
            for(int i = 0; i< userSim.length;i++)
            {
                myText.append(pengs.get(i)+"\t");
                for(int j = 0; j< userSim[i].length;j++)
                {
                    myText.append((Double.isNaN(userSim[i][j])? "-":formatter.format(userSim[i][j]))+"\t");
                }
                myText.append("\n");
            }
        } catch (SQLException ex) {
            Logger.getLogger(CollaborativeFiltering.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CollaborativeFiltering.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(CollaborativeFiltering.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public double getDotProduct(double[] userSatu,double[] userDua)
    {
        double hasil = 0;
        for(int i = 0; i< userSatu.length;i++)
        {
            hasil += userSatu[i]*userDua[i];
        }
        
        return hasil;
    }
    public double getMAgnitudeUser(double[] user)
    {
        double hasil = 0;
        for(int i = 0; i< user.length;i++)
        {
            hasil += Math.pow(user[i],2);
        }
        return Math.sqrt(hasil);
    }
    
}
