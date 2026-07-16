package com.projects.domain.Conditions;

import java.util.List;

import com.projects.domain.Condition;


// For each condition present in the ArrayList of conditions
// if only one is succesful then you return true as this is an OR
public class OrCondition<C> implements Condition<C> {
    List<Condition<C>> conditions;

    public OrCondition(List<Condition<C>> conditions) {
        this.conditions = conditions;
    }
    
    @Override
    public boolean test(C context) {
        
        for(Condition<C> condition : conditions) {
            if(condition.test(context)) {
                return true;
            }
        }

        return false;
    }
}
