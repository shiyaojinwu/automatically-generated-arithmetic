package com.sz.arithmeticgenerator;

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
}
