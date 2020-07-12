package library.assistant.database;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import library.assistant.iu.listbook.BookListController;
import library.assistant.iu.listmember.MemberListController;

import javax.swing.*;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseHandler {

    private static DatabaseHandler handler = null;

    private static final String DB_URL = "jdbc:derby:database;create=true";
    private static Connection conn = null;
    private static Statement stmt = null;

    private DatabaseHandler(){
        createConnection();
        setupBookTable();
        setupMemberTable();
        setupIssueTable();
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
            JOptionPane.showMessageDialog(null, "Can't load database", "Database Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }

    void setupBookTable(){
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

    private void setupIssueTable() {
        String TABLE_NAME = "ISSUE";
        try{
            stmt = conn.createStatement();

            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null, null, TABLE_NAME.toUpperCase(), null);

            if (tables.next()){
                System.out.println("Table " + TABLE_NAME + "already exists for go!");
            }else {
                stmt.execute("CREATE TABLE " + TABLE_NAME + "("
                        + "         bookID varchar(200) primary key,\n"
                        + "         memberID varchar(200),\n"
                        + "         issueTime timestamp default CURRENT_TIMESTAMP,\n"
                        + "         renew_count integer default 0,\n"
                        + "         FOREIGN KEY (bookID) REFERENCES BOOK(id),\n"
                        + "         FOREIGN KEY (memberID) REFERENCES MEMBER(id)"
                        + ")");
            }

        }catch (SQLException e){
            System.err.println(e.getMessage() + " ... setupDatabase");
        }finally {

        }

    }

    public boolean deleteBook(BookListController.Book book){
        try {
        String deleteStatement = "DELETE FROM BOOK WHERE ID = ?";

            PreparedStatement stmt = conn.prepareStatement(deleteStatement);
            stmt.setString(1, book.getId());
            int res = stmt.executeUpdate();
            if (res == 1){
                return true;
            }
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean isBookAlreadyIssued(BookListController.Book book){
        try {
            String checkstmt = "SELECT COUNT(*) FROM ISSUE WHERE bookid=?";
            PreparedStatement stmt = conn.prepareStatement(checkstmt);
            stmt.setString(1, book.getId());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()){
                int count = rs.getInt(1);
                System.out.println(count);
                return (count > 0);
                }

        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean deleteMember(MemberListController.Member member) {
        try {
            String deleteStatement = "DELETE FROM MEMBER WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(deleteStatement);
            stmt.setString(1, member.getId());
            int res = stmt.executeUpdate();
            if (res == 1){
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean isMemberHasAnyBooks(MemberListController.Member member){
        try {
            String checkstmt = "SELECT COUNT(*) FROM ISSUE WHERE memberID=?";
            PreparedStatement stmt = conn.prepareStatement(checkstmt);
            stmt.setString(1, member.getId());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()){
                int count = rs.getInt(1);
                System.out.println(count);
                return (count > 0);
            }

        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean updateBook(BookListController.Book book){
        try {
            String update = "UPDATE BOOK SET TITLE=?, AUTHOR=?, PUBLISHER=? WHERE ID=?";
            PreparedStatement stmt = conn.prepareStatement(update);
            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setString(3, book.getPublisher());
            stmt.setString(4, book.getId());
            int res = stmt.executeUpdate();
            return (res > 0);

        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean updateMember(MemberListController.Member member){
        try {
            String update = "UPDATE MEMBER SET NAME=?, EMAIL=?, MOBILE=? WHERE ID=?";
            PreparedStatement stmt = conn.prepareStatement(update);
            stmt.setString(1, member.getName());
            stmt.setString(2, member.getEmail());
            stmt.setString(3, member.getMobile());
            stmt.setString(4, member.getId());
            int res = stmt.executeUpdate();
            return (res > 0);

        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public ObservableList<PieChart.Data> getBookGraphStatistics(){
        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
        try {
        String qu1 = "SELECT COUNT(*) FROM BOOK";
        String qu2 = "SELECT COUNT(*) FROM ISSUE";
        ResultSet rs = execQuery(qu1);
        if (rs.next()){
            int count = rs.getInt(1);
            data.add(new PieChart.Data("Total Books {" + count + "}", count));

        }

        rs = execQuery(qu2);
            if (rs.next()){
                int count = rs.getInt(1);
                data.add(new PieChart.Data("Issued Books {" + count + "}", count));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    public ObservableList<PieChart.Data> getMemberGraphStatistics(){
        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
        try {
            String qu1 = "SELECT COUNT(*) FROM MEMBER";
            String qu2 = "SELECT COUNT(DISTINCT memberID) FROM ISSUE";
            ResultSet rs = execQuery(qu1);
            if (rs.next()){
                int count = rs.getInt(1);
                data.add(new PieChart.Data("Total Members {" + count + "}", count));

            }

            rs = execQuery(qu2);
            if (rs.next()){
                int count = rs.getInt(1);
                data.add(new PieChart.Data("Members with books {" + count + "}", count));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }
}

