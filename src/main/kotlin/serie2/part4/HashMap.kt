package serie2.part4

class HashMap<K, V>(initialCapacity: Int = 16, val loadFactor: Float = 0.75f) : MutableMap<K, V> {
    private class HashNode<K, V>(
        override val key: K,
        override var value: V,
        var next: HashNode<K, V>? = null
    ) : MutableMap.MutableEntry<K, V> {
        val hc = key.hashCode()

        override fun setValue(newValue: V): V {
            val oldValue = value
            value = newValue
            return oldValue
        }
    }

    private var table: Array<HashNode<K, V>?> = arrayOfNulls(initialCapacity)
    override var size: Int = 0
    override val capacity: Int get() = table.size

    private fun index(key: K): Int {
        return (key.hashCode() and 0x7FFFFFFF) % table.size
    }

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
            if (node.key == key) {
                return node.value
            }
            node = node.next
        }

        return null
    }

    override fun put(key: K, value: V): V? {
        // Check if we need to expand the table
        if (size >= loadFactor * table.size) {
            expand()
        }

        val idx = index(key)
        var node = table[idx]

        // Search for the key in the chain
        while (node != null) {
            if (node.key == key) {
                // Key found - update value and return old value
                val oldValue = node.value
                node.value = value
                return oldValue
            }
            node = node.next
        }

        // Key not found - insert new node
        val newNode = HashNode(key, value, table[idx])
        table[idx] = newNode
        size++

        return null
    }

    override fun iterator(): Iterator<MutableMap.MutableEntry<K, V>> {
        return object : Iterator<MutableMap.MutableEntry<K, V>> {
            private var currentIndex = 0
            private var currentNode: HashNode<K, V>? = null

            init {
                findNextNode()
            }

            private fun findNextNode() {
                // If we have a current node, move to its next
                if (currentNode != null) {
                    currentNode = currentNode?.next
                }

                // If current node is null, find next bucket with nodes
                while (currentNode == null && currentIndex < table.size) {
                    currentNode = table[currentIndex]
                    currentIndex++
                }
            }

            override fun hasNext(): Boolean {
                return currentNode != null
            }

            override fun next(): MutableMap.MutableEntry<K, V> {
                if (!hasNext()) throw NoSuchElementException()

                val node = currentNode!!
                findNextNode()
                return node
            }
        }
    }
}