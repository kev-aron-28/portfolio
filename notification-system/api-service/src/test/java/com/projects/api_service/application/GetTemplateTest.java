package com.projects.api_service.application;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.projects.api_service.domain.NotificationChannel;
import com.projects.api_service.domain.Template;
import com.projects.api_service.domain.TemplateRepository;

@ExtendWith(MockitoExtension.class)
public class GetTemplateTest {
    @Mock
    private TemplateRepository repository;

    @InjectMocks
    private GetTemplate getTemplate;


    @Test
    void shouldReturnTheRequestTemplate() {
        Template template = new Template("test", NotificationChannel.EMAIL, "Test", "Hi {{kevin}}");

        when(repository.findById(1L)).thenReturn(Optional.of(template));

        getTemplate.run(1L);

        verify(repository, times(1)).findById(1L);
    }

}
