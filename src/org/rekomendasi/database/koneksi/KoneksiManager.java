/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.rekomendasi.database.koneksi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 *
 * @author YuliArnezia
 */
public class KoneksiManager {
    
    public static Connection getKoneksi() throws ClassNotFoundException, SQLException
    {
        Connection koneksi = null;
        Class.forName("com.mysql.jdbc.Driver");
        koneksi = DriverManager.getConnection("jdbc:mysql://localhost/rekomendasisatu", "root", "");
        return koneksi;
    }
}

