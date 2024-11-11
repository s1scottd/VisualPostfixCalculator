package com.s1scottd;

import java.util.LinkedList;
import java.util.List;

public class LinkedListStack<T> {

  private LinkedList<T> elements;

  public LinkedListStack() {
    elements = new LinkedList<>();
  }

  public void push(T value) {
    elements.addFirst(value); // Adds element to the top of the stack
  }

  public T pop() {
    if (elements.isEmpty()) {
      throw new IllegalStateException("Stack is empty");
    }
    return elements.removeFirst(); // Removes element from the top of the stack
  }

  public T peek() {
    if (elements.isEmpty()) {
      throw new IllegalStateException("Stack is empty");
    }
    return elements.getFirst(); // Returns the top element without removing it
  }

  public void clear() {
    elements.clear(); // Clears all elements in the stack
  }

  public int size() {
    return elements.size(); // Returns the number of elements in the stack
  }

  public boolean isEmpty() {
    return elements.isEmpty(); // Checks if the stack is empty
  }

  // Helper method to retrieve the entire contents of the stack
  public List<T> getContents() {
    return new LinkedList<>(elements); // Return a copy to prevent modification
  }
}
