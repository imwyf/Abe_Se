package com.duwei.util;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import org.apache.commons.math3.linear.*;

import java.text.NumberFormat;
import java.util.Arrays;

/**
 * @BelongsProject: Abe_Se
 * @BelongsPackage: com.duwei.util
 * @Author: duwei
 * @Date: 2023/5/11 9:10
 * @Description: 数学工具类
 */
public class MathUtils {
    static NumberFormat format = NumberFormat.getInstance();


    public static Element[] solverOnField(int[][] coefficients,int[] constants,Field field){
        int m = coefficients.length;
        int n = coefficients[0].length;
        double[][] coefficientDouble = new double[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                coefficientDouble[i][j] = coefficients[i][j];
            }
        }
        double[] constantDouble = new double[constants.length];
        for (int i = 0; i < constantDouble.length; i++) {
            constantDouble[i] = constants[i];
        }
        int[] solution = format(solvingEquations(coefficientDouble,constantDouble));
        Element[] result = new Element[solution.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = field.newElement(solution[i]).getImmutable();
        }
        return result;
    }

    public static double[] solvingEquations(double[][] coefficients, double[] constants) {
        RealMatrix coefficientMatrix = new Array2DRowRealMatrix(coefficients, false);
        DecompositionSolver solver = new QRDecomposition(coefficientMatrix).getSolver();

        RealVector constantVector = new ArrayRealVector(constants, false);
        RealVector solution = solver.solve(constantVector);
        return solution.toArray();
    }


    private static int[] format(double[] result){
        int[] formatResult = new int[result.length];
         for (int i = 0; i < result.length; i++) {
           String formatStr = format.format(result[i]);
           try {
               int formatNumber = Integer.parseInt(formatStr);
               formatResult[i] = formatNumber;
           }catch (NumberFormatException e){
               throw new RuntimeException(e);
           }
        }
         return formatResult;
    }

//    public static void main(String[] args) {
//        solvingEquations(new double[][]{
//                        {1, 0}, {1, 1}, {0, 0}, {0, 0}},
//                new double[]{1, 0, 0, 0});
//        System.out.println(Integer.parseInt("1.2"));
//    }
}
