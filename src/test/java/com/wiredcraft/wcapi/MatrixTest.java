package com.wiredcraft.wcapi;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MatrixTest {

    public boolean testMatrix(int[][] mat, int target) {
        int m = mat[0].length;
        int i = 0, j = m - 1;

        while (true) {
            int val = mat[i][j];
            if (val == target) {
                return true;
            } else if (val < target && i < m - 1) {
                i++;
            } else if (val > target && j > 0) {
                j--;
            } else {
                return false;
            }
        }
    }

    @Test
    public void testMat() {
        //
        MatrixTest t = new MatrixTest();
        int[][] mat = {
                {1, 4, 7, 11, 15},
                {2, 5, 8, 12, 19},
                {3, 6, 9, 16, 22},
                {10, 13, 14, 17, 24},
                {18, 21, 23, 26, 30}};

        Assertions.assertFalse(t.testMatrix(mat, 20));
        Assertions.assertTrue(t.testMatrix(mat, 5));
    }
}
