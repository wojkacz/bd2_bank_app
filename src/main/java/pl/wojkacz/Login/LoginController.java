package pl.wojkacz.Login;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import pl.wojkacz.Data.UserData;

import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    final static String api = "http://localhost:8080/";
    HttpClient httpClient;
    MessageDigest md = null;

    @FXML
    private Pane mainPane;

    // ======================================================================
    // Login
    @FXML
    private Label l_errorLabel;

    @FXML
    private TextField l_loginTextField;

    @FXML
    private PasswordField l_passwordTextField;

    @FXML
    private Button l_loginButton;

    @FXML
    private AnchorPane loginPane;
    // ======================================================================
    // Create Account
    @FXML
    private AnchorPane createAccountPane;

    @FXML
    private Label c_errorLabel;

    @FXML
    private TextField c_loginTextField;

    @FXML
    private TextField c_passwordTextField;

    @FXML
    private TextField c_nameTextField;

    @FXML
    private TextField c_surnameTextField;

    @FXML
    private Button c_registerButton;

    // ======================================================================
    // Activate Account
    @FXML
    private AnchorPane activatePane;

    @FXML
    private TextField a_loginTextField;

    @FXML
    private TextField a_codeTextField;

    @FXML
    private Label a_errorLabel;

    // ======================================================================
    // Forgot Password

    // ======================================================================

    @FXML
    private void loginButtonAction(ActionEvent event) {
        l_errorLabel.setVisible(false);
        l_errorLabel.setTextFill(Color.RED);

        String login = l_loginTextField.getText();
        String password = l_passwordTextField.getText();

        if(login.length() <= 1) {
            l_errorLabel.setText("Login must be longer than 1 character!");
            l_errorLabel.setVisible(true);
            return;
        }

        if(password.length() <= 1) {
            l_errorLabel.setText("Password must be longer than 1 character!");
            l_errorLabel.setVisible(true);
            return;
        }

        l_loginButton.setDisable(true);
        String passwordHash = getHash(password);
        String url = api + "login?" +
                "login=" + login +
                "&password=" + passwordHash;
        HttpPost request = new HttpPost(url);

        try {
            HttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity);

            switch(response.getStatusLine().getStatusCode()){
                case 200:
                    System.out.println("Zalogowano");

                    JSONObject obj = new JSONObject(result);
                    String nameRes = obj.getJSONObject("user-login-data").getString("name");
                    String surnameRes = obj.getJSONObject("user-login-data").getString("surname");
                    String loginRes = obj.getJSONObject("user-login-data").getString("login");
                    int permissionRes = obj.getJSONObject("user-login-data").getInt("permission_level");
                    String tokenRes = obj.getJSONObject("user-login-data").getString("token");

                    UserData.setUserData(new UserData(nameRes, surnameRes, loginRes, permissionRes, tokenRes));

                    Parent root = FXMLLoader.load(getClass().getResource("../MainScene/MainScene.fxml"));
                    Scene scene = new Scene(root);
                    scene.getStylesheets().add("/styles/Styles.css");

                    Stage stage = (Stage) mainPane.getScene().getWindow();
                    stage.setScene(scene);
                    stage.show();
                    break;
                case 404:
                case 417:
                    l_errorLabel.setText("Incorrect login or password!");
                    l_errorLabel.setVisible(true);
                    break;
                case 405:
                    l_errorLabel.setText("User is not activated! Check your email");
                    l_errorLabel.setVisible(true);
                    break;
                default:
                    l_errorLabel.setText("Unknown Error!");
                    l_errorLabel.setVisible(true);
                    break;

            }
            l_loginButton.setDisable(false);

        } catch(Exception e) {
            System.out.println("[Login] " + e.getMessage());
        } finally {
            request.releaseConnection();
        }

    }

    @FXML
    private void registerButtonAction(ActionEvent event){
        c_errorLabel.setVisible(false);
        c_errorLabel.setTextFill(Color.RED);
        String name = c_nameTextField.getText();
        String surname = c_surnameTextField.getText();
        String login = c_loginTextField.getText();
        String password = c_passwordTextField.getText();

        if(name.length() <= 1) {
            c_errorLabel.setText("Name must be longer than 1 character!");
            c_errorLabel.setVisible(true);
            return;
        }

        if(surname.length() <= 1) {
            c_errorLabel.setText("Surname must be longer than 1 character!");
            c_errorLabel.setVisible(true);
            return;
        }

        if(login.length() <= 1) {
            c_errorLabel.setText("Email must be longer than 1 character!");
            c_errorLabel.setVisible(true);
            return;
        }

        if(login.indexOf('@') == -1 || login.indexOf('@') == login.length()-1 || login.indexOf('@') == 0) {
            c_errorLabel.setText("Incorrect email!");
            c_errorLabel.setVisible(true);
            return;
        }

        if(password.length() <= 1) {
            c_errorLabel.setText("Password must be longer than 1 character!");
            c_errorLabel.setVisible(true);
            return;
        }

        c_registerButton.setDisable(true);
        String passwordHash = getHash(password);
        String url = api + "register?" +
                "name=" + name +
                "&surname=" + surname +
                "&login=" + login +
                "&password=" + passwordHash;
        HttpPost request = new HttpPost(url);

        try {
            HttpResponse response = httpClient.execute(request);

            switch(response.getStatusLine().getStatusCode()){
                case 302:
                    c_errorLabel.setText("User with that email already exist!");
                    c_errorLabel.setVisible(true);
                    break;
                case 417:
                    c_errorLabel.setText("Incorrect email!");
                    c_errorLabel.setVisible(true);
                    break;
                case 200:
                    toggleRegisterVisibility(null);
                    toggleActivationVisibility(null);
                    a_errorLabel.setTextFill(Color.GREEN);
                    a_errorLabel.setText("Successfully registered! Check your email");
                    a_errorLabel.setVisible(true);
                    break;
                default:
                    c_errorLabel.setText("Unknown error!");
                    c_errorLabel.setVisible(true);
                    break;
            }
            c_registerButton.setDisable(false);
        } catch(Exception e) {
            System.out.println("[Register]" + e.getMessage());
        } finally {
            request.releaseConnection();
        }
    }

    @FXML
    private void activateButtonAction(ActionEvent event){
        a_errorLabel.setVisible(false);
        a_errorLabel.setTextFill(Color.RED);

        String login = a_loginTextField.getText();
        String code = a_codeTextField.getText();

        if(login.length() <= 1) {
            a_errorLabel.setText("Incorrect Email!");
            a_errorLabel.setVisible(true);
            return;
        }

        if(code.length() != 4) {
            a_errorLabel.setText("Incorrect Code!");
            a_errorLabel.setVisible(true);
            return;
        }

        String url = api + "activate?" +
                "login=" + login +
                "&code=" + code;
        HttpPost request = new HttpPost(url);

        try {
            HttpResponse response = httpClient.execute(request);

            switch(response.getStatusLine().getStatusCode()){
                case 404:
                    a_errorLabel.setText("Incorrect Email!");
                    a_errorLabel.setVisible(true);
                    break;
                case 417:
                    a_errorLabel.setText("Incorrect Code!");
                    a_errorLabel.setVisible(true);
                    break;
                case 405:
                    a_errorLabel.setText("That user is already activated!");
                    a_errorLabel.setVisible(true);
                    break;
                case 200:
                    toggleActivationVisibility(null);
                    l_errorLabel.setTextFill(Color.GREEN);
                    l_errorLabel.setText("Successfully activated! You can log in");
                    l_errorLabel.setVisible(true);
                    break;
                default:
                    a_errorLabel.setText("Unknown error!");
                    a_errorLabel.setVisible(true);
                    break;
            }
        } catch(Exception e) {
            System.out.println("[Activate] " + e.getMessage());
        } finally {
            request.releaseConnection();
        }

    }

    @FXML
    private void toggleRegisterVisibility(ActionEvent event){
        loginPane.setVisible(!loginPane.isVisible());
        createAccountPane.setVisible(!createAccountPane.isVisible());

        c_errorLabel.setVisible(false);
        c_nameTextField.setText("");
        c_surnameTextField.setText("");
        c_loginTextField.setText("");
        c_passwordTextField.setText("");

        l_errorLabel.setVisible(false);
        l_loginTextField.setText("");
        l_passwordTextField.setText("");
    }

    @FXML
    private void toggleActivationVisibility(ActionEvent event){
        loginPane.setVisible(!loginPane.isVisible());
        activatePane.setVisible(!activatePane.isVisible());

        a_errorLabel.setVisible(false);
        a_loginTextField.setText("");
        a_codeTextField.setText("");

        l_errorLabel.setVisible(false);
        l_loginTextField.setText("");
        l_passwordTextField.setText("");
    }

    private String getHash(String text){
        md.update(text.getBytes());
        byte[] digest = md.digest();
        StringBuilder sb = new StringBuilder(digest.length * 2);
        for(byte b : digest)
            sb.append(String.format("%02x", b));
        return sb.toString();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loginPane.setVisible(true);
        createAccountPane.setVisible(false);
        activatePane.setVisible(false);

        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("[MessageDigest]" + e.getMessage());
        }
        assert md != null;

        httpClient = HttpClientBuilder.create().build();
    }
}
