package org.example.service.board;

import org.example.persistence.dao.board.BoardDao;
import org.example.persistence.dao.board_column.BoardColumnDao;
import org.example.persistence.entity.board.BoardEntity;

import java.sql.Connection;
import java.sql.SQLException;

public class BoardService {

    private Connection connection;

    private BoardDao dao = new BoardDao(connection);

    public BoardService(Connection connection) {
        this.connection = connection;
    }

    public void insert(final BoardEntity entity) throws SQLException{
        connection.setAutoCommit(false);
        var boardColumnDao = new BoardColumnDao(connection);
        try {
            dao.insert(entity);
            var columns = entity.getBoardColumn().stream().map(c -> {
                c.setBoard(entity);
                return c;
            }).toList();

            for (var column: columns){
                boardColumnDao.insert(column);
            }
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw new RuntimeException(e);
        }

    }

    public boolean delete(Integer id) throws SQLException {
        connection.setAutoCommit(false);
        var bool = false;
        try {
            dao = BoardDao.getInstance(connection);

            if (!dao.exists(id)){
                return bool;
            } else {
                bool = true;
                connection.commit();
                return bool;
            }

        } catch (SQLException e) {
            connection.rollback();
            System.out.println("----- Erro no Board Service ----- " + e);
            return false;
        }
    }
}
