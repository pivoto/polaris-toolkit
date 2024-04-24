package io.polaris.web.mock;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.el.ELException;
import javax.servlet.jsp.el.Expression;
import javax.servlet.jsp.el.ExpressionEvaluator;
import javax.servlet.jsp.el.FunctionMapper;
import javax.servlet.jsp.el.VariableResolver;

import org.apache.taglibs.standard.lang.support.ExpressionEvaluatorManager;


public class MockExpressionEvaluator extends ExpressionEvaluator {

	private final PageContext pageContext;


	public MockExpressionEvaluator(PageContext pageContext) {
		this.pageContext = pageContext;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Expression parseExpression(final String expression, final Class expectedType,
		final FunctionMapper functionMapper) throws ELException {

		return new Expression() {

			@Override
			public Object evaluate(VariableResolver variableResolver) throws ELException {
				return doEvaluate(expression, expectedType, functionMapper);
			}
		};
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Object evaluate(String expression, Class expectedType, VariableResolver variableResolver,
		FunctionMapper functionMapper) throws ELException {

		if (variableResolver != null) {
			throw new IllegalArgumentException("Custom VariableResolver not supported");
		}
		return doEvaluate(expression, expectedType, functionMapper);
	}

	@SuppressWarnings("rawtypes")
	protected Object doEvaluate(String expression, Class expectedType, FunctionMapper functionMapper)
		throws ELException {

		if (functionMapper != null) {
			throw new IllegalArgumentException("Custom FunctionMapper not supported");
		}
		try {
			return ExpressionEvaluatorManager.evaluate("JSP EL expression", expression, expectedType, this.pageContext);
		} catch (JspException ex) {
			throw new ELException("Parsing of JSP EL expression \"" + expression + "\" failed", ex);
		}
	}

}
