package ru.on8off.reloadable.resource.spring.life_cycle;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LifeCycleTest {
    @Autowired
    private LifeCycleTestBean lifeCycleTestBean;
    @Autowired
    private LifeCycleTestDataLoader lifeCycleTestDataLoader;
    @Autowired
    private AtomicLong postConstructCounter;
    @Autowired
    private AtomicLong preDestroyCounter;

    @Test
    @DirtiesContext
    public void testLifeCycle() throws InterruptedException {
        assertNotNull(lifeCycleTestBean);
        assertEquals( 0, lifeCycleTestBean.getNumber());
        assertEquals("test", lifeCycleTestBean.getTestProperty());
        assertEquals(1, postConstructCounter.get());
        assertEquals(0, preDestroyCounter.get());

        lifeCycleTestDataLoader.incrementAndGet();
        Thread.sleep(100);
        assertNotNull(lifeCycleTestBean);
        assertEquals( 1, lifeCycleTestBean.getNumber());
        assertEquals("test", lifeCycleTestBean.getTestProperty());
        assertEquals(2, postConstructCounter.get());
        assertEquals(1, preDestroyCounter.get());
    }

}