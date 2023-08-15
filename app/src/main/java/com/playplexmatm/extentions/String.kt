package com.playplexmatm.extentions

fun generateUniqueRandom(): Int {
    val usedNumbers = mutableSetOf<Int>()
    val availableNumbers = (0..99).toMutableSet()
    availableNumbers.removeAll(usedNumbers)

    if (availableNumbers.isEmpty()) {
        usedNumbers.clear()
        availableNumbers.addAll((0..99))
    }

    val randomNumber = availableNumbers.random()
    usedNumbers.add(randomNumber)

    return randomNumber
}