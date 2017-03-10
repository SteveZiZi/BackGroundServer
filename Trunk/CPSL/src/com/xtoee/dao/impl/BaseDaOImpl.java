package com.xtoee.dao.impl;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.xtoee.dao.BaseDao;

/**
 * 基础数据库操作实现类
 * @author zgm
 *
 * @param <T>
 */
public class BaseDaOImpl<T> implements BaseDao<T>
{
    private SessionFactory          sessionFactory;             // session工厂

    
    /**
     * 获得session工厂
     * @return  session工厂对象
     */
    public SessionFactory getSessionFactory()
    {
        return sessionFactory;
    }

    /**
     * 设置session工厂
     * @param sessionFactory    session工厂
     */
    public void setSessionFactory(SessionFactory sessionFactory)
    {
        this.sessionFactory = sessionFactory;
    }

    /**
     * 获得一个session对象
     * @return  session对象
     */
    private Session getCurrentSession()
    {
        return sessionFactory.getCurrentSession();
    }

    /**
     * 保存一个对象
     * 
     * @param o     待保存的对象
     * @return
     */
    public Serializable save(T o)
    {
        return this.getCurrentSession().save(o);
    }

    /**
     * 删除一个对象
     * 
     * @param o     待删除的对象
     */
    public void delete(T o)
    {
        this.getCurrentSession().delete(o);
    }

    /**
     * 更新一个对象
     * 
     * @param o     待更新的对象
     */
    public void update(T o)
    {
        this.getCurrentSession().update(o);
    }

    /**
     * 保存或更新对象
     * 
     * @param o     待保存或更新的对象
     */
    public void saveOrUpdate(T o)
    {
        this.getCurrentSession().saveOrUpdate(o);
    }

    /**
     * HQL查询
     * 
     * @param hql   HQL查询语句
     * @return  满足查询条件的对象列表
     */
    @SuppressWarnings("unchecked")
    public List<T> find(String hql)
    {
        return this.getCurrentSession().createQuery(hql).list();
    }

    /**
     * 根据HQL查询语句和参数，查询满足条件的对象列表
     * 
     * @param hql   HQL查询语句
     * @param param 查询参数数组
     * @return  满足查询条件的对象列表
     */
    @SuppressWarnings("unchecked")
    public List<T> find(String hql, Object[] param)
    {
        Query q = this.getCurrentSession().createQuery(hql);
        if (param != null && param.length > 0)
        {
            for (int i = 0; i < param.length; i++)
            {
                q.setParameter(i, param[i]);
            }
        }
        
        return q.list();
    }

    /**
     * 根据HQL查询语句和参数，查询满足条件的对象列表
     * 
     * @param hql   HQL查询语句
     * @param param 查询参数列表
     * @return  满足查询条件的对象列表
     */
    @SuppressWarnings("unchecked")
    public List<T> find(String hql, List<Object> param)
    {
        Query q = this.getCurrentSession().createQuery(hql);
        if (param != null && param.size() > 0)
        {
            for (int i = 0; i < param.size(); i++)
            {
                q.setParameter(i, param.get(i));
            }
        }
        
        return q.list();
    }

    /**
     * 根据HQL查询语句和参数，查询指定页满足条件的对象列表(带分页)
     * 
     * @param hql       HQL查询语句
     * @param param     查询参数数组
     * @param page      查询第几页
     * @param rows      每页显示几条记录
     * @return  满足查询条件的对象列表
     */
    @SuppressWarnings("unchecked")
    public List<T> find(String hql, Object[] param, Integer page, Integer rows)
    {
        if (page == null || page < 1)
        {
            page = 1;
        }
        
        if (rows == null || rows < 1)
        {
            rows = 10;
        }
        
        Query q = this.getCurrentSession().createQuery(hql);
        if (param != null && param.length > 0)
        {
            for (int i = 0; i < param.length; i++)
            {
                q.setParameter(i, param[i]);
            }
        }
        
        return q.setFirstResult((page - 1) * rows).setMaxResults(rows).list();
    }

    /**
     * 根据HQL查询语句和参数，查询指定页满足条件的对象列表(带分页)
     * 
     * @param hql       HQL查询语句
     * @param param     查询参数列表
     * @param page      查询第几页
     * @param rows      每页显示几条记录
     * @return  满足查询条件的对象列表
     */
    @SuppressWarnings("unchecked")
    public List<T> find(String hql, List<Object> param, Integer page, Integer rows)
    {
        if (page == null || page < 1)
        {
            page = 1;
        }
        
        if (rows == null || rows < 1)
        {
            rows = 10;
        }
        
        Query q = this.getCurrentSession().createQuery(hql);
        if (param != null && param.size() > 0)
        {
            for (int i = 0; i < param.size(); i++)
            {
                q.setParameter(i, param.get(i));
            }
        }
        
        return q.setFirstResult((page - 1) * rows).setMaxResults(rows).list();
    }

    /**
     * 获得一个对象
     * 
     * @param c         对象类型
     * @param id        主键
     * @return Object   指定主键的对象
     */
    @SuppressWarnings("unchecked")
    public T get(Class<T> c, Serializable id)
    {
        return (T)this.getCurrentSession().get(c, id);
    }

    /**
     * 根据HQL查询语句和参数，查询第一个满足条件的对象
     * 
     * @param hql   HQL查询语句
     * @param param 查询参数数组
     * @return  第一个满足查询条件的对象
     */
    public T get(String hql, Object[] param)
    {
        List<T> l = this.find(hql, param);
        if (l != null && l.size() > 0)
        {
            return l.get(0);
        }
        else
        {
            return null;
        }
    }

    /**
     * 根据HQL查询语句和参数，查询第一个满足条件的对象
     * 
     * @param hql   HQL查询语句
     * @param param 查询参数列表
     * @return  第一个满足查询条件的对象
     */
    public T get(String hql, List<Object> param)
    {
        List<T> l = this.find(hql, param);
        if (l != null && l.size() > 0)
        {
            return l.get(0);
        }
        else
        {
            return null;
        }
    }

    /**
     * select count(*) from 类
     * 
     * @param hql   HQL查询语句
     * @return  满足查询条件的记录数
     */
    public Long count(String hql)
    {
        return (Long) this.getCurrentSession().createQuery(hql).uniqueResult();
    }

    /**
     * select count(*) from 类
     * 
     * @param hql   HQL查询语句
     * @param param 查询参数数组
     * @return  满足查询条件的记录数
     */
    public Long count(String hql, Object[] param)
    {
        Query q = this.getCurrentSession().createQuery(hql);
        if (param != null && param.length > 0)
        {
            for (int i = 0; i < param.length; i++)
            {
                q.setParameter(i, param[i]);
            }
        }
        
        return (Long) q.uniqueResult();
    }

    /**
     * select count(*) from 类
     * 
     * @param hql   HQL查询语句
     * @param param 查询参数列表
     * @return  满足查询条件的记录数
     */
    public Long count(String hql, List<Object> param)
    {
        Query q = this.getCurrentSession().createQuery(hql);
        if (param != null && param.size() > 0)
        {
            for (int i = 0; i < param.size(); i++)
            {
                q.setParameter(i, param.get(i));
            }
        }
        return (Long) q.uniqueResult();
    }

    /**
     * 执行HQL语句
     * 
     * @param hql       待执行的HQL语句
     * @return 响应数目
     */
    public Integer executeHql(String hql)
    {
        return this.getCurrentSession().createQuery(hql).executeUpdate();
    }

    /**
     * 执行HQL语句
     * 
     * @param hql       待执行的HQL语句
     * @param param     参数数组
     * @return 响应数目
     */
    public Integer executeHql(String hql, Object[] param)
    {
        Query q = this.getCurrentSession().createQuery(hql);
        if (param != null && param.length > 0)
        {
            for (int i = 0; i < param.length; i++)
            {
                q.setParameter(i, param[i]);
            }
        }
        
        return q.executeUpdate();
    }

    /**
     * 执行HQL语句
     * 
     * @param hql       待执行的HQL语句
     * @param param     参数列表
     * @return 响应数目
     */
    public Integer executeHql(String hql, List<Object> param)
    {
        Query q = this.getCurrentSession().createQuery(hql);
        if (param != null && param.size() > 0)
        {
            for (int i = 0; i < param.size(); i++)
            {
                q.setParameter(i, param.get(i));
            }
        }
        
        return q.executeUpdate();
    }
}
