interface StackInterface<Element> {
    val count: Int
        get

    fun peek(): Element?
    val isEmpty: Boolean
        get() = count == 0
    fun push(element: Element)
    fun pop(): Element?
}

class Stack<Element>: StackInterface<Element> {
    private val storage = arrayListOf<Element>()
    override fun toString(): String = buildString {
        appendLine("----top----")
        storage.asReversed().forEach {
            appendLine("$it")
        }
        appendLine("-----------")
    }

    override fun push(element: Element) {
        storage.add(element)
    }

    override val count: Int
        get() = storage.size

    override fun peek(): Element? {
        return storage.lastOrNull()
    }

    override fun pop(): Element? {
        if (storage.size == 0) {
            return null
        }
        return storage.removeAt(storage.size - 1)
    }
}