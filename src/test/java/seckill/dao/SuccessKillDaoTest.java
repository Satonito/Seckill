package seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import seckill.entity.SuccessKilled;

import javax.annotation.Resource;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SuccessKillDaoTest {

    @Resource
    private SuccessKilledDao successKillDao;

    @Test
    public void insertSuccessKilled() {
        int insertSuccessKilled = successKillDao.insertSuccessKilled(1000, 87623632);
        System.out.println(insertSuccessKilled);
    }

    @Test
    public void queryByIdWithSeckill() {
        SuccessKilled successKilled = successKillDao.queryByIdWithSeckill(1000,87623632);
        System.out.println(successKilled);
        System.out.println(successKilled.getSeckill());
    }
}
