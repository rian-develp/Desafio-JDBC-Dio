package org.example;

import org.example.dto.BoardColumnInfoDTO;
import org.example.excptions.BlockedCardException;
import org.example.excptions.EntityNotFoundException;
import org.example.persistence.entity.board.BoardEntity;
import org.example.persistence.entity.board_column.BoardColumnEntity;
import org.example.persistence.entity.card.CardEntity;
import org.example.service.board.BoardQueryService;
import org.example.service.board_column.BoardColumnQueryService;
import org.example.service.cards.CardQueryService;
import org.example.service.cards.CardService;

import java.sql.SQLException;
import java.util.Scanner;

import static org.example.config.ConnectionConfig.getConnection;
import static org.example.persistence.entity.board_column.BoardColumnKindEnum.INITIAL;

public class BoardMenu {

    private final BoardEntity boardEntity;
    private final Scanner scanner = new Scanner(System.in);

    public BoardMenu(BoardEntity boardEntity) {
        this.boardEntity = boardEntity;
    }


    public BoardEntity getBoardEntity() {
        return boardEntity;
    }


    public void execute(){

        try{
            System.out.printf("Bem vindo ao board %s, escolhe a opção desejada:\n", boardEntity.getId());
            int options = -1;

            while (options != 9) {
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
                switch (options) {
                    case 1 -> createCard();
                    case 2 -> moveCardToNextColumn();
                    case 3 -> blockCard();
                    case 4 -> unblockCard();
                    case 5 -> cancelCard();
                    case 6 -> showBoards();
                    case 7 -> showColumnWithCards();
                    case 8 -> showCard();
                    case 9 -> System.out.println("Voltando ao menu anterior...");
                    case 10 -> System.exit(0);

                    default -> System.out.println("Opção Inválida");
                }
            }
        } catch (SQLException e){
            System.out.println("---- Erro no execute() do BOARD MENU ----");
            System.out.println(e);
            System.exit(0);
        } catch (BlockedCardException | EntityNotFoundException e) {
            System.out.println("Erro -=-=-> " + e.getMessage());
        }
    }

    private void showCard() throws SQLException {

        System.out.println("Informe o ID do card que deseja ser mostrado");
        var selectedCardId = scanner.nextLong();
        new CardQueryService(getConnection()).
                findById(selectedCardId)
                .ifPresentOrElse(
                        c -> {
                            System.out.printf("Card %s - %s\n", c.id(), c.title());
                            System.out.printf("Descrição: %s\n", c.description());
                            System.out.printf("Foi bloqueado %c vezes\n", c.blocksAmount());
                        },
                        () -> System.out.printf("Não existe cartão com este id: %s\n", selectedCardId));
    }

    private void showColumnWithCards() throws SQLException{
        var columnsIds = boardEntity.getBoardColumn().stream().map(BoardColumnEntity::getId).toList();
        var selectedColumns = -1L;
        while(!columnsIds.contains(selectedColumns)){
            System.out.printf("Escolha uma coluna do board %s\n,", boardEntity.getName());
            boardEntity.getBoardColumn().forEach(bc -> System.out.printf("%s - %s [%s]\n", bc.getId(), bc.getName(), bc.getKind()));
            selectedColumns = scanner.nextLong();
        }

        try(var connection = getConnection()){
            var column = new BoardColumnQueryService(connection).findById(selectedColumns);
            column.ifPresent(co -> {
                System.out.printf("Coluna %s do tipo %s\n", co.getName(), co.getKind());
                co.getCards().forEach(ca -> System.out.printf("Card %s - %s\n description - %s", ca.getId(), ca.getTitle(), ca.getDescription()));
            });
        }
    }

    private void createCard() throws SQLException{
        var card = new CardEntity();
        System.out.println("Informe o título do Card: ");
        scanner.nextLine();
        System.out.println("Informe a descrição do Card: ");
        scanner.nextLine();
        var initialColumn = boardEntity.getBoardColumn().stream().
                filter(bc -> bc.getKind().equals(INITIAL))
                .findFirst().orElseThrow();
        card.setBoardColumnEntity(initialColumn);
        try(var connection = getConnection()){
            new CardService(connection).insert(card);
        }
    }

    private void moveCardToNextColumn() throws SQLException, BlockedCardException, EntityNotFoundException {
        System.out.println("Informe o Id do card que deseja mover para a próxima coluna: ");
        var cardId = scanner.nextLong();
        var boardColumnsInfo = boardEntity.getBoardColumn().stream()
                .map(bc -> new BoardColumnInfoDTO(bc.getId(), bc.getOrder(), bc.getKind()))
                .toList();

        try(var connection = getConnection()){
            new CardService(connection).moveCardToNextColumn(cardId, boardColumnsInfo);
        }
    }

    private void blockCard() throws SQLException {
        System.out.println("Informe o id do cartão que será desbloqueado: ");
        var cardId = scanner.nextLong();
        System.out.println("Informe o motivo do desbloqueio do cartão: ");
        var unblockReason = scanner.nextLine();
        var boardColumnsInfo = boardEntity.getBoardColumn().stream()
                .map(bc -> new BoardColumnInfoDTO(bc.getId(), bc.getOrder(), bc.getKind()))
                .toList();
        try(var connection = getConnection()){
            new CardService(connection).block(cardId, unblockReason, boardColumnsInfo);
        }
    }

    private void unblockCard() throws SQLException {
        System.out.println("Informe o id do cartão que será desbloqueado: ");
        var cardId = scanner.nextLong();
        System.out.println("Informe o motivo do desbloqueio do cartão: ");
        var unblockReason = scanner.nextLine();
        try(var connection = getConnection()){
            new CardService(connection).unblock(cardId, unblockReason);
        }
    }

    private void cancelCard() throws SQLException {

        System.out.println("Informe o Id do card que deseja mover para a coluna de cancelamento: ");
        var cardId = scanner.nextLong();
        var cancelColumn = boardEntity.getCancelColumn();
        var boardColumnsInfo = boardEntity.getBoardColumn().stream()
                .map(bc -> new BoardColumnInfoDTO(bc.getId(), bc.getOrder(), bc.getKind()))
                .toList();

        try(var connection = getConnection()){
            new CardService(connection).cancel(cardId, cancelColumn.getId(), boardColumnsInfo);
        } catch (Exception e){
            System.out.println("---- Erro no Cancel Card ----");
            System.out.println("@*#*@---@(*#@(");
            System.out.println(e.getMessage());
        }
    }

    private void showBoards() throws SQLException {
        try (var connection = getConnection()) {
            var optional = new BoardQueryService(connection).showBoardDetails(boardEntity.getId());
            optional.ifPresent(b -> {
                System.out.printf("Board [%s, %s]\n", b.id(), b.name());
                b.columns().forEach(columns -> {
                    System.out.printf("Coluna [%s] tipo: [%s] tem %s cards", columns.name(), columns.kind(), columns.cardsAmount());
                });
            });

        }
    }
}
