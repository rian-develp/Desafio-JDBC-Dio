package org.example;

import org.example.config.ConnectionConfig;
import org.example.persistence.entity.board.BoardEntity;
import org.example.persistence.entity.board_column.BoardColumnEntity;
import org.example.persistence.entity.board_column.BoardColumnKindEnum;
import org.example.service.board.BoardQueryService;
import org.example.service.board.BoardService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.example.config.ConnectionConfig.getConnection;
import static org.example.persistence.entity.board_column.BoardColumnKindEnum.*;

public class MainMenu {

    private final Scanner scanner = new Scanner(System.in);

    public void execute() throws SQLException {
        System.out.println("Bem vindo ao gerenciador de boards, escolhe a opção desejada: ");
        int options = -1;

        while (options != 9){
            System.out.println("1 - Criar um card");
            System.out.println("2 - Mover um card");
            System.out.println("3 - Bloquear um card");
            System.out.println("4 - Desbloquear um card");
            System.out.println("5 - Cancelar um card");
            System.out.println("6 - Visualizar boards");
            System.out.println("7 - Visualizar coluna com card");
            System.out.println("8 - Visualizar card");
            System.out.println("9 - Voltar para o menu anterior um card");
            System.out.println("10 - Sair");
            options = scanner.nextInt();
            switch (options){
                case 1 -> createBoard();
                case 2 -> selectBoard();
                case 3-> deleteBoard();
                case 4 -> System.out.println("Voltando ao menu anterior...");
                case 5 -> System.exit(0);

                default -> System.out.println("Opção Inválida");
            }
        }
    }

    private void deleteBoard() throws SQLException{
        System.out.println("Informe o id do board que será deletado");
        var id = scanner.nextInt();
        try(var connection = getConnection()){
            var service = new BoardService(connection);
            if (service.delete(id)){
                System.out.printf("O board %s foi excluído \n", id);
            } else {
                System.out.printf("Não encontrado um board com o id %s\n", id);
            }
        }
    }

    private void selectBoard() throws SQLException {
        System.out.println("Informe o id do board que deseja selecionar: ");
        var selectedBoardId = scanner.nextLong();

        try(var connection = ConnectionConfig.getConnection()){
            var queryService = new BoardQueryService(connection);
            var optional = queryService.findByBoardId(selectedBoardId);
            optional.ifPresentOrElse(b -> new BoardMenu(b).execute(),
                    () -> System.out.printf("Não foi encontrado um board com esse id %s\n", selectedBoardId)
            );
        }
    }

    private void createBoard() throws SQLException {
        var entity = new BoardEntity();
        System.out.println("Insira o nome do seu board");
        entity.setName(scanner.next());

        System.out.println("Seu board terá colunas além de 3 padrões? Se sim digite quantas, senão digite '0' ");
        var adicionalColumns = scanner.nextInt();

        List<BoardColumnEntity> columns = new ArrayList<>();

        System.out.println("Informe o nome da coluna inicial do board");
        var initialColumnName = scanner.next();
        var initialColumn = createColumn(initialColumnName, INITIAL, 0);
        columns.add(initialColumn);

        for (int i = 0; i < adicionalColumns; i++) {
            System.out.println("Informe o nome da coluna pendente: ");
            var pendingColumnName = scanner.next();
            var pendingColumn = createColumn(pendingColumnName, PENDING, i+1);
            columns.add(pendingColumn);
        }

        System.out.println("Informe o nome da coluna final");
        var finalColumnName = scanner.next();
        var finalColumn = createColumn(finalColumnName, FINAL, adicionalColumns + 1);
        columns.add(finalColumn);

        System.out.println("Informe o nome da coluna de cancelamento do board");
        var cancelColumnName = scanner.next();
        var cancelColumn = createColumn(cancelColumnName, CANCEL, adicionalColumns + 2);
        columns.add(cancelColumn);

        entity.setBoardColumn(columns);

        try(var connection = getConnection()){
            var service = new BoardService(connection);
            service.insert(entity);
        }
    }


    private BoardColumnEntity createColumn(String name, BoardColumnKindEnum kind, int order){
        var entity = new BoardColumnEntity();
        entity.setName(name);
        entity.setKind(kind);
        entity.setOrder(order);
        return entity;
    }

}
