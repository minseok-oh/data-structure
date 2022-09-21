class LinkedList<T> {
    var head: Node<T>? = null
    var tail: Node<T>? = null
    var size = 0

    override fun toString(): String {
        if (head == null) {
            return ""
        }
        return head.toString()
    }

    fun push(data: T) {
        head = Node<T>(data=data, next=head)
        size++

        if (tail == null) {
            tail = head
        }
    }

    fun append(data: T) {
        if (tail == null) {
            push(data)
        } else {
            tail?.next = Node<T>(data=data)
            tail = tail?.next
            size++
        }
    }

    fun nodeAt(index: Int): Node<T> {
        var iter: Node<T>? = head
        var count: Int = 0
        while (iter != null && count < index) {
            iter = iter.next
            count++
        }
        return iter!!
    }

    fun insert(data: T, preNode: Node<T>): Node<T> {
        if (preNode == tail) {
            append(data)
            return tail!!
        } else {
            preNode.next = Node<T>(data=data, next=preNode.next)
            size++
            return preNode.next!!
        }
    }
}