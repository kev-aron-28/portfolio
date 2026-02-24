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

// Here we read the file and create the rules based on that file
// you only have one method which is the load()
public class RuleLoader<C> {
    private final ActionRegistry<C> actionsRegistry;
    private final ConditionRegistry<C> conditionsRegistry;
    
    public RuleLoader(ActionRegistry<C> actionsRegistry, ConditionRegistry<C> conditionsRegistry) {
        this.actionsRegistry = actionsRegistry;
        this.conditionsRegistry = conditionsRegistry;
    }
    

    /**
     * 
     * @param The path where the rules file is located
     * @return List of the parsed rules from the file if its a valid file
     * @throws ParseException when the file is not valid
    */
   public List<Rule<C>> load(Path path) {
       List<Rule<C>> rules = new ArrayList<>();
       List<RuleError> errors = new ArrayList<>();
       
       
       try (BufferedReader file = Files.newBufferedReader(path, Charset.defaultCharset())) {
           Condition<C> rootCondition = null;
           int currentLine = 0;
           Rule<C> currentRule = null;
           
            
            String line;
            while((line = file.readLine()) != null) {
                line = line.trim();
                
                currentLine++;

                if(line.isEmpty()) continue;
                
                if(line.startsWith("RULE")) {
                    finalizeRule(currentRule, rootCondition, rules, errors, currentLine);

                    currentRule = this.createRule(line, errors, currentLine);

                    rootCondition = null;
                    
                    continue;
                }

                if(line.startsWith("WHEN") || line.startsWith("OR") || line.startsWith("AND")) {

                    if(currentRule == null) {
                        errors.add(new RuleError(currentLine, "You must provide a valid RULE <ID> block before a condition block"));

                        continue;
                    }

                    rootCondition = this.parseCondition(line, rootCondition, errors, currentLine);

                    continue;
                }

                if(line.startsWith("THEN")) {
                    parseAction(line, currentRule, errors, currentLine);
                }
            }

            finalizeRule(currentRule, rootCondition, rules, errors, currentLine);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if(!errors.isEmpty()) {
            throw new ParseException(errors);
        }

        return List.copyOf(rules);
    }

    private void parseAction(String line, Rule<C> currentRule, List<RuleError> errors, int lineNumber) {

        if(currentRule == null) {
            errors.add(new RuleError(lineNumber, "You must provide a valid RULE <ID> block before an action"));
        
            return;
        }

        String[] parts = line.split("\\s+", 2);

        if(parts.length < 2) {
            errors.add(new RuleError(lineNumber, "Invalid action format THEN <ACTION>"));
            return;
        }

        String actionParts[] = parts[1].split("\\s+", 2);

        List<String> paramsList = List.of();
        Action<C> action = null;

        if(actionParts.length == 2) {
            paramsList = List.of(actionParts[1]);
        }

        action = this.actionsRegistry.get(actionParts[0], paramsList);

        if(action == null) {
            errors.add(new RuleError(lineNumber, "Action " + actionParts[0] + " not found in registry"));

            return;
        }

        currentRule.addAction(action);
    }

    private Condition<C> parseCondition(String line, Condition<C> currentRoot, List<RuleError> errors, int lineNumber) {
        String[] parts = line.split("\\s+", 2);

        if(parts.length < 2) {
            errors.add(new RuleError(lineNumber, "Invalid condition statement"));
            
            return null;
        }

        String typeOfConditional = parts[0];
        // here we get all the part of the string tnat contains the condition, which must be registered in the ConditionsRegistry
        String condition = parts[1];

        Condition<C> resultCondition = this.conditionsRegistry.get(condition);

        if(resultCondition == null) {
            errors.add(new RuleError(lineNumber, "The condition " + condition + " was not found in the registry"));

            return currentRoot;
        }

        if (currentRoot == null) {
            return resultCondition;
        }

        return switch (typeOfConditional) {
            case "AND", "WHEN" -> new AndCondition<>(List.of(currentRoot, resultCondition));
            case "OR" -> new OrCondition<>(List.of(currentRoot, resultCondition));
            default -> {
                errors.add(new RuleError(lineNumber, "Unknown operator "+ typeOfConditional));

                yield currentRoot;
            }
        };
    }

    private void finalizeRule(Rule<C> rule, Condition<C> rootCondition, List<Rule<C>> rules, List<RuleError> errors, int lineNumber) {
        if(rule == null) return;
        
        rule.setRootCondition(rootCondition);
        
        if(!rule.hasActions()) {
            errors.add(new RuleError(lineNumber, "You must provide at least one action"));
        }
        
        if(!rule.hasConditions()) {
            errors.add(new RuleError(lineNumber, "You must provide at least one condition"));
        }
        
        if(rule.hasActions() && rule.hasConditions())  {
            rules.add(rule);
        }
    }
    
    private Rule<C> createRule(String line, List<RuleError> errors, int lineNumber) {
        String parts[] = line.split("\\s+", 2);
        
        if(parts.length < 2) {
            return null;
        }
        
        String id = parts[1];
        
        return new Rule<>(id);
    }
    

}
