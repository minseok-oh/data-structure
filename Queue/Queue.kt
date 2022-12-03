interface QueueInterface<T> {
    fun enqueue(element: T): Boolean
    fun dequeue(): T?
    val count: Int
        get
    val isEmpty: Boolean
        get() = count == 0
    fun peek(): T?

    fun reverse() {
        var aux = Stack<T>()
        var next = this.dequeue()
        while (next != null) {
            aux.push(next)
            next = this.dequeue()
        }
        var next2 = aux.pop()
        while (next2 != null) {
            this.enqueue(next2)
            next2 = aux.pop()
        }
    }
}

class ArrayListQueue<T> : QueueInterface<T> {
    private val list = arrayListOf<T>()

    override val count: Int
        get() = list.size
    override fun peek(): T? = list.getOrNull(0)
    override fun enqueue(element: T): Boolean {
        list.add(element)
        return true
    }
    override fun dequeue(): T? =
        if (isEmpty) null else list.removeAt(0)

    override fun toString(): String = list.toString()
}

class LinkedListQueue<T>: QueueInterface<T> {
    private val list = LinkedList<T>()
    private var size = 0
    override val count: Int
        get() = size

    override fun peek(): T? = list.nodeAt(0)?.value
    override fun enqueue(element: T): Boolean {
        list.append(element)
        size++
        return true
    }
    override fun dequeue(): T? {
        val firstnode = list.nodeAt(0) ?: return null
        size--
        return list.removeHead()
    }
    override fun toString(): String = list.toString()
}

class RingBufferQueue<T>(size: Int): QueueInterface<T> {
    private val ringBuffer: RingBuffer<T> = RingBuffer(size)
    override val count: Int
        get() = ringBuffer.count
    override fun peek(): T? = ringBuffer.first()

    override fun enqueue(element: T): Boolean =
        ringBuffer.write(element)
    override fun dequeue(): T? =
        if (isEmpty) null else ringBuffer.read()
    override fun toString(): String = ringBuffer.toString()
}

class RingBuffer<Element> (val size: Int) {
    private var elements = arrayOfNulls<Any>(size)
    private var read_point = 0
    private var write_point = 0
    var count: Int = 0
        private set

    fun first(): Element? {
        if (count > 0)
            return elements[read_point] as Element
        return null
    }

    fun write(element: Element): Boolean {
        if (count == size)
            return false
        elements.set(write_point, element)
        count += 1
        write_point = if (write_point == size - 1) 0 else write_point + 1
        return true
    }

    fun read(): Element? {
        if (count <= 0)
            return null
        var ret = elements[read_point] as Element
        read_point = if (read_point == size - 1) 0 else read_point + 1
        return ret
    }

    override fun toString(): String {
        var ret = "["
        var tmp_read_point = read_point
        var tmp_count = count
        while (tmp_read_point != write_point) {
            ret += " ${elements[tmp_read_point] as Element}"
            tmp_read_point += 1
            if (tmp_read_point == size - 1)
                tmp_read_point = 0
            tmp_count -= 1
        }
        ret += " ]"
        return ret
    }
}

abstract class AbstractPriorityQueue<T> : QueueInterface<T> {
    abstract val heap: AbstractHeap<T>

    override fun enqueue(element: T): Boolean {
        heap.insert(element)
        return true
    }
    override fun dequeue() = heap.remove()

    override val count: Int
        get() = heap.count

    override fun peek() = heap.peek()
}

class MaxPriorityQueue<T: Comparable<T>> : AbstractPriorityQueue<T>() {
    override val heap: MaxHeap<T> = MaxHeap<T>()
}
class PriorityQueue<T>(
    private val comparator: Comparator<T>
) : AbstractPriorityQueue<T>() {
    override val heap: Heap<T> = Heap(comparator)
}