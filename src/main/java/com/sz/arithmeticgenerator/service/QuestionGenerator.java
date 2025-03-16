package com.sz.arithmeticgenerator.service;

import java.util.*;

/**
 * 题目生成器
 * @author zyh
 * @version 2.0.0
 * @date 2025/3/16
 */
public class QuestionGenerator {

    /**
     * 定义可用的运算符
     */
    private static final String[] OPERATORS = {"+", "-", "*", "÷"};

    /**
     * 生成指定数量和范围的数学题目
     * @param numQuestions 题目数量
     * @param range        运算范围
     * @return {@code List<String> } 题目列表
     * @author zyh
     * @date 2025/03/16
     */
    public static List<String> generateQuestions(int numQuestions, int range) {
        // 用于存储不重复的题目 这里用hashset检查快
        Set<String> uniqueQuestions = new HashSet<>();
        // 存储生成的题目
        List<String> questions = new ArrayList<>();
        // 随机数生成器
        Random rand = new Random();
        // 生成题目
        while (uniqueQuestions.size() < numQuestions) {
            // 生成随机表达式
            String question = generateRandomExpression(range, rand);
            // 归一化表达式，避免重复
            String normalized = normalizeExpression(question);
            // 确保唯一性 检查uniqueQuestions是否存在该表达式
            if (!uniqueQuestions.contains(normalized)) {
                // 不存在则添加到哈希set便于后续快速筛查
                uniqueQuestions.add(normalized);
                // 添加到列表
                questions.add(question + " =");
            }
        }
        // 返回
        return questions;
    }

    /**
     * 生成一个随机数学表达式，最多3个运算符
     * @param range 运算范围
     * @param rand  随机数生成器
     * @return {@code String } 数学表达式
     * @author zyh
     * @date 2025/03/16
     */
    private static String generateRandomExpression(int range, Random rand) {
        int numOperators = rand.nextInt(2) + 1; // 生成 1-3 个运算符
        List<String> elements = new ArrayList<>(); // 存储表达式的元素

        // 生成第一个操作数
        elements.add(generateOperand(range, rand));

        for (int i = 0; i < numOperators; i++) {
            // 随机选择运算符
            String operator = OPERATORS[rand.nextInt(OPERATORS.length)];
            // 生成下一个操作数
            String operand = generateOperand(range, rand);

            // 确保减法不会产生负数
            if (operator.equals("-")) {
                if (elements.size() == 1) { // 只有一个操作数
                    if (isSmaller(elements.getFirst(), operand)) {
                        elements.addFirst(operator);
                        elements.addFirst(operand);
                    } else {
                        elements.add(operator); // 添加运算符
                        elements.add(operand); // 添加操作数
                    }
                } else if (elements.size() == 3) { // 已有两个操作数和一个运算符
                    //计算中间值
                    String result = computeIntermediateResult(elements.get(0), elements.get(1), elements.get(2));
                    if (isSmaller(result, operand)) {
                        elements.addFirst(operator);
                        elements.addFirst(operand);
                    } else {
                        elements.add(operator); // 添加运算符
                        elements.add(operand); // 添加操作数
                    }
                }
            } else if (operator.equals("÷")) {// 确保除法不会生成不合理的分数
                if (elements.size() == 1) { // 只有一个操作数
                    if (isSmaller(elements.getFirst(), operand)) {
                        elements.add(operator); // 添加运算符
                        elements.add(operand); // 添加操作数
                    } else {
                        elements.addFirst(operator);
                        elements.addFirst(operand);
                    }
                } else if (elements.size() == 3) { // 已有两个操作数和一个运算符
                    //计算中间值
                    String result = computeIntermediateResult(elements.get(0), elements.get(1), elements.get(2));
                    if (isSmaller(result, operand)) {
                        elements.add(operator); // 添加运算符
                        elements.add(operand); // 添加操作数
                    } else {
                        elements.addFirst(operator);
                        elements.addFirst(operand);
                    }
                }
            } else {
                elements.add(operator); // 添加运算符
                elements.add(operand); // 添加操作数
            }
        }

        return String.join(" ", elements); // 以空格拼接表达式
    }

    /**
     * 计算两个操作数的中间结果，支持整数和分数运算
     * @param num1 操作数1（可以是整数或分数，如 "3" 或 "1/2"）
     * @param op   运算符（"+"、"-"、"*"、"÷"）
     * @param num2 操作数2（可以是整数或分数，如 "4" 或 "2/3"）
     * @return 计算后的结果（以最简分数或整数形式返回，例如 "5/6" 或 "2"）
     * @author zyh
     * @date 2025/03/16
     */
    private static String computeIntermediateResult(String num1, String op, String num2) {
        // 解析操作数，将其转换为 [分子, 分母] 数组
        int[] fraction1 = parseFraction(num1);
        int[] fraction2 = parseFraction(num2);

        // 提取分子和分母
        int numerator1 = fraction1[0], denominator1 = fraction1[1];
        int numerator2 = fraction2[0], denominator2 = fraction2[1];

        // 计算结果的分子和分母
        int resultNumerator, resultDenominator;

        // 根据运算符执行相应的运算
        switch (op) {
            case "+": // 加法，使用交叉相乘法
                resultNumerator = numerator1 * denominator2 + numerator2 * denominator1;
                resultDenominator = denominator1 * denominator2;
                break;
            case "-": // 减法，使用交叉相乘法
                resultNumerator = numerator1 * denominator2 - numerator2 * denominator1;
                resultDenominator = denominator1 * denominator2;
                break;
            case "*": // 乘法，直接分子乘分子，分母乘分母
                resultNumerator = numerator1 * numerator2;
                resultDenominator = denominator1 * denominator2;
                break;
            case "÷": // 除法，相当于乘以倒数
                if (numerator2 == 0) {
                    throw new ArithmeticException("除数不能为零");
                }
                resultNumerator = numerator1 * denominator2;
                resultDenominator = denominator1 * numerator2;
                break;
            default:
                throw new IllegalArgumentException("不支持的运算符: " + op);
        }

        // 返回化简后的结果
        return simplifyFraction(resultNumerator, resultDenominator);
    }

    /**
     * 解析整数或分数为 [分子, 分母] 数组
     * @param fractionStr 操作数，可能是整数（如 "3"）或分数（如 "2/5"）
     * @return 一个包含分子和分母的数组 [numerator, denominator]
     * @author zyh
     * @date 2025/03/16
     */
    private static int[] parseFraction(String fractionStr) {
        if (fractionStr.contains("/")) { // 处理分数
            String[] parts = fractionStr.split("/");
            return new int[]{Integer.parseInt(parts[0]), Integer.parseInt(parts[1])};
        }
        // 处理整数，将其视为分母为 1 的分数
        return new int[]{Integer.parseInt(fractionStr), 1};
    }

    /**
     * 约分分数
     * @param numerator   计算得到的分子
     * @param denominator 计算得到的分母
     * @return 约分后的分数字符串（如 "1/2" 或 "3"）
     * @author zyh
     * @date 2025/03/16
     */
    private static String simplifyFraction(int numerator, int denominator) {
        if (denominator == 1) { // 如果分母是 1，则直接返回整数
            return String.valueOf(numerator);
        }
        // 计算最大公约数（GCD）
        int gcd = gcd(Math.abs(numerator), Math.abs(denominator));
        // 进行约分
        numerator /= gcd;
        denominator /= gcd;
        return numerator + "/" + denominator;
    }

    /**
     * 计算最大公约数（GCD）使用欧几里得算法（辗转相除法）
     * @param a 数字1
     * @param b 数字2
     * @return a 和 b 的最大公约数
     * @author zyh
     * @date 2025/03/16
     */
    private static int gcd(int a, int b) {
        return b == 0 ? a : gcd(b, a % b);
    }

    /**
     * 生成随机操作数（整数或真分数）
     * @param range 运算范围
     * @param rand  随机数生成器
     * @return {@code String} 操作数
     * @author zyh
     * @date 2025/03/16
     */
    private static String generateOperand(int range, Random rand) {
        if (rand.nextBoolean()) {
            // 生成真分数
            int denominator = rand.nextInt(9) + 2; // 生成 2-10 之间的分母
            int numerator = rand.nextInt(denominator); // 确保是真分数 (分子 < 分母)
            return numerator + "/" + denominator;
        } else {
            return String.valueOf(rand.nextInt(range) + 1); // 生成 1-range 之间的整数
        }
    }

    /**
     * 比较两个操作数（整数或分数）的大小 判断 e1 是否小于 e2（适用于整数和分数）
     * @param num1 操作数1
     * @param num2 操作数2
     * @return boolean
     * @author zyh
     * @date 2025/03/16
     */
    private static boolean isSmaller(String num1, String num2) {
        if (num1 == null || num1.trim().isEmpty() || num2 == null || num2.trim().isEmpty()) {
            throw new IllegalArgumentException("输入的数字不能为空");
        }
        // 解析操作数，将其转换为 [分子, 分母] 数组
        int[] fraction1 = parseFraction(num1);
        int[] fraction2 = parseFraction(num2);
        // 比较大小
        return fraction1[0] / fraction1[1] < fraction2[0] / fraction2[1];
    }

    /**
     * 归一化表达式（避免交换律导致重复）
     * @param expression 原始表达式
     * @return {@code String} 归一化后的表达式
     * @author zyh
     * @date 2025/03/16
     */
    private static String normalizeExpression(String expression) {
        // 将表达式按空格拆分为操作数和运算符的令牌列表
        List<String> tokens = Arrays.asList(expression.split(" "));
        // 创建可修改的副本用于排序操作（原列表不可变）
        List<String> sortedTokens = new ArrayList<>(tokens);

        // 遍历所有运算符位置（索引1,3,5...）
        for (int i = 1; i < tokens.size(); i += 2) {
            // 仅处理可交换运算符：加法或乘法
            if (tokens.get(i).equals("+") || tokens.get(i).equals("*")) {
                // 获取运算符左右两侧的操作数
                String leftOperand = tokens.get(i - 1);
                String rightOperand = tokens.get(i + 1);

                // 比较操作数顺序（例如按字母序或数值大小）
                if (isSmaller(rightOperand, leftOperand)) {
                    // 交换操作数位置以实现标准化
                    sortedTokens.set(i - 1, rightOperand);  // 左位置替换为右操作数
                    sortedTokens.set(i + 1, leftOperand);   // 右位置替换为左操作数
                }
            }
        }
        // 将处理后的令牌列表重新拼接为表达式字符串
        return String.join(" ", sortedTokens);
    }
}
