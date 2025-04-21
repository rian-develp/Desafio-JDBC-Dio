package org.example.service.cards;

import org.example.dto.BoardColumnInfoDTO;
import org.example.excptions.BlockedCardException;
import org.example.excptions.CardFinishedException;
import org.example.excptions.EntityNotFoundException;
import org.example.excptions.UnblockedCardException;
import org.example.persistence.dao.block.BlockDao;
import org.example.persistence.dao.card.CardDao;
import org.example.persistence.entity.card.CardEntity;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.example.persistence.entity.board_column.BoardColumnKindEnum.CANCEL;
import static org.example.persistence.entity.board_column.BoardColumnKindEnum.FINAL;

public class CardService {

    private Connection connection;

    public CardService(Connection connection) {
        this.connection = connection;
    }

    public CardEntity insert(CardEntity entity) throws SQLException {
        try{
            CardDao cardDao = new CardDao(connection);
            var cardEntity = cardDao.insert(entity);
            connection.commit();
            return cardEntity;
        } catch (Exception e){
            connection.rollback();
            System.out.println("Exceção no Card Service");
            System.out.println("-$#%#%#$@+=@#");
            throw e;
        }
    }

    public void moveCardToNextColumn(Long cardId, List<BoardColumnInfoDTO> boardColumnsInfo) throws SQLException, EntityNotFoundException, BlockedCardException {

        try {
            CardDao cardDao = new CardDao(connection);
            var optional = cardDao.findById(cardId);
            var dto = optional.orElseThrow(() ->
                    new EntityNotFoundException("Não foi possível encontrar o card %s".formatted(cardId)
                    )
            );

            if (dto.blocked()){
                throw new BlockedCardException("O card %s está bloqueado, é necessário desbloqueá-lo para mover".formatted(cardId));
            }

            var currentColumn = boardColumnsInfo.stream().filter(
                    bc -> bc.id().equals(dto.columnId()))
                    .findFirst().
                    orElseThrow(() -> new IllegalStateException("O card informado pertence a outro board"));

            if (currentColumn.kind().equals(FINAL)){
                throw new CardFinishedException("O card já foi finalizado");
            }

            var nextColumn = boardColumnsInfo.stream().filter(
                            bc -> bc.order() == currentColumn.order() + 1).findFirst()
                            .orElseThrow(() -> new IllegalStateException("O card informado pertence a outro board"));

            cardDao.moveToColumn(nextColumn.id(), cardId);
        } catch (SQLException | EntityNotFoundException | BlockedCardException e) {
            connection.rollback();
            throw e;
        } catch (CardFinishedException e) {
            throw new RuntimeException(e);
        }
    }

    public void cancel(Long cardId, Long cancelColumnId, List<BoardColumnInfoDTO> boardColumnsInfo) throws SQLException {

        try {
            var dao = new CardDao(connection);
            var optional = dao.findById(cardId);
            var dto = optional.orElseThrow(
                    () -> new EntityNotFoundException("Não foi possível encontrar o card %s".formatted(cardId))
            );

            if (dto.blocked()){
                throw new BlockedCardException("O card %s está bloqueado, é necessário desbloqueá-lo para cancelar".formatted(cardId));
            }

            var currentColumn = boardColumnsInfo.stream().filter(
                            bc -> bc.id().equals(dto.columnId()))
                    .findFirst().
                    orElseThrow(() -> new IllegalStateException("O card informado pertence a outro board"));

            boardColumnsInfo.stream().filter(
                            bc -> bc.order() == currentColumn.order() + 1).findFirst()
                    .orElseThrow(() -> new IllegalStateException("O card informado pertence a outro board"));

            if (currentColumn.kind().equals(FINAL)){
                throw new CardFinishedException("Cartão já foi finalizado");
            }

            dao.moveToColumn(cancelColumnId, cardId);
            connection.commit();
        } catch (SQLException | EntityNotFoundException | BlockedCardException e) {
            connection.rollback();
            throw new RuntimeException(e);
        } catch (CardFinishedException e) {
            throw new RuntimeException(e);
        }
    }

    public void block(Long id, String reason, List<BoardColumnInfoDTO> boardColumnsInfo) throws SQLException {
        try {
            CardDao dao = new CardDao(connection);
            var optional = dao.findById(id);
            var dto = optional.orElseThrow(
                    () -> new EntityNotFoundException("Não foi possível encontrar o card %s".formatted(id))
            );

            if (dto.blocked()){
                throw new BlockedCardException("O card %s já está bloqueado".formatted(id));
            }

            var currentColumn = boardColumnsInfo.stream().filter(bc -> bc.id().equals(dto.columnId())).
                    findFirst().orElseThrow();

            if(currentColumn.kind().equals(FINAL) || currentColumn.kind().equals(CANCEL)){
                throw new IllegalStateException("O card está em uma coluna do tipo %s e não pode ser bloqueado".formatted(
                        currentColumn.kind()));
            }

            BlockDao blockDao = new BlockDao(connection);
            blockDao.block(id, reason);
            connection.commit();
        } catch (BlockedCardException | EntityNotFoundException e) {
            connection.rollback();
            throw new RuntimeException(e);
        }
    }

    public void unblock(Long id, String reason) throws SQLException {
        try {
            CardDao dao = new CardDao(connection);
            var optional = dao.findById(id);
            var dto = optional.orElseThrow(
                    () -> new EntityNotFoundException("Não foi possível encontrar o card %s".formatted(id))
            );

            if (!dto.blocked()) {
                throw new UnblockedCardException("O card %s já está desbloqueado".formatted(id));
            }
            BlockDao blockDao = new BlockDao(connection);
            blockDao.unblock(id, reason);
            connection.commit();
        } catch (SQLException | EntityNotFoundException | UnblockedCardException e) {

            connection.rollback();
            throw new RuntimeException(e);
        }
    }
 }