/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stockcontrol;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author celik
 */
public class ConnectionDB {
    
    private String username = "root";
    private String password = "";
    private String databaseName = "users";
    private String host = "localhost";
    private int port = 3306;
    
    private String connectedUser = null;

    
    
    public static Connection connection = null;
    private boolean connectionStatus = false;
    
    public static Statement statement=null;

    public ConnectionDB() {
        
        String url = "jdbc:mysql://" + host +":" + port + "/" + databaseName;
        
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            System.out.println("Not found Driver...");
        }
        
        try {
            connection = DriverManager.getConnection(url, username, password);
            statement = (Statement) connection.createStatement();
            System.out.println("Connection is successfull...");
            connectionStatus = true;
            
        } catch (SQLException ex) {
            System.out.println("Connection is unsuccessfull...");
            connectionStatus = false;
        }
    }
    
    public boolean login(String username, String password) {
        try {
            String sql = "SELECT * FROM users WHERE username = '"+username+"' AND password = '"+String.valueOf(password)+"'\n";
            ResultSet rss = statement.executeQuery(sql);
            connectedUser = username; // get connected username
            
            return rss.next();
            
        } catch (SQLException e) {
            System.out.println("Some mistakes...");
            return false;
        }
    }
    
    public String getConnectedUser() {
        return connectedUser;
    }
    
    /*
    * Return database item by ID
    */
    public String[] getItemsByID() {
        int i = 0;
        String[] items = new String[64];        
        String rqst = "SELECT ID FROM products"; //get all items on product table by id  
        try {
            
            statement = connection.createStatement();
            
            ResultSet result = statement.executeQuery(rqst);
            
            while(result.next()) {
                items[i] = result.getString("ID");
                i++;
            }            
            i=0;
            
            //Remove null items
            items = Arrays.stream(items)
                     .filter(s -> (s != null && s.length() > 0))
                     .toArray(String[]::new);    
            
            
        } catch (SQLException ex) {
            Logger.getLogger(ConnectionDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return items;
    }
    
    public ResultSet getItemByID(String ID) {
        
        String rqst = "SELECT * FROM products WHERE ID = '"+ ID +"'"; //get items product table by id  
        try {
            
            statement = connection.createStatement();
            
           ResultSet result = statement.executeQuery(rqst);
           return result;
            
        } catch (SQLException ex) {
            Logger.getLogger(ConnectionDB.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public void addProduct(String ID, int CurrQty, int extraQty) {
        
        String sql = "";        
        int CurrenQuantity = CurrQty;
        int newQuantity = CurrenQuantity + extraQty;
        sql = "UPDATE products SET stocklevel = '"+ newQuantity +"' WHERE ID = "
                +ID+"";
        
        try {
            statement = connection.createStatement();
            statement.executeUpdate(sql);
        } catch (SQLException ex) {
            Logger.getLogger(ConnectionDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void decreaseProduct(String ID, int CurrQty, int extraQty) {
        String sql = "";        
        int CurrenQuantity = CurrQty;
        int newQuantity = CurrenQuantity - extraQty;
        if(newQuantity<0)
            newQuantity = 0;
        sql = "UPDATE products SET stocklevel = '"+ newQuantity +"' WHERE ID = "
                +ID+"";        
        try {
            statement = connection.createStatement();
            statement.executeUpdate(sql);
        } catch (SQLException ex) {
            Logger.getLogger(ConnectionDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void addNewProduct(String[] params)
    {
        String sqlRequest = "INSERT INTO products (ID, name, description, stocklevel, "
                + "criticallevel, cost) VALUES ("+Integer.parseInt(params[0])+", "+"'"+params[1]+"', "
                +"'"+params[2]+"', "+Integer.parseInt(params[3])+", "
                +Integer.parseInt(params[4])+", "+Integer.parseInt(params[5])+")";
        
        try {
            statement = connection.createStatement();
            statement.executeUpdate(sqlRequest);
        } catch (SQLException ex) {
            
            Logger.getLogger(ConnectionDB.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
   
    public void deleteItem(String ID) {
        String sqlRequest = "DELETE FROM products WHERE ID = "+
                Integer.parseInt(ID);
        try {
            statement = connection.createStatement();
            statement.executeUpdate(sqlRequest);
                    } catch (SQLException ex) {
            Logger.getLogger(ConnectionDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public boolean isConnectionStatus() {
        return connectionStatus;
    }

    public void setConnectionStatus(boolean connectionStatus) {
        this.connectionStatus = connectionStatus;
    }
    
    
    
}
