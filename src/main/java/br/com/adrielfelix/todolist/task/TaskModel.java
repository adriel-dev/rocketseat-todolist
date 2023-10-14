package br.com.adrielfelix.todolist.task;

import br.com.adrielfelix.todolist.user.UserModel;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "tb_task")
public class TaskModel {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne
    private UserModel user;
    private String description;
    @Column(length = 50)
    private String title;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private TaskPriority priority;
    @CreationTimestamp
    private LocalDateTime createdAt;

    public void setTitle(String title) {
        if(title.length() > 50) throw new RuntimeException("Title must not be longer than 50 characters!");
        this.title = title;
    }

}
