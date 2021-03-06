/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dal;

import base.DbBaseFunctions;
import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 * @author u203011
 */
public class DBConnection {
    
    private static Connection con = null;
    private static final String ServerName = "vgdagi05";
    private static final String dbName = "gdwh";
    private static String schemaName = "\"raster\"";
    private static String username = "gdwh_open_user";
    private static String pw = /* pw */;
    private static String dbtype = "postgresql";
    
    private DBConnection() {
    }
    
    public static Connection getConnection() {
        if(con == null) {
            con = DbBaseFunctions.connectDatabase(ServerName, dbName, username, pw, dbtype, null);
        }
        
        return con;
    }
    
    public static void closeConnection(){
        try {
            if(con != null) {
                con.close();

                con = null;
            }
        } catch (SQLException ex) {
            //Log error
        }
    }
}
