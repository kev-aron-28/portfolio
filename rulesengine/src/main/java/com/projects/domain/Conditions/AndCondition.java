package com.projects.domain.Conditions;
import java.util.List;

import com.projects.domain.Condition;

// For each condition if one fails you must return false as this is the AND
public final class AndCondition<C> implements Condition<C> {
    List<Condition<C>> conditions;

    public AndCondition(List<Condition<C>> conditions) {
        this.conditions = conditions;
    }

    @Override
    public boolean test(C context) {
        for(Condition<C> condition : conditions) {
            if(!condition.test(context)) {
                return false;
            }
        }

        return true;
    }
    
}
