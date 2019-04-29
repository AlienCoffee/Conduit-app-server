package ru.shemplo.conduit.appserver.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.shemplo.conduit.appserver.entities.Identifiable;

@RequiredArgsConstructor
public class LRUCache <T extends Identifiable> {
    
    protected final Map <Long, Node> links = new HashMap <> ();
    
    @Getter protected final int capacity;
    protected Node head, tail;
    
    @Getter @Setter 
    @RequiredArgsConstructor
    @ToString (exclude = {"prev", "next"})
    protected final class Node {
        
        private Node prev, next;
        private final T value;
        
    }
    
    public synchronized T put (T value) {
        if (value == null) { return null; }
        
        if (links.containsKey (value.getId ())) { 
            return value; 
        }
        
        if (links.size () >= capacity) {
            links.remove (tail.value.getId ());
            onNodeRemoved (tail);
            
            tail = tail.prev;
            tail.next = null;
        }
        
        final Node node = new Node (value);
        links.put (value.getId (), node);
        moveToHead (node);
        
        return value;
    }
    
    protected void onNodeRemoved (Node node) {
        // it's a stub method
    }
    
    public synchronized T get (Long key) {
        Node node = links.get (key);
        if (node == null) {
            return null;
        }
        
        moveToHead (node);
        return node.value;
    }
    
    public synchronized T getOrPut (Long key, Supplier <T> source) {
        Node node = links.get (key);
        if (node == null) {
            put (source.get ());
        }
        
        return get (key);
    }
    
    public synchronized void invalidate () {
        head = tail = null;
        links.clear ();
    }
    
    public synchronized T invalidate (Long key) {
        Node node = links.get (key);
        if (node == null) {
            return null;
        }
        
        links.remove (node.value.getId ());
        onNodeRemoved (node);
        
        moveToHead (node);
        head = head.next;
        if (head != null) {            
            head.prev = null;
        }
        
        return node.value;
    }
    
    protected void moveToHead (Node node) {
        final Node prev = node.prev, next = node.next;
        if (prev != null && next != null) { // middle
            prev.next = next; next.prev = prev;
        } else if (prev != null && next == null) { // tail
            tail = tail.prev; tail.next = null;
        } else if (prev == null && next != null) { // head
            return; // this node is already head
        } else if (prev == null && next == null) { // new || just 1 node
            if (head == null && tail == null) { // empty cache
                head = tail = node; return;
            }
        }
        
        head.prev = node;
        node.next = head;
        head = node;
    }
    
}
