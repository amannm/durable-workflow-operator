package dev.restate.sdk;

public interface WorkflowContext {
    default void run(String name, Runnable task) {
        task.run();
    }
}
