package model;

public class Chamados {

    private int id;
    private String solicitante;
    private String descricao;
    private String prioridade;
    private String status;
    private String dataAbertura;

    // Construtor vazio
    public Chamados() {
    }

    // Construtor completo
    public Chamados(int id, String solicitante, String descricao, String prioridade, String status, String dataAbertura) {
        this.id = id;
        this.solicitante = solicitante;
        this.descricao = descricao;
        this.prioridade = prioridade;
        this.status = status;
        this.dataAbertura = dataAbertura;
    }

    // Construtor para novos cadastros (Inicializa o status como 'aberto')
    public Chamados(String solicitante, String descricao, String prioridade) {
        this.solicitante = solicitante;
        this.descricao = descricao;
        this.prioridade = prioridade;
        this.status = "aberto";
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(String solicitante) {
        this.solicitante = solicitante;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(String prioridade) {
        this.prioridade = prioridade;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDataAbertura() {
        return dataAbertura;
    }

    public void setDataAbertura(String dataAbertura) {
        this.dataAbertura = dataAbertura;
    }
}