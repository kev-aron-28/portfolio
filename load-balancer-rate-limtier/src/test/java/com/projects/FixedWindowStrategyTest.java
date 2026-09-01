package com.projects;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.projects.balancer.ratelimit.fixedwindow.FixedWindowStrategy;

public class FixedWindowStrategyTest {
    private final FixedWindowStrategy fixedWindow = new FixedWindowStrategy(5, 10_000);

    @Test
    public void shouldAllowRequestWithinWindow() {
        String id = "client-1";

        for(int i = 0; i < 5; i++){
            assertTrue(fixedWindow.allow(id));

        }        
    }

    @Test
    public void shouldRejectRequestsAfterLimit() {
        String id = "client-1";
        
        for(int i = 0; i < 5; i++){
            assertTrue(fixedWindow.allow(id));

        }
        
        assertFalse(fixedWindow.allow(id));
    }  

    @Test
    public void shouldAllowAfterWindowExpires() throws InterruptedException {
        FixedWindowStrategy strategy = new FixedWindowStrategy(2, 100);

        String id = "client-id";

        strategy.allow(id);
        strategy.allow(id);

        assertFalse(strategy.allow(id));

        Thread.sleep(150);

        assertTrue(strategy.allow(id));
    }
}
