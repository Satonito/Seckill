package seckill.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import seckill.dto.Exposer;
import seckill.dto.SeckillExecution;
import seckill.entity.Seckill;
import seckill.exception.RepeatKillException;
import seckill.exception.SeckillException;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({
        "classpath:spring/spring-dao.xml",
        "classpath:spring/spring-service.xml"
})
public class SeckillServiceTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillService seckillService;

    @Test
    public void getSeckillList() {
        List<Seckill> list = seckillService.getSeckillList();
        logger.info("list={}", list);
    }

    @Test
    public void getById() {
        long id = 1000;
        Seckill seckill = seckillService.getById(id);
        logger.info("seckill={}", seckill);
    }

    @Test
    public void exportSeckillUrl() {
        long id = 1001;
        long phone = 87623632l;
        Exposer exposer = seckillService.exportSeckillUrl(id);
        String md5 = exposer.getMd5();
        if (exposer.isExposed()) {
            try {
                SeckillExecution seckillExecution = seckillService.executeSeckill(id, phone, md5);
            } catch (RepeatKillException e) {
                logger.info(e.getMessage());
            } catch (SeckillException e) {
                logger.info(e.getMessage());
            }
            logger.info("exposer={}", exposer);
        } else {
            logger.error("exposer={}", exposer);
        }
    }
}

