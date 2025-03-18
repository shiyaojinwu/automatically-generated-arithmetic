package com.sz.arithmeticgenerator.service;

import java.util.Objects;

//Fraction 类
@SuppressWarnings("unused")
public class Fraction implements Comparable<Fraction> {
    // 分子
    private int numerator;
    // 分母
    private int denominator;


    public Fraction(Integer numerator, Integer denominator) {
        if (denominator == 0) {
            throw new IllegalArgumentException("Denominator cannot be zero.");
        }
        this.numerator = numerator;
        this.denominator = denominator;
        simplify(); // 创建分数时立即化简
    }

    //from String, 形如1/2, 1, 2'2/3
    public Fraction(String fractionStr) {
        if (fractionStr.contains("'")) {
            String[] parts = fractionStr.split("'");
            int whole = Integer.parseInt(parts[0]);
            String[] fracParts = parts[1].split("/");
            int num = Integer.parseInt(fracParts[0]);
            int den = Integer.parseInt(fracParts[1]);

            this.numerator = whole * den + num;
            this.denominator = den;

        }else if (fractionStr.contains("/")) {
            String[] parts = fractionStr.split("/");
            this.numerator = Integer.parseInt(parts[0]);
            this.denominator = Integer.parseInt(parts[1]);

        } else {
            this.numerator = Integer.parseInt(fractionStr);
            this.denominator = 1;
        }

        simplify();
    }

    // 加法
    public Fraction add(Object other) {
        if (other instanceof Fraction otherFrac) {
            int newNumerator = this.numerator * otherFrac.denominator + otherFrac.numerator * this.denominator;
            int newDenominator = this.denominator * otherFrac.denominator;
            return new Fraction(newNumerator, newDenominator);
        } else if (other instanceof Integer) {
            // 将整数视为分母为 1 的分数
            return this.add(new Fraction((Integer) other, 1));
        } else {
            throw new IllegalArgumentException("Unsupported operand type for addition: " + other.getClass());
        }
    }

    // 减法
    public Fraction subtract(Object other) {
        if (other instanceof Fraction otherFrac) {
            int newNumerator = this.numerator * otherFrac.denominator - otherFrac.numerator * this.denominator;
            int newDenominator = this.denominator * otherFrac.denominator;
            return new Fraction(newNumerator, newDenominator);
        } else if (other instanceof Integer) {
            return this.subtract(new Fraction((Integer) other, 1));
        } else {
            throw new IllegalArgumentException("Unsupported operand type for subtraction: " + other.getClass());
        }
    }

    // 乘法
    public Fraction multiply(Object other) {
        if (other instanceof Fraction otherFrac) {
            int newNumerator = this.numerator * otherFrac.numerator;
            int newDenominator = this.denominator * otherFrac.denominator;
            return new Fraction(newNumerator, newDenominator);
        } else if (other instanceof Integer) {
            return this.multiply(new Fraction((Integer) other, 1));
        } else {
            throw new IllegalArgumentException("Unsupported operand type for multiplication: " + other.getClass());
        }
    }
    // 除法
    public Fraction divide(Object other) {
        if (other instanceof Fraction otherFrac) {
            if (otherFrac.numerator == 0) {
                throw new ArithmeticException("Division by zero.");
            }
            int newNumerator = this.numerator * otherFrac.denominator;
            int newDenominator = this.denominator * otherFrac.numerator;
            return new Fraction(newNumerator, newDenominator);
        } else if (other instanceof Integer) {
            return this.divide(new Fraction((Integer) other, 1));
        } else {
            throw new IllegalArgumentException("Unsupported operand type for division: " + other.getClass());
        }
    }

    // 化简分数 (求最大公约数，并进行约分)
    private void simplify() {
        // 使用绝对值，避免负号问题
        int gcd = gcd(Math.abs(numerator), denominator);
        numerator /= gcd;
        denominator /= gcd;
        if (denominator < 0) {
            // 将负号移到分子上
            numerator = -numerator;
            denominator = -denominator;
        }
    }

    // 求最大公约数 (Greatest Common Divisor, GCD) - 欧几里得算法
    private int gcd(int a, int b) {
        return b == 0 ? a : gcd(b, a % b);
    }
    //转为String
    @Override
    public String toString() {
        return toMixedNumberString();
    }

    // 转换为真分数形式的字符串 (如 2'3/8)
    public String toMixedNumberString() {
        int whole = numerator / denominator;  // 整数部分
        int newNumerator = numerator % denominator; // 余数 (新的分子)

        if (newNumerator == 0) {
            return String.valueOf(whole); // 如果余数为0，只返回整数部分
        } else if (whole == 0) {
            return newNumerator + "/" + denominator; // 如果整数部分为0，返回假分数形式
        } else {
            return whole + "'" + Math.abs(newNumerator) + "/" + denominator; // 真分数形式
        }
    }
    //是否为0
    public boolean isZero() {
        return numerator == 0;
    }
    //获取分子
    public int getNumerator(){
        return  numerator;
    }
    //获取分母
    public int getDenominator(){
        return denominator;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Fraction other = (Fraction) obj;
        return this.numerator == other.numerator && this.denominator == other.denominator;
    }

    @Override
    public int hashCode() {
        return Objects.hash(numerator, denominator);
    }

    @Override
    public int compareTo(Fraction other) {
        //  先通分，再比较分子
        long diff = (long) this.numerator * other.denominator - (long) other.numerator * this.denominator;
        if (diff > 0) {
            return 1;
        } else if (diff < 0) {
            return -1;
        } else {
            return 0;
        }
    }
}
