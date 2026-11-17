package ru.on8off.reloadable.resource.spring.exception_on_reload;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ExceptionOnReloadTest {
    @Autowired
    private ExceptionOnReloadTestBean exceptionOnReloadTestBean;
    @Autowired
    private ExceptionOnReloadTestDataLoader exceptionOnReloadTestDataLoader;
    @Autowired
    private AtomicLong postConstructCounter;
    @Autowired
    private AtomicLong preDestroyCounter;
    @Autowired
    @Qualifier("exceptionCounter")
    private AtomicLong exceptionCounter;
    @Autowired
    @Qualifier("exceptionCounterCommon")
    private AtomicLong exceptionCounterCommon;

    @Test
    @DirtiesContext
    public void testExceptionOnReload() throws InterruptedException {
        assertNotNull(exceptionOnReloadTestBean);
        assertEquals( 0, exceptionOnReloadTestBean.getNumber());
        assertEquals(1, postConstructCounter.get());
        assertEquals(0, preDestroyCounter.get());
        assertEquals(0, exceptionCounter.get());
        assertEquals(0, exceptionCounterCommon.get());

        exceptionOnReloadTestDataLoader.incrementAndGet();
        Thread.sleep(100);
        assertNotNull(exceptionOnReloadTestBean);
        assertEquals( 0, exceptionOnReloadTestBean.getNumber());
        assertEquals(1, postConstructCounter.get());
        assertEquals(0, preDestroyCounter.get());
        assertTrue(exceptionCounter.get() > 0);
        assertTrue(exceptionCounterCommon.get() > 0);
    }

}