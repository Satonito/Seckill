package seckill.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import seckill.SeckillStateEnum.SeckillStateEnum;
import seckill.dao.SeckillDao;
import seckill.dao.SuccessKilledDao;
import seckill.dto.Exposer;
import seckill.dto.SeckillExecution;
import seckill.entity.Seckill;
import seckill.entity.SuccessKilled;
import seckill.exception.RepeatKillException;
import seckill.exception.SeckillCloseException;
import seckill.exception.SeckillException;
import seckill.service.SeckillService;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

//@Component @Service @Dao @Conroller
@Service
public class SeckillServiceImpl implements SeckillService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    //注入Service依赖
    @Autowired//@Resource,@Inject
    private SeckillDao seckillDao;

    @Autowired
    private SuccessKilledDao successKilledDao;

    //混淆，让用户猜不到这是什么,用于混淆md5
    private final String slat = "df#@%@￥%salDFGAGJALKkf47546jalkffsdasa";

    public List<Seckill> getSeckillList() {
        return seckillDao.queryAll(0, 4);
    }

    public Seckill getById(long seckillId) {
        return seckillDao.queryById(seckillId);
    }

    /*
     * 秒杀开启时输出接口地址，
     * 否则输出系统时间和秒杀时间
     *
     */
    public Exposer exportSeckillUrl(long seckillId) {
        Seckill seckill = seckillDao.queryById(seckillId);

        if (seckill == null) {
            return new Exposer(false, seckillId);
        }
        Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();
        //当前时间
        Date nowTime = new Date();

        if (nowTime.getTime() < startTime.getTime()
                || nowTime.getTime() > endTime.getTime()) {
            return new Exposer(false, seckillId, nowTime.getTime(), startTime.getTime(), endTime.getTime());
        }
        //转化特定字符串的过程，不可逆
        String md5 = getMD5(seckillId);   //TODO
        return new Exposer(true, md5, seckillId);
    }

    //生成md5
    private String getMD5(long seckillId) {
        String base = seckillId + "/" + slat;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }

    @Transactional
    /*
    *   使用注解控制事务方法的有点
    *   1：开发团队达成一致约定，明确标注事务方法的编程风格
    *   2：保证事务方法的执行时间尽可能短，不要穿插其他网络操作，RPC/HTTP请求/或者剥离到事务方法外
    *   3：不是所有的方法都需要事务，如只有一条修改操作，只读操作不需要事务控制
    *
    */
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
            throws SeckillException, RepeatKillException, SeckillCloseException {

        //通过md5判断
        if (md5 == null || !md5.equals(getMD5(seckillId))) {
            throw new SeckillException("seckill data rewrite");
        }

        //执行秒杀逻辑：减库存+记录购买记录
        Date nowTime = new Date();
        try {
            //减库存成功，记录购买行为
            int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
            //验证唯一：seckillId,userPhone
            if (insertCount <= 0) {
                //重复秒杀
                throw new RepeatKillException("seckill repeated");
            } else {
                //减库存，热点商品竞争
                int updateCount = seckillDao.reduceNumber(seckillId, nowTime);
                if (updateCount <= 0) {
                    //没有更新到记录,秒杀结束 rollback
                    throw new SeckillCloseException("seckill is closed");
                } else {
                    //秒杀成功 commit
                    SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
                    return new SeckillExecution(seckillId,SeckillStateEnum.SUCCESS,successKilled);
                }
            }


        } catch (SeckillCloseException e1) {
            throw e1;
        } catch (RepeatKillException e2){
            throw e2;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new SeckillException("seckill inner error" + e.getMessage());
        }
    }

}
