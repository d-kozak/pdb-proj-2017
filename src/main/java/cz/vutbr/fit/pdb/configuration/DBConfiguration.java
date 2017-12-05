package cz.vutbr.fit.pdb.configuration;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class DBConfiguration {
    private StringProperty username = new SimpleStringProperty(System.getProperty("username"));
    private StringProperty password = new SimpleStringProperty(System.getProperty("password"));
    private StringProperty databaseURI = new SimpleStringProperty("LALALA");

    public String getUsername() {
        return username.get();
    }

    public void setUsername(String username) {
        this.username.set(username);
    }

    public StringProperty usernameProperty() {
        return username;
    }

    public String getPassword() {
        return password.get();
    }

    public void setPassword(String password) {
        this.password.set(password);
    }

    public StringProperty passwordProperty() {
        return password;
    }

    public String getDatabaseURI() {
        return databaseURI.get();
    }

    public void setDatabaseURI(String databaseURI) {
        this.databaseURI.set(databaseURI);
    }

    public StringProperty databaseURIProperty() {
        return databaseURI;
    }

    @Override
    public String toString() {
        return "DBConfiguration{" +
                "username=" + username +
                ", password=" + password +
                ", databaseURI=" + databaseURI +
                '}';
    }
}
