/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package monitorcontrolid;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Emerson
 */
public class DadosDao {

    public static String servidor_mysql;
    public static String port_mysql;
    public static String path_mysql;
    private JSONObject jsonObject = null;
    private final JSONParser parser = new JSONParser();

    public List<Dados> listar() throws FileNotFoundException, SQLException {

        List<Dados> dados = new ArrayList<>();

        try {
            mysql();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DadosDao.class.getName()).log(Level.SEVERE, null, ex);
        }

        PreparedStatement pstm = null;
        ResultSet rs = null;

        String sql = "select\n"
                + "pe.id as idPessoa,\n"
                + "pe.IdTipoPessoa,\n"
                + "pr.dt_proxVencimento, \n"
                + "IF(pl.IdPeriodo =  1, (select max(date_add(c.DataVencimento, interval pl.PeriodoDoPlano day))),\n"
                + " (\n"
                + "   select  case pl.parceladoEm \n"
                + "     when '1'  then max(date_add(c.DataVencimento, interval  pl.PeriodoDoPlano     month))\n"
                + "     when '2'  then max(date_add(c.DataVencimento, interval  pl.PeriodoDoPlano -1  month))\n"
                + "     when '3'  then max(date_add(c.DataVencimento, interval  pl.PeriodoDoPlano -2  month))\n"
                + "     when '4'  then max(date_add(c.DataVencimento, interval  pl.PeriodoDoPlano -3  month))\n"
                + "     when '5'  then max(date_add(c.DataVencimento, interval  pl.PeriodoDoPlano -4  month))\n"
                + "     when '6'  then max(date_add(c.DataVencimento, interval  pl.PeriodoDoPlano -5  month))\n"
                + "     when '7'  then max(date_add(c.DataVencimento, interval  pl.PeriodoDoPlano -6  month))\n"
                + "     when '8'  then max(date_add(c.DataVencimento, interval  pl.PeriodoDoPlano -7  month))\n"
                + "     when '9'  then max(date_add(c.DataVencimento, interval  pl.PeriodoDoPlano -8  month))\n"
                + "     when '10' then max(date_add(c.DataVencimento, interval  pl.PeriodoDoPlano -9  month))\n"
                + "     when '11' then max(date_add(c.DataVencimento, interval  pl.PeriodoDoPlano -10 month))\n"
                + "     when '12' then max(date_add(c.DataVencimento, interval  pl.PeriodoDoPlano -11 month))\n"
                + "     end \n"
                + "  )\n"
                + ") FinalPlano,\n"
                + "pl.limitarHorario,\n"
                + "(select ph.Hr_De from planohorario  where Dia = (CASE WEEKDAY(current_date()) \n"
                + "                       when 0 then 'SEGUNDA'\n"
                + "                       when 1 then 'TERCA'\n"
                + "                       when 2 then 'QUARTA'\n"
                + "                       when 3 then 'QUINTA'\n"
                + "                       when 4 then 'SEXTA'\n"
                + "                       when 5 then 'SABADO'\n"
                + "                       when 6 then 'DOMINGO'                 \n"
                + "                       END)  ) Hr_De,\n"
                + "(select ph.Hr_Ate from planohorario  where Dia = (CASE WEEKDAY(current_date()) \n"
                + "                       when 0 then 'SEGUNDA'\n"
                + "                       when 1 then 'TERCA'\n"
                + "                       when 2 then 'QUARTA'\n"
                + "                       when 3 then 'QUINTA'\n"
                + "                       when 4 then 'SEXTA'\n"
                + "                       when 5 then 'SABADO'\n"
                + "                       when 6 then 'DOMINGO'                 \n"
                + "                       END)  ) Hr_Ate\n"
                + "FROM pessoa pe\n"
                + "LEFT JOIN contasReceber c\n"
                + "ON PE.Id = c.idPessoa  \n"
                + "left join proxvencimento pr\n"
                + "on pe.Id = pr.idPessoa  \n"
                + "LEFT JOIN Plano pl\n"
                + "ON pe.IdPlano = pl.id\n"
                + "left join planohorario ph\n"
                + "on pl.Id = ph.IdPlano \n"
                + "WHERE PE.DataDeletou is null\n"
                + "and pe.IdSituacao = 1\n"
                + "and pe.id > 2\n"
                + "group by pe.id";

        try {
            pstm = mysql().prepareStatement(sql);
            rs = pstm.executeQuery();

            while (rs.next()) {
                Dados dado = new Dados();

                dado.setIdPessoa(rs.getInt(1));
                dado.setIdTipoPessoa(rs.getInt(2));
                dado.setDt_proxVencimento(rs.getString(3));
                dado.setFinalPlano(rs.getString(4));
                dado.setLimitarHorario(rs.getInt(5));
                dado.setHr_De(rs.getString(6));
                dado.setHr_Ate(rs.getString(7));

                dados.add(dado);
            }

        } catch (SQLException ErroSql) {
            JOptionPane.showMessageDialog(null, "Erro ao listar dados: " + ErroSql, "Erro", JOptionPane.ERROR_MESSAGE);
        } finally {
            mysql().close();
        }

        return dados;
    }

    public List<Dados> listarPassagem() {

        List<Dados> dadosPassagem = new ArrayList<>();

        return dadosPassagem;
    }

    public Connection mysql() throws FileNotFoundException {
        servidor_mysql = (String) paramJson().get("servidor_mysql");
        port_mysql = (String) paramJson().get("port_mysql");
        path_mysql = (String) paramJson().get("bd_mysql");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            /* Aqui registra */
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://" + servidor_mysql + ":" + port_mysql + "/" + path_mysql + "?autoReconnect=true&useTimezone=true&serverTimezone=UTC", "emerson", "Jj2201@0205");

            return conn;

        } catch (ClassNotFoundException | SQLException ErroSql) {
            JOptionPane.showMessageDialog(null, "Erro ao conectar ao banco de Dados:\n" + ErroSql);
            System.exit(0);
        }
        return null;
    }

    public List<Dados> listarAcessoPorPassagem(int id) throws FileNotFoundException, SQLException {

        List<Dados> dados = new ArrayList<>();

        try {
            mysql();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DadosDao.class.getName()).log(Level.SEVERE, null, ex);
        }

        PreparedStatement pstm = null;
        ResultSet rs = null;

        String sql = "SELECT\n"
                + "p.id,\n"
                + "pl.numPassagem ,\n"
                + "pl.tipoPassagem \n"
                + "from pessoa p\n"
                + "inner join plano pl\n"
                + "on p.IdPlano  = pl.Id \n"
                + "inner join frequencia fr\n"
                + "on p.Id  = fr.IdPessoa \n"
                + "where p.DataDeletou is null\n"
                + "and p.IdSituacao  = 1\n"
                + "and pl.limitarPassagem  = 1\n"
                + "and (case pl.tipoPassagem \n"
                + "    	when 0 then fr.Ponto \n"
                + "        when 1 then Extract(Week from fr.Ponto)\n"
                + "        when 2 then Extract(month from fr.Ponto)\n"
                + "          end) >=\n"
                + "        (case pl.tipoPassagem\n"
                + "           when 0 then current_date\n"
                + "           when 1 then Extract(Week from current_date)\n"
                + "           when 2 then Extract(month from current_date)\n"
                + "              end)\n"
                + "and Extract(year from fr.ponto) >= Extract(year from current_date)\n"
                + "and fr.mensagem LIKE '%Passagem Liberada!%'\n"
                + "and p.Id  = " + id + "";

        try {
            pstm = mysql().prepareStatement(sql);
            rs = pstm.executeQuery();

            while (rs.next()) {
                Dados dado = new Dados();

                dado.setId(rs.getInt(1));
                dado.setNumPassagem(rs.getInt(2));
                dado.setTipoPassagem(rs.getInt(3));

                dados.add(dado);
            }

        } catch (SQLException ErroSql) {
            JOptionPane.showMessageDialog(null, "Erro ao listar dados: " + ErroSql, "Erro", JOptionPane.ERROR_MESSAGE);
        } finally {
            mysql().close();
        }

        return dados;
    }

    public void updateCadastros() throws FileNotFoundException, SQLException {

        try {
            mysql();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DadosDao.class.getName()).log(Level.SEVERE, null, ex);
        }

        PreparedStatement pstm = null;

        String sql = "update pessoa \n"
                + "set PessoaExportada = null,\n"
                + "	GrupoExportado = null,\n"
                + "	FotoExportada = null \n"
                + "where PessoaExportada is not null";

        try {
            pstm = mysql().prepareStatement(sql);
            pstm.executeUpdate();

        } catch (SQLException ErroSql) {
            JOptionPane.showMessageDialog(null, "Erro Update: " + ErroSql, "Erro", JOptionPane.ERROR_MESSAGE);
        } finally {
            mysql().close();
        }
    }

    public JSONObject paramJson() throws FileNotFoundException {
        try {
            //Salva no objeto JSONObject o que o parse tratou do arquivo
            jsonObject = (JSONObject) parser.parse(new FileReader("C:\\UpAdminGym\\param.json"));
        } catch (ParseException ex) {
        } catch (IOException ex) {
            Logger.getLogger(DadosDao.class.getName()).log(Level.SEVERE, null, ex);
        }

        return jsonObject;
    }
}
