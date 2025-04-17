package org.example;

import java.util.NoSuchElementException;

public class CustomArrayList <T>{

    private int size;
    private int capacity;
    private Object[] array;
    private int offset;

    public CustomArrayList(int initialCapacity){
        this.capacity = Math.max(10, initialCapacity);
        this.array = new Object[capacity];
        this.size = 0;
        this.offset = 0;
    }

    public void add(T element){
        if (size == capacity){
            capacity = Math.max(capacity  * 2, 10);
            Object [] newArray = new Object[capacity];
            System.arraycopy(array, offset, newArray, 0, size);
            array = newArray;
        }

        array[offset] = element;
        offset++;
        size++;
    }

    public void remove(int index){
        if (index < 0 || index >= size){
            throw new ArrayIndexOutOfBoundsException();
        }

        Object [] tempArray = new Object[size -1 ];
        System.arraycopy(array, 0, tempArray, 0, index);
        System.arraycopy(array, index + 1, tempArray, index, size - index - 1);
        array = tempArray;
        offset--;
        size--;
    }

    public T get(int index){
        if (index <0 || index >= size){
            throw new IndexOutOfBoundsException();
        }

        return (T) array[offset + index];
    }

    public void addFirst(T element){
        if (size == capacity){
            capacity = Math.max(capacity * 2, 10);
            Object[] newArray = new Object[capacity];
            System.arraycopy(array, offset, newArray, 1, size);
            array = newArray;
        }

        for (int i = size; i > 0; i--){
            array[i] = array[i-1];
        }

        array[0] = element;
        size++;
    }


    public void removeFirst(){
        if (size == 0){
            throw new NoSuchElementException();
        }

        offset--;
        size--;
    }

    public T[] toArray(){
        Object[] arrayCopy = new Object[size];
        System.arraycopy(array, offset, arrayCopy, 0, size);
        return (T[]) arrayCopy;
    }

}
