package serie2.part3

class Node<T> (
    var value: T = Any() as T,
    var next: Node<T>? = null,
    var previous: Node<T>? = null) {
}

fun splitEvensAndOdds(list: Node<Int>){
    var current = list.next
    while (current != list){
        val nextNode = current!!.next
        if(current.value % 2 == 0) {
            current.previous!!.next = current.next
            current.next!!.previous = current.previous

            current.next = list.next
            current.previous = list
            list.next!!.previous = current
            list.next = current
        }
        current = nextNode
    }
}

fun <T> intersection(list1: Node<T>, list2: Node<T>, cmp: Comparator<T>) : Node<T>?{
    var current1 = list1.next
    var current2 = list2.next
    var nodeHead:Node<T>? = null
    var nodeTail:Node<T>? = null
    while(current1 != list1 && current2 != list2){
        val comp = cmp.compare(current1!!.value, current2!!.value)
        when{
            comp < 0 -> current1 = current1.next
            comp > 0 -> current2 = current2.next
            else ->{ //current1 == current2
                val node = current1 // retira-se o nó de list1 e fica na lista de retorno
                val value = node.value

                while (current1 != list1 && cmp.compare(current1!!.value, value) == 0){ // retira todos os nós iguais em list1
                    val next = current1.next
                    current1.previous!!.next = current1.next
                    current1.next!!.previous = current1.previous
                    current1 = next
                }
                while (current2 != list2 && cmp.compare(current2!!.value, value) == 0){ // retira todos os nós igauis em list2
                    val next = current2.next
                    current2.previous!!.next = current2.next
                    current2.next!!.previous = current2.previous
                    current2 = next
                }
                node.previous = null //disconecta o nó de list1
                node.next = null

                if (nodeHead == null){ //faz caso o nó seja o 1º
                    nodeHead = node
                    nodeTail = node
                }
                else{
                    nodeTail!!.next = node
                    node.previous = nodeTail
                    nodeTail = node
                }
            }
        }
    }
    return nodeHead
}