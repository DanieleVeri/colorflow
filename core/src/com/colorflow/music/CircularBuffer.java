package com.colorflow.music;

public class CircularBuffer {

    private double[][] data;
    private int rows, columns;
    private int circularIndex;

    public CircularBuffer(int rows, int columns) {
        this.data = new double[rows][columns];
        this.rows = rows;
        this.columns = columns;
        this.circularIndex = 0;
    }

    public int getNumRows() {
        return rows;
    }

    public int getNumColumns() {
        return columns;
    }

    public int getRowIndex() {
        return circularIndex;
    }

    public void incRowIndex() {
        circularIndex ++;
        circularIndex %= rows;
    }

    public double[] getRow(int row) {
        return data[row].clone();
    }

    public double[] getColumn(int column) {
        double[] col = new double[rows];
        for(int i = circularIndex; i < circularIndex + rows; i++) {
            col[i - circularIndex] = data[i % rows][column];
        }
        return col;
    }

    public double[][] getMatrix() {
        return data;
    }
}
