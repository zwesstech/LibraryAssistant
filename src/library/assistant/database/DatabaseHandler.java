package library.assistant.database;

import javax.swing.*;
import java.sql.*;

public class DatabaseHandler {

    private static DatabaseHandler handler = null;

    private static final String DB_URL = "jdbc:derby:database;create=true";
    private static Connection conn = null;
    private static Statement stmt = null;

    private DatabaseHandler(){
        createConnection();
        setupTable();
        setupMemberTable();
    }

    public static DatabaseHandler getInstance(){

        if (handler == null){
            handler = new DatabaseHandler();
        }
        return handler;
    }

    private static void createConnection() {
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
            conn = DriverManager.getConnection(DB_URL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void setupTable(){
        String TABLE_NAME = "BOOK";
        try{
            stmt = conn.createStatement();
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null, null, TABLE_NAME.toUpperCase(), null);
            if (tables.next()){
                System.out.println("Table " + TABLE_NAME + "already exists for go!");
            }else {
                stmt.execute("CREATE TABLE " + TABLE_NAME + "("
                        + "         id varchar(200) primary key,\n"
                        + "         title varchar(200),\n"
                        + "         author varchar(200),\n"
                        + "         publisher varchar(100),\n"
                        + "         isAvail boolean default true"
                        +
                        ")");
            }

        }catch (SQLException e){
            System.err.println(e.getMessage() + " ... setupDatabase");
        }finally {

        }
    }
    public ResultSet execQuery(String query) {
        ResultSet result;
        try {
            stmt = conn.createStatement();
            result = stmt.executeQuery(query);
        }
        catch (SQLException ex) {
            System.out.println("Exception at execQuery:dataHandler" + ex.getLocalizedMessage());
            return null;
        }
        finally {
        }
        return result;
    }

    public boolean execAction(String qu) {
        try {
            stmt = conn.createStatement();
            stmt.execute(qu);
            return true;
        }
        catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error:" + ex.getMessage(), "Error Occured", JOptionPane.ERROR_MESSAGE);
            System.out.println("Exception at execQuery:dataHandler" + ex.getLocalizedMessage());
            return false;
        }
        finally {
        }
    }

    private void setupMemberTable() {
        String TABLE_NAME = "MEMBER";
        try{
            stmt = conn.createStatement();
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null, null, TABLE_NAME.toUpperCase(), null);
            if (tables.next()){
                System.out.println("Table " + TABLE_NAME + "already exists for go!");
            }else {
                stmt.execute("CREATE TABLE " + TABLE_NAME + "("
                        + "         id varchar(200) primary key,\n"
                        + "         name varchar(200),\n"
                        + "         mobile varchar(20),\n"
                        + "         email varchar(100)\n"
                        + ")");
            }

        }catch (SQLException e){
            System.err.println(e.getMessage() + " ... setupDatabase");
        }finally {

        }

    }
}

