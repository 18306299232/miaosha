package com.mzy.miaosha.service;

import com.mzy.miaosha.dao.OrderDao;
import com.mzy.miaosha.domin.MiaoshaOrder;
import com.mzy.miaosha.domin.MiaoshaUser;
import com.mzy.miaosha.domin.OrderInfo;
import com.mzy.miaosha.redis.OrderKey;
import com.mzy.miaosha.redis.RedisService;
import com.mzy.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;


@Service
public class OrderService {

    @Autowired
    OrderDao orderDao;
    @Autowired
    RedisService redisService;

    public MiaoshaOrder getMiaoshaOrderByUserIdGoodsId(long userId, long goodsId) {
        return orderDao.getMiaoshaOrderByUserIdGoodsId(userId, goodsId);
    }

    public OrderInfo getOrderById(long orderId) {
        return orderDao.getOrderById(orderId);
    }

    /**
     * 创建用户订单，根据miaosha订单和用户订单
     */
    @Transactional
    public OrderInfo createOrder(MiaoshaUser user, GoodsVo goods) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCreateDate(new Date());
        orderInfo.setDeliveryAddrId(0L);
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsId(goods.getId());
        orderInfo.setGoodsName(goods.getGoodsName());
        orderInfo.setGoodsPrice(goods.getMiaoshaPrice());
        orderInfo.setOrderChannel(1);
        orderInfo.setStatus(0);
        orderInfo.setUserId(user.getId());
        orderDao.insert(orderInfo);
        MiaoshaOrder miaoshaOrder = new MiaoshaOrder();
        miaoshaOrder.setGoodsId(goods.getId());
        miaoshaOrder.setOrderId(orderInfo.getGoodsId());
        miaoshaOrder.setUserId(user.getId());
        orderDao.insertMiaoshaOrder(miaoshaOrder);
        redisService.set(OrderKey.getMiaoshaOrderByUidGid, "" + user.getId() + "_" + goods.getId(), miaoshaOrder);
        return orderInfo;
    }

}
