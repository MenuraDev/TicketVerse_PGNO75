package utils;

public class CircularQueue {
    private static final int DEFAULT_CAPACITY = 100;
    private int[] queue;
    private int front;
    private int rear;
    private int size;
    private int capacity;

    public CircularQueue() {
        this(DEFAULT_CAPACITY);
    }

    public CircularQueue(int capacity) {
        this.capacity = capacity;
        this.queue = new int[capacity];
        this.front = 0;
        this.rear = -1;
        this.size = 0;
    }

    public boolean isFull() {
        return size == capacity;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public boolean enqueue(int ticketId) {
        if (isFull()) {
            return false;
        }
        rear = (rear + 1) % capacity;
        queue[rear] = ticketId;
        size++;
        return true;
    }

    public int dequeue() {
        if (isEmpty()) {
            return -1; // Return -1 to indicate empty queue
        }
        int ticketId = queue[front];
        front = (front + 1) % capacity;
        size--;
        return ticketId;
    }

    public int peek() {
        if (isEmpty()) {
            return -1;
        }
        return queue[front];
    }

    public void clear() {
        front = 0;
        rear = -1;
        size = 0;
    }



    // Method to get all tickets in the queue
    public int[] getAllTickets() {
        if (isEmpty()) {
            return new int[0];
        }
        int[] tickets = new int[size];
        int current = front;
        for (int i = 0; i < size; i++) {
            tickets[i] = queue[current];
            current = (current + 1) % capacity;
        }
        return tickets;
    }
} 