package library.assistant.settings;

import com.google.gson.Gson;
import library.assistant.alert.AlertMaker;
import library.assistant.iu.main.MainController;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Preferences {
    public static final String CONFIG_FILE = "config.txt";

    int nDaysWithoutFine;
    float finePerDay;
    String username;
    String password;

    public Preferences(){
        nDaysWithoutFine = 14;
        finePerDay = 2;
        username = "admin";
        password = "admin";
    }

    public int getnDaysWithoutFine() {
        return nDaysWithoutFine;
    }

    public void setnDaysWithoutFine(int nDaysWithoutFine) {
        this.nDaysWithoutFine = nDaysWithoutFine;
    }

    public float getFinePerDay() {
        return finePerDay;
    }

    public void setFinePerDay(float finePerDay) {
        this.finePerDay = finePerDay;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static void initConfig(){
        Writer writer = null;
        try {
        Preferences preferences = new Preferences();
        Gson gson = new Gson();
            writer = new FileWriter(CONFIG_FILE);
            gson.toJson(preferences, writer);
        } catch (IOException e) {
            Logger.getLogger(Preferences.class.getName()).log(Level.SEVERE, null, e);
        }finally {

            try {
                writer.close();
            } catch (IOException e) {
                Logger.getLogger(Preferences.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }

    public static Preferences getPreferences(){
        Gson gson = new Gson();
        Preferences preferences = new Preferences();

        try {
            preferences = gson.fromJson(new FileReader(CONFIG_FILE), Preferences.class);
        } catch (FileNotFoundException e) {
            initConfig();
            Logger.getLogger(Preferences.class.getName()).log(Level.SEVERE, null, e);
        }
        return preferences;
    }

    public static void writePreferenceToFile(Preferences preferences){
        Writer writer = null;
        try {
            Gson gson = new Gson();
            writer = new FileWriter(CONFIG_FILE);
            gson.toJson(preferences, writer);

            AlertMaker.showSimpleAlert("Success", "Settings updated");
        } catch (IOException e) {
            Logger.getLogger(Preferences.class.getName()).log(Level.SEVERE, null, e);
            AlertMaker.showErrorMessage(e, "Failed", "Can't save configuration file");
        }finally {

            try {
                writer.close();
            } catch (IOException e) {
                Logger.getLogger(Preferences.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }
}
