package org.example.persistence.dao.block;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.OffsetDateTime;

public class BlockDao {

    private Connection connection;

    public BlockDao(Connection connection) {
        this.connection = connection;
    }

    public void block(Long cardId, String reason) throws SQLException {
        final String sql = """
                    INSERT INTO BLOCKS
                    (id_card, blocked_at, blocked_reason)
                    VALUES
                    (?, ?, ?);
                """;

        try(var statement = connection.prepareStatement(sql)){

            statement.setLong(1, cardId);
            statement.setTimestamp(2, Timestamp.valueOf(OffsetDateTime.now().toString()));
            statement.setString(3, reason);
            statement.executeUpdate();

        }
    }

    public void unblock(Long cardId, String reason) throws SQLException{
        final String sql = """
                    UPDATE BLOCKS
                    SET unblocked_at = ?,
                        unblocked_reason = ?,
                    WHERE id_card = ? AND unblocked_reason IS NULL;
                """;

        try(var statement = connection.prepareStatement(sql)){

            statement.setTimestamp(1, Timestamp.valueOf(OffsetDateTime.now().toString()));
            statement.setString(2, reason);
            statement.setLong(3, cardId);
            statement.executeUpdate();

        }
    }

}
