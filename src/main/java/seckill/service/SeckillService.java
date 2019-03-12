package seckill.service;

/*
* 业务接口：站在“使用者”角度设计接口
* 三个方面：方法定义粒度，参数，返回类型（return 类型/异常）
*
*/

import seckill.dto.Exposer;
import seckill.dto.SeckillExecution;
import seckill.entity.Seckill;
import seckill.exception.RepeatKillException;
import seckill.exception.SeckillCloseException;
import seckill.exception.SeckillException;

import java.util.List;

public interface SeckillService {

    List<Seckill> getSeckillList();

    /*
    *   查询单个秒杀记录
    **/
    Seckill getById(long seckillId);

    /*
    * 秒杀开启时输出接口地址，
    * 否则输出系统时间和秒杀时间
    *
    */

    Exposer exportSeckillUrl(long seckillId);

    /*
    *  执行秒杀操作
    *
    * */
    SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
        throws SeckillException, RepeatKillException, SeckillCloseException;
}
