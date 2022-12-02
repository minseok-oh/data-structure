fun <T> ArrayList<T>.swapAt(first: Int, second: Int) {
    val aux = this[first]
    this[first] = this[second]
    this[second] = aux
}
fun <T> Array<T>.swapAt(first: Int, second: Int) {
    val aux = this[first]
    this[first] = this[second]
    this[second] = aux
}
fun <T> MutableList<T>.swapAt(first: Int, second: Int) {
    val aux = this[first]
    this[first] = this[second]
    this[second] = aux
}