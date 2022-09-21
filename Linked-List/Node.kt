data class Node<T>(var data: T, var next: Node<T>? = null) {
    override fun toString(): String {
        if (next != null) {
            return "$data -> ${next.toString()}"
        } else {
            return "$data"
        }
    }

}