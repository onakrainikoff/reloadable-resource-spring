package ru.on8off.reloadable.resource.spring.disabled;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DisabledTest {
    @Autowired
    private DisabledTestBean disabledTestBean;
    @Autowired
    private DisabledTestDataLoader disabledTestDataLoader;

    @Test
    @DirtiesContext
    public void testDisabled() throws InterruptedException {
        assertNotNull(disabledTestBean);
        assertNull(disabledTestBean.getNumber());

        Thread.sleep(100);
        disabledTestDataLoader.incrementAndGet();
        Thread.sleep(100);
        assertNotNull(disabledTestBean);
        assertNull(disabledTestBean.getNumber());
    }

}