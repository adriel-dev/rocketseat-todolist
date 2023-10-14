package br.com.adrielfelix.todolist.task;

import br.com.adrielfelix.todolist.user.UserModel;
import br.com.adrielfelix.todolist.util.Util;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.http.ResponseEntity.*;

@RestController
@RequestMapping("/api/v1/task")
@AllArgsConstructor
public class TaskController {

    private TaskRepository taskRepository;

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody TaskModel taskModel, HttpServletRequest request) {
        var userId = (UUID) request.getAttribute("userId");
        var user = new UserModel();
        user.setId(userId);
        taskModel.setUser(user);
        var currentDate = LocalDateTime.now();
        if(currentDate.isAfter(taskModel.getStartAt()) || currentDate.isAfter(taskModel.getEndAt())) {
            return badRequest().body("Start/End date must be after current date!");
        }
        if(taskModel.getStartAt().isAfter(taskModel.getEndAt())) {
            return badRequest().body("Start date must be before end date!");
        }
        var createdTask = taskRepository.save(taskModel);
        return ok().body(createdTask);
    }

    @GetMapping
    public ResponseEntity<Object> list(HttpServletRequest request) {
        var userId = (UUID) request.getAttribute("userId");
        var tasks = taskRepository.findByUserId(userId);
        return ok().body(tasks);
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<Object> update(HttpServletRequest request, @PathVariable UUID taskId, @RequestBody TaskModel taskModel) {
        var userId = (UUID) request.getAttribute("userId");
        var foundTask = taskRepository.findById(taskId);
        if(foundTask.isEmpty()){
            return badRequest().body("No task with id ["+taskId+"] was found!");
        }
        var taskObj = foundTask.get();
        if(!taskObj.getUser().getId().equals(userId)) {
            return status(403).build();
        }
        Util.copyNonNullProperties(taskModel, taskObj);
        return ok().body(taskRepository.save(taskObj));
    }

}
