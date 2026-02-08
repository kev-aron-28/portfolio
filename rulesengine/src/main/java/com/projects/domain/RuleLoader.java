package com.projects.domain;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


// This is where you parse the file and build the rules based on the file
// you only have one method which is the load()
public class RuleLoader<C> {
    private final ActionRegistry<C> actionsRegistry;
    private final ConditionRegistry<C> conditionsRegistry;
    List<RuleError> errors = new ArrayList<>();
    
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
                        if(!currentRule.hasActions()) errors.add(new RuleError(currentLine, "You must provide at least one action"));
                        
                        if(!currentRule.hasConditions()) errors.add(new RuleError(currentLine, "You must provide at least one condition"));
                        
                        if(currentRule.hasActions() && currentRule.hasConditions()) rules.add(currentRule);

                        currentRule = null;
                    }

                    String id = this.getRuleId(line, currentLine);

                    if(id == null) {
                        errors.add(new RuleError(currentLine, "You must provide a valid RULE id section"));
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

                    Condition<C> currentCondition = this.getConditionFromLine(line, currentLine);

                    if(currentCondition == null) {
                        errors.add(new RuleError(currentLine, "You must provide a valid condition when using WHEN,OR,AND: " + line));
                        
                        continue;
                    }

                    currentRule.addCondition(currentCondition);

                }

                // TODO: Parse all the actions acordingly
                if(line.startsWith("THEN")) {
                    if(currentRule == null) {
                        errors.add(new RuleError(currentLine, "You must provide a RULE Id and a WHEN section before a THEN section"));

                        continue;
                    }
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

    private Condition<C> getConditionFromLine(String line, int currentLine) {

        String[] parts = line.split("\\s+", 2);

        if(parts.length < 2) {
            return null;
        }
        
        String typeOfConditional = parts[0];

        String condition = parts[1];


        // A condition can only be compounded of max 3 parts
        // - <property> <operand> <property>
        String conditionParts[] = condition.split("\\s+");

        // So you cannot have more than 3 elements in the array
        if(conditionParts.length > 3) {
            return null;
        }

        // Case when only a condition is provided, so the array only has 1 element
        if(conditionParts.length == 1) {
            Condition<C> conditionFromRegistry = conditionsRegistry.get(conditionParts[0]);

            // If no condition is available in the registry then the user introduced a invalid condition
            if(conditionFromRegistry == null) return null;

            return conditionFromRegistry;
        } 
        // Case when there is a full comparision between properties <property> <operand> <property>
        else if(conditionParts.length == 3) {
            String property1 = conditionParts[0];
            String operand = conditionParts[1];
            String property2 = conditionParts[2];


        }
    
        return null;
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
