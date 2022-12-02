import java.lang.Math.pow
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

//BubbleSort
fun <T: Comparable<T>> ArrayList<T>.bubbleSort(showPasses: Boolean = false) {
    if (this.size < 2) return

    for (end in (1 until this.size).reversed()) {
        var swapped = false
        for (current in 0 until end) {
            if (this[current] > this[current + 1]) {
                this.swapAt(current, current + 1)
                swapped = true
            }
        }
        if (showPasses) println(this)
        if (!swapped) return
    }
}

//SelectionSort
fun <T: Comparable<T>> ArrayList<T>.selectionSort(showPasses: Boolean = false) {
    if (this.size < 2) return

    for (current in 0 until (this.size - 1)) {
        var lowest = current
        for (other in (current + 1) until this.size) {
            if (this[lowest] > this[other]) {
                lowest = other
            }
        }
        if (lowest != current) {
            this.swapAt(lowest, current)
        }
        if (showPasses) println(this)
    }
}

//InsertionSort
fun <T: Comparable<T>> ArrayList<T>.insertionSort(showPasses: Boolean = false) {
    if (this.size < 2) return

    for (current in 1 until this.size) {
        for (shifting in (1..current).reversed()) {
            if (this[shifting] < this[shifting - 1]) {
                this.swapAt(shifting, shifting - 1)
            } else {
                break
            }
        }
        if (showPasses) println(this)
    }
}

//MergeSort
fun <T: Comparable<T>> List<T>.mergeSort(): List<T> {
    if (this.size < 2) return this
    val middle = this.size / 2

    val left = this.subList(0, middle).mergeSort()
    val right = this.subList(middle, this.size).mergeSort()

    return merge(left, right)
}

private fun <T: Comparable<T>> merge(left: List<T>, right: List<T>): List<T> {
    var leftIndex = 0
    var rightIndex = 0

    val result = mutableListOf<T>()

    while (leftIndex < left.size && rightIndex < right.size) {
        val leftElement = left[leftIndex]
        val rightElement = right[rightIndex]
        if (leftElement < rightElement) {
            result.add(leftElement)
            leftIndex += 1
        } else if (leftElement > rightElement) {
            result.add(rightElement)
            rightIndex += 1
        } else {
            result.add(leftElement)
            leftIndex += 1
            result.add(rightElement)
            rightIndex += 1
        }
    }
    if (leftIndex < left.size) {
        result.addAll(left.subList(leftIndex, left.size))
    }
    if (rightIndex < right.size) {
        result.addAll(right.subList(rightIndex, right.size))
    }
    return result
}

private fun <T> Iterator<T>.nextOrNull(): T? {
    return if (this.hasNext()) this.next() else null
}

fun <T: Comparable<T>> merge(
    first: Iterable<T>,
    second: Iterable<T>
): Iterable<T> {
    val result = mutableListOf<T>()
    val firstIterator = first.iterator()
    val secondIterator = second.iterator()

    if (!firstIterator.hasNext()) return second
    if (!secondIterator.hasNext()) return first

    var firstEl = firstIterator.nextOrNull()
    var secondEl = secondIterator.nextOrNull()
    while (firstEl != null && secondEl != null) {
        when {
            firstEl < secondEl -> {
                result.add(firstEl)
                firstEl = firstIterator.nextOrNull()
            }
            secondEl < firstEl -> {
                result.add(secondEl)
                secondEl = secondIterator.nextOrNull()
            }
            else -> {
                result.add(firstEl)
                result.add(secondEl)
                firstEl = firstIterator.nextOrNull()
                secondEl = secondIterator.nextOrNull()
            }
        }
    }
    while (firstEl != null) {
        result.add(firstEl)
        firstEl = firstIterator.nextOrNull()
    }
    while (secondEl != null) {
        result.add(secondEl)
        secondEl = secondIterator.nextOrNull()
    }
    return result
}

//RadixSort
fun MutableList<Int>.radixSort() {
    val base = 10
    var done = false
    var digits = 1
    while (!done) {
        done = true
        val buckets = arrayListOf<MutableList<Int>>().apply {
            for (i in 0 .. 9) {
                this.add(arrayListOf())
            }
        }
        this.forEach {
            number ->
            val remainingPart = number / digits
            val digit = remainingPart % base
            buckets[digit].add(number)
            if (remainingPart > 0) {
                done = false
            }
        }
        digits *= base
        this.clear()
        this.addAll(buckets.flatten())
    }
}

fun Int.digits(): Int {
    var count = 0
    var num = this
    while (num != 0) {
        count += 1
        num /= 10
    }
    return count
}

fun Int.digit(atPosition: Int): Int? {
    if (atPosition > digits()) return null
    var num = this
    val correctedPosition = (atPosition + 1).toDouble()
    while (num / (pow(10.0, correctedPosition).toInt()) != 0) {
        num /= 10
    }
    return num % 10
}

private fun msdRadixSorted(list: MutableList<Int>, position: Int): MutableList<Int> {
    if (position > list.maxDigits()) return list

    val buckets = arrayListOf<MutableList<Int>>().apply {
        for (i in 0 .. 9) {
            this.add(arrayListOf())
        }
    }
    val priorityBucket = arrayListOf<Int>()
    list.forEach { number ->
        val digit = number.digit(position)
        if (digit == null) {
            priorityBucket.add(number)
            return@forEach
        }
        buckets[digit].add(number)
    }
    priorityBucket.addAll(
        buckets.reduce { result, bucket ->
            if (bucket.isEmpty()) return@reduce result
            result.addAll(msdRadixSorted(bucket, position + 1))
            result
        })
    return priorityBucket
}
fun MutableList<Int>.lexicographicalSort() {
    val sorted = msdRadixSorted(this, 0)
    this.clear()
    this.addAll(sorted)
}
private fun MutableList<Int>.maxDigits(): Int {
    return this.maxOrNull()?.digits() ?: 0
}

//HeapSort
private fun leftChildIndex(index: Int) = (2 * index) + 1
private fun rightChildIndex(index: Int) = (2 * index) + 2
fun <T> Array<T>.siftDown(
    index: Int,
    upTo: Int,
    comparator: Comparator<T>
) {
    var parent = index
    while (true) {
        val left = leftChildIndex(parent)
        val right = rightChildIndex(parent)
        var candidate = parent
        if (left < upTo &&
                comparator.compare(this[left], this[candidate]) > 0) {
            candidate = left
        }
        if (right < upTo &&
                comparator.compare(this[right], this[candidate]) > 0) {
            candidate = right
        }
        if (candidate == parent) {
            return
        }
        this.swapAt(parent, candidate)
        parent = candidate
    }
}
fun <T> Array<T>.heapify(comparator: Comparator<T>) {
    if (this.isNotEmpty()) {
        (this.size / 2 downTo 0).forEach {
            this.siftDown(it, this.size, comparator)
        }
    }
}
fun <T> Array<T>.heapSort(comparator: Comparator<T>) {
    this.heapify(comparator)
    for (index in this.indices.reversed()) {
        this.swapAt(0, index)
        siftDown(0, index, comparator)
    }
}

//QuickSort
fun <T: Comparable<T>> MutableList<T>.partitionLomuto(low: Int, high: Int): Int {
    val pivot = this[high]
    var i = low
    for (j in low until high) {
        if (this[j] <= pivot) {
            this.swapAt(i, j)
            i += 1
        }
    }
    this.swapAt(i, high)
    return i
}
fun <T: Comparable<T>> MutableList<T>.quicksortLomuto(low: Int, high: Int) {
    if (low < high) {
        val pivot = this.partitionLomuto(low, high)
        this.quicksortLomuto(low, pivot - 1)
        this.quicksortLomuto(pivot + 1, high)
    }
}

fun <T:Comparable<T>> MutableList<T>.partitionHoare(low: Int, high: Int): Int {
    val pivot = this[low]
    var i = low - 1
    var j = high + 1
    while (true) {
        do {
            j -= 1
        } while (this[j] > pivot)
        do {
            i += 1
        } while (this[i] < pivot)
        if (i < j) {
            this.swapAt(i, j)
        } else {
            return j
        }
    }
}
fun <T:Comparable<T>> MutableList<T>.quicksortHoare(low: Int, high: Int) {
    if (low < high) {
        val p = partitionHoare(low, high)
        this.quicksortHoare(low, p)
        this.quicksortHoare(p + 1, high)
    }
}

fun <T: Comparable<T>> MutableList<T>.partitionDutchFlag(
    low: Int, high: Int, pivotIndex: Int): Pair<Int, Int> {
    val pivot = this[pivotIndex]
    var smaller = low
    var equal = low
    var larger = high
    while (equal <= larger) {
        if (this[equal] < pivot) {
            this.swapAt(smaller, equal)
            smaller += 1
            equal += 1
        } else if (this[equal] == pivot) {
            equal += 1
        } else {
            this.swapAt(equal, larger)
            larger -= 1
        }
    }
    return Pair(smaller, larger)
}
fun <T:Comparable<T>> MutableList<T>.quicksortDutchFlag(low: Int, high: Int) {
    if (low < high) {
        val middle = partitionDutchFlag(low, high, high)
        this.quicksortDutchFlag(low, middle.first - 1)
        this.quicksortDutchFlag(middle.second + 1, high)
    }
}

fun <T: Comparable<T>> MutableList<T>.quicksortIterativeLomuto(low: Int, high: Int) {
    val stack = Stack<Int>()
    stack.push(low)
    stack.push(high)
    while (!stack.isEmpty()) {
        val end = stack.pop() ?: continue
        val start = stack.pop() ?: continue
        val p = this.partitionLomuto(start, end)
        if ((p -1) > start) {
            stack.push(start)
            stack.push(p - 1)
        }
        if ((p + 1) < end) {
            stack.push(p + 1)
            stack.push(end)
        }
    }
}