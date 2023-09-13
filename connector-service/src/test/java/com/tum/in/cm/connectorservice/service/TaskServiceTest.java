package com.tum.in.cm.connectorservice.service;

import com.tum.in.cm.connectorservice.model.task.Task;
import com.tum.in.cm.connectorservice.util.Constants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class TaskServiceTest {
    @Autowired
    private TaskService taskService;

    @BeforeEach
    public void setup() {
        taskService.deleteAll();
    }

    @AfterEach
    public void teardown() {
        taskService.deleteAll();
    }

    //Base Test
    @Test
    public void testInsert() {
        Task task = new Task();
        task.setType(Constants.MeasurementType.PING);
        Task resultTask = taskService.insert(task);
        assertThat(resultTask.getType()).isEqualTo(Constants.MeasurementType.PING);
    }

    //Base Test
    @Test
    public void testUpdate() {
        Task task = new Task();
        task.setNextExecutionNumber(1);
        Task resultTask = taskService.insert(task);
        assertThat(resultTask.getNextExecutionNumber()).isEqualTo(1);
        resultTask.setNextExecutionNumber(2);
        Task updatedTask = taskService.update(resultTask);
        assertThat(updatedTask.getNextExecutionNumber()).isEqualTo(2);
    }
}
