package cz.vutbr.fit.pdb.configuration;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class DBConfiguration {
    public static final String USERNAME_DEFAULT = System.getProperty("username");
    public static final String PASSWORD_DEFAULT = System.getProperty("password");
    public static final String HOST_DEFAULT = "gort.fit.vutbr.cz";
    public static final int PORT_DEFAULT = 1521;
    public static final String SERVICE_NAME_DEFAULT = "gort.fit.vutbr.cz";

    private StringProperty username = new SimpleStringProperty(USERNAME_DEFAULT);
    private StringProperty password = new SimpleStringProperty(PASSWORD_DEFAULT);
    private StringProperty host = new SimpleStringProperty(HOST_DEFAULT);
    private IntegerProperty port = new SimpleIntegerProperty(PORT_DEFAULT);
    private StringProperty serviceName = new SimpleStringProperty(SERVICE_NAME_DEFAULT);

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

    public String getHost() {
        return host.get();
    }

    public void setHost(String host) {
        this.host.set(host);
    }

    public StringProperty hostProperty() {
        return host;
    }

    public int getPort() {
        return port.get();
    }

    public void setPort(int port) {
        this.port.set(port);
    }

    public IntegerProperty portProperty() {
        return port;
    }

    public String getServiceName() {
        return serviceName.get();
    }

    public void setServiceName(String serviceName) {
        this.serviceName.set(serviceName);
    }

    public StringProperty serviceNameProperty() {
        return serviceName;
    }

    @Override
    public String toString() {
        return "DBConfiguration{" +
                "username=" + username +
                ", password=" + password +
                ", host=" + host +
                ", port=" + port +
                ", serviceName=" + serviceName +
                '}';
    }
}
