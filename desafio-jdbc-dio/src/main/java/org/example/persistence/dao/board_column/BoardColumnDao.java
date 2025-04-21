package org.example.persistence.dao.board_column;


import com.mysql.cj.jdbc.StatementImpl;
import org.example.dto.BoardColumnDTO;
import org.example.persistence.entity.board_column.BoardColumnEntity;
import org.example.persistence.entity.card.CardEntity;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.isNull;
import static org.example.persistence.entity.board_column.BoardColumnKindEnum.findByName;

public class BoardColumnDao {

    private final Connection connection;

    public BoardColumnDao(Connection connection) {
        this.connection = connection;
    }

    public BoardColumnEntity insert(BoardColumnEntity boardColumnEntity){
        final String sql = "INSERT INTO BOARDS_COLUMNS(name, `order`, kind, id_board) VALUES(?, ?, ?, ?)";
        try {
            var statement = connection.prepareStatement(sql);
            statement.setString(1, boardColumnEntity.getName());
            statement.setInt(2, boardColumnEntity.getOrder());
            statement.setString(3, boardColumnEntity.getKind().name());
            statement.setLong(4, boardColumnEntity.getBoard().getId());
            statement.executeUpdate();

            if (statement instanceof StatementImpl impl){
                boardColumnEntity.setId(impl.getLastInsertID());
            }

            return boardColumnEntity;
        } catch (Exception e) {
            System.out.println("Erro no INSERT do BoardColumnDao");
            System.out.println("#$(*@(***&&%&%&&#");
            System.out.println("O erro foi: " + e);
        }

        return boardColumnEntity;
    }

    public List<BoardColumnEntity> findByBoardId(Long boardId) throws SQLException {

        List<BoardColumnEntity> entities = new ArrayList<>();
        final String sql = "SELECT id, name, `order` FROM BOARDS_COLUMNS WHERE id = ? ORDER BY `order`;";

        try(var statement = connection.prepareStatement(sql)){
            statement.setLong(1, boardId);
            statement.executeQuery();
            var resultSet = statement.getResultSet();

            while (resultSet.next()) {
                var entity = new BoardColumnEntity();
                entity.setId(resultSet.getLong("id"));
                entity.setName(resultSet.getString("name"));
                entity.setOrder(resultSet.getInt("order"));
                entities.add(entity);
            }
        }
        return null;
    }

    public List<BoardColumnDTO> getBoardDetailsFoundByBoardId(Long boardId) throws SQLException {

        List<BoardColumnDTO> dtos = new ArrayList<>();
        final String sql =
                """
                SELECT bc.id, 
                       bc.name, 
                       bc.kind, 
                       (SELECT COUNT(c.id) FROM CARDS c WHERE c.id_board_column = bc.id) cards_amount 
                FROM BOARDS_COLUMNS bc WHERE id = ? ORDER BY `order`;
                """;

        try(var statement = connection.prepareStatement(sql)){
            statement.setLong(1, boardId);
            statement.executeQuery();
            var resultSet = statement.getResultSet();

            while (resultSet.next()) {
                var dto = new BoardColumnDTO(
                        resultSet.getLong("bc.id"),
                        resultSet.getString("bc.name"),
                        resultSet.getInt("bc.order"),
                        findByName(resultSet.getString("bc.kind")),
                        resultSet.getInt("cards_amount")
                );
                dtos.add(dto);
            }
        }
        return null;
    }

    public Optional<BoardColumnEntity> findById(Long boardId) throws SQLException{
        final String sql =
        """
        SELECT bc.name, bc.kind, c.id, c.title, c.description
        FROM BOARDS_COLUMNS bc
        LEFT JOIN CARDS c ON c.id_board_column = bc.id
        WHERE id_board = ? ORDER BY `order`;
        """;

        try(var statement = connection.prepareStatement(sql)){
            statement.setLong(1, boardId);
            var resultSet = statement.executeQuery();
            if (resultSet.next()){
                var entity = new BoardColumnEntity();
                entity.setName(resultSet.getString("name"));
                entity.setKind(findByName(resultSet.getString("kind")));

                do {

                    var cardEntity = new CardEntity();
                    cardEntity.setId(resultSet.getLong("c.id"));
                    cardEntity.setTitle("c.title");
                    cardEntity.setDescription("c.description");
                    entity.getCards().add(cardEntity);

                    if (isNull(resultSet.getString("c.title"))){
                        break;
                    }
                } while (resultSet.next());
                return Optional.of(entity);
            }

            return Optional.empty();
        }
    }
}
