    interface MutableMap<K,V>: Iterable<MutableMap.MutableEntry<K,V>> {
        interface MutableEntry<K, V> {
            val key: K
            var value: V
            fun setValue(newValue: V): V
        }
        val size: Int
        operator fun get(key: K): V?
        fun put(key: K, value: V): V?
        fun clear()
    }

    class HashMap<K, V> : MutableMap<K, V> {
        private class HashNode<K, V>(override val key: K, override var value: V, var next: HashNode<K, V>? = null) : MutableMap.MutableEntry<K, V> {
            override fun setValue(newValue: V): V {
                val oldValue = value
                value = newValue
                return oldValue
            }
        }

        private var table: Array<HashNode<K, V>?> = arrayOfNulls(16)
        override var size: Int = 0

        private fun index(key: K): Int = (key.hashCode() and 0x7FFFFFFF) % table.size

        private fun expand() {
            val oldTable = table
            table = arrayOfNulls(oldTable.size * 2)
            size = 0

            for (node in oldTable) {
                var current = node
                while (current != null) {
                    put(current.key, current.value)
                    current = current.next
                }
            }
        }

        override operator fun get(key: K): V? {
            val idx = index(key)
            var node = table[idx]

            while (node != null) {
                if (node.key == key) return node.value
                node = node.next
            }
            return null
        }

        override fun put(key: K, value: V): V? {
            if (size >= table.size * 0.75) expand()

            val idx = index(key)
            var node = table[idx]

            while (node != null) {
                if (node.key == key) {
                    val oldValue = node.value
                    node.value = value
                    return oldValue
                }
                node = node.next
            }

            val newNode = HashNode(key, value, table[idx])
            table[idx] = newNode
            size++
            return null
        }

        override fun clear() {
            table = arrayOfNulls(16)
            size = 0
        }

        override fun iterator(): Iterator<MutableMap.MutableEntry<K, V>> {
            return object : Iterator<MutableMap.MutableEntry<K, V>> {
                private var currentIndex = 0
                private var currentNode: HashNode<K, V>? = null

                init { findNextNode() }

                private fun findNextNode() {
                    if (currentNode != null) {
                        currentNode = currentNode?.next
                    }

                    while (currentNode == null && currentIndex < table.size) {
                        currentNode = table[currentIndex]
                        currentIndex++
                    }
                }

                override fun hasNext(): Boolean = currentNode != null

                override fun next(): MutableMap.MutableEntry<K, V> {
                    if (!hasNext()) throw NoSuchElementException()
                    val node = currentNode!!
                    findNextNode()
                    return node
                }
            }
        }
    }

    class ProcessPointsCollections {
        private val map1 = HashMap<String, Point>()
        private val map2 = HashMap<String, Point>()

        fun load(file1: String, file2: String) {
            map1.clear()
            map2.clear()
            loadPoints(file1, map1)
            loadPoints(file2, map2)
            println("Loaded ${countEntries(map1)} points from $file1 and ${countEntries(map2)} from $file2")
        }

        fun union(output: String) {
            val result = HashMap<String, Point>()
            addAllEntries(map1, result)
            addUniqueEntries(map2, result)
            savePoints(output, result)
            println("Union saved to $output")
        }

        fun intersection(output: String) {
            val result = HashMap<String, Point>()
            addCommonEntries(map1, map2, result)
            savePoints(output, result)
            println("Intersection saved to $output")
        }

        fun difference(output: String) {
            val result = HashMap<String, Point>()
            addUniqueEntries(map1, map2, result)
            savePoints(output, result)
            println("Difference saved to $output")
        }

        private fun countEntries(map: HashMap<String, Point>): Int {
            var count = 0
            for (entry in map) { count++ }
            return count
        }

        private fun loadPoints(filename: String, targetMap: HashMap<String, Point>) {
            val lines = readFileLines(filename)
            for (line in lines) {
                if (line.startsWith("v ")) {
                    val parts = splitLine(line)
                    if (parts.size == 4) {
                        val id = parts[1]
                        val x = parts[2].toDoubleOrNull()
                        val y = parts[3].toDoubleOrNull()
                        if (x != null && y != null) {
                            targetMap.put(id, Point(id, x, y))
                        }
                    }
                }
            }
        }

        private fun savePoints(filename: String, points: HashMap<String, Point>) {
            val content = buildString {
                for (entry in points) {
                    val point = entry.value
                    append("v ${point.id} ${point.x} ${point.y}\n")
                }
            }
            writeFileContent(filename, content)
        }

        private fun addAllEntries(source: HashMap<String, Point>, target: HashMap<String, Point>) {
            for (entry in source) {
                target.put(entry.key, entry.value)
            }
        }

        private fun addUniqueEntries(source: HashMap<String, Point>, target: HashMap<String, Point>) {
            for (entry in source) {
                if (target.get(entry.key) == null) {
                    target.put(entry.key, entry.value)
                }
            }
        }

        private fun addCommonEntries(source1: HashMap<String, Point>, source2: HashMap<String, Point>, target: HashMap<String, Point>) {
            for (entry in source1) {
                if (source2.get(entry.key) != null) {
                    target.put(entry.key, entry.value)
                }
            }
        }

        private fun addUniqueEntries(source: HashMap<String, Point>, exclusion: HashMap<String, Point>, target: HashMap<String, Point>) {
            for (entry in source) {
                if (exclusion.get(entry.key) == null) {
                    target.put(entry.key, entry.value)
                }
            }
        }

        // Implementação básica de leitura/escrita de arquivos
        private fun readFileLines(filename: String): List<String> {
            val lines = mutableListOf<String>()
            try {
                val file = java.io.File(filename)
                val reader = file.bufferedReader()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    line?.let { lines.add(it) }
                }
                reader.close()
            } catch (e: Exception) {
                println("Error reading file: ${e.message}")
            }
            return lines
        }

        private fun writeFileContent(filename: String, content: String) {
            try {
                val file = java.io.File(filename)
                file.writeText(content)
            } catch (e: Exception) {
                println("Error writing file: ${e.message}")
            }
        }

        private fun splitLine(line: String): List<String> {
            val parts = mutableListOf<String>()
            var current = ""
            var inWhitespace = true

            for (c in line) {
                if (c.isWhitespace()) {
                    if (!inWhitespace) {
                        parts.add(current)
                        current = ""
                    }
                    inWhitespace = true
                } else {
                    current += c
                    inWhitespace = false
                }
            }

            if (current.isNotEmpty()) {
                parts.add(current)
            }

            return parts
        }
    }

    data class Point(val id: String, val x: Double, val y: Double)

    fun main() {
        val processor = ProcessPointsCollections()
        println("Point Collection Processor")
        println("Commands: load <file1> <file2> | union|intersection|difference <output> | exit")

        while (true) {
            print("> ")
            val input = readLine()?.trim()?.split(Regex("\\s+")) ?: continue

            when (input.firstOrNull()?.lowercase()) {
                "exit" -> return
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
                else -> println("Unknown command")
            }
        }
    }