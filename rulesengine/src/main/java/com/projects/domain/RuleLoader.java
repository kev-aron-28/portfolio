package com.projects.domain;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.projects.domain.Conditions.AndCondition;
import com.projects.domain.Conditions.OrCondition;


// This is where you parse the file and build the rules based on the file
// you only have one method which is the load()
public class RuleLoader<C> {
    private final ActionRegistry<C> actionsRegistry;
    private final ConditionRegistry<C> conditionsRegistry;
    private List<RuleError> errors = new ArrayList<>();
    private Condition<C> rootCondition = null;
    
    public RuleLoader(ActionRegistry<C> actionsRegistry, ConditionRegistry<C> conditionsRegistry) {
        this.actionsRegistry = actionsRegistry;
        this.conditionsRegistry = conditionsRegistry;
    }
    
    /**
     * 
     * @param The path there the rule file is located
     * @return List of the parsed rules from the file if its a valid file
     * @throws ParseException when the file is not valid
    */
   public List<Rule<C>> load(Path path) {
       List<Rule<C>> rules = new ArrayList<>();
       Rule<C> currentRule = null;
       int currentLine = 0;

        try (BufferedReader file = Files.newBufferedReader(path, Charset.defaultCharset())) {
            String line;
            while((line = file.readLine()) != null) {
                currentLine++;

                if(line.startsWith("RULE")) {
                    // IF we are reading another RULE means there are multiple rules in the file
                    // You should only add a rule when the user specifies the conditions and actions of it
                    if(currentRule != null) {
                        currentRule.setRootCondition(this.rootCondition);

                        if(!currentRule.hasActions()) errors.add(new RuleError(currentLine, "You must provide at least one action"));
                        
                        if(!currentRule.hasConditions()) errors.add(new RuleError(currentLine, "You must provide at least one condition"));

                        // We add the rule if it was processed correctly, 
                        if(currentRule.hasActions() && currentRule.hasConditions()) {
                            rules.add(currentRule);
                        }

                        this.rootCondition = null;
                        currentRule = null;
                    }

                    String id = this.getRuleId(line, currentLine);

                    if(id == null) {
                        errors.add(new RuleError(currentLine, "You must provide a valid RULE id section"));

                        continue;
                    }

                    currentRule = new Rule<>(id);

                    continue;
                }

                if(line.startsWith("WHEN") || line.startsWith("OR") || line.startsWith("AND")) {
                    // If you reach here and there is no rule, means there was no RULE ID section
                    if(currentRule == null) {
                        errors.add(new RuleError(currentLine, "You must provide a RULE id before a WHEN section"));

                        continue;
                    }

                    this.buildConditionFromLine(line, currentLine);

                }

                if(line.startsWith("THEN")) {
                    if(currentRule == null) {
                        errors.add(new RuleError(currentLine, "You must provide a RULE Id and a WHEN section before a THEN section"));
                        continue;
                    }

                    Action<C> currentAction = this.getActionFromLine(line, currentLine);

                    if(currentAction == null) {
                        errors.add(new RuleError(currentLine, "Invalid action provided"));
                        
                        continue;
                    }

                    currentRule.addAction(currentAction);
                }
            }

            if(currentRule != null) {
                currentRule.setRootCondition(this.rootCondition);

                System.out.println(currentRule.hasActions());

                if(!currentRule.hasActions()) errors.add(new RuleError(currentLine, "You must provide at least one action"));
                        
                if(!currentRule.hasConditions()) errors.add(new RuleError(currentLine, "You must provide at least one condition"));

                // We add the rule if it was processed correctly, 
                if(currentRule.hasActions() && currentRule.hasConditions()) {
                    rules.add(currentRule);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if(!errors.isEmpty()) {
            throw new ParseException(this.errors);
        }

        return List.copyOf(rules);
    }

    private Action<C> getActionFromLine(String line, int currentLine) {
        String[] parts = line.split("\\s+", 2);

        if(parts.length < 2) {
            errors.add(new RuleError(currentLine, "Invalid action format THEN <ACTION>"));
            return null;
        }

        // To get the action name and the args
        String actionParts[] = parts[1].split("\\s+", 2);

        List<String> paramsList = List.of();
        Action<C> action = null;

        if(actionParts.length == 2) {
            paramsList = List.of(actionParts[1]);
        }

        action = this.actionsRegistry.get(actionParts[0], paramsList);

        if(action == null) {
            errors.add(new RuleError(currentLine, "Action " + actionParts[0] + " not found in registry"));

            return null;
        }

        return action;
    }

    private void buildConditionFromLine(String line, int currentLine) {

        // We split into two parts, <WHEN|OR|AND> <condition>
        String[] parts = line.split("\\s+", 2);

        if(parts.length < 2) {
            errors.add(new RuleError(currentLine, "Invalid condition statement"));
            
            return;
        }

        // Can be OR|WHEN|AND
        String typeOfConditional = parts[0];
        // here we get all the part of the string tnat contains the condition, which must be registered in the ConditionsRegistry
        String condition = parts[1];

        Condition<C> resultCondition = this.conditionsRegistry.get(condition);

        if(resultCondition == null) {
            errors.add(new RuleError(currentLine, "The condition " + condition + " was not found in the registry"));

            return;
        }

        // When its the first condition we
        if(rootCondition == null) {
            rootCondition = resultCondition;
        }

        switch (typeOfConditional) {
            case "AND":
            case "WHEN":
                rootCondition = new AndCondition<>(List.of(rootCondition, resultCondition));
                break;
            case "OR":
                rootCondition = new OrCondition<>(List.of(rootCondition, resultCondition));
            default:
                errors.add(new RuleError(currentLine, "Unknown operator "+ typeOfConditional));
        }
    
    }

    private String getRuleId(String line, int currentLine) {
        String parts[] = line.split("\\s+", 2);
        
        if(parts.length < 2) {
            return null;
        }
        
        String id = parts[1];

        return id;
    }

}
