package Repository.Chamados;

import model.Chamados;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class ChamadosDAO {

    private Connection conexao;

    public ChamadosDAO() throws SQLException {
        // Conexão com o banco de dados TECHSERV
        this.conexao = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/TECHSERV",
                "root",
                ""
        );
    }

    // 1. CADASTRAR CHAMADO (T01, T02, T03, T04)
    public void inserirSolicitacao(Chamados c) throws SQLException {
        String sql = "INSERT INTO chamados (solicitante, descricao, prioridade) VALUES (?, ?, ?)";

        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setString(1, c.getSolicitante());
            ps.setString(2, c.getDescricao());
            ps.setString(3, c.getPrioridade());

            ps.executeUpdate();
            System.out.println("Chamado registrado com sucesso!");
        }
    }

    // 2. LISTAR TODOS OS CHAMADOS
    public List<Chamados> listarChamados() throws SQLException {
        List<Chamados> lista = new ArrayList<>();
        String sql = "SELECT * FROM chamados";

        try (PreparedStatement ps = conexao.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(instanciarChamado(rs));
            }
        }
        return lista;
    }

    // 3. CONSULTAR CHAMADO POR ID (T05)
    public Chamados buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM chamados WHERE id_chamados = ?";
        Chamados c = null;

        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    c = instanciarChamado(rs);
                }
            }
        }
        return c;
    }

    // 4. ATUALIZAR STATUS COM VALIDAÇÃO DE TRANSIÇÃO (T06, T07, T08)
    public boolean atualizarStatus(int id, String novoStatus) throws SQLException {
        Chamados chamadoAtual = buscarPorId(id);

        if (chamadoAtual == null) {
            System.out.println("Erro: Chamado não encontrado.");
            return false;
        }

        String statusAtual = chamadoAtual.getStatus();
        boolean transicaoValida = false;

        // Comparações ajustadas para cobrir variações com/sem acento e caixa alta/baixa
        if ("aberto".equalsIgnoreCase(statusAtual) && ("em atendimento".equalsIgnoreCase(novoStatus) || "em_atendimento".equalsIgnoreCase(novoStatus))) {
            transicaoValida = true;
        } else if (("em atendimento".equalsIgnoreCase(statusAtual) || "em_atendimento".equalsIgnoreCase(statusAtual))
                && ("concluido".equalsIgnoreCase(novoStatus) || "concluído".equalsIgnoreCase(novoStatus))) {
            transicaoValida = true;
        }

        if (!transicaoValida) {
            System.out.println("Erro: Transição inválida! Não é permitido alterar de '"
                    + statusAtual + "' para '" + novoStatus + "'.");
            return false;
        }

        String sql = "UPDATE chamados SET status = ? WHERE id_chamados = ?";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setString(1, novoStatus.toLowerCase());
            ps.setInt(2, id);
            ps.executeUpdate();
            System.out.println("Status atualizado com sucesso!");
            return true;
        }
    }

    // 5. FILTRAR CHAMADOS POR PRIORIDADE OU STATUS (T10)
    public List<Chamados> filtrarChamados(String prioridade, String status) throws SQLException {
        List<Chamados> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM chamados WHERE 1=1");

        if (prioridade != null && !prioridade.trim().isEmpty()) {
            sql.append(" AND prioridade = ?");
        }
        if (status != null && !status.trim().isEmpty()) {
            sql.append(" AND status = ?");
        }

        try (PreparedStatement ps = conexao.prepareStatement(sql.toString())) {
            int paramIndex = 1;

            if (prioridade != null && !prioridade.trim().isEmpty()) {
                ps.setString(paramIndex++, prioridade.toLowerCase());
            }
            if (status != null && !status.trim().isEmpty()) {
                ps.setString(paramIndex++, status.toLowerCase());
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(instanciarChamado(rs));
                }
            }
        }
        return lista;
    }

    // 6. APRESENTAR RESUMO POR STATUS (T10)
    public Map<String, Integer> obterResumoPorStatus() throws SQLException {
        Map<String, Integer> resumo = new HashMap<>();
        resumo.put("aberto", 0);
        resumo.put("em atendimento", 0);
        resumo.put("concluido", 0);

        String sql = "SELECT status, COUNT(*) as total FROM chamados GROUP BY status";

        try (PreparedStatement ps = conexao.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String status = rs.getString("status");
                int total = rs.getInt("total");
                resumo.put(status, total);
            }
        }
        return resumo;
    }

    // MÉTODO AUXILIAR: Converte o registro do ResultSet em um objeto Chamados
    private Chamados instanciarChamado(ResultSet rs) throws SQLException {
        Chamados c = new Chamados();
        c.setId(rs.getInt("id_chamados"));
        c.setSolicitante(rs.getString("solicitante"));
        c.setDescricao(rs.getString("descricao"));
        c.setPrioridade(rs.getString("prioridade"));
        c.setStatus(rs.getString("status"));

        Timestamp timestamp = rs.getTimestamp("data_abertura");
        if (timestamp != null) {
            // Suporta tanto dataAbertura quanto data_abertura no modelo
            c.setDataAbertura(timestamp.toString());
        }

        return c;
    }
}