package org.example.persistence.dao.board;

import com.mysql.cj.jdbc.StatementImpl;
import org.example.persistence.entity.board.BoardEntity;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public class BoardDao {

    private final Connection connection;
    private static BoardDao dao = null;
    public BoardDao(Connection connection) {
        this.connection = connection;
    }

    public BoardEntity insert(final BoardEntity boardEntity) throws SQLException {

        final String sql = "INSERT INTO BOARDS(name) VALUES(?);";
        try(var statement = connection.prepareStatement(sql)){
            statement.setString(1, boardEntity.getName());
            statement.executeUpdate();

            if (statement instanceof StatementImpl impl){
                boardEntity.setId(impl.getLastInsertID());
            }

        } catch (Exception e){
            System.out.println("---------------------");
            System.out.println("###&*%(#@**&&!!__(");
            System.out.println("O Erro a Seguir foi: " + e);
        }

        return boardEntity;
    }

    public void delete(final Long id) throws SQLException {
        final String sql = "DELETE FROM BOARDS WHERE id = ?";
        try(var statement = connection.prepareStatement(sql)){
            statement.setLong(1, id);
            statement.executeUpdate();
        }
    }

    public Optional<BoardEntity> findById(final Long id) throws SQLException {

        final String sql = "SELECT * FROM BOARDS WHERE id = ?";

        try(var statement = connection.prepareStatement(sql)){
            statement.setLong(1, id);
            statement.executeQuery();
            var resultSet = statement.getResultSet();
            if (resultSet.next()){
                BoardEntity boardEntity = new BoardEntity();
                boardEntity.setId(resultSet.getLong("id"));
                boardEntity.setName(resultSet.getString("name"));
                return Optional.of(boardEntity);
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public boolean exists(final Long id) throws SQLException {

        boolean isResult = false;
        final String sql = "SELECT 1 FROM BOARDS WHERE id = ?";

        try(var statement = connection.prepareStatement(sql)){

            statement.setLong(1, id);
            statement.executeQuery();
            isResult = statement.getResultSet().next();
            return isResult;

        } catch (SQLException e) {
           e.printStackTrace();
        }

        return isResult;
    }

    public static BoardDao getInstance(Connection connection) throws SQLException {
        if (dao == null){
            dao = new BoardDao(connection);
        }
        return dao;
    }
}
