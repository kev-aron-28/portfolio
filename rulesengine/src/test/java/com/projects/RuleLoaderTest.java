package com.projects;

import java.nio.file.Path;

import org.junit.Before;
import org.junit.Test;

import com.projects.domain.ActionRegistry;
import com.projects.domain.ConditionRegistry;
import com.projects.domain.ParseException;
import com.projects.domain.RuleLoader;

/**
 * Unit tests for the RuleLoader, in charge of reading a rule file
 * and creating the rules based on that
 */
public class RuleLoaderTest
{
    RuleLoader<TestContext> loader;
    ActionRegistry<TestContext> actions;
    ConditionRegistry<TestContext> conditions;

    @Before
    public void setup() {
        actions = new ActionRegistry<>();
        conditions = new ConditionRegistry<>();
        
        actions.add("ADD_1", arg -> ctx -> ctx.add_one());
        conditions.add("GREATER_THAN_0", ctx -> ctx.getNumber() > 0);

        loader = new RuleLoader<>(actions, conditions);
    }

    @Test(expected=ParseException.class)
    public void shouldFailWhenNoIdIsProvided() {
        Path path = Path.of("src/main/java/com/projects/domain/test_rules/no_id.rule");


        loader.load(path);
    }

    @Test(expected=ParseException.class)
    public void shouldFailWhenNoRuleSectionIsProvided() {
        Path path = Path.of("src/main/java/com/projects/domain/test_rules/no_rule.rule");
        
        loader.load(path);
    }

    @Test(expected=ParseException.class)
    public void shouldFailWhenNoThenSectionProvided() {
        Path path = Path.of("src/main/java/com/projects/domain/test_rules/no_then.rule");

        loader.load(path);
    }

    @Test(expected=ParseException.class)
    public void shouldFailWhenConditionNotExists() {
        Path path = Path.of("src/main/java/com/projects/domain/test_rules/invalid_condition.rule");

        loader.load(path);
    }

    @Test(expected=ParseException.class)
    public void shouldFailWhenActionNotExists() {
        Path path = Path.of("src/main/java/com/projects/domain/test_rules/invalid_action.rule");

        loader.load(path);
    }
}
