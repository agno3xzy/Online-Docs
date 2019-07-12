package com.OnlineDocs.utils;

import java.util.Random;

public class ColorUtil {

    public static String[] color = {"#FFFAFA", "#F8F8FF", "#F5F5F5",
            "#FFFAF0", "#FDF5E6", "#FAF0E6", "#FAEBD7", "#FFEFD5",
            "#FFEBCD", "#FFE4C4", "#FFDAB9", "#FFDEAD", "#FFE4B5", "#FFF8DC",
            "#FFFFF0", "#FFFACD", "#FFF5EE", "#F0FFF0", "#F5FFFA", "#F0FFFF",
            "#F0F8FF", "#E6E6FA", "#FFF0F5", "#FFE4E1", "#D3D3D3", "#87CEFA",
            "#ADD8E6", "#FAFAD2", "#FFFFE0", "#F5F5DC"};

    public static String getRandomColor() {
        String red;
        //绿色
        String green;
        //蓝色
        String blue;
        //生成随机对象
        Random random = new Random();
        //生成红色颜色代码
        red = Integer.toHexString(random.nextInt(256)).toUpperCase();
        //生成绿色颜色代码
        green = Integer.toHexString(random.nextInt(256)).toUpperCase();
        //生成蓝色颜色代码
        blue = Integer.toHexString(random.nextInt(256)).toUpperCase();

        //判断红色代码的位数
        red = red.length() == 1 ? "0" + red : red;
        //判断绿色代码的位数
        green = green.length() == 1 ? "0" + green : green;
        //判断蓝色代码的位数
        blue = blue.length() == 1 ? "0" + blue : blue;
        //生成十六进制颜色值
        return "#" + red + green + blue;
    }

    public static String getColor() {
        Random random = new Random();
        return color[random.nextInt(color.length)];
    }
}
