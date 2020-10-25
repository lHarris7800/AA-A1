class LinkedListNode<T> {
    LinkedListNode<T> next;
    T element;

    LinkedListNode(LinkedListNode<T> next, T element){
        this.next = next;
        this.element = element;
    }
}
