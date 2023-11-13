/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package monitorcontrolid;

import com.google.gson.JsonArray;
import java.io.FileReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Map;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import com.google.gson.JsonObject;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.time.Instant;
import javax.imageio.ImageIO;
import org.apache.commons.net.util.Base64;

/**
 *
 * @author Emerson
 */
public final class ControlIdFace {

    private final JSONParser parser = new JSONParser();
    private JSONObject jsonObject = null;
    private final String IDACCESS_IP = (String) jCatraca().get("ip");
    private final int IDACCESS_PORT = Integer.parseInt((String) jCatraca().get("ipPorta")); //PORTA DO HTTP DO CONTROLID PADRÃO É 80
    private String session;

    public boolean session() {
        this.session = login();

        return this.session != null;
    }

    public String login() {
        Map<String, String> headers = HttpHeaders.newEmptyHeaders();
        HttpHeaders.addHeader(headers, HttpHeaders.createContentTypeHeader("application/json"));

        JsonObject body = new JsonObject();
        body.addProperty("login", "admin");
        body.addProperty("password", "22010205");

        HttpURLConnection httpURLConnection = HttpConnectionUtils.post(this.IDACCESS_IP, this.IDACCESS_PORT, "login.fcgi", headers, null, body.toString().getBytes());

        try {
            if (httpURLConnection.getResponseCode() != 200) {
                InputStreamUtils.inputStreamToString(httpURLConnection.getErrorStream());

                return null;
            }

            JsonObject response = InputStreamUtils.inputStreamToJsonObject(httpURLConnection.getInputStream());

            String session = response.get("session").getAsString();

            session = session;

            return session;
        } catch (IOException e) {

            return null;
        }
    }

    public void liberarCatraca() {
        Map<String, String> headers = HttpHeaders.newEmptyHeaders();
        HttpHeaders.addHeader(headers, HttpHeaders.createContentTypeHeader("application/json"));

        JsonObject objActions = new JsonObject();
        objActions.addProperty("action", "sec_box");
        objActions.addProperty("parameters", "id=65793,reason=3");

        JsonArray actions = new JsonArray();
        actions.add(objActions);

        JsonObject body = new JsonObject();
        body.add("actions", actions);

        HttpURLConnection httpURLConnection
                = HttpConnectionUtils.post(this.IDACCESS_IP, this.IDACCESS_PORT, "execute_actions.fcgi?session=" + this.session, headers, null, body.toString().getBytes());

        try {
            if (httpURLConnection.getResponseCode() != 200) {
                InputStreamUtils.inputStreamToString(httpURLConnection.getErrorStream());
            }

        } catch (IOException e) {

        }
    }

    public void cadastroUsuario(String nome, String id, String ftBase64) throws ParseException {

        Map<String, String> headers = HttpHeaders.newEmptyHeaders();
        HttpHeaders.addHeader(headers, HttpHeaders.createContentTypeHeader("application/json"));

        JsonObject objValues = new JsonObject();
        objValues.addProperty("name", nome);
        objValues.addProperty("registration", id);
        objValues.addProperty("password", "");
        objValues.addProperty("salt", "");

        JsonArray values = new JsonArray();
        values.add(objValues);

        JsonObject body = new JsonObject();
        body.addProperty("object", "users");
        body.add("values", values);

        HttpURLConnection httpURLConnection
                = HttpConnectionUtils.post(this.IDACCESS_IP, this.IDACCESS_PORT, "create_objects.fcgi?session=" + this.session, headers, null, body.toString().getBytes());

        try {
            if (httpURLConnection.getResponseCode() != 200) {
                InputStreamUtils.inputStreamToString(httpURLConnection.getErrorStream());
            }

            JsonObject response = InputStreamUtils.inputStreamToJsonObject(httpURLConnection.getInputStream());

            int user_id = Integer.parseInt(response.get("ids").getAsString());

            this.cadastroGrupoUsuario(user_id);
            if (ftBase64 == null) {
                this.cadFaceDispositivo(user_id, id);
            } else {
                this.cadastroFts(user_id, ftBase64);

            }

        } catch (IOException e) {

        }
    }

    public void cadastroGrupoUsuario(int id) {

        Map<String, String> headers = HttpHeaders.newEmptyHeaders();
        HttpHeaders.addHeader(headers, HttpHeaders.createContentTypeHeader("application/json"));

        JsonArray fields = new JsonArray();
        fields.add("user_id");
        fields.add("group_id");

        JsonArray values = new JsonArray();

        JsonObject objValues = new JsonObject();
        objValues.addProperty("user_id", id);
        objValues.addProperty("group_id", 1);

        values.add(objValues);

        JsonObject body = new JsonObject();
        body.addProperty("object", "user_groups");
        body.add("fields", fields);
        body.add("values", values);

        HttpURLConnection httpURLConnection
                = HttpConnectionUtils.post(this.IDACCESS_IP, this.IDACCESS_PORT, "create_objects.fcgi?session=" + this.session, headers, null, body.toString().getBytes());

        try {
            if (httpURLConnection.getResponseCode() != 200) {
                InputStreamUtils.inputStreamToString(httpURLConnection.getErrorStream());

            }

        } catch (IOException e) {

        }
    }

    public void cadastroFts(int id, String ftBase64) {

        Map<String, String> headers = HttpHeaders.newEmptyHeaders();
        HttpHeaders.addHeader(headers, HttpHeaders.createContentTypeHeader("application/json"));

        long timestampEpoch = Instant.now().getEpochSecond();

        JsonArray user_images = new JsonArray();

        JsonObject objUser_images = new JsonObject();
        objUser_images.addProperty("user_id", id);
        objUser_images.addProperty("timestamp", timestampEpoch);
        objUser_images.addProperty("image", ftBase64);

        user_images.add(objUser_images);

        JsonObject body = new JsonObject();
        body.addProperty("match", true);
        body.add("user_images", user_images);

        HttpURLConnection httpURLConnection
                = HttpConnectionUtils.post(this.IDACCESS_IP, this.IDACCESS_PORT, "user_set_image_list.fcgi?session=" + this.session, headers, null, body.toString().getBytes());

        try {
            if (httpURLConnection.getResponseCode() != 200) {
                InputStreamUtils.inputStreamToString(httpURLConnection.getErrorStream());

            }

        } catch (IOException e) {

        }

    }

    public void cadFaceDispositivo(int id, String idd) throws ParseException {
        Map<String, String> headers = HttpHeaders.newEmptyHeaders();
        HttpHeaders.addHeader(headers, HttpHeaders.createContentTypeHeader("application/json"));

        JsonObject body = new JsonObject();
        body.addProperty("type", "face");
        body.addProperty("user_id", id);
        body.addProperty("save", true);
        body.addProperty("sync", true);
        body.addProperty("auto", true);
        body.addProperty("countdown", 2);

        HttpURLConnection httpURLConnection
                = HttpConnectionUtils.post(this.IDACCESS_IP, this.IDACCESS_PORT, "remote_enroll.fcgi?session=" + this.session, headers, null, body.toString().getBytes());

        try {
            if (httpURLConnection.getResponseCode() != 200) {
                InputStreamUtils.inputStreamToString(httpURLConnection.getErrorStream());

            }

            JsonObject response = InputStreamUtils.inputStreamToJsonObject(httpURLConnection.getInputStream());

            String fto = response.get("user_image").getAsString();

            BufferedImage image = null;

            Base64 teste = new Base64();
            byte[] imageByte = Base64.decodeBase64(fto);
            ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
            image = ImageIO.read(bis);
            bis.close();

        } catch (IOException e) {

        }
    }

    public void excluirUsuario(String id) {
        Map<String, String> headers = HttpHeaders.newEmptyHeaders();
        HttpHeaders.addHeader(headers, HttpHeaders.createContentTypeHeader("application/json"));

        JsonObject registration = new JsonObject();
        registration.addProperty("registration", id);

        JsonObject users = new JsonObject();
        users.add("users", registration);

        JsonObject body = new JsonObject();
        body.addProperty("object", "users");
        body.add("where", users);

        HttpURLConnection httpURLConnection
                = HttpConnectionUtils.post(this.IDACCESS_IP, this.IDACCESS_PORT, "destroy_objects.fcgi?session=" + this.session, headers, null, body.toString().getBytes());

        try {
            if (httpURLConnection.getResponseCode() != 200) {
                InputStreamUtils.inputStreamToString(httpURLConnection.getErrorStream());

            }

        } catch (IOException e) {

        }
    }

    public void updateInicialFinal(long begin, long end, int user_id) {
        Map<String, String> headers = HttpHeaders.newEmptyHeaders();
        HttpHeaders.addHeader(headers, HttpHeaders.createContentTypeHeader("application/json"));

        JsonObject id = new JsonObject();
        id.addProperty("id", user_id);

        JsonObject users = new JsonObject();
        users.add("users", id);

        JsonObject values = new JsonObject();
        values.addProperty("begin_time", begin);
        values.addProperty("end_time", end);

        JsonObject body = new JsonObject();
        body.addProperty("object", "users");
        body.add("values", values);
        body.add("where", users);

        HttpURLConnection httpURLConnection
                = HttpConnectionUtils.post(this.IDACCESS_IP, this.IDACCESS_PORT, "modify_objects.fcgi?session=" + this.session, headers, null, body.toString().getBytes());

        try {
            if (httpURLConnection.getResponseCode() != 200) {
                InputStreamUtils.inputStreamToString(httpURLConnection.getErrorStream());

            }

        } catch (IOException e) {

        }
    }

    public void excluirObjUserCompleto() {

        Map<String, String> headers = HttpHeaders.newEmptyHeaders();
        HttpHeaders.addHeader(headers, HttpHeaders.createContentTypeHeader("application/json"));

        JsonObject body = new JsonObject();
        body.addProperty("object", "users");

        HttpURLConnection httpURLConnection
                = HttpConnectionUtils.post(this.IDACCESS_IP, this.IDACCESS_PORT, "destroy_objects.fcgi?session=" + this.session, headers, null, body.toString().getBytes());

        try {
            if (httpURLConnection.getResponseCode() != 200) {
                InputStreamUtils.inputStreamToString(httpURLConnection.getErrorStream());

            }

        } catch (IOException e) {

        }
    }

    public void monitor(String ip, String port) {

        Map<String, String> headers = HttpHeaders.newEmptyHeaders();
        HttpHeaders.addHeader(headers, HttpHeaders.createContentTypeHeader("application/json"));

        JsonObject monitor = new JsonObject();
        monitor.addProperty("request_timeout", "5000");
        monitor.addProperty("hostname", ip);
        monitor.addProperty("port", port);

        JsonObject body = new JsonObject();
        body.add("monitor", monitor);

        HttpURLConnection httpURLConnection
                = HttpConnectionUtils.post(this.IDACCESS_IP, this.IDACCESS_PORT, "set_configuration.fcgi?session=" + this.session, headers, null, body.toString().getBytes());

        try {
            if (httpURLConnection.getResponseCode() != 200) {
                InputStreamUtils.inputStreamToString(httpURLConnection.getErrorStream());
            }

        } catch (IOException e) {

        }
    }

    public JSONObject jCatraca() {
        try {
            try {
                //Salva no objeto JSONObject o que o parse tratou do arquivo
                jsonObject = (JSONObject) parser.parse(new FileReader("C:\\UpAdminGym\\jCatraca.json"));
            } catch (IOException ex) {

            }
        } catch (ParseException ex) {

        }

        return jsonObject;
    }
}
