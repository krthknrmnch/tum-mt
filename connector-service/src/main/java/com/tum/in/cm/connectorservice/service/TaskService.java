package com.tum.in.cm.connectorservice.service;

import com.tum.in.cm.connectorservice.model.task.Task;
import com.tum.in.cm.connectorservice.repository.primary.TaskRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Task service containing business logic for managing tasks.
 */
@Service
@Slf4j
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;

    public Optional<Task> findById(String id) {
        return taskRepository.findById(id);
    }

    public Optional<Task> findEarliestTaskForProbe(String probeId) {
        return taskRepository.findFirstByProbeIdOrderByScheduledStartTimestampAsc(probeId);
    }

    public List<Task> findAllByProbeId(String probeId) {
        return taskRepository.findAllByProbeId(probeId);
    }

    public Task insert(Task task) {
        Task insertedTask = taskRepository.insert(task);
        log.info("Inserted Task");
        return insertedTask;
    }

    public Task update(Task updatedTask) {
        log.info("Updating Task");
        Task task = taskRepository.findById(updatedTask.getId()).orElse(null);
        if (task != null) {
            updatedTask.setId(task.getId());
            return taskRepository.save(updatedTask);
        }
        return null;
    }

    public void delete(Task task) {
        taskRepository.delete(task);
        log.info("Deleted Task");
    }

    //Used in tests
    public void deleteAll() {
        taskRepository.deleteAll();
        log.info("Deleted All Tasks");
    }
}
