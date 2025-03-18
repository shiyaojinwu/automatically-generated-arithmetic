package com.sz.arithmeticgenerator;

import com.sz.arithmeticgenerator.service.AnswerEvaluator;
import com.sz.arithmeticgenerator.service.Fraction;
import com.sz.arithmeticgenerator.service.QuestionGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static com.sz.arithmeticgenerator.service.QuestionGenerator.isSmaller;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ArithmeticGeneratorApplicationTests {

    /**
     * 确保除法生成合理分数
     * @author zyh
     * @date 2025/03/18
     */
    @Test
    public void testDivisionReasonableFraction() {
        Random rand = new Random();
        String expression = QuestionGenerator.generateRandomExpression(10, rand);
        assertFalse(expression.contains("÷") && isSmaller(expression.split(" ")[0], expression.split(" ")[2]));
    }

    /**
     * 生成多个不重复的题目
     * @author zyh
     * @date 2025/03/18
     */
    @Test
    public void testGenerateMultipleUniqueQuestions() {
        List<String> questions = QuestionGenerator.generateQuestions(5, 10);
        assertEquals(5, questions.size());
        Set<String> uniqueQuestions = new HashSet<>(questions);
        assertEquals(5, uniqueQuestions.size()); // 确保题目不重复
    }

    /**
     * 操作数生成
     * @author zyh
     * @date 2025/03/18
     */
    @Test
    public void testGenerateOperand() {
        Random rand = new Random();
        String operand = QuestionGenerator.generateOperand(10, rand);
        assertTrue(operand.matches("\\d+") || operand.matches("\\d+/\\d+"));
    }

    /**
     * 表达式归一化
     * @author zyh
     * @date 2025/03/18
     */
    @Test
    public void testNormalizeExpression() {
        String expression = "3 + 5";
        String normalized = QuestionGenerator.normalizeExpression(expression);
        assertEquals("3 + 5", normalized);
    }

    /**
     * 确保减法不产生负数
     * @author zyh
     * @date 2025/03/18
     */
    @Test
    public void testSubtractionNoNegativeResult() {
        Random rand = new Random();
        String expression = QuestionGenerator.generateRandomExpression(10, rand);
        assertFalse(expression.contains("-") && isSmaller(expression.split(" ")[0], expression.split(" ")[2]));
    }

    /**
     * AnswerEvaluator测试计算答案
     */
    @Test
    public void testAnswerEvaluator() {
        List<String> questions = QuestionGenerator.generateQuestions(5, 10);
        List<String> answers = AnswerEvaluator.evaluateQuestions(questions);
        assertEquals(5, answers.size());
    }

    /**
     * 测试AnswerEvaluator评分
     */
    @Test
    public void testAnswerEvaluatorGrade() {
        String result = AnswerEvaluator.gradeAnswers("Exercises.txt", "Answers.txt");
        assertNotNull(result);
    }

    /**
     * 测试Fraction类
     */
    @Test
    public void testFraction() {
        assertEquals(new Fraction(1, 2), new Fraction("1/2"));
        assertNotEquals(new Fraction(1, 2), new Fraction("1"));
    }

    /**
     * 测试Fraction类的计算
     */
    @Test
    public void testFractionCalculation() {
        assertEquals(new Fraction(1, 2), new Fraction("1/4").add(new Fraction("1/4")));
        assertEquals(new Fraction(1, 2), new Fraction("1/2").subtract(new Fraction("0/2")));
    }

    /**
     * 测试Fraction类的转化真分数
     */
    @Test
    public void testFractionToProper() {
        assertEquals("1'1/2", new Fraction("3/2").toMixedNumberString());
        assertNotEquals("1'1/2", new Fraction("4/2").toMixedNumberString());
    }


}
