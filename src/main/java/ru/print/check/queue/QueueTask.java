package ru.print.check.queue;

import ru.print.check.image.PdfToImageTask;

import java.util.concurrent.ConcurrentLinkedQueue;

public class QueueTask {
    private final ConcurrentLinkedQueue<PdfToImageTask> concurrentLinkedQueue = new ConcurrentLinkedQueue();

    public PdfToImageTask getTask() {
        return (PdfToImageTask) concurrentLinkedQueue.poll();
    }

    public void addTask(PdfToImageTask task) {
        concurrentLinkedQueue.add(task);
    }

    public ConcurrentLinkedQueue<PdfToImageTask> getQueue() {
        return concurrentLinkedQueue;
    }
}
