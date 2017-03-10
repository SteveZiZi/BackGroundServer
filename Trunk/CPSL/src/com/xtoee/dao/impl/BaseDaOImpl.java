package com.xtoee.dao.impl;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.xtoee.dao.BaseDao;

/**
 * �������ݿ����ʵ����
 * @author zgm
 *
 * @param <T>
 */
public class BaseDaOImpl<T> implements BaseDao<T>
{
    private SessionFactory          sessionFactory;             // session����

    
    /**
     * ���session����
     * @return  session��������
     */
    public SessionFactory getSessionFactory()
    {
        return sessionFactory;
    }

    /**
     * ����session����
     * @param sessionFactory    session����
     */
    public void setSessionFactory(SessionFactory sessionFactory)
    {
        this.sessionFactory = sessionFactory;
    }

    /**
     * ���һ��session����
     * @return  session����
     */
    private Session getCurrentSession()
    {
        return sessionFactory.getCurrentSession();
    }

    /**
     * ����һ������
     * 
     * @param o     ������Ķ���
     * @return
     */
    public Serializable save(T o)
    {
        return this.getCurrentSession().save(o);
    }

    /**
     * ɾ��һ������
     * 
     * @param o     ��ɾ���Ķ���
     */
    public void delete(T o)
    {
        this.getCurrentSession().delete(o);
    }

    /**
     * ����һ������
     * 
     * @param o     �����µĶ���
     */
    public void update(T o)
    {
        this.getCurrentSession().update(o);
    }

    /**
     * �������¶���
     * 
     * @param o     ���������µĶ���
     */
    public void saveOrUpdate(T o)
    {
        this.getCurrentSession().saveOrUpdate(o);
    }

    /**
     * HQL��ѯ
     * 
     * @param hql   HQL��ѯ���
     * @return  �����ѯ�����Ķ����б�
     */
    @SuppressWarnings("unchecked")
    public List<T> find(String hql)
    {
        return this.getCurrentSession().createQuery(hql).list();
    }

    /**
     * ����HQL��ѯ���Ͳ�������ѯ���������Ķ����б�
     * 
     * @param hql   HQL��ѯ���
     * @param param ��ѯ��������
     * @return  �����ѯ�����Ķ����б�
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
     * ����HQL��ѯ���Ͳ�������ѯ���������Ķ����б�
     * 
     * @param hql   HQL��ѯ���
     * @param param ��ѯ�����б�
     * @return  �����ѯ�����Ķ����б�
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
     * ����HQL��ѯ���Ͳ�������ѯָ��ҳ���������Ķ����б�(����ҳ)
     * 
     * @param hql       HQL��ѯ���
     * @param param     ��ѯ��������
     * @param page      ��ѯ�ڼ�ҳ
     * @param rows      ÿҳ��ʾ������¼
     * @return  �����ѯ�����Ķ����б�
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
     * ����HQL��ѯ���Ͳ�������ѯָ��ҳ���������Ķ����б�(����ҳ)
     * 
     * @param hql       HQL��ѯ���
     * @param param     ��ѯ�����б�
     * @param page      ��ѯ�ڼ�ҳ
     * @param rows      ÿҳ��ʾ������¼
     * @return  �����ѯ�����Ķ����б�
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
     * ���һ������
     * 
     * @param c         ��������
     * @param id        ����
     * @return Object   ָ�������Ķ���
     */
    @SuppressWarnings("unchecked")
    public T get(Class<T> c, Serializable id)
    {
        return (T)this.getCurrentSession().get(c, id);
    }

    /**
     * ����HQL��ѯ���Ͳ�������ѯ��һ�����������Ķ���
     * 
     * @param hql   HQL��ѯ���
     * @param param ��ѯ��������
     * @return  ��һ�������ѯ�����Ķ���
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
     * ����HQL��ѯ���Ͳ�������ѯ��һ�����������Ķ���
     * 
     * @param hql   HQL��ѯ���
     * @param param ��ѯ�����б�
     * @return  ��һ�������ѯ�����Ķ���
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
     * select count(*) from ��
     * 
     * @param hql   HQL��ѯ���
     * @return  �����ѯ�����ļ�¼��
     */
    public Long count(String hql)
    {
        return (Long) this.getCurrentSession().createQuery(hql).uniqueResult();
    }

    /**
     * select count(*) from ��
     * 
     * @param hql   HQL��ѯ���
     * @param param ��ѯ��������
     * @return  �����ѯ�����ļ�¼��
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
     * select count(*) from ��
     * 
     * @param hql   HQL��ѯ���
     * @param param ��ѯ�����б�
     * @return  �����ѯ�����ļ�¼��
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
     * ִ��HQL���
     * 
     * @param hql       ��ִ�е�HQL���
     * @return ��Ӧ��Ŀ
     */
    public Integer executeHql(String hql)
    {
        return this.getCurrentSession().createQuery(hql).executeUpdate();
    }

    /**
     * ִ��HQL���
     * 
     * @param hql       ��ִ�е�HQL���
     * @param param     ��������
     * @return ��Ӧ��Ŀ
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
     * ִ��HQL���
     * 
     * @param hql       ��ִ�е�HQL���
     * @param param     �����б�
     * @return ��Ӧ��Ŀ
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