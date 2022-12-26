package com.mz.common.testing

//fun main() {
//    val sortedArray = mergeSort(listOf(4, 2, 5, 1, 3))
//    sortedArray.forEach { println(it) }
//}

fun mergeSort(list: List<Int>): List<Int> {
    // base case: if the list has less than 2 elements, it is already sorted
    if (list.size < 2) return list

    // split the list into two halves
    val middle = list.size / 2
    val left = list.subList(0, middle)
    val right = list.subList(middle, list.size)

    // sort the two halves
    val sortedLeft = mergeSort(left)
    val sortedRight = mergeSort(right)

    // merge the sorted halves back together
    return merge(sortedLeft, sortedRight)
}

fun merge(left: List<Int>, right: List<Int>): List<Int> {
    val result = mutableListOf<Int>()

    // merge the lists until one of them is empty
    var leftIndex = 0
    var rightIndex = 0
    while (leftIndex < left.size && rightIndex < right.size) {
        if (left[leftIndex] < right[rightIndex]) {
            result.add(left[leftIndex])
            leftIndex++
        } else {
            result.add(right[rightIndex])
            rightIndex++
        }
    }

    // append the remaining elements from the non-empty list
    if (leftIndex < left.size) result.addAll(left.subList(leftIndex, left.size))
    if (rightIndex < right.size) result.addAll(right.subList(rightIndex, right.size))

    return result
}
