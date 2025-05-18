package serie2.part1_2
import kotlin.random.Random

fun minimum(maxHeap: Array<Int>, heapSize: Int): Int? {
    if (heapSize == 0) return null

    val firstLeafIndex = heapSize / 2
    var min = maxHeap[firstLeafIndex]

    for (i in firstLeafIndex + 1 until heapSize) {
        if (maxHeap[i] < min) {
            min = maxHeap[i]
        }
    }

    return min
}