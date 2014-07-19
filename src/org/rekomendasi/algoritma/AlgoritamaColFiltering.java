/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.rekomendasi.algoritma;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import javax.swing.JTextArea;
import org.rekomendasi.gui.Rekomendasi;

/**
 *
 * @author YuliArnezia
 */
public class AlgoritamaColFiltering 
{
    public static double[][] getSemuaUserSimilarity(double[][] matriksTradisional)
    {
        double[][] sim = new double[matriksTradisional.length][matriksTradisional.length];
        for(int i = 0; i< sim.length;i++)
        {
            for(int j = 0; j<sim[i].length;j++)
            {
                 if(i!=j)
                 {
                     sim[i][j] = getDotProduct(matriksTradisional[i],matriksTradisional[j])/(getMAgnitudeUser(matriksTradisional[i])*getMAgnitudeUser(matriksTradisional[j]));
                 }
                 if(Double.isNaN(sim[i][j]))
                 {
                     sim[i][j] = 0;
                 }
            }
        }
        return sim;
    }
    
    public static double[][] getTradMAtriksAktifUSer(double[][] trad, int[] user, int[] res)
    {
        double sim[][] = new double[user.length][res.length];
        for(int i  = 0; i< user.length;i++)
        {
            for(int j = 0; j< res.length;j++)
            {
                sim[i][j] = trad[user[i]][res[j]];
            }
        }
        return sim;
    }
    
   
    public static int[] getIndeksResourceAktifUserDanNeighbour(double[][] matriksTrad, int[] users )
    {
        List<Integer> indeks = new ArrayList<>();
        for(int k = 0; k< users.length;k++)
        {
            for(int j = 0; j< matriksTrad[k].length;j++)
            {
                if(matriksTrad[users[k]][j] != 0  && !sudahPadaList(indeks, j))
                {
                    indeks.add(j);
                }
            }
        }
        int[] ind = new int[indeks.size()];
        for(int i = 0; i<ind.length;i++)
        {
            ind[i] = indeks.get(i);
        }
        return ind;
    }
    public static boolean sudahPadaList(List<Integer> data,int ind)
    {
        for(Integer dat : data)
        {
            if(dat.intValue()==ind)
            {
                return true;
            }
        }
        return false;
    }
    
    public static int[] getIndeksNeigbourUSerAktif(double[][] userSimalirity, int indeks)
    {
        double[] userSim = userSimalirity[indeks];
        List<Integer> indeksListYAngTidakKosong = new ArrayList<>();
        for(int i = 0; i< userSim.length;i++)
        {
            if(userSim[i]!=0)
            {
                indeksListYAngTidakKosong.add(i);
            }
        }
        int[] indeksTidakKosong = new int[indeksListYAngTidakKosong.size()];
        
        for(int i = 0;i< indeksTidakKosong.length;i++)
        {
            indeksTidakKosong[i] = indeksListYAngTidakKosong.get(i).intValue() ;
        }
        return indeksTidakKosong;
    }
    
    public static double getDotProduct(double[] userSatu,double[] userDua)
    {
        double hasil = 0;
        for(int i = 0; i< userSatu.length;i++)
        {
            hasil += userSatu[i]*userDua[i];
        }
        
        return hasil;
    }
    public static double getMAgnitudeUser(double[] user)
    {
        double hasil = 0;
        for(int i = 0; i< user.length;i++)
        {
            hasil += Math.pow(user[i],2);
        }
        return Math.sqrt(hasil);
    }
    public static double[] getAllScorePrediction(double[][] trad,double[][] sim, int[] res,int[] users, int[] resPred, int indAktifUser)
   {
       double[] score = new double[resPred.length];
       int counter = 0;
       for(int  i = 0; i<resPred.length;i++)
       {
           score[counter] = getScoreTiapResource(sim, trad, users, indAktifUser,resPred[i]);
           counter++;
       }
       return score;
    }
    
    public static int[] getAllResPred(double[][] trad,double[][] sim, int[] res,int[] users, int[] resUSerAktif, int[] resNeigh, int indAktifUser)
   {
       List<Integer> dataRes = new ArrayList<>();
       int counter = 0;
       for(int  i = 0; i<resNeigh.length;i++)
       {
           if(!isNotResourceAktif(resUSerAktif, resNeigh[i]))
           {
               dataRes.add(resNeigh[i]);
           }
           
       }
       int[] reso = new int[dataRes.size()];
       int co  = 0;
       for(Integer ints : dataRes)
       {
           reso[co++] = ints;
       }
       
       return reso;
    }
    public static boolean isNotResourceAktif(int[] resAktifUSer, int res)
    {
      
        for(int i = 0; i< resAktifUSer.length; i++)
        {
          
               if(resAktifUSer[i]==res) 
               {
                   return true;
               }
           
        }
        return false;
    }
    public static double getScoreTiapResource(double sim[][],double[][] trad, int[] user, int indeksAktifUser, int indeksRes)
    {
        double score = 0;
        double scorePembilang = 0;
        for(int i = 0; i< user.length;i++)
        {
            scorePembilang += sim[indeksAktifUser][user[i]]*trad[user[i]][indeksRes];
        }
        return scorePembilang/kumSimalirityAktifUSer(sim, indeksAktifUser);
    }
    public static double kumSimalirityAktifUSer(double[][] sim , int aktifUser)
    {
        double kum  = 0;
        for(int i = 0; i< sim.length; i++)
        {
            if(i != aktifUser)
            {
                kum += sim[aktifUser][i];
            }
        }
        
        return kum;
    }
    
    public static int[] eksekusiTradisionalFiltering(double[][] trad,int indeksPenggunaAktif,HashMap<String,Integer> mapUser,HashMap<String,Integer> mapImage, JTextArea texts) 
    {
        int resourcePrediction[] = null;
        double[][] sim = getSemuaUserSimilarity(trad);
        System.out.println("batas");
        for(int i = 0; i< trad.length;i++)
        {
            
            for(int j = 0; j< trad[i].length;j++)
            {
                
                System.out.print("\t"+trad[i][j]+" \t\t");
            }
            System.out.println("");
         //   texts.append("\n");
        }
        System.out.println("batas");
        int[] indeksNEig = getIndeksNeigbourUSerAktif(sim,indeksPenggunaAktif);
        if(indeksNEig.length>0)
        {
        Arrays.sort(indeksNEig);
        int[] semua = new int[indeksNEig.length+1];
        for(int i = 1; i< semua.length;i++)
        {
            semua[i] = indeksNEig[i-1];
        }
        semua[0] = indeksPenggunaAktif;
        System.out.println("isnya adalah = "+ Arrays.toString(semua));
        NumberFormat formatter = new DecimalFormat("0.000");
        int[] res = getIndeksResourceAktifUserDanNeighbour(trad, semua);
        Arrays.sort(semua);
        Arrays.sort(res);
        double[][] matrikTradAktifUSer = getTradMAtriksAktifUSer(trad,semua, res);
        texts.append("************************************************************************\n");
        texts.append("Matriks Tradisional User Aktif "+Rekomendasi.pengguna.getNama()+"\n\t");
        printIndeks(res,mapImage,texts);
        for(int i = 0; i< matrikTradAktifUSer.length;i++)
        {
            texts.append(ketNama(mapUser,semua[i])+"");
            for(int j = 0; j< matrikTradAktifUSer[i].length;j++)
            {
                texts.append("\t"+matrikTradAktifUSer[i][j]+" \t");
            }
            System.out.println("");
            texts.append("\n");
        }
        texts.append("************************************************************************\n");
        texts.append("Matriks User Similarity User Aktif "+Rekomendasi.pengguna.getNama()+"\n\t");
        double[][] simAktifUSer  = getSemuaUserSimilarity(matrikTradAktifUSer);
        printIndeks(semua,mapUser,texts);
      
        for(int i = 0; i< simAktifUSer.length;i++)
        {
            texts.append(ketNama(mapUser,semua[i])+"");
            for(int j = 0; j< simAktifUSer[i].length;j++)
            {
                texts.append("\t"+formatter.format(simAktifUSer[i][j])+" \t");
                System.out.print("\t"+simAktifUSer[i][j]+" \t\t");
            }
            System.out.println("");
            texts.append("\n");
        }
        int[] resUSerAktif = getIndeksResourceAktifUserDanNeighbour(trad,new int[]{indeksPenggunaAktif});
        System.out.println("JUM RES USER AKTIF = "+ resUSerAktif.length);
        int[] resUSerNeigh = getIndeksResourceAktifUserDanNeighbour(trad,indeksNEig);
        System.out.println("JUMLAH RES NEIGHBOUR "+ resUSerNeigh.length);
        int[] resPred = getAllResPred(trad, sim, res, semua, resUSerAktif, resUSerNeigh, indeksPenggunaAktif);
        double[] scoreAll = getAllScorePrediction(trad, sim, res, semua, resPred, indeksPenggunaAktif);
        double[] scoreBerurut = Arrays.copyOf(scoreAll,scoreAll.length);
        Arrays.sort(scoreBerurut);
        int[] urut = indeksTerurut(scoreBerurut, scoreAll);
        System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA = "+Arrays.toString(urut));
        texts.append("************************************************************************\n");
        texts.append("Matriks Score Prediction "+Rekomendasi.pengguna.getNama()+"\n");
        printIndeks(resPred,mapImage,texts);  
      
          for(int i = 0; i< scoreAll.length;i++)
          {
              texts.append(formatter.format(scoreAll[i])+"\t\t");
          }
          resPred = resourceBerurut(resPred, urut);
          System.out.println("----------------------------------------------------------------------------------------"+Arrays.toString(resPred));
           resourcePrediction = new int[resPred.length];
          for(int i = 0; i< resPred.length;i++)
          {
              resourcePrediction[i] = ketNamaRes(mapImage, resPred[i]);
          }
        }
          return resourcePrediction;
    }
    public static int[] resourceBerurut(int[] resource, int[] urut)
    {
        int[] res = new int[resource.length];
        int co = urut.length-1;
      if(resource.length>0&&urut.length>0)
        for(int i = 0; i< res.length;i++)
        {
            res[i] = resource[urut[co--]-1];
        }
        return res;
    }
    public static int[] indeksTerurut(double[] urut,double[] acak )
    {
        int[] urutan = new int[urut.length];
        for(int i = 0;i< urutan.length;i++)
        {
            for(int j = 0; j< urutan.length;j++)
            {
                if(acak[j] == urut[i] && !checkUrutan(urutan, j+1))
                {
                    urutan[i] = j+1;
                }
            }
        }
        return urutan;
    }
    public static boolean checkUrutan(int[] urut, int data)
    {
        for(int i : urut)
        {
            if(i==data)
            {
               return true;
            }
        }
        return false;
    }
    public static void printIndeks(int[] indeks,HashMap<String,Integer> maps,JTextArea text)
    {
        for(int data : indeks)
        {
            text.append(ketNama(maps, data)+"\t\t");
        }
        text.append("\n");
    }
    public static String ketNama(HashMap<String,Integer>maps,int ind)
    {
        String nama = "";
        if(maps.containsValue(ind))
        {
            for(String nam : maps.keySet())
            {
                if(maps.get(nam).equals(ind))
                {
                    nama = nam;
                }
            }
        }
        return nama;
    }
    
    public static int ketNamaRes(HashMap<String,Integer>maps,int ind)
    {
        int nama = -1;
        if(maps.containsValue(ind))
        {
            for(String nam : maps.keySet())
            {
                if(maps.get(nam).equals(ind))
                {
                    nama = Integer.parseInt(nam);
                }
            }
        }
        return nama;
    }
//    public static void main(String[] args) 
//    {
//        double[][] trad = {
//                            {1.0,0.0,0.0,0.0,0.0,0.0,0.0},
//                            {1.0,1.0,1.0,0.0,0.0,0.0,0.0},
//                            {0.0,0.0,1.0,0.0,0.0,0.0,0.0},
//                            {0.0,0.0,1.0,1.0,0.0,0.0,0.0},
//                            {0.0,0.0,0.0,0.0,1.0,1.0,0.0},
//                            {0.0,0.0,0.0,0.0,0.0,1.0,0.0},
//                            {0.0,0.0,0.0,0.0,0.0,0.0,1.0},
//                            
//                          };
//        double[][] sim = getSemuaUserSimilarity(trad);
//     
//        int[] indeksNEig = getIndeksNeigbourUSerAktif(sim,2);
//        Arrays.sort(indeksNEig);
//        int[] semua = new int[indeksNEig.length+1];
//        for(int i = 1; i< semua.length;i++)
//        {
//            semua[i] = indeksNEig[i-1];
//        }
//        semua[0] = 2;
//        
//        int[] res = getIndeksResourceAktifUserDanNeighbour(trad, semua);
//        Arrays.sort(semua);
//        Arrays.sort(res);
//        System.out.println("jumla = "+Arrays.toString(semua));
//        System.out.println("Res :"+ Arrays.toString(res));
//        double[][] matrikTradAktifUSer = getTradMAtriksAktifUSer(trad,semua, res);
//        
//        for(int i = 0; i< matrikTradAktifUSer.length;i++)
//        {
//            for(int j = 0; j< matrikTradAktifUSer[i].length;j++)
//            {
//                System.out.print(matrikTradAktifUSer[i][j]+" \t\t\t");
//            }
//            System.out.println("");
//        }
//        double[][] simAktifUSer  = getSemuaUserSimilarity(matrikTradAktifUSer);
//        System.out.println("\n");
//        for(int i = 0; i< simAktifUSer.length;i++)
//        {
//            for(int j = 0; j< simAktifUSer[i].length;j++)
//            {
//                System.out.print(simAktifUSer[i][j]+" \t\t\t");
//            }
//            System.out.println("");
//        }
//         int[] resUSerAktif = getIndeksResourceAktifUserDanNeighbour(trad,new int[]{2});
//         System.out.println("JUM RES USER AKTIF = "+ resUSerAktif.length);
//          int[] resUSerNeigh = getIndeksResourceAktifUserDanNeighbour(trad,indeksNEig);
//          System.out.println("JUMLAH RES NEIGHBOUR "+ resUSerNeigh.length);
//          double[] scoreAll = getAllScorePrediction(trad, sim, res, semua, resUSerAktif, resUSerNeigh, 2);
//          System.out.println("Hasilnya :");
//          for(int i = 0; i< scoreAll.length;i++)
//          {
//              System.out.print(scoreAll[i]+"\t");
//          }
//    }
}
