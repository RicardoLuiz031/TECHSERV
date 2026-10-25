import model.Chamados;
import Repository.Chamados.ChamadosDAO;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {

    private static Scanner scanner = new Scanner(System.in);
    private static ChamadosDAO dao;

    public static void main(String[] args) {
        try {
            dao = new ChamadosDAO();
        } catch (SQLException e) {
            System.err.println("Erro ao conectar ao banco de dados: " + e.getMessage());
            System.err.println("Verifique se o MySQL está ativo e a base 'TECHSERV' existe.");
            return;
        }

        int opcao = -1;

        do {
            exibirMenu();
            try {
                System.out.print("Escolha uma opção: ");
                String entrada = scanner.nextLine();
                opcao = Integer.parseInt(entrada);

                switch (opcao) {
                    case 1:
                        cadastrarChamado();
                        break;
                    case 2:
                        listarTodosChamados();
                        break;
                    case 3:
                        consultarChamadoPorId();
                        break;
                    case 4:
                        atualizarStatus();
                        break;
                    case 5:
                        filtrarChamados();
                        break;
                    case 6:
                        exibirResumo();
                        break;
                    case 0:
                        System.out.println("\nEncerrando o sistema. Até logo!");
                        break;
                    default:
                        System.out.println("\n[!] Opção inválida. Escolha um número de 0 a 6.");
                }
            } catch (NumberFormatException e) {
                System.out.println("\n[!] Entrada inválida. Por favor, digite apenas números.");
            } catch (SQLException e) {
                System.out.println("\n[!] Erro na operação com o banco de dados: " + e.getMessage());
            }

            if (opcao != 0) {
                System.out.println("\nPressione ENTER para continuar...");
                scanner.nextLine();
            }

        } while (opcao != 0);
    }

    private static void exibirMenu() {
        System.out.println("\n==================================================");
        System.out.println("          TECHSERV - CONTROLE DE CHAMADOS          ");
        System.out.println("==================================================");
        System.out.println("1 - Cadastrar Chamado");
        System.out.println("2 - Listar Todos os Chamados");
        System.out.println("3 - Consultar Chamado por ID");
        System.out.println("4 - Atualizar Status do Chamado");
        System.out.println("5 - Filtrar Chamados (Prioridade / Status)");
        System.out.println("6 - Resumo por Status");
        System.out.println("0 - Sair");
        System.out.println("==================================================");
    }

    // 1. CADASTRAR CHAMADO (T01, T02, T03, T04)
    private static void cadastrarChamado() throws SQLException {
        System.out.println("\n--- CADASTRAR CHAMADO ---");

        // Validação do Solicitante (T02)
        System.out.print("Solicitante: ");
        String solicitante = scanner.nextLine();
        if (solicitante.trim().length() < 3) {
            System.out.println("[Erro] O solicitante deve ter pelo menos 3 caracteres válidos.");
            return;
        }

        // Validação da Descrição (T03)
        System.out.print("Descrição do problema: ");
        String descricao = scanner.nextLine();
        if (descricao.trim().length() < 10) {
            System.out.println("[Erro] A descrição deve ter pelo menos 10 caracteres.");
            return;
        }

        // Validação da Prioridade (T04)
        System.out.print("Prioridade (baixa, media, alta): ");
        String prioridade = scanner.nextLine().trim().toLowerCase();
        if (!prioridade.equals("baixa") && !prioridade.equals("media") && !prioridade.equals("média") && !prioridade.equals("alta")) {
            System.out.println("[Erro] Prioridade inválida. Permite-se apenas: baixa, média ou alta.");
            return;
        }

        // Ajusta acentuação padrão
        if (prioridade.equals("media")) {
            prioridade = "média";
        }

        Chamados novoChamado = new Chamados(solicitante.trim(), descricao.trim(), prioridade);
        dao.inserirSolicitacao(novoChamado);
    }

    // 2. LISTAR CHAMADOS
    private static void listarTodosChamados() throws SQLException {
        System.out.println("\n--- LISTA DE CHAMADOS ---");
        List<Chamados> lista = dao.listarChamados();

        if (lista.isEmpty()) {
            System.out.println("Nenhum chamado cadastrado até o momento.");
            return;
        }

        imprimirCabecalhoTabela();
        for (Chamados c : lista) {
            imprimirLinhaChamado(c);
        }
    }

    // 3. CONSULTAR POR ID (T05)
    private static void consultarChamadoPorId() throws SQLException {
        System.out.println("\n--- CONSULTAR CHAMADO ---");
        System.out.print("Informe o ID do chamado: ");

        try {
            int id = Integer.parseInt(scanner.nextLine());
            Chamados chamado = dao.buscarPorId(id);

            if (chamado == null) {
                System.out.println("[!] Chamado com ID " + id + " não foi encontrado.");
            } else {
                imprimirCabecalhoTabela();
                imprimirLinhaChamado(chamado);
            }
        } catch (NumberFormatException e) {
            System.out.println("[!] ID deve ser um número inteiro.");
        }
    }

    // 4. ATUALIZAR STATUS (T06, T07, T08)
    private static void atualizarStatus() throws SQLException {
        System.out.println("\n--- ATUALIZAR STATUS DE CHAMADO ---");
        System.out.print("Informe o ID do chamado: ");

        try {
            int id = Integer.parseInt(scanner.nextLine());
            Chamados chamado = dao.buscarPorId(id);

            if (chamado == null) {
                System.out.println("[!] Chamado com ID " + id + " não foi encontrado.");
                return;
            }

            System.out.println("Status atual: [" + chamado.getStatus() + "]");
            System.out.print("Informe o novo status (em atendimento / concluido): ");
            String novoStatus = scanner.nextLine().trim();

            dao.atualizarStatus(id, novoStatus);

        } catch (NumberFormatException e) {
            System.out.println("[!] ID deve ser um número inteiro.");
        }
    }

    // 5. FILTRAR CHAMADOS
    private static void filtrarChamados() throws SQLException {
        System.out.println("\n--- FILTRAR CHAMADOS ---");
        System.out.print("Filtrar por Prioridade (baixa/média/alta ou Deixe em branco para ignorar): ");
        String prioridade = scanner.nextLine().trim();

        System.out.print("Filtrar por Status (aberto/em atendimento/concluido ou Deixe em branco): ");
        String status = scanner.nextLine().trim();

        List<Chamados> lista = dao.filtrarChamados(prioridade, status);

        if (lista.isEmpty()) {
            System.out.println("Nenhum chamado encontrado para os filtros informados.");
            return;
        }

        imprimirCabecalhoTabela();
        for (Chamados c : lista) {
            imprimirLinhaChamado(c);
        }
    }

    // 6. APRESENTAR RESUMO (T10)
    private static void exibirResumo() throws SQLException {
        System.out.println("\n--- RESUMO DE CHAMADOS POR STATUS ---");
        Map<String, Integer> resumo = dao.obterResumoPorStatus();

        int abertos = resumo.getOrDefault("aberto", 0);
        int emAtendimento = resumo.getOrDefault("em atendimento", 0) + resumo.getOrDefault("em_atendimento", 0);
        int concluidos = resumo.getOrDefault("concluido", 0) + resumo.getOrDefault("concluído", 0);

        System.out.println("• Abertos:        " + abertos);
        System.out.println("• Em atendimento: " + emAtendimento);
        System.out.println("• Concluídos:     " + concluidos);
        System.out.println("------------------------------------");
        System.out.println("Total registrado: " + (abertos + emAtendimento + concluidos));
    }

    // MÉTODOS AUXILIARES DE FORMATAÇÃO
    private static void imprimirCabecalhoTabela() {
        System.out.printf("%-5s | %-15s | %-12s | %-15s | %-30s | %-19s%n",
                "ID", "Solicitante", "Prioridade", "Status", "Descrição", "Data Abertura");
        System.out.println("---------------------------------------------------------------------------------------------------");
    }

    private static void imprimirLinhaChamado(Chamados c) {
        String desc = c.getDescricao();
        if (desc != null && desc.length() > 27) {
            desc = desc.substring(0, 27) + "...";
        }
        System.out.printf("%-5d | %-15s | %-12s | %-15s | %-30s | %-19s%n",
                c.getId(),
                c.getSolicitante(),
                c.getPrioridade(),
                c.getStatus(),
                desc,
                c.getDataAbertura() != null ? c.getDataAbertura() : "N/A");
    }
}