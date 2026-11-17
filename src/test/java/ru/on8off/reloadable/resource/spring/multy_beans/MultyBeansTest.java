package ru.on8off.reloadable.resource.spring.multy_beans;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class MultyBeansTest {
    @Autowired
    private TestBean1 testBean1;
    @Autowired
    private TestBean1 testBean2;
    @Autowired
    private MultyBeansTestDataLoader multyBeansTestDataLoader;

    @Test
    @DirtiesContext
    public void testMultyBeans() throws InterruptedException {
        assertNotNull(testBean1);
        assertEquals( 0, testBean1.getNumber());
        assertNotNull(testBean2);
        assertEquals( 0, testBean2.getNumber());

        multyBeansTestDataLoader.incrementAndGet();
        Thread.sleep(100);
        assertNotNull(testBean1);
        assertEquals( 1, testBean1.getNumber());
        assertNotNull(testBean2);
        assertEquals( 1, testBean2.getNumber());
    }

}