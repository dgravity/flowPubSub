package com.system.flow;


import com.system.flow.entity.Task;
import com.system.flow.valueobject.TaskId;

import java.util.UUID;
import java.util.concurrent.Flow;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class FlowLearningApp {

    public static void main (String[] args) throws InterruptedException {
        // Create a Publisher that emits Task objects with simulated delays
        Flow.Publisher<Task> publisher = new Flow.Publisher<>() {
            private final AtomicInteger count = new AtomicInteger(1);
            private Flow.Subscriber<? super Task> subscriber;

            @Override
            public void subscribe (Flow.Subscriber<? super Task> subscriber) {
                this.subscriber = subscriber;
                subscriber.onSubscribe(new Flow.Subscription() {
                    private long demand = 0;

                    @Override
                    public void request (long n) {
                        demand += n;
                        emitNextTask(Math.min(demand, 1)); // Emit tasks in chunks of 1
                    }

                    @Override
                    public void cancel () {
                        // Handle cancellation if needed
                    }

                    private void emitNextTask (long n) {
                        while (n > 0 && count.get() <= 3) { // Simulate producing 3 tasks
                            try {
                                TaskId taskId = new TaskId(UUID.randomUUID());
                                Task task = new Task(taskId, System.currentTimeMillis(), "Operation " + count);
                                subscriber.onNext(task);
                                TimeUnit.SECONDS.sleep(1); // Simulate delay
                                count.incrementAndGet();
                                demand--;
                            } catch (InterruptedException e) {
                                subscriber.onError(e);
                                break;
                            }
                        }
                        if (count.get() > 3) {
                            subscriber.onComplete();
                        }
                    }
                });
            }
        };

        // Create a Subscriber that logs received tasks and simulates processing
        Flow.Subscriber<Task> subscriber = new Flow.Subscriber<>() {
            private Flow.Subscription subscription;
            private long demand = 20; // Initial demand

            @Override
            public void onSubscribe (Flow.Subscription subscription) {
                this.subscription = subscription;
                subscription.request(demand);
            }

            @Override
            public void onNext (Task task) {
                System.out.println("Received task: " + task.getId() + ", timestamp: " + task.getTimestamp()
                        + ", operation: " + task.getOperationName());
                // Simulate processing time
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                } catch (InterruptedException e) {
                    onError(e);
                }
                // Signal backpressure if demand is low
                if (demand <= 1) {
                    subscription.request(1);
                    demand++;
                }
            }

            @Override
            public void onError (Throwable error) {
                System.err.println("Error: " + error.getMessage());
            }

            @Override
            public void onComplete () {
                System.out.println("Stream completed.");
            }
        };

        // Connect the Publisher and Subscriber
        publisher.subscribe(subscriber);

        // Wait for a while to allow data flow and backpressure management
        TimeUnit.SECONDS.sleep(5);
    }
}