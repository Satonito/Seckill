package seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ImportResource;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import seckill.entity.Seckill;

import javax.annotation.Resource;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/*配置spring和junit整合，junit启动时加载springIOC容器
*  srping-test,junit
**/
@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit spring配置文件
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SeckillDaoTest {

    //注入Dao实现类依赖
    @Resource
    private SeckillDao seckillDao;


    @Test
    public void testQueryById() throws Exception{
        //long id = 1000;
        Seckill seckill = seckillDao.queryById(1000L);
        System.out.println(seckill.getName());
        System.out.println(seckill);
    }

    /*org.mybatis.spring.MyBatisSystemException:
     nested exception is org.apache.ibatis.binding.BindingException:
     Parameter 'offset' not found. Available parameters are [0, 1, param1, param2]
    *  原因是java没有保存形参的记录
    *  queryAll(int offset,int limit) -> queryAll(arg0,arg1)
    *  所以使用@param注释
    */
    @Test
    public void queryAll() throws Exception{
        List<Seckill> seckills = seckillDao.queryAll(0, 100);
        for (Seckill seckill: seckills){
            System.out.println(seckills);
        }
    }

    @Test
    public void reduceNumber() throws Exception{
        Date killTime = new Date();
        int updateCount = seckillDao.reduceNumber(1000L,killTime);
        System.out.println(updateCount);
    }


}
