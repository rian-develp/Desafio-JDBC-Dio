package org.example.service.board_column;

import org.example.persistence.dao.board_column.BoardColumnDao;
import org.example.persistence.entity.board_column.BoardColumnEntity;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public class BoardColumnQueryService {

    private Connection connection;

    public BoardColumnQueryService(Connection connection) {
        this.connection = connection;
    }

    public Optional<BoardColumnEntity> findById(Long id) throws SQLException {
        var dao = new BoardColumnDao(connection);
        return dao.findById(id);
    }
}
