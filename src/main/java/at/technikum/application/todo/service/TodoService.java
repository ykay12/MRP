package at.technikum.application.todo.service;

import at.technikum.application.todo.exception.EntityNotFoundException;
import at.technikum.application.todo.model.Todo;
import at.technikum.application.todo.repository.TodoRepository;

import java.util.List;
import java.util.UUID;

public class TodoService {

    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public Todo create(Todo todo) {
        // is todo valid?
        todo.setId(UUID.randomUUID().toString());

        return todoRepository.save(todo);
    }

    public Todo get(String id) {
        return todoRepository.find(id)
                .orElseThrow(EntityNotFoundException::new);
    }

    public List<Todo> getAll() {
        return todoRepository.findAll();
    }

    public List<Todo> getAllOpen() {
        return todoRepository.findAll()
                .stream()
                .filter(todo -> !todo.isDone())
                .toList();
    }

    public Todo update(String id, Todo update) {
        Todo todo = todoRepository.find(id)
                .orElseThrow(EntityNotFoundException::new);

        todo.setDescription(update.getDescription());
        todo.setDone(update.isDone());

        return todoRepository.save(todo);
    }

    public Todo delete(String id) {
        return todoRepository.delete(id);
    }
}
