package com.projects.domain.Conditions;

import com.projects.domain.Condition;


//Here Not only accepts one conditions and negates it as this is the NOT condition
public class NotCondition<C> implements Condition<C> {
    Condition<C> condition;

    public NotCondition(Condition<C> condition) {
        this.condition = condition;
    }

    
    @Override
    public boolean test(C context) {
        return !this.condition.test(context);
    }
}
