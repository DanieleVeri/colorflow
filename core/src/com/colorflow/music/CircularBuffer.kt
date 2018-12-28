package com.colorflow.music

class CircularBuffer(val numRows: Int, val numColumns: Int) {

    val matrix: Array<DoubleArray> = Array(numRows) { DoubleArray(numColumns) }
    var rowIndex: Int = 0
        private set

    fun incRowIndex() {
        rowIndex++
        rowIndex %= numRows
    }

    fun getRow(row: Int): DoubleArray {
        return matrix[row].clone()
    }

    fun getColumn(column: Int): DoubleArray {
        val col = DoubleArray(numRows)
        for (i in rowIndex until rowIndex + numRows) {
            col[i - rowIndex] = matrix[i % numRows][column]
        }
        return col
    }
}
