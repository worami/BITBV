package dbconnections;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import calendar.CalendarItem;

public class Connector {
	Connection con = null;
    Statement st = null;
    ResultSet rs = null;

    Properties props = new Properties();
    
    String tabel;

    /**
     * Laad de properties (uses, db-naam en pw)
     * Test vervolgens de verbinding
     */
    public Connector(String properties){
    	this.loadProperties(properties);
    	this.tabel = props.getProperty("db.tabel");
    	this.TestConnection();
    }
    
    /**
     * Maak een connectie met een database 
     * !!Altijd Close na Connect
     */
    protected void Connect(){
    	try {
            con = DriverManager.getConnection(props.getProperty("db.url"), props.getProperty("db.user"), props.getProperty("db.passwd"));
            st = con.createStatement();

        } catch (SQLException ex) {
            System.err.print("error conncect: " + ex.getMessage());
            
        }
    }
    
    /**
     * Test de connectie met de database, print de dbversie als bewijs
     */
    private void TestConnection(){
    	this.Connect();
    	try {
    		rs = st.executeQuery("SELECT VERSION()");
			if (rs.next()) {
			    System.out.println("Verbonden: db versie " + rs.getString(1));
			}
		} catch (SQLException e) {
			System.err.println("TestConncetion(): " + e.getMessage());
		} finally {
			this.Close();
		}
    }
    
    /**
     * Laad de connectie eigenschappen uit een properties bestand
     */
    private void loadProperties(String prop){
    	try {
    		FileInputStream in = new FileInputStream(prop);
            props.load(in);

        } catch (FileNotFoundException ex) {
            System.err.println(ex.getMessage());

        } catch (IOException e){
        	System.err.println(e.getMessage());
        }
    }
    
    /**
     * Sluit de databaseconnectie netjes af
     */
    public void Close(){
    	try {
            if (rs != null) {
                rs.close();
            }
            if (st != null) {
                st.close();
            }
            if (con != null) {
                con.close();
            }

        } catch (SQLException ex) {
        	System.out.println("Error tweede: " + ex.getMessage());
        }
    }
    
    public String getTabel(){
    	return this.tabel;
    }
}

