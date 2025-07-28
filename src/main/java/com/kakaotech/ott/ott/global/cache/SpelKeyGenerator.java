package com.kakaotech.ott.ott.global.cache;

import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

@Component
public class SpelKeyGenerator {
    private final ExpressionParser parser= new SpelExpressionParser();

    public Object generateKey(String keyExpression, MethodSignature signature, Object[] args) {
        StandardEvaluationContext context = new StandardEvaluationContext();
        String[] paramterNames = signature.getParameterNames();
        for (int i = 0; i < paramterNames.length; i++) {
            context.setVariable(paramterNames[i], args[i]);
        }
        return parser.parseExpression(keyExpression).getValue(context);
    }
}
