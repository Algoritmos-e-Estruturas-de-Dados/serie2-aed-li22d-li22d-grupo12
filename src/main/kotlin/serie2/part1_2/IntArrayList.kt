package serie2.part1_2

class IntArrayList(val k: Int) {
    val list = IntArray(k)
    var head = 0 // 1º elemento da lista
    var tail = 0 // último elemento da lista
    var size = 0 // tamanho da lista
    var change = 0 // alteração a valores antes de qualquer adição

    fun append(x: Int): Boolean {
        if (size == k) return false
        list[tail] = x - change  // Store the original value (subtract current change)
        tail = (tail + 1) % k
        size++
        return true
    }

    fun get(n: Int): Int? {
        if (n < 0 || n >= size) return null
        return list[(head + n) % k] + change
    }

    fun addToAll(x: Int) {
        change += x
    }

    fun remove(): Boolean {
        if (size == 0) return false
        head = (head + 1) % k
        size--
        return true
    }
}


