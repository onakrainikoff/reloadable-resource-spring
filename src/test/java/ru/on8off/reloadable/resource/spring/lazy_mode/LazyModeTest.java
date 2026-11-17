package ru.on8off.reloadable.resource.spring.lazy_mode;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LazyModeTest {
    @Autowired
    private LazyModeTestBean lazyModeTestBean;
    @Autowired
    private LazyModeTestDataLoader lazyModeTestDataLoader;

    @Test
    @DirtiesContext
    public void testLazyMode() throws InterruptedException {
        assertNotNull(lazyModeTestBean);
        assertNull(lazyModeTestBean.getNumber());
        Thread.sleep(1000);
        lazyModeTestDataLoader.incrementAndGet();
        Thread.sleep(1100);
        assertNotNull(lazyModeTestBean);
        assertEquals( 1, lazyModeTestBean.getNumber());
    }

}