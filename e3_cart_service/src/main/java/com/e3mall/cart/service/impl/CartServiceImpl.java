package com.e3mall.cart.service.impl;

import com.e3mall.cart.service.CartService;
import com.e3mall.jedis.JedisClient;
import com.e3mall.mapper.TbItemMapper;
import com.e3mall.pojo.TbItem;
import com.e3mall.utils.E3Result;
import com.e3mall.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by qimenggao on 2017/12/30.
 */
@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private JedisClient jedisClient;

    @Value("${CART_REDIS_KEY}")
    private String CART_REDIS_KEY;

    @Autowired
    private TbItemMapper itemMapper;

    @Override
    public E3Result addCart(Long userId, Long itemId, Integer num) {

        // a)判断购物车中是否有此商品
        Boolean flag = jedisClient.hexists(CART_REDIS_KEY + ":" + userId, itemId + "");
        // b)如果有，数量相加
        if (flag) {
            //从hash中取商品数据
            String json = jedisClient.hget(CART_REDIS_KEY + ":" + userId, itemId + "");
            //转换成java对象
            TbItem tbItem = JsonUtils.jsonToPojo(json, TbItem.class);
            //数量相加
            tbItem.setNum(tbItem.getNum() + num);
            //写入hash
            jedisClient.hset(CART_REDIS_KEY + ":" + userId, itemId + "", JsonUtils.objectToJson(tbItem));
            //返回添加成功
            return E3Result.ok();
        }
        // c)如果没有，根据商品id查询商品信息。
        TbItem tbItem = itemMapper.selectByPrimaryKey(itemId);
        //设置商品数量
        tbItem.setNum(num);
        String image = tbItem.getImage();
        //取一张图片
        if (StringUtils.isNotBlank(image)) {
            tbItem.setImage(image.split(",")[0]);
        }
        // d)把商品信息添加到购物车
        jedisClient.hset(CART_REDIS_KEY + ":" + userId, itemId + "", JsonUtils.objectToJson(tbItem));
        return E3Result.ok();
    }
}
