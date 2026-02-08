package com.sky.mapper;

import java.util.List;

public interface SetmealDishMapper {
    /*
    * 根据菜品id查询对应的套餐id
    * */
    //select setmeal_id from setmeal_dish where dish_id in(1,2,3,4) 动态sql 传进来的是一个集合
    List<Long> getSetmeaIdsByDishIds(List<Long>dishIds);
}
