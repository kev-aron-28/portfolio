package com.projects.api_service.domain;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.projects.api_service.domain.services.TemplateVariableExtractor;

public class TemplateVariableExtractorTest {
    TemplateVariableExtractor extractor;

    @BeforeEach
    public void beforeEach() {
        extractor = new TemplateVariableExtractor();
    }


    @Test
    public void shouldGetAllLabelsInTemplate() {
        String template = "Hi {{name}}, test variable {{var}}";
        
        Set<String> variables = this.extractor.extract(template);

        assertEquals(2, variables.size());
    }
}   
