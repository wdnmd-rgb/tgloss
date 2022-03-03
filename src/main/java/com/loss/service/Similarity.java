package com.loss.service;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Similarity {
    public static void main(String[] args) {

    }

    public static Map<String,Object> test(double[] xData, double[] yData) {
        /*用于测试*/
        int num = xData.length;
        double a = 0.0;
        double b = 0.0;
        double c = 0.0;
        double d = 0.0;
        double e = 0.0;
        double f = 0.0;
        double g = 0.0;
        double h = 0.0;
        for (int i = 0; i < (num - 3); i++) {
            double s = getPearsonCorrelationScore(new double[]{xData[i], xData[i + 1], xData[i + 2], xData[i + 3]}, new double[]{yData[i], yData[i + 1], yData[i + 2], yData[i + 3]});
            switch (i % 8){
                case 0:a+=s;break;
                case 1:b+=s;break;
                case 2:c+=s;break;
                case 3:d+=s;break;
                case 4:e+=s;break;
                case 5:f+=s;break;
                case 6:g+=s;break;
                case 7:h+=s;break;
            }
        }
        double[] test = new double[]{a/7,b/7,c/7,d/7,e/7,f/6,g/6,h/6};
        double max = 0;
        int index = 0;
        for (int i = 0; i < test.length; i++) {
            double s = test[i];
            if (s>max){
                max = s;
                index = i;
            }
        }
        double score = getPearsonCorrelationScore(xData, yData);
        Map<String,Object> map = new HashMap<>();
        map.put("index",index);
        map.put("score",score);
        return map;
    }

    public static double getPearsonCorrelationScore(List<Double> x, List<Double> y) {
        if (x.size() != y.size())
            throw new RuntimeException("数据不正确！");
        double[] xData = new double[x.size()];
        double[] yData = new double[x.size()];
        for (int i = 0; i < x.size(); i++) {
            xData[i] = x.get(i);
            yData[i] = y.get(i);
        }
        return getPearsonCorrelationScore(xData, yData);
    }

    public static double getPearsonCorrelationScore(double[] xData, double[] yData) {
        if (xData.length != yData.length)
            throw new RuntimeException("数据不正确！");
        double xMeans;
        double yMeans;
        double numerator = 0;// 求解皮尔逊的分子
        double denominator = 0;// 求解皮尔逊系数的分母

        double result = 0;
        // 拿到两个数据的平均值
        xMeans = getMeans(xData);
        yMeans = getMeans(yData);
        // 计算皮尔逊系数的分子
        numerator = generateNumerator(xData, xMeans, yData, yMeans);
        // 计算皮尔逊系数的分母
        denominator = generateDenomiator(xData, xMeans, yData, yMeans);
        // 计算皮尔逊系数
        result = numerator / denominator;
        return result;
    }

    /**
     * 计算分子
     *
     * @param xData
     * @param xMeans
     * @param yData
     * @param yMeans
     * @return
     */
    private static double generateNumerator(double[] xData, double xMeans, double[] yData, double yMeans) {
        double numerator = 0.0;
        for (int i = 0; i < xData.length; i++) {
            numerator += (xData[i] - xMeans) * (yData[i] - yMeans);
        }
        return numerator;
    }

    /**
     * 生成分母
     *
     * @param yMeans
     * @param yData
     * @param xMeans
     * @param xData
     * @return 分母
     */
    private static double generateDenomiator(double[] xData, double xMeans, double[] yData, double yMeans) {
        double xSum = 0.0;
        for (int i = 0; i < xData.length; i++) {
            xSum += (xData[i] - xMeans) * (xData[i] - xMeans);
        }
        double ySum = 0.0;
        for (int i = 0; i < yData.length; i++) {
            ySum += (yData[i] - yMeans) * (yData[i] - yMeans);
        }
        return Math.sqrt(xSum) * Math.sqrt(ySum);
    }

    /**
     * 根据给定的数据集进行平均值计算
     *
     * @param
     * @return 给定数据集的平均值
     */
    private static double getMeans(double[] datas) {
        double sum = 0.0;
        for (int i = 0; i < datas.length; i++) {
            sum += datas[i];
        }
        return sum / datas.length;
    }

}
