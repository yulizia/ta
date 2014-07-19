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
import static org.rekomendasi.algoritma.AlgoritamaColFiltering.getAllResPred;
import static org.rekomendasi.algoritma.AlgoritamaColFiltering.ketNamaRes;
import org.rekomendasi.database.dao.KontrollerDatabase;
import org.rekomendasi.gui.Rekomendasi;

/**
 *
 * @author YuliArnezia
 */
public class AlgoritmaTagBased 
{
    public static int[] eksekusiTradisionalFiltering(double[][] trad,int indeksPenggunaAktif,HashMap<String,Integer> mapUser,HashMap<String,Integer> mapImage, JTextArea texts) 
    {
        int resourcePrediction[] = null;
        NumberFormat formatter = new DecimalFormat("0.000");
        double[][] sim = getSemuaUserSimilarity(trad);
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
        
            int[] res = getIndeksResourceAktifUserDanNeighbour(trad, semua);
            Arrays.sort(semua);
            Arrays.sort(res);
            double[][] matrikTradAktifUSer = getTradMAtriksAktifUSer(trad,semua, res);
            printPembatas(texts);;
            texts.append("Matriks Tradisional User Aktif "+Rekomendasi.pengguna.getNama()+"\n\t");
            printIndeks(res,mapImage,texts);
            print(matrikTradAktifUSer, texts, mapUser, semua);
            printPembatas(texts);
            texts.append("Matriks User Similarity User Aktif "+Rekomendasi.pengguna.getNama()+"\n\t");
            System.out.println("dari sini");
            double[][] simAktifUSer  = getSemuaUserSimilarityBerTag(trad,semua,res,mapUser,mapImage);
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
            int neighTerdekat =  getIndeksSimTerbesarNeighbourAktifUser(simAktifUSer,semua, indeksPenggunaAktif,indeksNEig);
            if(neighTerdekat!=-1)
            {
            
                System.out.println("siapakah neigbour terdekat = "+neighTerdekat);
                int[] resPred = getAllResPred(trad, sim, res, semua, resUSerAktif, resUSerNeigh, indeksPenggunaAktif);
                double[] scoreAll = getAllScorePrediction(trad, simAktifUSer, res, semua, resPred, indeksPenggunaAktif,neighTerdekat,mapUser,mapImage);
                double[] scoreBerurut = Arrays.copyOf(scoreAll,scoreAll.length);
                Arrays.sort(scoreBerurut);
                int[] urut = indeksTerurut(scoreBerurut, scoreAll);
                texts.append("************************************************************************\n");
                texts.append("Matriks Score Prediction "+Rekomendasi.pengguna.getNama()+"\n");
                printIndeks(resPred,mapImage,texts);  
      
                for(int i = 0; i< scoreAll.length;i++)
                {
                    texts.append(formatter.format(scoreAll[i])+"\t\t");
                }
                resPred = resourceBerurut(resPred, urut);
                resourcePrediction = new int[resPred.length];
                for(int i = 0; i< resPred.length;i++)
                {
                    resourcePrediction[i] = ketNamaRes(mapImage, resPred[i]);
                }
            }
        }
          return resourcePrediction;
    }
    
    public static int[] resourceBerurut(int[] resource, int[] urut)
    {
        int[] res = new int[resource.length];
        int co = urut.length-1;
        if(resource.length>0 && urut.length>0)
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
    public static double[] getAllScorePrediction(double[][] trad,double[][] sim, int[] res,int[] users, int[] resPred, int indAktifUser, int userTerdekat,HashMap<String,Integer> mapUser,HashMap<String,Integer> mapImage)
   {
       double[] score = new double[resPred.length];
       int[] resourceTagBersama = getResourceDitagBersama(trad, indAktifUser, userTerdekat,users);
       int counter = 0;
       for(int  i = 0; i<resPred.length;i++)
       {
          // score[counter] = getScoreTiapResource(sim, trad, users, indAktifUser,resPred[i]);
           System.out.println("Indeks sim = "+sim[getIndeks(users,indAktifUser)][getIndeks(users,userTerdekat)]);
           System.out.println("SIm Res = "+getSimilarityResOlehNeighTerdekat(resourceTagBersama, resPred[i], userTerdekat, mapUser, mapImage));
           System.out.println("kumlatif = "+kumSimalirityAktifUSer(sim, indAktifUser,users,userTerdekat));
           score[counter] =(0.5* sim[getIndeks(users,indAktifUser)][getIndeks(users,userTerdekat)]*(getSimilarityResOlehNeighTerdekat(resourceTagBersama, resPred[i], userTerdekat, mapUser, mapImage)+1))/kumSimalirityAktifUSer(sim, indAktifUser,users,userTerdekat);
           counter++;
        }
        return score;
    }
    
    public static double kumSimalirityAktifUSer(double[][] sim , int aktifUser, int[] users,int neighTerdekat)
    {
        double kum  = 0;
        System.out.println("qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq+ user = "+ users.length);
        for(int i = 0; i< sim.length; i++)
        {
            if(  i == getIndeks(users,neighTerdekat))
            {
             //   System.out.println("mau cek disni apaemang ? = "+sim[aktifUser][i]);
                kum += sim[getIndeks(users,aktifUser)][i];
            }
        }
        
        return kum;
    }
    
    public static boolean isFriendUser(int[] users, int in)
    {
        boolean benar = false;
        for(int i : users)
        {
            if(i == in)
            {
                return true;
            }
        }
        return false;
    }
    public static double getSimilarityResOlehNeighTerdekat(int[] resourceTagBersama,int indekResourcePred, int neigh,HashMap<String,Integer> mapUser,HashMap<String,Integer> mapImage)
    {
        double pred = 0;
        try {
            List<String> tag1 = KontrollerDatabase.getTaggingPenggunaTerhadapImage(ketNama(mapUser,neigh),ketNamaRes(mapImage, indekResourcePred));
            for(String d : tag1)
            {
                System.out.println("print datanya adalah = "+d);
            }
            for(int in : resourceTagBersama)
            {
                List<String> tag2 = KontrollerDatabase.getTaggingPenggunaTerhadapImage(ketNama(mapUser,neigh),ketNamaRes(mapImage, in));
                List<String> tag3 = new ArrayList<>();
                if(tag1.size()>0)
                {
                    tag3.addAll(tag1);
                }
                if(tag2.size()>0)
                {
                    for(String t : tag2)
                    {
                        if(!isTagEksis(tag3, t))
                        {
                            tag3.add(t);
                        }
                    }
                }
                
                double[][] tags = new double[2][tag3.size()];
                for(int i  = 0; i< tags[0].length;i++)
                {
                    if(isTagEksis(tag1,tag3.get(i)))
                    {
                        tags[0][i] = 1;
                    }
                    if(isTagEksis(tag2,tag3.get(i)))
                    {
                        tags[1][i] = 1;
                    }
                
                }
                System.out.println("bingungggggggggggggggggggggggg");
                
                for(int k=0; k< tags.length;k++)
                {
                    for(int l = 0;l< tags[k].length;l++)
                    {
                        System.out.print(tags[k][l]+"\t");
                    }
                    System.out.println("");
                }
                
                double sims = getDotProduct(tags[0], tags[1])/(getMAgnitudeUser(tags[0])*getMAgnitudeUser(tags[1]));
                if(!Double.isNaN(sims) && !Double.isInfinite(sims) && pred<sims)
                {
                    pred = sims;
                }
                System.out.println("hasilnya akan seperti ini "+ sims);
            }
        } 
        catch (SQLException ex) 
        {
            Logger.getLogger(AlgoritmaTagBased.class.getName()).log(Level.SEVERE, null, ex);
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(AlgoritmaTagBased.class.getName()).log(Level.SEVERE, null, ex);
        } 
        catch (ClassNotFoundException ex) 
        {
            Logger.getLogger(AlgoritmaTagBased.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return pred;
    }
    
    
    
    public static int[] getResourceDitagBersama(double[][] trad, int aktifUser, int neighbour, int[] semua)
    {
        System.out.println("aku dapat dari sini = "+aktifUser +" dan neig = "+neighbour+" dab besar trad =" +trad[0].length);
        List<Integer> data = new ArrayList<>();
        for(int i = 0; i< trad[0].length;i++)
        {
            if(trad[aktifUser][i] == 1 && trad[neighbour][i]==1)
            {
                data.add(i);
            }
        }
        int[] dataInt = new int[data.size()];
        int c = 0;
        if(data.size()>0)
        for(Integer d : data)
        {
            dataInt[c++] = d;
          //  System.out.println("isinya = "+d);
        }
        return dataInt;
    }
    public static int getIndeksSimTerbesarNeighbourAktifUser(double[][] simUser,int[] semua,int aktifUser,int[] indeksNeigh)
    {
        System.out.println("emanya = "+aktifUser);
        System.out.println("kalau kita lihat indeksnya = "+ getIndeks(semua, aktifUser));
        int ind = -1;
        double sim = -1;
        for(int i = 0; i< simUser[getIndeks(semua, aktifUser)].length;i++)
        {
            System.out.println("sim = "+sim + " dan simuseri = "+simUser[getIndeks(semua, aktifUser)][i]);
            if(simUser[getIndeks(semua, aktifUser)][i]>sim && isNeigh(indeksNeigh,semua[i]))
            {
                System.out.println("kacauuuuuuuuuuuuuuuuuuuuuuuuuu--------------------------------------------------------------------------");
                sim = simUser[getIndeks(semua,aktifUser)][i];
                ind = semua[i];
            }
            
        }
        return ind;
    }
    public static int getIndeks(int[] semua, int a)
    {
        int in = -1;
        int co = 0;
        for(int i : semua)
        {
            if(i==a)
            {
                return co;
            }
            co++;
        }
        
        return in;
    
    }
    public static boolean isNeigh(int neigh[] , int indek)
    {
        boolean ada = false;
        for(int i = 0; i< neigh.length;i++)
        {
            if(neigh[i] == indek)
            {
                return true;
            }
        }
        return false;
    }

    public static void print(double[][] matrikTradAktifUSer, JTextArea texts, HashMap<String, Integer> mapUser, int[] semua) {
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
     public static double[][] getSemuaUserSimilarityBerTag(double[][] matriksTradisional,int[] users,int[] res, HashMap<String,Integer> mapUser, HashMap<String,Integer> mapImage )
    {
        System.out.println("testing");
        System.out.println("---------------888888---------------------------------");
        for(int i = 0; i< matriksTradisional.length;i++)
        {
            for(int j = 0; j< matriksTradisional[i].length;j++)
            {
                System.out.print(matriksTradisional[i][j]+"\t");
            }
            System.out.println("");
        }
        System.out.println("---------------888888---------------------------------");
        double[][] sim = new double[matriksTradisional.length][matriksTradisional.length];
        HashMap<String,List<Integer>> resTegBersama = new HashMap<>();
        for(int i = 0; i< users.length;i++)
        {
            for(int k = 0; k< users.length;k++)
            {
                if(i!=k)
                {
                   resTegBersama.put(ketNama(mapUser, users[i])+"-"+ketNama(mapUser, users[k]),getIndeksTagBersama(matriksTradisional, users[i], users[k]));
                }
                
            }
        }
        System.out.println("xxxxxxxxxxxxxxxxxgfhfhfhfhhfghghf");
        for(String d : resTegBersama.keySet())
        {
            List<Integer> f = resTegBersama.get(d);
            System.out.println(d+"\t");
            for(Integer s : f)
            {
                System.out.print(s+" \t");
            }
            System.out.println("");
        }
        sim = getSimilarityTag(resTegBersama, users, mapUser, mapImage);
       
        
//        for(int i = 0; i< sim.length;i++)
//        {
//            for(int j = 0; j<sim[i].length;j++)
//            {
//                 if(i!=j)
//                 {
//                     sim[i][j] = getDotProduct(matriksTradisional[i],matriksTradisional[j])/(getMAgnitudeUser(matriksTradisional[i])*getMAgnitudeUser(matriksTradisional[j]));
//                 }
//            }
//        }
        return sim;
    }
     
    public static double[][] getSimilarityTag( HashMap<String,List<Integer>> resTegBersama,int[] users,HashMap<String,Integer> mapUser, HashMap<String,Integer> mapImage)
    {
        double[][] sim = new double[users.length][users.length];
        for(String  a: resTegBersama.keySet())
        {
            String[] userBer= a.split("-");
            List<Integer> c = resTegBersama.get(a);
            System.out.print(a+"\t");
            int jumlah = 0;
            double data = 0;
            for(Integer d : c)
            {
               // System.out.println("a = "+a+ "dan "+a.substring(0,1)+" - "+ a.substring(1,2));
                data = getCosineSimilarity(userBer[0], userBer[1], Integer.parseInt(ketNama(mapImage,d)));
                
                jumlah++;
            }
            if(jumlah>0)
            data = (1d/(2*jumlah))*(data+1);
            sim[getIndeksUser(mapUser,userBer[0], users)][getIndeksUser(mapUser,userBer[1], users)] = data;
            System.out.println("user1 = "+userBer[0]+"\t user 2 ="+userBer[0]+ "\t data = "+data);
            System.out.println("");
        }
        for(int i = 0; i< users.length;i++)
        {
            for(int k = 0; k< users.length;k++)
            {
                if(i!=k)
                {
                 //  resTegBersama.put(ketNama(mapUser, users[i])+ketNama(mapUser, users[k]),getIndeksTagBersama(matriksTradisional, i, k));
                }                
            }
        }
        return sim;
    }
    
    public static int getIndeksUser(HashMap<String,Integer> mapUser,String user,int[] userArray)
    {
        int ind = -1;
        int u = -1;
        for(String map : mapUser.keySet())
        {
            if(map.equalsIgnoreCase(user))
            {
                u = mapUser.get(map);
            }
        }
        for(int i = 0; i< userArray.length;i++)
        {
            if(userArray[i]==u)
            {
                ind = i;
            }
        }
        return ind;
    }
    
    public static double getCosineSimilarity(String a, String b, int c)
    {
        System.out.println("sampai ke sini yaiut user 1= "+a + " dan user 2= "+ b+ "  dan image = "+c);
        double hasil = 0;
        try 
        {
           
            List<String>  tag1= KontrollerDatabase.getTaggingPenggunaTerhadapImage(a, c);
           
            List<String>  tag2= KontrollerDatabase.getTaggingPenggunaTerhadapImage(b, c);
            
            String[] tag = new String[tag1.size()+tag2.size()-getKesamaanTag(tag1, tag2)];
            if(tag1.size()>0)
            {
                for(int j = 0;j< tag1.size();j++)
                {
                    tag[j] = tag1.get(j);
                }
            }
            int counter = tag1.size();
            if(tag2.size()>0)
            {
                 for(int j = 0;j< tag2.size();j++)
                {
                    if(!sudahAdaPadaArray(tag, tag2.get(j)))
                    {
                        tag[counter++] = tag2.get(j);
                    }
                    
                }
            }
            System.out.println("besarnya tahhhhhhhhhhhhhhhhhhhhhhhh = "+ tag.length);
            double[][] matriksTag = new double[2][tag.length];
            for(int i = 0; i< matriksTag.length;i++)
            {
                for(int j=0; j< matriksTag[i].length;j++)
                {
                    if(i == 0)
                    {
                        if(isTagEksis(tag1,tag[j]))
                        {
                            matriksTag[i][j] = 1;
                        }
                        else
                        {
                            matriksTag[i][j] = 0;
                        }
                                
                    }
                    else
                    {
                        if(isTagEksis(tag2,tag[j]))
                        {
                            matriksTag[i][j] = 1;
                        }
                        else
                        {
                            matriksTag[i][j] = 0;
                        }
                    }
                }
            }
            hasil = getDotProduct(matriksTag[0],matriksTag[1])/(getMAgnitudeUser(matriksTag[0])*getMAgnitudeUser(matriksTag[1]));
                
            
                    
            for(int i = 0; i< tag.length;i++)
            {
                System.out.print(tag[i]);
            }
            System.out.println("");
            for(int i = 0; i< matriksTag.length;i++)
            {
                for(int j=0; j< matriksTag[i].length;j++)
                {
                    System.out.print(matriksTag[i][j]+"\t");
                }
                System.out.println("");
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(AlgoritmaTagBased.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AlgoritmaTagBased.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(AlgoritmaTagBased.class.getName()).log(Level.SEVERE, null, ex);
        }
        return hasil;
    }
    public static boolean sudahAdaPadaArray(String[] tags, String tag)
    {
       if(tags.length>0)
       {
            for(int i = 0; i< tags.length;i++)
            {
                
                if(tags[i]!=null && tags[i].equalsIgnoreCase(tag))
                {
                    return true;
                }
            }
       }
      
       return false;
    }
    public static int getKesamaanTag(List<String> tag1, List<String> tag2)
    {
        int jumlah = 0;
        for(String tag : tag1)
        {
            for(String t : tag2)
            {
                if(tag.equalsIgnoreCase(t))
                {
                    jumlah++;
                }
            }
        }
        return jumlah;
    }
    public static boolean isTagEksis(List<String> tags, String tag)
    {
        for(String t : tags)
        {
            if(t.equalsIgnoreCase(tag))
            {
                return true;
            
            }
        }
        return false;
    }
    public static List<Integer> getIndeksTagBersama(double[][] mats,int user1, int user2)
    {
        List<Integer> lists = new ArrayList<>();
        for(int  k = 0; k< mats[0].length;k++)
        {
            if(mats[user1][k] == 1 && mats[user2][k]==1)
            {
                lists.add(k);
            }
        }
        return lists;
    }
    public static double[][] getSemuaUserSimilarity(double[][] matriksTradisional)
    {
        
        System.out.println("---------------888888---------------------------------");
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
    public static void printPembatas(JTextArea texts)
    {
        texts.append("************************************************************************\n");
    }
   
}
