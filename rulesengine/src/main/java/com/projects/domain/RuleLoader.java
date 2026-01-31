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

                    String id = getRuleId(line, currentLine);

                    System.out.println("RULE WITH ID: " + id);

                    currentRule = new Rule<>(id);
                }


                // TODO: parse the conditions acordinly
                if(line.startsWith("WHEN") || line.startsWith("OR") || line.startsWith("AND")) {
                    // If no RULE statement was provided
                    if(currentRule == null) {
                        errors.add(new RuleError(currentLine, "You must provide a RULE id before a WHEN or a THEN"));
                    }


                }

                // TODO: Parse all the actions acordingly
                if(line.startsWith("THEN")) {
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

    private String getRuleId(String line, int currentLine) {
        String parts[] = line.split("\\s+", 2);
        
        if(parts.length < 2) {
            errors.add(new RuleError(currentLine, "RULE section is missing"));
            return "";
        }
        
        String id = parts[1];

        return id;
    }

}
