package com.projects.domain;

import java.util.List;

public class RuleEngine<C> {
    List<Rule<C>> rules;

    public void test (C ctx) {
        if(rules.isEmpty()) throw new Error("You need to provide the rules");

        for(Rule<C> rule: rules) {
            if(rule.matches(ctx)) {
                rule.execute(ctx);
            }
        }
    }
}

