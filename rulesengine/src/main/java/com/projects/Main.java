package com.projects;

import java.nio.file.Path;

import com.projects.domain.ActionRegistry;
import com.projects.domain.ConditionRegistry;
import com.projects.domain.RuleLoader;

class NumberContext {
    public int number;

    public void setNumber(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public void add_one() {
        this.number = this.number + 1;
    }
}

public class Main {
    public static void main(String[] args) {
        ConditionRegistry<NumberContext> conditionRegistry = new ConditionRegistry<>();
        ActionRegistry<NumberContext> actionRegistry = new ActionRegistry<>();
        
        conditionRegistry.add("GREATER_THAN_0", ctx -> ctx.getNumber() > 0);
        actionRegistry.add("ADD_1", arg -> ctx -> ctx.add_one());

        RuleLoader<NumberContext> loader = new RuleLoader<>(actionRegistry, conditionRegistry);
        loader.load(Path.of("src/main/java/com/projects/domain/test_rules/rule01.rule"));
    }
}
