package ru.on8off.reloadable.resource.spring.multy_dataloaders;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("MultyDataloadersNegativeTest")
class MultyDataLoadersNegativeTest {
    @Autowired
    private MultyDataLoadersTestBean multyDataloadersTestBean;
    @Autowired
    private TestDataLoader dataLoader;

    @Test
    @DirtiesContext
    public void testMultyDataloaders() throws InterruptedException {
        assertNotNull(multyDataloadersTestBean);
        assertEquals(0, multyDataloadersTestBean.getNumber());
        Thread.sleep(100);
        dataLoader.update();
        Thread.sleep(100);
        assertNotNull(multyDataloadersTestBean);
        assertEquals( -1, multyDataloadersTestBean.getNumber());
    }

}