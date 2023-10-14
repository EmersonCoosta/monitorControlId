/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package monitorcontrolid;

/**
 *
 * @author Emerson
 */
public class Dados {
    //VERIFICA ACESSO POR PASSAGEM
    private int id;
    private int numPassagem;
    private int tipoPassagem;
    //VERIFICA DATA DO PROX VENCIMENTO OU FINAL DO PLANO E ACESSO POR HORARIO
    private int idPessoa;
    private int idTipoPessoa;
    private String dt_proxVencimento;
    private String FinalPlano;
    private int limitarHorario;
    private String Hr_De;
    private String Hr_Ate;

    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumPassagem() {
        return numPassagem;
    }

    public void setNumPassagem(int numPassagem) {
        this.numPassagem = numPassagem;
    }

    public int getTipoPassagem() {
        return tipoPassagem;
    }

    public void setTipoPassagem(int tipoPassagem) {
        this.tipoPassagem = tipoPassagem;
    }

    public int getIdPessoa() {
        return idPessoa;
    }

    public void setIdPessoa(int idPessoa) {
        this.idPessoa = idPessoa;
    }

    public int getIdTipoPessoa() {
        return idTipoPessoa;
    }

    public void setIdTipoPessoa(int idTipoPessoa) {
        this.idTipoPessoa = idTipoPessoa;
    }

    public String getDt_proxVencimento() {
        return dt_proxVencimento;
    }

    public void setDt_proxVencimento(String dt_proxVencimento) {
        this.dt_proxVencimento = dt_proxVencimento;
    }

    public String getFinalPlano() {
        return FinalPlano;
    }

    public void setFinalPlano(String FinalPlano) {
        this.FinalPlano = FinalPlano;
    }

    public int getLimitarHorario() {
        return limitarHorario;
    }

    public void setLimitarHorario(int limitarHorario) {
        this.limitarHorario = limitarHorario;
    }

    public String getHr_De() {
        return Hr_De;
    }

    public void setHr_De(String Hr_De) {
        this.Hr_De = Hr_De;
    }

    public String getHr_Ate() {
        return Hr_Ate;
    }

    public void setHr_Ate(String Hr_Ate) {
        this.Hr_Ate = Hr_Ate;
    }
    
    

}
