package com.sz.arithmeticgenerator.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnswerEvaluator {

    // 计算一系列题目的答案
    public static List<String> evaluateQuestions(List<String> questions) {
        List<String> answers = new ArrayList<>();
        for (String question : questions) {
            // 预处理题目字符串
            String processedQuestion = preprocessString(question);
            // 计算表达式的值
            Object result = evaluateExpression(processedQuestion);
            if (result != null) {
                // 如果结果是 Fraction 类型，转换为混合数字字符串
                if (result instanceof Fraction) {
                    answers.add(((Fraction) result).toMixedNumberString());
                } else {
                    // 否则，直接转换为字符串
                    answers.add(result.toString());
                }
            } else {
                // 如果计算出错，添加 "N/A"
                answers.add("N/A");
            }
        }
        return answers;
    }


    // 计算单个表达式的值
    public static Object evaluateExpression(String expression) {
        try {
            // 去除空格
            expression = expression.replace(" ", "");

            // 统一除号
            expression = expression.replace("÷", "/");

            // 将表达式解析为 Token 列表
            List<Token> tokens = tokenize(expression);

            // 使用递归下降解析器计算表达式的值
            return parseExpression(tokens, 0).value; // 从索引 0 开始解析
        } catch (Exception e) {
            System.err.println("表达式计算错误: " + expression + " " + e.getMessage());
            return null;
        }
    }


    // 预处理题目字符串 (去除序号和等号)
    private static String preprocessString(String str) {
        String trimmedStr = str.trim();
        // 查找点号的位置
        int dotIndex = trimmedStr.indexOf('.');
        // 如果点号存在且之前的子字符串是数字，则去除序号
        if (dotIndex > 0 && trimmedStr.substring(0, dotIndex).matches("\\d+")) {
            trimmedStr = trimmedStr.substring(dotIndex + 1).trim();
        }
        // 如果字符串以等号结尾，则去除等号
        if (trimmedStr.endsWith("=")) {
            trimmedStr = trimmedStr.substring(0, trimmedStr.length() - 1).trim();
        }
        return trimmedStr;
    }

    // 对比题目答案并评分
    public static String gradeAnswers(String exerciseFile, String answerFile) {
        try {
            // 读取题目文件
            List<String> questions = readLines(exerciseFile);
            // 计算正确答案
            List<String> correctAnswers = evaluateQuestions(questions);
            // 读取用户答案
            List<String> userAnswers = readLines(answerFile);

            // 检查答案数量是否一致
            if (questions.size() != userAnswers.size()) {
                return "Error: The number of questions and answers do not match.";
            }


            List<Integer> correctIndices = new ArrayList<>();
            List<Integer> wrongIndices = new ArrayList<>();

            for (int i = 0; i < questions.size(); i++) {
                // 获取正确答案
                String correctAnswer = correctAnswers.get(i);
                // 获取用户答案
                String userAnswer = userAnswers.get(i);

                // 比较答案 (去除序号和空格)
                if (correctAnswer.replaceAll("\\s+", "").equals(userAnswer.replaceAll("\\s+", "").substring(userAnswer.indexOf('.') + 1).trim())) {
                    // 如果答案正确，记录题目编号
                    correctIndices.add(i + 1); // 题目编号从 1 开始
                } else {
                    // 如果答案错误，记录题目编号
                    wrongIndices.add(i + 1);
                }
            }

            // 构建输出字符串

            // 将结果写入 Grade.txt 文件 (此处省略了写入文件的操作，避免重复)
            return "Correct: " + correctIndices.size() + " " + correctIndices + "\n" +
                    "Wrong: " + wrongIndices.size() + " " + wrongIndices;


        } catch (IOException e) {
            return "Error reading files: " + e.getMessage();
        }
    }

    // 读取文件内容
    private static List<String> readLines(String filePath) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines;
    }


    //region  以下是解析器相关代码
    // 定义 Token 类型
    static class Token {
        enum Type {
            NUMBER,  // 数字 (整数或分数)
            OPERATOR, // 运算符 (+, -, *, /)
            LPAREN,   // 左括号 (
            RPAREN    // 右括号 )
        }

        Type type;
        // 对于数字，存储原始字符串; 对于分数, 存储 Fraction 对象
        String value;

        Token(Type type, String value) {
            this.type = type;
            this.value = value;
        }
    }

    // 将表达式字符串转换为 Token 列表
    private static List<Token> tokenize(String expression) {
        List<Token> tokens = new ArrayList<>();
        // 匹配分数、整数、运算符和括号
        Pattern pattern = Pattern.compile("(\\d+'?\\d*/\\d+)|(\\d+)|([+\\-*/()])");
        Matcher matcher = pattern.matcher(expression);

        while (matcher.find()) {
            String tokenStr = matcher.group();
            // 数字 (整数或分数)
            if (tokenStr.matches("\\d+'?\\d*/\\d+|\\d+")) {
                tokens.add(new Token(Token.Type.NUMBER, tokenStr));
                // 运算符
            } else if (tokenStr.matches("[+\\-*/]")) {
                tokens.add(new Token(Token.Type.OPERATOR, tokenStr));
                // 左括号
            } else if (tokenStr.equals("(")) {
                tokens.add(new Token(Token.Type.LPAREN, tokenStr));
                // 右括号
            } else if (tokenStr.equals(")")) {
                tokens.add(new Token(Token.Type.RPAREN, tokenStr));
            }
        }
        return tokens;
    }


    // 解析表达式 (递归下降解析器的入口)
    private static Result parseExpression(List<Token> tokens, int index) {
        return parseAdditionSubtraction(tokens, index);
    }

    // 解析加减法 (处理优先级)
    private static Result parseAdditionSubtraction(List<Token> tokens, int index) {
        Result result = parseMultiplicationDivision(tokens, index);
        Object currentValue = result.value;
        index = result.nextIndex;

        while (index < tokens.size()) {
            Token token = tokens.get(index);
            // 如果是加号或减号
            if (token.type == Token.Type.OPERATOR && (token.value.equals("+") || token.value.equals("-"))) {
                // 解析乘除法
                Result nextResult = parseMultiplicationDivision(tokens, index + 1);
                Object nextValue = nextResult.value;
                index = nextResult.nextIndex;

                // 调用对应的方法进行计算
                currentValue = invokeMethod(currentValue, nextValue, token.value);
            } else {
                // 不是加减运算符，退出循环
                break;
            }
        }
        return new Result(currentValue, index);
    }


    // 解析乘除法 (处理优先级)
    private static Result parseMultiplicationDivision(List<Token> tokens, int index) {
        // 解析基本单元 (数字、括号内的表达式)
        Result result = parsePrimary(tokens, index);
        Object currentValue = result.value;
        index = result.nextIndex;

        while (index < tokens.size()) {
            Token token = tokens.get(index);
            // 如果是乘号或除号
            if (token.type == Token.Type.OPERATOR && (token.value.equals("*") || token.value.equals("/"))) {
                // 解析基本单元
                Result nextResult = parsePrimary(tokens, index + 1);
                Object nextValue = nextResult.value;
                index = nextResult.nextIndex;

                // 调用对应的方法进行计算
                currentValue = invokeMethod(currentValue, nextValue, token.value);
            } else {
                // 不是乘除运算符，退出循环
                break;
            }
        }
        return new Result(currentValue, index);
    }

    // 解析基本单元 (数字、括号内的表达式)
    private static Result parsePrimary(List<Token> tokens, int index) {
        if (index >= tokens.size()) {
            throw new IllegalArgumentException("Unexpected end of expression");
        }

        Token token = tokens.get(index);
        // 如果是数字
        if (token.type == Token.Type.NUMBER) {
            // 统一转换为 Fraction 对象
            Object value = new Fraction(token.value);
            return new Result(value, index + 1);

            // 如果是左括号
        } else if (token.type == Token.Type.LPAREN) {
            // 遇到左括号，递归解析括号内的表达式

            int closingParenIndex = findClosingParen(tokens, index);
            //如果没找到
            if(closingParenIndex == -1){
                throw  new IllegalArgumentException("Mismatched parentheses");
            }
            // 递归调用, 注意这里要传入index + 1
            Result result = parseExpression(tokens, index + 1);

            // 跳过右括号
            return new Result(result.value, closingParenIndex + 1);

        } else {
            throw new IllegalArgumentException("Unexpected token: " + token.value);
        }
    }

    // 查找与给定左括号匹配的右括号的索引
    private static int findClosingParen(List<Token> tokens, int openParenIndex) {
        int parenCount = 1;
        for (int i = openParenIndex + 1; i < tokens.size(); i++) {
            Token token = tokens.get(i);
            // 如果是左括号
            if (token.type == Token.Type.LPAREN) {
                parenCount++;
                // 如果是右括号
            } else if (token.type == Token.Type.RPAREN) {
                parenCount--;
                // 找到匹配的右括号
                if (parenCount == 0) {
                    return i;
                }
            }
        }
        // 没有找到匹配的右括号
        return -1;
    }


    // 用于存储当前的计算结果, 和下一个token的index
    private static class Result {
        // 当前计算的值
        Object value;
        // 下一个Token的Index
        int nextIndex;

        public Result(Object value, int nextIndex) {
            this.value = value;
            this.nextIndex = nextIndex;
        }
    }


    // 使用反射调用 Fraction 类中对应的方法
    private static Object invokeMethod(Object obj1, Object obj2, String operator) {
        try {
            String methodName = switch (operator) {
                case "+" -> "add";
                case "-" -> "subtract";
                case "*" -> "multiply";
                case "/" -> "divide";
                default -> throw new IllegalArgumentException("Invalid operator: " + operator);
            };
            // 根据运算符确定要调用的方法名

            // 获取 Fraction 类的方法
            Method method = Fraction.class.getMethod(methodName, Object.class);

            // 调用方法 (所有操作数都是 Fraction)
            return method.invoke(obj1, obj2);

        } catch (Exception e) {
            throw new RuntimeException("Error invoking method: " + e.getMessage(), e);
        }
    }

    //endregion
}