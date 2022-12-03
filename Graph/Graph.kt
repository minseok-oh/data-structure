data class Vertex<T>(val index: Int, val data: T)
data class Edge<T>(
    val source: Vertex<T>,
    val destination: Vertex<T>,
    val weight: Double? = null
)

interface Graph<T> {
    fun createVertex(data: T): Vertex<T>
    fun addDirectedEdge(source: Vertex<T>,
                        destination: Vertex<T>,
                        weight: Double?)
    fun addUndirectedEdge(source: Vertex<T>,
                        destination: Vertex<T>,
                        weight: Double?) {
        addDirectedEdge(source, destination, weight)
        addDirectedEdge(destination, source, weight)
    }
    fun add(edge: EdgeType,
            source: Vertex<T>,
            destination: Vertex<T>,
            weight: Double?) {
        when (edge) {
            EdgeType.DIRECTED -> addDirectedEdge(source, destination, weight)
            EdgeType.UNDIRECTED -> addUndirectedEdge(source, destination, weight)
        }
    }
    fun edges(source: Vertex<T>): ArrayList<Edge<T>>
    fun weight(source: Vertex<T>,
                destination: Vertex<T>): Double?
    fun numberOfPaths(
        source: Vertex<T>,
        destination: Vertex<T>
    ): Int {
        val visited: MutableSet<Vertex<T>> = mutableSetOf()
        return paths(source, destination, visited)
    }
    fun paths(
        source: Vertex<T>, destination: Vertex<T>,
        visited: MutableSet<Vertex<T>>, printPath: Boolean = true
    ): Int {
        var ct = 0
        visited.add(source)
        if (source == destination) {
            ct = 1
            if (printPath) {
                visited.forEach {
                    print("->${it.data}")
                }
                println()
            }
        } else {
            val neighbors = edges(source)
            neighbors.forEach { edge ->
                if (edge.destination !in visited)
                    ct == paths(edge.destination, destination, visited)
            }
        }
        visited.remove(source)
        return ct
    }

    fun breadthFirstSearch(source: Vertex<T>): ArrayList<Vertex<T>> {
        val queue = LinkedListQueue<Vertex<T>>()
        val enqueued = ArrayList<Vertex<T>>()
        val visited = ArrayList<Vertex<T>>()

        queue.enqueue(source)
        enqueued.add(source)
        while (true) {
            val vertex = queue.dequeue() ?: break
            visited.add(vertex)
            val neighborEdges = edges(vertex)
            neighborEdges.forEach {
                if (!enqueued.contains(it.destination)) {
                    queue.enqueue(it.destination)
                    enqueued.add(it.destination)
                }
            }
        }
        return visited
    }

    abstract val allVertices: ArrayList<Vertex<T>>
    fun isDisconnected(): Boolean {
        val firstVertex = allVertices.firstOrNull() ?: return false
        val visited = breadthFirstSearch(firstVertex)
        allVertices.forEach {
            if (!visited.contains(it)) return true
        }
        return false
    }

    fun depthFirstSearch(source: Vertex<T>): ArrayList<Vertex<T>> {
        val stack = Stack<Vertex<T>>()
        val visited = arrayListOf<Vertex<T>>()
        val pushed = arrayListOf<Vertex<T>>()

        outer@ while (true) {
            if (stack.isEmpty) break
            val vertex = stack.peek()!!
            val neighbors = edges(vertex)
            if (neighbors.isEmpty()) {
                stack.pop()
                continue
            }
            for (i in 0 until neighbors.size) {
                val destination = neighbors[i].destination
                if (destination !in pushed) {
                    stack.push(destination)
                    pushed.add(destination)
                    visited.add(destination)
                    continue@outer
                }
            }
            stack.pop()
        }
        return visited
    }
    fun hasCycle(source: Vertex<T>): Boolean {
        val pushed = mutableSetOf<Vertex<T>>()
        return hasCycle(source, pushed)
    }
    private fun hasCycle(source: Vertex<T>, pushed:MutableSet<Vertex<T>>):
            Boolean {
        pushed.add(source)
        val neighbors = edges(source)
        neighbors.forEach {
            if (it.destination !in pushed && hasCycle(it.destination,
                pushed)) {
                return true
            } else if (it.destination in pushed) {
                return true
            }
        }
        pushed.remove(source)
        return false
    }
}
enum class EdgeType {
    DIRECTED,
    UNDIRECTED
}

class AdjacencyList<T>: Graph<T> {
    private val adjacencies: HashMap<Vertex<T>,
            ArrayList<Edge<T>>> = HashMap()

    override fun createVertex(data: T): Vertex<T> {
        val vertex = Vertex(adjacencies.count(), data)
        adjacencies[vertex] = ArrayList()
        return vertex
    }

    override fun addDirectedEdge(
        source: Vertex<T>, destination: Vertex<T>,
        weight: Double?) {
        val edge = Edge(source, destination, weight)
        adjacencies[source]?.add(edge)
    }

    override fun edges(source: Vertex<T>) =
        adjacencies[source] ?: arrayListOf()

    override fun weight(source: Vertex<T>, destination: Vertex<T>): Double? {
        return edges(source).firstOrNull { it.destination ==
            destination }?.weight
    }

    override fun toString(): String {
        return buildString {
            adjacencies.forEach { (vertex, edges) ->
                val edgeString = edges.joinToString { it.destination.data.toString() }
                append("${vertex.data} ---> [ $edgeString ]\n")
            }
        }
    }

    override val allVertices: ArrayList<Vertex<T>>
        get() = ArrayList(adjacencies.keys)
    fun copyVertices(graph: AdjacencyList<T>) {
        graph.allVertices.forEach {
            adjacencies[it] = arrayListOf()
        }
    }
}

class AdjacencyMatrix<T> : Graph<T> {
    private val vertices = arrayListOf<Vertex<T>>()
    private val weights = arrayListOf<ArrayList<Double?>>()

    override fun createVertex(data: T): Vertex<T> {
        val vertex = Vertex(vertices.count(), data)
        vertices.add(vertex)
        weights.forEach {
            it.add(null)
        }
        val row = ArrayList<Double?>(vertices.count())
        repeat(vertices.count()) {
            row.add(null)
        }
        weights.add(row)
        return vertex
    }

    override fun addDirectedEdge(source: Vertex<T>, destination: Vertex<T>, weight: Double?) {
        weights[source.index][destination.index] = weight
    }

    override fun edges(source: Vertex<T>): ArrayList<Edge<T>> {
        val edges = arrayListOf<Edge<T>>()
        (0 until weights.size).forEach { column ->
            val weight = weights[source.index][column]
            if (weight != null) {
                edges.add(Edge(source, vertices[column], weight))
            }
        }
        return edges
    }

    override fun weight(source: Vertex<T>, destination: Vertex<T>): Double? {
        return weights[source.index][destination.index]
    }

    override fun toString(): String {
        val verticesDescription = vertices.joinToString(separator = "\n") {
            "${it.index}: ${it.data}"
        }
        val grid = weights.map { row ->
            buildString {
                (0 until weights.size).forEach { columnIndex ->
                    val value = row[columnIndex]
                    if (value != null) {
                        append("$value\t")
                    } else {
                        append("X\t\t")
                    }
                }
            }
        }
        val edgesDescription = grid.joinToString("\n")
        return "$verticesDescription\n\n$edgesDescription"
    }

    override val allVertices: ArrayList<Vertex<T>>
        get() = vertices
}