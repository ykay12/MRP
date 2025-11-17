package at.technikum.application.todo.repository;

import at.technikum.application.todo.model.Todo;

import java.util.List;
import java.util.Optional;

public interface TodoRepository {

    Optional<Todo> find(String id);

    List<Todo> findAll();

    Todo save(Todo todo);

    // Todo update(Todo todo);

    Todo delete(String id);
}
