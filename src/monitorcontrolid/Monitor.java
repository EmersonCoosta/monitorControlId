/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package monitorcontrolid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.google.gson.JsonObject;
import com.google.gson.Gson;
import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

/**
 *
 * @author Emerson Coosta
 */
public class Monitor extends javax.swing.JFrame {

    /**
     * Creates new form Monitor
     */
    private static final long serialVersionUID = 1L;

    private static ServerSocket server;
    private static final String port = "3000"; //PORTA DO MONITOR || LBERAR O FIREWALL DO COMPUTADOR DO CLIENTE
    public String ret = "";
    public String ip;
    public String ipComputadorLocal;
    private HttpURLConnection http;
    private List<Dados> dados;
    private Map<String, String> dts;

    public Monitor() throws IOException, IOException, FileNotFoundException, SQLException, ParseException {
        initComponents();
        setIcon();
        dtVenc();

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public void server() throws UnknownHostException {

        //CONFIGURA O MONITOR PRA RECEBER LISTA DE PASSAGEM
        ControlIdFace controlId = new ControlIdFace();
        controlId.session();
        ipComputadorLocal = InetAddress.getLocalHost().getHostAddress(); //IP DO COMPUTADOR LOCAL
        controlId.monitor(ipComputadorLocal, port);

        new Thread() {

            @Override
            public void run() {
                try {
                    server = new ServerSocket(Integer.parseInt(port));

                    while (server.isBound()) {

                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(Monitor.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        Socket socket = server.accept();
                        ip = socket.getInetAddress().getHostAddress();

                        if (socket.isConnected()) {

                            BufferedReader in = new BufferedReader(
                                    new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));

                            while (in.readLine() != null) {
                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException ex) {
                                    Logger.getLogger(Monitor.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                ret = in.readLine();
                            }

                            String responseAsString = ret;

                            JsonObject jsonObject = new Gson().fromJson(responseAsString, JsonObject.class);

                            String device = (jsonObject.get("device_id").getAsString());

                            if (jsonObject.get("user_id") != null) {
                                int user_id = (jsonObject.get("user_id").getAsInt());
                                long time = (jsonObject.get("time").getAsLong());
                                int event = (jsonObject.get("event").getAsInt());

                                Date date = new Date(time * 1000L);
                                SimpleDateFormat jdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
                                jdf.setTimeZone(TimeZone.getTimeZone("GMT-0"));
                                String timeStr = jdf.format(date);

                                String idStr = Integer.toString(user_id);
                                String resultado = null;
                                int idResult = 1;

                                System.out.println(dtVenc().get(idStr) + " " + idStr);

                                if (!"".equals(dtVenc().get(idStr)) && dtVenc().get(idStr) != null) {
                                    Date dt = new Date();
                                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

                                    dt = formatter.parse(dtVenc().get(idStr));

                                    Date dtAtual = new Date();
                                    formatter.format(dtAtual);

                                    long diff = dt.getTime() - dtAtual.getTime();
                                    long dias = (diff / (1000 * 60 * 60 * 24));

                                    if (event == 7 && dias < 5) {

                                        resultado = "Próximo Vencimento " + new SimpleDateFormat("dd/MM/yyyy").format(dt);
                                        idResult = 2;

                                    } else {

                                        resultado = event == 7 ? "Passagem Liberada!" : "Bloqueado. Consulte a Recepção";
                                        idResult = event == 7 ? 1 : 2;

                                    }

                                } else {

                                    resultado = event == 7 ? "Passagem Liberada!" : "Bloqueado. Consulte a Recepção";
                                    idResult = event == 7 ? 1 : 2;

                                }

                                postFrequencia(user_id, timeStr, idResult, resultado);

//                                try {
//
//                                    acesso();
//
//                                } catch (FileNotFoundException | SQLException | ParseException ex) {
//                                    Logger.getLogger(Monitor.class.getName()).log(Level.SEVERE, null, ex);
//                                }
                            }

                            socket.close();
                        }

                    }
                } catch (IOException | ParseException | SQLException ex) {
                    Logger.getLogger(Monitor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }.start();

    }

    private Map<String, String> dtVenc() throws FileNotFoundException, SQLException, ParseException {
        dts = new HashMap<>();

        dados = Dados();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date data_fp = new Date();
        Date data_pv = new Date();

        for (int i = 0; i < dados.size(); i++) {
            String id = Integer.toString(dados.get(i).getIdPessoa());
            String dataFinal = "";

            if (dados.get(i).getFinalPlano() != null) {
                data_fp = formatter.parse(dados.get(i).getFinalPlano());
                dataFinal = dados.get(i).getFinalPlano();
            }
            if (dados.get(i).getDt_proxVencimento() != null) {
                data_pv = formatter.parse(dados.get(i).getDt_proxVencimento());

                if (data_pv.before(data_fp)) {
                    dataFinal = dados.get(i).getDt_proxVencimento();
                } else {
                    dataFinal = dados.get(i).getFinalPlano();
                }
            }

            dts.put(id, dataFinal);

        }

        return dts;
    }

    private void setIcon() {
        if (!SystemTray.isSupported()) {
            System.exit(0);
            return;
        }
        Image image = (Toolkit.getDefaultToolkit().getImage(getClass().getResource("img/catraca.png")));
        PopupMenu popup = new PopupMenu();

        ActionListener fecharCatraca = (ActionEvent e) -> {
            int i = JOptionPane.showConfirmDialog(null, "Deseja Fechar a Catraca?", "UpAdmin GYM", JOptionPane.YES_NO_OPTION);
            if (i == JOptionPane.YES_OPTION) {

                System.exit(0);
            }
        };

        ActionListener atualizaDados = (ActionEvent e) -> {
            try {
                acesso();
            } catch (FileNotFoundException | SQLException | ParseException ex) {
                Logger.getLogger(Monitor.class.getName()).log(Level.SEVERE, null, ex);
            }
        };

        ActionListener liberaCatraca = (ActionEvent e) -> {
            ControlIdFace control = new ControlIdFace();
            control.session();
            control.liberarCatraca();
        };

        ActionListener zeraTerminal = (ActionEvent e) -> {
            JFrame password = new JFrame();
            password.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("img/UpAdminIco.png")));
            password.setSize(5, 100);
            password.setVisible(true);
            password.setResizable(false);
            password.setLocationRelativeTo(null);
            password.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            JPasswordField pass = new JPasswordField(10);
            password.add(pass);
            pass.addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent e) {
                    int key = e.getKeyCode();
                    if (key == KeyEvent.VK_ENTER) {
                        Toolkit.getDefaultToolkit().beep();
                        char[] sen = pass.getPassword();
                        char[] senStr = {'2', '2', '0', '2'};

                        if (Arrays.equals(sen, senStr)) {
                            try {
                                int i = JOptionPane.showConfirmDialog(null, "Todos usuário serão apagados, tem certeza?", "UpAdmin GYM", JOptionPane.YES_NO_OPTION);
                                if (i == JOptionPane.YES_OPTION) {

                                    new DadosDao().updateCadastros();
                                    ControlIdFace control = new ControlIdFace();
                                    control.session();
                                    control.excluirObjUserCompleto();
                                    password.setVisible(false);

                                }
                            } catch (FileNotFoundException | SQLException ex) {
                                Logger.getLogger(Monitor.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "Senha Errada!!!", "Senha...", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                }

            });
        };

        MenuItem liberarCatraca = new MenuItem("Liberar Catraca");
        liberarCatraca.addActionListener(liberaCatraca);
        popup.add(liberarCatraca);

        MenuItem acesso = new MenuItem("Atualizar Dados");
        acesso.addActionListener(atualizaDados);
        popup.add(acesso);

        MenuItem fechar = new MenuItem("Fechar Catraca");
        fechar.addActionListener(fecharCatraca);
        popup.add(fechar);

        MenuItem limparTerminal = new MenuItem("Limpar Terminal");
        limparTerminal.addActionListener(zeraTerminal);
        popup.add(limparTerminal);

        TrayIcon trayIcon;

        if (ip != null) {
            trayIcon = new TrayIcon(image, "| UpAdmin 2.0.23 | " + ip, popup);
        } else {
            trayIcon = new TrayIcon(image, "| UpAdmin 2.0.23 | ONLine>>>", popup);
        }

        trayIcon.setImageAutoSize(true);
        SystemTray tray = SystemTray.getSystemTray();

        try {
            tray.add(trayIcon);

        } catch (AWTException e) {

        }
    }

    //INSERE A FREQUENCIA NO UPADMIN
    public void postFrequencia(int idPessoa, String ponto, int idMensagem, String mensagem) throws IOException {

        String key = "ApiKey";
        String value = "3b953031-ec18-11ec-b3f7-020000b5a280==1977";

        ControlIdFace control = new ControlIdFace();
        control.jCatraca();

        String apiIntegracao = (String) control.jCatraca().get("apiIntegracao");

        URL url = new URL(apiIntegracao + "IntegracaoCatraca");

        http = (HttpURLConnection) url.openConnection();

        http.setRequestProperty(key, value);
        http.setRequestMethod("POST");
        http.setDoOutput(true);
        http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        http.connect();

        ponto = ponto.replace(" ", "T") + "Z";

        String data = "{\n"
                + "  \"idPessoa\": " + idPessoa + ",\n"
                + "  \"ponto\": \"" + ponto + "\",\n"
                + "  \"idMensagem\": " + idMensagem + ",\n"
                + "  \"mensagem\": \"" + mensagem + "\"\n"
                + "}";

        byte[] out = data.getBytes(StandardCharsets.UTF_8);

        OutputStream stream = http.getOutputStream();
        stream.write(out);
        stream.flush();

        int responseCode = http.getResponseCode();

        System.out.println("Response Code : " + responseCode);
        http.disconnect();
    }

    public void acesso() throws FileNotFoundException, SQLException, ParseException {
        new Thread() {

            @Override
            public void run() {

                try {

                    dados = Dados();

                    ControlIdFace controlId = new ControlIdFace();
                    controlId.session();
                    Instant beginInst = Instant.parse("2023-01-01T00:00:00.00Z");
                    long begin = beginInst.getEpochSecond();
                    long end = 0;

                    BarProgress bar = new BarProgress(dados.size());
                    bar.setVisible(true);

                    for (int i = 0; i < dados.size(); i++) {
                        bar.bar().setValue(i);

                        int idPessoa = dados.get(i).getIdPessoa();
                        int tpCad = dados.get(i).getIdTipoPessoa();
                        int limitarHorario = dados.get(i).getLimitarHorario();
                        String data_fp = dados.get(i).getFinalPlano();
                        String data_pv = dados.get(i).getDt_proxVencimento();
                        String hr_De = dados.get(i).getHr_De();
                        String hr_Ate = dados.get(i).getHr_Ate();

                        Date dataFp = new Date();
                        Date dataPv = new Date();

//                        controlId.cadastroGrupoUsuario(idPessoa);//provissorio | Cadastra o group 1 em todos os usuarios
                        end = 0;

                        if (tpCad == 1) {
                            if (limitarHorario == 1) {
                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                                Date date = new Date();
                                String dataHoje = formatter.format(date);

                                beginInst = Instant.parse(dataHoje + "T" + hr_De + ":00.00Z");
                                begin = beginInst.getEpochSecond();

                                Instant endInst = Instant.parse(dataHoje + "T" + hr_Ate + ":59.00Z");
                                end = endInst.getEpochSecond();

                            } else {
                                if (data_fp != null) {
                                    Instant endInst = Instant.parse(data_fp + "T23:59:59.00Z");
                                    end = endInst.getEpochSecond();

                                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                                    dataFp = formatter.parse(data_fp);

                                }
                                if (data_pv != null) {
                                    Instant endInst = Instant.parse(data_pv + "T23:59:59.00Z");
                                    end = endInst.getEpochSecond();

                                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                                    dataPv = formatter.parse(data_pv);

                                    if (dataPv.before(dataFp)) {
                                        end = endInst.getEpochSecond();
                                    }
                                }
                                if (data_fp == null && data_pv == null) {
                                    end = begin;
                                }
                            }

                            controlId.updateInicialFinal(begin, end, idPessoa);
                        }

                    }

                    bar.setVisible(false);
                    JOptionPane.showMessageDialog(null, "Carga enviada com sucesso!!!", "UpAdmin", JOptionPane.INFORMATION_MESSAGE);

                } catch (Exception e) {

                }

            }
        }.start();

    }

    //CARREGA LISTA DE DADOS DAS PESSOAS 
    public List<Dados> Dados() throws FileNotFoundException, SQLException {
        DadosDao dadodao = new DadosDao();
        List<Dados> dados = dadodao.listar();

        return dados;
    }

    //fora ate ajustes muito lento
    public boolean DadosAcessoPassagem(int id) throws FileNotFoundException, SQLException {
        DadosDao dadodao = new DadosDao();
        List<Dados> dadosPassagem = dadodao.listarAcessoPorPassagem(id);

        boolean res = true;

        if (!dadosPassagem.isEmpty()) {

            int n_passagem = dadosPassagem.get(0).getNumPassagem();

            if (n_passagem * 2 <= dadosPassagem.size()) {
                res = false;
            }
        }

        return res;
    }

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Monitor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Monitor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Monitor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Monitor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Monitor monitor = new Monitor();
                    monitor.server();

                } catch (IOException | SQLException | ParseException ex) {
                    Logger.getLogger(Monitor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
