package org.example.persistence.dao.card;

import com.mysql.cj.jdbc.StatementImpl;
import org.example.dto.CardDetailsDTO;
import org.example.persistence.entity.card.CardEntity;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.ZoneOffset;
import java.util.Optional;

import static java.util.Objects.nonNull;

public class CardDao {

    private Connection connection;

    public CardDao(Connection connection) {
        this.connection = connection;
    }

    public CardEntity insert(CardEntity cardEntity) throws SQLException{
        final String sql = """
                INSERT INTO CARDS(title, description. board_column_id)
                VALUES
                (?, ?, ?)
                """;

        try(var statement = connection.prepareStatement(sql)){
            statement.setString(1, cardEntity.getTitle());
            statement.setString(2, cardEntity.getDescription());
            statement.setLong(3, cardEntity.getBoardColumnEntity().getId());
            statement.executeUpdate();

            if (statement instanceof StatementImpl impl){
                cardEntity.setId(impl.getLastInsertID());
            }
        }

        return null;
    }

    public Optional<CardDetailsDTO> findById(Long id) throws SQLException {
        final String sql =
                """
                   SELECT 
                   c.id, 
                   c.title, 
                   c.description,
                   b.blocked_at,
                   b.blocked_reason,
                   c.id_board_column,
                   bc.name,
                   (SELECT COUNT(sub_b.id) FROM BLOCKS sub_b WHERE sub_b.id = c.id) AS blocks_amount
                    FROM CARDS c  
                    LEFT JOIN BLOCKS b
                    ON c.id = b.id_card
                    AND b.unblocked_at IS NULL
                    INNER JOIN BOARDS_COLUMNS bc 
                    ON c.id_board_column = bc.id 
                    WHERE c.id = ?;
                """;

        try(var statement = connection.prepareStatement(sql)){
            statement.setLong(1, id);
            var resultSet = statement.executeQuery();
            if (resultSet.next()){
                var dto = new CardDetailsDTO(
                        resultSet.getLong("c.id"),
                        resultSet.getString("c.title"),
                        resultSet.getString("c.description"),
                        nonNull(resultSet.getString("b.blocked_reason").isEmpty()),
                        resultSet.getTimestamp("b.blocked_at").toLocalDateTime().atOffset(ZoneOffset.UTC),
                        resultSet.getString("b.blocked_reason"),
                        resultSet.getInt("blocks_amount"),
                        resultSet.getLong("c.id_board_column"),
                        resultSet.getString("bc.name")

                );

                return Optional.of(dto);
            }

        }
        return Optional.empty();
    }

    public void moveToColumn(Long columnId, Long cardId) throws SQLException{

        final String sql = "UPDATE CARDS SET id_board_column = ? WHERE id = ?;";
        try(var statement = connection.prepareStatement(sql)){
            statement.setLong(1, columnId);
            statement.setLong(2, cardId);
            statement.executeUpdate();
        }

    }
}