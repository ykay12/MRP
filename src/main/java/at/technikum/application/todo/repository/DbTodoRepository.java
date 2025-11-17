package at.technikum.application.todo.repository;

import at.technikum.application.common.ConnectionPool;
import at.technikum.application.todo.model.Todo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class DbTodoRepository implements TodoRepository {

    private final ConnectionPool connectionPool;

    private static final String SELECT_BY_ID
            = "SELECT * FROM todos WHERE id = ?";

    public DbTodoRepository(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public Optional<Todo> find(String id) {
        try (
                Connection conn = connectionPool.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(SELECT_BY_ID)
            ) {

            pstmt.setString(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }

                Todo todo = new Todo(
                        rs.getString("id"),
                        rs.getString("description"),
                        rs.getBoolean("done")
                );

                return Optional.of(todo);
            }

        } catch (SQLException e) {

            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Todo> findAll() {

        // while (rs.next()) {
        //   // create object
        //   // add to list
        // }
        return List.of();
    }

    @Override
    public Todo save(Todo todo) {
        return null;
    }

    @Override
    public Todo delete(String id) {
        return null;
    }
}
