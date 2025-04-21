package org.example.service.board;

import org.example.dto.BoardDeatailsDTO;
import org.example.persistence.dao.board.BoardDao;
import org.example.persistence.dao.board_column.BoardColumnDao;
import org.example.persistence.entity.board.BoardEntity;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public class BoardQueryService {

    private Connection connection;

    public BoardQueryService(Connection connection) {
        this.connection = connection;
    }

    public Optional<BoardEntity> findByBoardId(Long boardId) throws SQLException {
        var dao = new BoardDao(connection);
        var boardColumnDao = new BoardColumnDao(connection);
        var optional = dao.findById(boardId);
        if (optional.isPresent()){
            var entity = optional.get();
            entity.setBoardColumn(boardColumnDao.findByBoardId(entity.getId()));
            return Optional.of(entity);
        } else {
            return Optional.empty();
        }
    }

    public Optional<BoardDeatailsDTO> showBoardDetails(Long boardId) throws SQLException {
        var dao = new BoardDao(connection);
        var boardColumnDao = new BoardColumnDao(connection);
        var optional = dao.findById(boardId);
        if (optional.isPresent()){
            var entity = optional.get();
            var columns = boardColumnDao.getBoardDetailsFoundByBoardId(entity.getId());
            var dto = new BoardDeatailsDTO(entity.getId(), entity.getName(), columns);
            return Optional.of(dto);
        } else {
            return Optional.empty();
        }
    }

}
