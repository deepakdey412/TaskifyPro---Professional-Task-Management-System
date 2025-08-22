package com.dtech.taskifypro.repository;

import com.dtech.taskifypro.entity.Task;
import com.dtech.taskifypro.entity.TaskStatus;
import com.dtech.taskifypro.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByCreatedBy(Long createdBy);

    List<Task> findByStatus(TaskStatus status);

    @Query("SELECT t FROM Task t JOIN t.assignedUsers u WHERE u.id = :userId")
    List<Task> findTasksByUserId(@Param("userId") Long userId);

    @Query("SELECT t FROM Task t JOIN t.assignedUsers u WHERE u.id = :userId AND t.status = :status")
    List<Task> findTasksByUserIdAndStatus(@Param("userId") Long userId, @Param("status") TaskStatus status);

    @Query("SELECT DISTINCT t FROM Task t LEFT JOIN FETCH t.assignedUsers")
    List<Task> findAllTasksWithUsers();

    List<Task> findByAssignedUsers(User user); // ✅ User, not Optional



}
