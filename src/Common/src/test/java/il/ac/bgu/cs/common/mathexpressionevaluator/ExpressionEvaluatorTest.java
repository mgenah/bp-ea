package il.ac.bgu.cs.common.mathexpressionevaluator;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class ExpressionEvaluatorTest {
	@Test
	public void testSimpleExpression(){
		String exp = "x1+x2";
		Map<String, Double> vars = new HashMap<>();
		vars.put("x1", 2.0);
		vars.put("x2", 3.0);
		Assert.assertEquals(5.0, ExpressionEvaluatior.evaluate(exp, vars), 0.0);
	}
	
	@Test
	public void testSimpleExpressionNoVariables(){
		String exp = "2.0+3";
		Map<String, Double> vars = new HashMap<>();
		Assert.assertEquals(5.0, ExpressionEvaluatior.evaluate(exp, vars), 0.0);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testMissingVariableAssignment(){
		String exp = "x1+x2";
		Map<String, Double> vars = new HashMap<>();
		Assert.assertEquals(5.0, ExpressionEvaluatior.evaluate(exp, vars), 0.0);
	}
}
