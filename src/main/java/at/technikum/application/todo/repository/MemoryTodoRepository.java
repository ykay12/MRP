package at.technikum.application.todo.repository;

import at.technikum.application.todo.model.Todo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MemoryTodoRepository implements TodoRepository {
    private final List<Todo> todos;

    public MemoryTodoRepository() {
        todos = new ArrayList<>();
    }

    @Override
    public Optional<Todo> find(String id) {
        return Optional.empty();
    }

    @Override
    public List<Todo> findAll() {
        return todos;
    }

    @Override
    public Todo save(Todo todo) {
        todos.add(todo);
        return todo;
    }

    @Override
    public Todo delete(String id) {
        return null;
    }
}
