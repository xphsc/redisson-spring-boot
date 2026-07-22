package com.xphsc.example.queue;

import java.io.Serializable;

public class ExampleMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String content;
    private int failTimes;
    private int attempts;

    public ExampleMessage() {
    }

    public ExampleMessage(String id, String content, int failTimes) {
        this.id = id;
        this.content = content;
        this.failTimes = failTimes;
    }

    public int nextAttempt() {
        return ++attempts;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getFailTimes() {
        return failTimes;
    }

    public void setFailTimes(int failTimes) {
        this.failTimes = failTimes;
    }

    public int getAttempts() {
        return attempts;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

    @Override
    public String toString() {
        return "ExampleMessage{" +
                "id='" + id + '\'' +
                ", content='" + content + '\'' +
                ", failTimes=" + failTimes +
                ", attempts=" + attempts +
                '}';
    }
}
