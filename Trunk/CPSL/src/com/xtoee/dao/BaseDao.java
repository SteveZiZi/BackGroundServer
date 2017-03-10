package com.xtoee.dao;

import java.io.Serializable;
import java.util.List;

/**
 * 基础数据库操作类
 * @author zgm
 *
 * @param <T>
 */
public interface BaseDao<T>
{
    /**
     * 保存一个对象
     * 
     * @param o     待保存的对象
     * @return
     */
    public Serializable save(T o);

    /**
     * 删除一个对象
     * 
     * @param o     待删除的对象
     */
    public void delete(T o);

    /**
     * 更新一个对象
     * 
     * @param o     待更新的对象
     */
    public void update(T o);

    /**
     * 保存或更新对象
     * 
     * @param o     待保存或更新的对象
     */
    public void saveOrUpdate(T o);

    /**
     * HQL查询
     * 
     * @param hql   HQL查询语句
     * @return  满足查询条件的对象列表
     */
    public List<T> find(String hql);

    /**
     * 根据HQL查询语句和参数，查询满足条件的对象列表
     * 
     * @param hql   HQL查询语句
     * @param param 查询参数数组
     * @return  满足查询条件的对象列表
     */
    public List<T> find(String hql, Object[] param);

    /**
     * 根据HQL查询语句和参数，查询满足条件的对象列表
     * 
     * @param hql   HQL查询语句
     * @param param 查询参数列表
     * @return  满足查询条件的对象列表
     */
    public List<T> find(String hql, List<Object> param);

    /**
     * 根据HQL查询语句和参数，查询指定页满足条件的对象列表(带分页)
     * 
     * @param hql       HQL查询语句
     * @param param     查询参数数组
     * @param page      查询第几页
     * @param rows      每页显示几条记录
     * @return  满足查询条件的对象列表
     */
    public List<T> find(String hql, Object[] param, Integer page, Integer rows);

    /**
     * 根据HQL查询语句和参数，查询指定页满足条件的对象列表(带分页)
     * 
     * @param hql       HQL查询语句
     * @param param     查询参数列表
     * @param page      查询第几页
     * @param rows      每页显示几条记录
     * @return  满足查询条件的对象列表
     */
    public List<T> find(String hql, List<Object> param, Integer page, Integer rows);

    /**
     * 获得一个对象
     * 
     * @param c         对象类型
     * @param id        主键
     * @return Object   指定主键的对象
     */
    public T get(Class<T> c, Serializable id);

    /**
     * 根据HQL查询语句和参数，查询第一个满足条件的对象
     * 
     * @param hql   HQL查询语句
     * @param param 查询参数数组
     * @return  第一个满足查询条件的对象
     */
    public T get(String hql, Object[] param);

    /**
     * 根据HQL查询语句和参数，查询第一个满足条件的对象
     * 
     * @param hql   HQL查询语句
     * @param param 查询参数列表
     * @return  第一个满足查询条件的对象
     */
    public T get(String hql, List<Object> param);

    /**
     * select count(*) from 类
     * 
     * @param hql   HQL查询语句
     * @return  满足查询条件的记录数
     */
    public Long count(String hql);

    /**
     * select count(*) from 类
     * 
     * @param hql   HQL查询语句
     * @param param 查询参数数组
     * @return  满足查询条件的记录数
     */
    public Long count(String hql, Object[] param);

    /**
     * select count(*) from 类
     * 
     * @param hql   HQL查询语句
     * @param param 查询参数列表
     * @return  满足查询条件的记录数
     */
    public Long count(String hql, List<Object> param);

    /**
     * 执行HQL语句
     * 
     * @param hql       待执行的HQL语句
     * @return 响应数目
     */
    public Integer executeHql(String hql);

    /**
     * 执行HQL语句
     * 
     * @param hql       待执行的HQL语句
     * @param param     参数数组
     * @return 响应数目
     */
    public Integer executeHql(String hql, Object[] param);

    /**
     * 执行HQL语句
     * 
     * @param hql       待执行的HQL语句
     * @param param     参数列表
     * @return 响应数目
     */
    public Integer executeHql(String hql, List<Object> param);

}
