class BinarySearchNode<T> {
    BinarySearchNode leftChild;
    BinarySearchNode rightChild;

    T element;

    BinarySearchNode(BinarySearchNode leftChild, BinarySearchNode rightChild, T element){
        this.leftChild = leftChild;
        this.rightChild = rightChild;

        this.element = element;
    }

}
