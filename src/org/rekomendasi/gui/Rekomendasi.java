/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.rekomendasi.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import org.rekomendasi.algoritma.CollaborativeFiltering;
import org.rekomendasi.algoritma.TagBasedCollaborativeFiltering;
import org.rekomendasi.database.dao.KontrollerDatabase;
import org.rekomendasi.type.MyImage;
import org.rekomendasi.type.Pengguna;

/**
 *
 * @author YuliArnezia
 */
public class Rekomendasi extends javax.swing.JPanel 
{

    /**
     * Creates new form Rekomendasi
     */
    private PanelUtama panelUtama;
    public static Pengguna pengguna;
    public Rekomendasi(PanelUtama panelUtama, Pengguna pengguna) 
    {
        initComponents();
        this.panelUtama = panelUtama;
        this.pengguna = pengguna; 
    }
    
    public void inisialisasi()
    {
        tampilkanPengguna();
        refreshKondisiPanel();   
    }
   
    public void refreshKondisiPanel()
    {
//        new Thread()
//        {
//            public void run()
//            {
                tampilkanSemuaImagePengguna();
//            }
//        }.start();
//        new Thread()
//        {
//            public void run()
//            {
                tampilkanSemuaImageUserLain();
//            }
//        }.start();
//        new Thread()
//        {
//            public void run()
//            {
                updateInformasiCollaborativeFiltering();
//            }
//        }.start();
//        new Thread()
//        {
//            public void run()
//            {
                 updateInformasiTagBasedCollaborativeFiltering();
//            }
//        }.start();
//        
//         new Thread()
//        {
//            public void run()
//            {
                 tampilkanFriends();
//            }
//        }.start();
        
       
    }
    
    public void updateInformasiCollaborativeFiltering()
    {
        try
        {
            List<MyImage> myImagesPengguna = KontrollerDatabase.getSemuaImagePengguna(pengguna.getUsername());
            List<MyImage> myImagesUserLain = KontrollerDatabase.getSemuaImageUserLain(pengguna.getUsername());
            
            CollaborativeFiltering colFIl = new CollaborativeFiltering(this,collaborativeFilteringTA,myImagesPengguna,myImagesUserLain);
            colFIl.updateInfoAwal();
        } 
        catch (SQLException ex) 
        {
            Logger.getLogger(Rekomendasi.class.getName()).log(Level.SEVERE, null, ex);
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(Rekomendasi.class.getName()).log(Level.SEVERE, null, ex);
        } 
        catch (ClassNotFoundException ex) 
        {
            Logger.getLogger(Rekomendasi.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
     public void updateInformasiTagBasedCollaborativeFiltering()
    {
        try
        {
            List<MyImage> myImagesPengguna = KontrollerDatabase.getSemuaImagePengguna(pengguna.getUsername());
            List<MyImage> myImagesUserLain = KontrollerDatabase.getSemuaImageUserLain(pengguna.getUsername());
            
            TagBasedCollaborativeFiltering colFIl = new TagBasedCollaborativeFiltering(this,tagBasedCollaborativeFilteringTA,myImagesPengguna,myImagesUserLain);
            colFIl.updateInfoAwal();
        } 
        catch (SQLException ex) 
        {
            Logger.getLogger(Rekomendasi.class.getName()).log(Level.SEVERE, null, ex);
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(Rekomendasi.class.getName()).log(Level.SEVERE, null, ex);
        } 
        catch (ClassNotFoundException ex) 
        {
            Logger.getLogger(Rekomendasi.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
     public void tampilkanFriends()
     {
        try {
            List<Pengguna> pengs = KontrollerDatabase.getSemuaUSer();
            System.out.println("Peng nya---------------------------------------------------- = ="+pengs.size());
            for(Pengguna peng : pengs)
            {
                if(!peng.getUsername().equalsIgnoreCase(pengguna.getUsername()))
                {
                    PanelProfilFriend pan = new PanelProfilFriend(peng.getImagePengguna(), peng.getUsername());
                    pan.setPreferredSize(new Dimension(100,100));
                    jPanel7.add(pan);
                }
                
                jPanel7.repaint();
                jPanel7.revalidate();
            }
        } catch (SQLException ex) {
            Logger.getLogger(Rekomendasi.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Rekomendasi.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Rekomendasi.class.getName()).log(Level.SEVERE, null, ex);
        }
         
     }
    
    public Pengguna getPengguna() 
    {
        return pengguna;
    }

    public void setPengguna(Pengguna pengguna) 
    {
        this.pengguna = pengguna;
    }
    
    public void tampilkanPengguna()
    {
        if(pengguna.getImagePengguna() !=null)
        {
            Image img = new ImageIcon(pengguna.getImagePengguna()).getImage();  
            Image newimg = img.getScaledInstance(205, 181,  java.awt.Image.SCALE_SMOOTH);  
            ImageIcon newIcon = new ImageIcon(newimg);  
            jLabel1.setIcon(newIcon);
        }
        
        namaPenggunaTF.setText(pengguna.getNama());
    }
    
     public void tampilkanSemuaImageREkomendasiTraditionalCollaborative(int[] idRes)
    {
        if(idRes !=null && idRes.length>0)
        {
           // System.out.println("kalau disini memangnya =============="+Arrays.toString(idRes));
            jPanel4.removeAll();
            try 
            {
                List<MyImage> myImages = KontrollerDatabase.getSemuaImageBerdasarkanId(idRes);
               // System.out.println("besarnya imaaaaaaaaaaaaaaaaaaaaaaa = "+ myImages.size());
                for (int i = 0; i < myImages.size(); i++) 
                {
                    System.out.println("besarnya = "+ myImages.size());
                    PanelThumbNail bp = new PanelThumbNail(myImages.get(i),this,PanelImages.REKOMENDASI);
           
           
                    //   bp.setSize(300,jScrollPane3.getHeight());
                    bp.setPreferredSize(new Dimension(300,150));
                    bp.repaint();
                    // panels.add(bp);
                    jPanel4.add(bp);
                    jPanel4.repaint();
                }
            
            } 
            catch (SQLException ex) 
            {
                System.out.println("error 1");
                ex.printStackTrace();
            } 
            catch (IOException ex) 
            {
                System.out.println("error 2");
            } 
            catch (ClassNotFoundException ex) 
            {
                Logger.getLogger(Rekomendasi.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
     
    public void tampilkanSemuaImageREkomendasiTagCollaborative(int[] idRes)
    {
        if(idRes !=null && idRes.length>0)
        {
            System.out.println("berapa disini kalo  = "+ idRes.length);
            jPanel5.removeAll();
            try 
            {
                List<MyImage> myImages = KontrollerDatabase.getSemuaImageBerdasarkanId(idRes);
              //  System.out.println("besarnya imaaaaaaaaaaaaaaaaaaaaaaa = "+ myImages.size());
                for (int i = 0; i < myImages.size(); i++) 
                {
                    System.out.println("besarnya = "+ myImages.size());
                    PanelThumbNail bp = new PanelThumbNail(myImages.get(i),this,PanelImages.REKOMENDASI);
           
           
                    //   bp.setSize(300,jScrollPane3.getHeight());
                    bp.setPreferredSize(new Dimension(300,150));
                    bp.repaint();
                    // panels.add(bp);
                    jPanel5.add(bp);
                    jPanel5.repaint();
                }
            } 
            catch (SQLException ex) 
            {
                System.out.println("error 1");
                ex.printStackTrace();
            } 
            catch (IOException ex) 
            {
                System.out.println("error 2");
            } 
            catch (ClassNotFoundException ex) 
            {
                Logger.getLogger(Rekomendasi.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void tampilkanSemuaImagePengguna()
    {
        jPanel8.removeAll();
        try 
        {
            List<MyImage> myImages = KontrollerDatabase.getSemuaImagePengguna(pengguna.getUsername());
            for (int i = 0; i < myImages.size(); i++) 
            {
                System.out.println("besarnya = "+ myImages.size());
                PanelThumbNail bp = new PanelThumbNail(myImages.get(i),this,PanelImages.IMAGE_PENGGUNA);
                bp.setPreferredSize(new Dimension(300,150));
                bp.repaint();
                // panels.add(bp);
                jPanel8.add(bp);
                jPanel8.repaint();
            }
            
        } 
        catch (SQLException ex) 
        {
            System.out.println("error 1");
        } 
        catch (IOException ex) 
        {
            System.out.println("error 2");
        } 
        catch (ClassNotFoundException ex) 
        {
            Logger.getLogger(Rekomendasi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void tampilkanSemuaImageUserLain()
    {
        jPanel9.removeAll();
        try 
        {
            List<MyImage> myImages = KontrollerDatabase.getSemuaImageUserLain(pengguna.getUsername());
            for (int i = 0; i < myImages.size(); i++) 
            {
                System.out.println("besarnya = "+ myImages.size());
                PanelThumbNail bp = new PanelThumbNail(myImages.get(i),this,10);
           
           
                //   bp.setSize(300,jScrollPane3.getHeight());
                bp.setPreferredSize(new Dimension(300,150));
                bp.repaint();
                // panels.add(bp);
                jPanel9.add(bp);
                jPanel9.repaint();
            }
            
        } 
        catch (SQLException ex) 
        {
            ex.printStackTrace();
            System.out.println("error 1");
        } 
        catch (IOException ex) 
        {
            System.out.println("error 2");
        } 
        catch (ClassNotFoundException ex) 
        {
            Logger.getLogger(Rekomendasi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        myProfilJP = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        potoPenggunaJP = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        namaPenggunaTF = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jPanel5 = new javax.swing.JPanel();
        myAnalisaJP = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        collaborativeFilteringTA = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        tagBasedCollaborativeFilteringTA = new javax.swing.JTextArea();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jPanel9 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();

        setBackground(new java.awt.Color(255, 255, 255));
        setLayout(new java.awt.BorderLayout());

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.setForeground(new java.awt.Color(26, 135, 215));
        jTabbedPane1.setToolTipText("");
        jTabbedPane1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        myProfilJP.setBackground(new java.awt.Color(255, 255, 255));
        myProfilJP.setForeground(new java.awt.Color(26, 135, 215));
        myProfilJP.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jButton1.setBackground(new java.awt.Color(255, 255, 255));
        jButton1.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/rekomendasi/images/exit_1.png"))); // NOI18N
        jButton1.setText("LOG OUT");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setBackground(new java.awt.Color(255, 255, 255));
        jButton2.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/rekomendasi/images/aptdaemon-update-cache.png"))); // NOI18N
        jButton2.setText("ADD IMAGE");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        potoPenggunaJP.setMaximumSize(new java.awt.Dimension(87, 185));
        potoPenggunaJP.setMinimumSize(new java.awt.Dimension(87, 185));
        potoPenggunaJP.setLayout(null);

        jLabel1.setMaximumSize(new java.awt.Dimension(87, 185));
        jLabel1.setMinimumSize(new java.awt.Dimension(87, 185));
        jLabel1.setPreferredSize(new java.awt.Dimension(87, 185));
        potoPenggunaJP.add(jLabel1);
        jLabel1.setBounds(0, 0, 205, 181);

        namaPenggunaTF.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        namaPenggunaTF.setForeground(new java.awt.Color(255, 255, 255));
        namaPenggunaTF.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(potoPenggunaJP, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(namaPenggunaTF, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(potoPenggunaJP, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(namaPenggunaTF, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 234, Short.MAX_VALUE)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.Y_AXIS));

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Collaborative Filtering", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Agency FB", 1, 24))); // NOI18N
        jPanel4.setLayout(new java.awt.GridLayout(1, 0));
        jScrollPane3.setViewportView(jPanel4);

        jPanel3.add(jScrollPane3);

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Tag-Based Contextual Collaborative Filtering", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Agency FB", 1, 24))); // NOI18N
        jPanel5.setLayout(new java.awt.GridLayout(1, 0));
        jScrollPane4.setViewportView(jPanel5);

        jPanel3.add(jScrollPane4);

        javax.swing.GroupLayout myProfilJPLayout = new javax.swing.GroupLayout(myProfilJP);
        myProfilJP.setLayout(myProfilJPLayout);
        myProfilJPLayout.setHorizontalGroup(
            myProfilJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(myProfilJPLayout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 573, Short.MAX_VALUE))
        );
        myProfilJPLayout.setVerticalGroup(
            myProfilJPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab(" My Profil ", new javax.swing.ImageIcon(getClass().getResource("/org/rekomendasi/images/web-launchpad_1.png")), myProfilJP); // NOI18N

        myAnalisaJP.setBackground(new java.awt.Color(255, 255, 255));
        myAnalisaJP.setLayout(new javax.swing.BoxLayout(myAnalisaJP, javax.swing.BoxLayout.LINE_AXIS));

        jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Collaborative Filteriing", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Agency FB", 1, 24))); // NOI18N

        collaborativeFilteringTA.setEditable(false);
        collaborativeFilteringTA.setColumns(20);
        collaborativeFilteringTA.setRows(5);
        jScrollPane1.setViewportView(collaborativeFilteringTA);

        myAnalisaJP.add(jScrollPane1);

        jScrollPane2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Tag-Based Contextual Collaborative Filtering", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Agency FB", 1, 24))); // NOI18N

        tagBasedCollaborativeFilteringTA.setEditable(false);
        tagBasedCollaborativeFilteringTA.setColumns(20);
        tagBasedCollaborativeFilteringTA.setRows(5);
        jScrollPane2.setViewportView(tagBasedCollaborativeFilteringTA);

        myAnalisaJP.add(jScrollPane2);

        jTabbedPane1.addTab("Analisa Rekomendasi", new javax.swing.ImageIcon(getClass().getResource("/org/rekomendasi/images/libreoffice3-calc_1.png")), myAnalisaJP); // NOI18N

        jPanel2.setBackground(new java.awt.Color(26, 135, 215));

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));
        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "My Images", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Agency FB", 1, 24))); // NOI18N
        jPanel8.setLayout(new javax.swing.BoxLayout(jPanel8, javax.swing.BoxLayout.LINE_AXIS));
        jScrollPane6.setViewportView(jPanel8);

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));
        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Image User Lain", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Agency FB", 1, 24))); // NOI18N
        jPanel9.setLayout(new javax.swing.BoxLayout(jPanel9, javax.swing.BoxLayout.LINE_AXIS));
        jScrollPane5.setViewportView(jPanel9);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 804, Short.MAX_VALUE)
            .addComponent(jScrollPane5)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("View images", new javax.swing.ImageIcon(getClass().getResource("/org/rekomendasi/images/showfoto_1.png")), jPanel2); // NOI18N

        jPanel6.setLayout(new java.awt.BorderLayout());

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));
        jPanel7.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        jPanel6.add(jPanel7, java.awt.BorderLayout.CENTER);

        jTabbedPane1.addTab("View Friends", new javax.swing.ImageIcon(getClass().getResource("/org/rekomendasi/images/start-here-fedora.png")), jPanel6); // NOI18N

        add(jTabbedPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        panelUtama.kembaliKeLoginAwal();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        InsertImagesJW insertImages = new InsertImagesJW(panelUtama, true,this);
        insertImages.setVisible(true);
    }//GEN-LAST:event_jButton2ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea collaborativeFilteringTA;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JPanel myAnalisaJP;
    private javax.swing.JPanel myProfilJP;
    private javax.swing.JLabel namaPenggunaTF;
    private javax.swing.JPanel potoPenggunaJP;
    private javax.swing.JTextArea tagBasedCollaborativeFilteringTA;
    // End of variables declaration//GEN-END:variables
}
class ButtonPanel extends JPanel 
{

        public ButtonPanel(int i) {
            Random rnd = new Random();
            this.setBackground(new Color(rnd.nextInt()));
           // this.add(new JButton("Button " + String.valueOf(i)));
        }
        protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        try {
            // paint the background image and scale it to fill the entire space
            g.drawImage(ImageIO.read(getClass().getResource("/org/rekomendasi/images/kosong.png")), 0, 0, getWidth(), getHeight(), this);
        } catch (IOException ex) {
            Logger.getLogger(LoginForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
 }
