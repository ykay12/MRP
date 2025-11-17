package at.technikum.application.todo.controller;

import at.technikum.application.common.Controller;
import at.technikum.application.todo.model.Todo;
import at.technikum.application.todo.service.TodoService;
import at.technikum.server.http.Method;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import at.technikum.server.http.Status;

import java.util.List;

public class TodoController extends Controller {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @Override
    public Response handle(Request request) {

        if (request.getMethod().equals(Method.GET.getVerb())) {
            if (request.getPath().equals("/todos")) {
                return readAll();
            }
            return read(request.getPath().replace("/todos/", ""));
        }

        if (request.getMethod().equals(Method.POST.getVerb())) {
            return create(request);
        }

        if (request.getMethod().equals(Method.PUT.getVerb())) {
            return update();
        }

        if (request.getMethod().equals(Method.DELETE.getVerb())) {
            return delete();
        }

        return null;
    }

    private Response readAll() {
        List<Todo> todos = todoService.getAll();

        return text(todos.toString());
    }

    private Response read(String id) {
        Todo todo = todoService.get(id);
        return json(todo, Status.OK);
    }

    private Response create(Request request) {
        Todo todo = toObject(request.getBody(), Todo.class);
        todo = todoService.create(todo);
        return json(todo, Status.CREATED);
    }

    private Response update() {
        return null;
    }

    private Response delete() {
        return null;
    }
}
