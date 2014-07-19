/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.rekomendasi.database.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.rekomendasi.database.koneksi.KoneksiManager;
import org.rekomendasi.gui.Rekomendasi;
import org.rekomendasi.type.MyImage;
import org.rekomendasi.type.Pengguna;

/**
 *
 * @author YuliArnezia
 */
public class KontrollerDatabase 
{
   static String url = "D:\\images\\";
  //  static String url = "/Users/arnezia/Pictures/images/";
    public static Pengguna getDetailPengguna(String username, String password) throws SQLException, ClassNotFoundException 
    {
        Pengguna pengguna = null;
        Connection conn = KoneksiManager.getKoneksi();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("select * from pengguna where username = " + "'" + username + "' and password = '"+password+"'");
       
        if (rs.next()) 
        {
            pengguna = new Pengguna();
            pengguna.setUsername(rs.getString(1));
            pengguna.setNama(rs.getString(2));
            pengguna.setPassword(rs.getString(3));
            try {
                File file = null;
                file = new File(url+rs.getString(4));
                pengguna.setImagePengguna(ImageIO.read(new File(url+rs.getString(4))));
            } catch (IOException ex)
            {
                ex.printStackTrace();
            }
            
        }

        if (conn != null) 
        {
            conn.close();
        }

        return pengguna;
    }
    
    public static List<MyImage> getSemuaImagePengguna(String username) throws SQLException, IOException, ClassNotFoundException 
    {
        Connection conn = KoneksiManager.getKoneksi();
        List<MyImage> myImages = new ArrayList<>();
        String sql = "SELECT * FROM  IMAGES WHERE PEMILIK = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1,username);
       
        ResultSet rs = pstmt.executeQuery();
        while(rs.next())
        {
            System.out.println("iiiii");
            MyImage ims = new  MyImage();
            ims.setIdImage(rs.getInt(1));
          
            ims.setBuffImage(ImageIO.read(new File(url+rs.getString(2))));
            ims.setPemilikImage(rs.getString(3));
            ims.setNamaImage(rs.getString(4));
            ims.setDeskripsiImage(rs.getString(5));
            myImages.add(ims);
        }
        
        if (conn != null) 
        {
            conn.close();
        }
        myImages = getSemuaTaggingImagePengguna(myImages);
        myImages = getSemuaTaggingImagePenggunaDariUserLain(myImages, username);
        myImages = getSemuaLike(myImages);
        return myImages;
    }
    
    public static List<MyImage> getSemuaImageBerdasarkanId(int[] id) throws SQLException, IOException, ClassNotFoundException 
    {
        String data = "";
        for(int in : id)
        {
            data += in+",";
        }
        System.out.println("dataaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa = "+data);
        if(data.length()>0) 
        {
            data = data.substring(0,data.length()-1);
        }
        else
        {
            data = "-1";
        }
        System.out.println("data========================"+ data);
        Connection conn = KoneksiManager.getKoneksi();
        List<MyImage> myImages = new ArrayList<>();
        String sql = "SELECT * FROM IMAGES WHERE IDIMAGE IN ("+data+") ORDER BY FIELD(IDIMAGE,"+data+") ";
        PreparedStatement pstmt = conn.prepareStatement(sql);
       // pstmt.setString(1,data);
       
        ResultSet rs = pstmt.executeQuery();
        while(rs.next())
        {
            System.out.println("anehhhhhhhhhhhhhhhhhhhhhhhhhhhh");
            MyImage ims = new  MyImage();
            ims.setIdImage(rs.getInt(1));
         //   ims.setBuffImage(ImageIO.read(rs.getBlob(2).getBinaryStream()));
            ims.setBuffImage(ImageIO.read(new File(url+rs.getString(2))));
            ims.setPemilikImage(rs.getString(3));
            ims.setNamaImage(rs.getString(4));
            ims.setDeskripsiImage(rs.getString(5));
            myImages.add(ims);
        }
        
        if (conn != null) 
        {
            conn.close();
        }
        System.out.println("dari sini berapa = "+myImages.size());
        myImages = getSemuaTaggingImagePengguna(myImages);
        myImages = getSemuaLike(myImages);
     
        //myImages = getSemuaTaggingImagePenggunaDariUserLain(myImages, username);
        return myImages;
    }
    public static List<MyImage> getSemuaTaggingImagePengguna(List<MyImage> images) throws SQLException, IOException, ClassNotFoundException 
    {
        Connection conn = KoneksiManager.getKoneksi();
       
        String sql = "SELECT * FROM  TAGGING WHERE USERNAME = ? AND IDIMAGE = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        for(MyImage ims : images)
        {
            pstmt.setString(1,ims.getPemilikImage());
            pstmt.setInt(2,ims.getIdImage());
       
            ResultSet rs = pstmt.executeQuery();
            while(rs.next())
            {
                int id = rs.getInt(2);
                if(id==ims.getIdImage())
                {
                    ims.getTagImagePemilik().add(rs.getString(4));
                }
            }
        }
        
        if (conn != null) 
        {
            conn.close();
        }

        return images;
    }
    
    public static List<MyImage> getSemuaLike(List<MyImage> images) throws SQLException, IOException, ClassNotFoundException 
    {
        Connection conn = KoneksiManager.getKoneksi();
       
        String sql = "SELECT * FROM  SUKA WHERE IDIMAGE = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        for(MyImage ims : images)
        {
            
            pstmt.setInt(1,ims.getIdImage());
       
            ResultSet rs = pstmt.executeQuery();
            while(rs.next())
            {
              ims.getLikeUsers().add(rs.getString(2));
                
            }
        }
        
        if (conn != null) 
        {
            conn.close();
        }

        return images;
    }
    
    public static List<MyImage> getSemuaTaggingImagePenggunaDariUserLain(List<MyImage> images,String username) throws SQLException, IOException, ClassNotFoundException 
    {
        Connection conn = KoneksiManager.getKoneksi();
       
        String sql = "SELECT * FROM  TAGGING WHERE USERNAME != ? AND IDIMAGE = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        for(MyImage ims : images)
        {
            pstmt.setString(1,username);
            pstmt.setInt(2,ims.getIdImage());
       
            ResultSet rs = pstmt.executeQuery();
            while(rs.next())
            {
                int id = rs.getInt(2);
                String user  = rs.getString(3);
                if(id==ims.getIdImage())
                {
                    masukkanTagDariUSerLain(ims,rs.getString(4) ,rs.getString(3));
                    
                }
            }
        }
        
        if (conn != null) 
        {
            conn.close();
        }

        return images;
    }
    public static MyImage getSemuaTaggingImageOrangLainPengguna(MyImage ims) throws SQLException, IOException, ClassNotFoundException 
    {
        Connection conn = KoneksiManager.getKoneksi();
       
        String sql = "SELECT * FROM  TAGGING WHERE USERNAME != ? AND IDIMAGE = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
       
            pstmt.setString(1,ims.getPemilikImage());
            pstmt.setInt(2,ims.getIdImage());
       
            ResultSet rs = pstmt.executeQuery();
            while(rs.next())
            {
                int id = rs.getInt(2);
                String user  = rs.getString(3);
                if(id==ims.getIdImage())
                {
                    masukkanTagDariUSerLain(ims,rs.getString(4) ,rs.getString(3));
                    
                }
            }
        
        
        if (conn != null) 
        {
            conn.close();
        }

        return ims;
    }
    public static void masukkanTagDariUSerLain(MyImage image,String tag,String username)
    {
        if(image.getTagOrangLain().size()==0)
        {
            List<String> tags = new ArrayList<>();
            tags.add(tag);
            image.getTagOrangLain().put(username, tags);
        }
        else
        {
            boolean sudahAda = false;
            for(String namaUser : image.getTagOrangLain().keySet())
            {
                if(namaUser.equalsIgnoreCase(username))
                {
                    image.getTagOrangLain().get(username).add(tag);
                    sudahAda = true;
                }
                
            }
            if(!sudahAda)
            {
                List<String> tags = new ArrayList<>();
                tags.add(tag);
                image.getTagOrangLain().put(username, tags);
            }
        }
    }
     public static List<MyImage> getSemuaImageUserLain(String username) throws SQLException, IOException, ClassNotFoundException 
    {
        Connection conn = KoneksiManager.getKoneksi();
        List<MyImage> myImages = new ArrayList<>();
        String sql = "SELECT * FROM  IMAGES WHERE PEMILIK != ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1,username);
       
        ResultSet rs = pstmt.executeQuery();
        while(rs.next())
        {
            System.out.println("iiiii");
            MyImage ims = new  MyImage();
            ims.setIdImage(rs.getInt(1));
        //    ims.setBuffImage(ImageIO.read(rs.getBlob(2).getBinaryStream()));
            ims.setBuffImage(ImageIO.read(new File(url+rs.getString(2))));
            ims.setPemilikImage(rs.getString(3));
            ims.setNamaImage(rs.getString(4));
            ims.setDeskripsiImage(rs.getString(5));
            myImages.add(ims);
        }
        if (conn != null) 
        {
            conn.close();
        }
        List<MyImage> imsBaru = new ArrayList<>();
        myImages = getSemuaTaggingImagePengguna(myImages);
         myImages = getSemuaLike(myImages);
        for(MyImage ims : myImages)
        
        {
            ims = getSemuaTaggingImageOrangLainPengguna(ims);
        }
        System.out.println("emang besarnya =============== "+imsBaru.size());
        return myImages;
    }
     
     public static boolean saveLike(MyImage myImage) throws SQLException, ClassNotFoundException 
     {
        Connection conn = KoneksiManager.getKoneksi();
        boolean status = false;
        String sql = "insert into SUKA values(?,?)";
         System.out.println(sql);
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1,myImage.getIdImage());
        pstmt.setString(2,Rekomendasi.pengguna.getUsername());
        int jumlah = pstmt.executeUpdate();
        if(jumlah>0) status = true;
        if (conn != null) {
            conn.close();
        }

        return status;
     }
     


     public static boolean saveTagging(Pengguna pengguna, MyImage myImage, String tagging) throws SQLException, ClassNotFoundException 
     {
        Connection conn = KoneksiManager.getKoneksi();
        boolean status = false;
        String sql = "insert into TAGGING values (?,?,?,?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1,0);
        pstmt.setInt(2, myImage.getIdImage());
        pstmt.setString(3, pengguna.getUsername());
        pstmt.setString(4, tagging);
        
        int jumlah = pstmt.executeUpdate();
        if(jumlah>0) status = true;
        if (conn != null) {
            conn.close();
        }

        return status;
     }
     
      public static boolean insertImage(MyImage myImage, String[] tagging, File fileImage,String path) throws SQLException, ClassNotFoundException 
     {
        Connection conn = KoneksiManager.getKoneksi();
        int keyImage = -1;
        boolean status = false;
        String sql = "insert into images values (null,?,?,?,?)";
         System.out.println("sql  =  "+sql);
        PreparedStatement pstmt = conn.prepareStatement(sql,PreparedStatement.RETURN_GENERATED_KEYS);
      //  pstmt.setInt(1,0);
        FileInputStream fis;
         FileOutputStream fos;
      
            System.out.println("file ini besarnya = "+(int)fileImage.length());
             copyfile(fileImage, path);
         
        pstmt.setString(1,path); 
        pstmt.setString(2, myImage.getPemilikImage());
        pstmt.setString(3, myImage.getNamaImage());
        pstmt.setString(4, myImage.getDeskripsiImage());
        int jumlah = pstmt.executeUpdate();
        ResultSet rs = pstmt.getGeneratedKeys();
            while (rs.next()) 
            {
                keyImage =  rs.getInt(1);
            } 
        
        if(jumlah>0) status = true;
        if (conn != null)
        {
            conn.close();
        }
        if(keyImage!=-1)
        {
            myImage.setIdImage(keyImage);
            for(String tag : tagging)
            saveTagging(Rekomendasi.pengguna, myImage, tag);
        }
        return status;
     }
      
       public static boolean insertPengguna(Pengguna peng,String path,File file) throws SQLException, ClassNotFoundException 
     {
        Connection conn = KoneksiManager.getKoneksi();
        int keyImage = -1;
        boolean status = false;
        String sql = "insert into pengguna values (?,?,?,?)";
        System.out.println("sql  =  "+sql);
        PreparedStatement pstmt = conn.prepareStatement(sql);
      //  pstmt.setInt(1,0);
        
        

         copyfile(file, path);

         
        pstmt.setString(1,peng.getUsername()); 
        pstmt.setString(2, peng.getNama());
        pstmt.setString(3, peng.getPassword());
        pstmt.setString(4, path);
        int jumlah = pstmt.executeUpdate();
        
        
        if (conn != null)
        {
            conn.close();
        }
        
        return status;
     }


    public static List<Pengguna> getSemuaUSer() throws SQLException, IOException, ClassNotFoundException 
    {
        Connection conn = KoneksiManager.getKoneksi();
        List<Pengguna> pengs = new ArrayList<>();
        String sql = "SELECT * FROM  PENGGUNA";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();
        while(rs.next())
        {
            Pengguna peng = new Pengguna();
            try {
                File file = null;
                String urls = rs.getString(4);
                if(urls !=null && !urls.equalsIgnoreCase(""))
                {
                    file = new File(url+rs.getString(4));
                
                    peng.setImagePengguna(ImageIO.read(new File(url+rs.getString(4))));
                }
                
            } catch (IOException ex) {
                //Logger.getLogger(KontrollerDatabase.class.getName()).log(Level.SEVERE, null, ex);
            }
            peng.setUsername(rs.getString(1));
            peng.setNama(rs.getString(2));
            pengs.add(peng);
        }
        
        
        if (conn != null) 
        {
            conn.close();
        }

        return pengs;
    }
    
     public static List<MyImage> getSemuaImages() throws SQLException, IOException, ClassNotFoundException 
    {
        Connection conn = KoneksiManager.getKoneksi();
        List<MyImage> ims = new ArrayList<>();
        String sql = "SELECT * FROM  IMAGES";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();
        while(rs.next())
        {
            MyImage im = new MyImage();
            im.setIdImage(rs.getInt(1));
            im.setNamaImage(rs.getString(4));
            ims.add(im);
        }
        
        
        if (conn != null) 
        {
            conn.close();
        }

        return ims;
    }
     
    public static List<Integer> getSemuaImageYAngDiSukaiPengguna(String username) throws SQLException, IOException, ClassNotFoundException 
    {
        Connection conn = KoneksiManager.getKoneksi();
        List<Integer> myImages = new ArrayList<>();
         String sql = "SELECT DISTINCT(IDIMAGE) AS ID FROM  IMAGES WHERE PEMILIK = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1,username);
       
        ResultSet rs = pstmt.executeQuery();
        while(rs.next())
        {
            myImages.add(rs.getInt(1));
        }
        
      sql = "SELECT DISTINCT(IDIMAGE) AS ID FROM  SUKA WHERE USERNAME = ?";
      pstmt = conn.prepareStatement(sql);
        pstmt.setString(1,username);
       
       rs = pstmt.executeQuery();
        while(rs.next())
        {
            myImages.add(rs.getInt(1));
        }
        
        if (conn != null) 
        {
            conn.close();
        }
        Collections.sort(myImages);
        return myImages;
    }
    
     public static List<Integer> getSemuaImageYAngDiTagPengguna(String username) throws SQLException, IOException, ClassNotFoundException 
    {
        Connection conn = KoneksiManager.getKoneksi();
        List<Integer> myImages = new ArrayList<>();
        String sql = "SELECT DISTINCT(IDIMAGE) AS ID FROM  TAGGING WHERE USERNAME = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1,username);
       
        ResultSet rs = pstmt.executeQuery();
        while(rs.next())
        {
            myImages.add(rs.getInt(1));
        }
        Collections.sort(myImages);
        return myImages;
    }
     
      public static List<String> getTaggingPenggunaTerhadapImage(String username,int idImage) throws SQLException, IOException, ClassNotFoundException 
    {
        System.out.println("memang disini username = "+username+ "dan idIage = "+ idImage);
        Connection conn = KoneksiManager.getKoneksi();
        List<String> tagging = new ArrayList<>();
        String sql = "SELECT DISTINCT(namaTagging) AS tag FROM  TAGGING WHERE USERNAME = ? AND IDIMAGE = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1,username);
        pstmt.setInt(2, idImage);
        ResultSet rs = pstmt.executeQuery();
        while(rs.next())
        {
            tagging.add(rs.getString("tag"));
            System.out.println("tagiigngggngnngg = "+rs.getString(1));
        }
      //  Collections.sort(myImages);
        
        return tagging;
    }
//     public static void main(String[] args) {
//        try {
//            List<Integer> is = getSemuaImageYAngDiTagPengguna("a");
//            for(Integer in : is)
//            {
//                System.out.println(in.intValue());
//            }
//        } catch (SQLException ex) {
//            Logger.getLogger(KontrollerDatabase.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException ex) {
//            Logger.getLogger(KontrollerDatabase.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        
//    }
      
 private static void copyfile(File f1, String dtFile)
 {
     try
     {
         File f2 = new File(url+dtFile);
         InputStream in = new FileInputStream(f1);
        
         OutputStream out = new FileOutputStream(f2);
         byte[] buf = new byte[1024];
  
        int len;
 
        while ((len = in.read(buf)) > 0)
        {
  
            out.write(buf, 0, len);
 
        }
 
        in.close();

        out.close();
 
    System.out.println("File copied.");
 
  }
     catch(FileNotFoundException ex)
     {
         System.out.println(ex.getMessage() + " in the specified directory.");
         
     }
     catch(IOException e)
     {
         System.out.println(e.getMessage());
     }  
  
  }
  
}
