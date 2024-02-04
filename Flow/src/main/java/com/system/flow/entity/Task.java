package com.system.flow.entity;


import com.system.flow.valueobject.TaskId;

public class Task extends AggregateRoot<TaskId>{

    private long timestamp;
    private String operationName;

    public Task (TaskId taskId,long timestamp, String operationName) {
        super.setId(taskId);
        this.timestamp = timestamp;
        this.operationName = operationName;
    }

    public long getTimestamp () {
        return timestamp;
    }

    public String getOperationName () {
        return operationName;
    }
}
