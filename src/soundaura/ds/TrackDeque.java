package soundaura.ds;

// import java.util.LinkedList;

import soundaura.java.entities.Track;

public class TrackDeque {
    private int front, rear;
    private Track[] arr;
    private int N;

    public TrackDeque(){
        this.N = 20;
        this.arr = new Track[N];
        this.front = -1;
        this.rear = -1;
    }

    public TrackDeque(int N){
        this.N = N;
        this.arr = new Track[N];
        this.front = -1;
        this.rear = -1;
    }

    private void resize(){
        Track[] tempArr = new Track[N + 5];
        int idx = 0;
        while (!this.isEmpty()) {
            tempArr[idx] = this.removeFromFront(); 
            idx++;
        }

        arr = tempArr;
        N = N + 5;
        front = 0;
        rear = idx - 1;
    }

    public Track peekFirst(){
        if (this.isEmpty()){
            return null;
        }
        return arr[front];
    }

    public Track peekLast(){
        if (this.isEmpty()){
            return null;
        }
        return arr[rear];
    }

    //insertAtRear
    public void nextInQueue(Track x){
        if ((rear+1) % N == front){
            this.resize();
        }

        if(front == -1){
            front = 0;
            rear = 0;
        }
        else {
            rear = (rear+1) % N;
        }
        arr[rear] = x;
    }

    //insertAtFront
    public void addFirst(Track x){
        if ((rear+1) % N == front){
            this.resize();
        }

        if(front == -1){
            front = 0;
            rear = 0;
        }
        else {
            front = ((front-1) + N) % N;
        }
        arr[front] = x;
    }

    //deleteAtFront
    public Track removeFromFront(){
        Track temp;
        if (front == -1){
            System.out.println("Queue is empty"); //Underflow
            temp = null;
        }
        else{
            if(front == rear){
                temp = arr[front];
                front = -1;
                rear = -1;
            }
            else {
                temp = arr[front];
                front = (front+1) % N;
            }
        }
        return temp;
    }

    //deleteByIndex
    public void deleteByNo(int idx){
        if (get(idx) == null){
            System.out.println("Invalid index."); //E.H
            return;
        }
        else{
            // Alternative approach: Shift elements to the left to fill the gap
            int currentIndex = (front + idx) % N;
            int nextIndex;
            while (currentIndex != ((rear-1)+N)%N) {
                nextIndex = (currentIndex + 1) % N; 
                arr[currentIndex] = arr[nextIndex];
                currentIndex = nextIndex;
            }
            arr[rear] = null; 
            rear = ((rear-1) + N) % N;
        }
    }

    public Track get(int i){
        if (i<0 || i>size()){
            return null;
        }
        else if (isEmpty()){
            return null;
        }
        else{
            return arr[(front+i) % N];
        }
    }

    public int size(){
        if (!isEmpty()){
            if (front <= rear){
                return (rear-front+1);
            }
            else{
                return ((arr.length - front) + (rear + 1));
            }
        }
        else{
            return -1;
        }
    }

    public void clear(){
        while(!isEmpty()){
            removeFromFront();
        }
    }

    public boolean isEmpty(){
        if(front==-1 && rear==-1){
            return true;
        }
        else{
            return false;
        }
    }
}
