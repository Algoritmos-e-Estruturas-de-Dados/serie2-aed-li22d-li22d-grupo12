package Problem_1Implementação

import java.io.File

data class Point(val id: String, val x: Double, val y: Double)

class ProcessPointsCollections {
    private val map1 = mutableMapOf<String, Point>()
    private val map2 = mutableMapOf<String, Point>()

    fun load(file1: String, file2: String) {
        val startTime = System.currentTimeMillis()

        map1.clear()
        map2.clear()
        loadPoints(file1, map1)
        loadPoints(file2, map2)

        val duration = System.currentTimeMillis() - startTime
        println("Loaded ${map1.size} points from $file1 and ${map2.size} from $file2 in $duration ms")
    }

    fun union(output: String) {
        val startTime = System.currentTimeMillis()

        val result = HashMap(map1)
        map2.forEach { (id, point) -> result.putIfAbsent(id, point) }
        savePoints(output, result.values)

        val duration = System.currentTimeMillis() - startTime
        println("Union saved to $output in $duration ms")
    }

    fun intersection(output: String) {
        val startTime = System.currentTimeMillis()

        val result = map1.filter { (id, _) -> map2.containsKey(id) }.values
        savePoints(output, result)

        val duration = System.currentTimeMillis() - startTime
        println("Intersection saved to $output in $duration ms")
    }

    fun difference(output: String) {
        val startTime = System.currentTimeMillis()

        val result = map1.filter { (id, _) -> !map2.containsKey(id) }.values
        savePoints(output, result)

        val duration = System.currentTimeMillis() - startTime
        println("Difference saved to $output in $duration ms")
    }

    private fun loadPoints(filename: String, targetMap: MutableMap<String, Point>) {
        File(filename).useLines { lines ->
            lines.forEach { line ->
                if (line.startsWith("v ")) {
                    val parts = line.split(Regex("\\s+"))
                    if (parts.size == 4) {
                        val id = parts[1]
                        val x = parts[2].toDoubleOrNull()
                        val y = parts[3].toDoubleOrNull()
                        if (x != null && y != null) {
                            targetMap[id] = Point(id, x, y)
                        }
                    }
                }
            }
        }
    }

    private fun savePoints(filename: String, points: Collection<Point>) {
        File(filename).printWriter().use { out ->
            points.forEach { point ->
                out.println("v ${point.id} ${point.x} ${point.y}")
            }
        }
    }
}

fun main() {
    val processor = ProcessPointsCollections()
    println("Point Collection Processor")
    println("Commands: load <file1> <file2> | union|intersection|difference <output> | exit")

    var running = true
    while (running) {
        print("> ")
        val input = readLine()?.trim()?.split(Regex("\\s+")) ?: emptyList()

        when (input.firstOrNull()?.lowercase()) {
            "exit" -> running = false
            "load" -> {
                if (input.size == 3) {
                    processor.load(input[1], input[2])
                } else {
                    println("Usage: load <file1> <file2>")
                }
            }
            "union", "intersection", "difference" -> {
                if (input.size == 2) {
                    when (input[0].lowercase()) {
                        "union" -> processor.union(input[1])
                        "intersection" -> processor.intersection(input[1])
                        "difference" -> processor.difference(input[1])
                    }
                } else {
                    println("Usage: ${input[0]} <outputFile>")
                }
            }
            else -> if (input.isNotEmpty()) println("Unknown command")
        }
    }
}