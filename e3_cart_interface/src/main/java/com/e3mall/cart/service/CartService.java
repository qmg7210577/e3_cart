package com.e3mall.cart.service;

import com.e3mall.utils.E3Result;

/**
 * Created by qimenggao on 2017/12/30.
 */
public interface CartService {
    E3Result addCart(Long userId,Long itemId,Integer num);
}
