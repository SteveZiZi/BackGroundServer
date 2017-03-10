package com.xtoee.dao;

import java.io.Serializable;
import java.util.List;

/**
 * �������ݿ������
 * @author zgm
 *
 * @param <T>
 */
public interface BaseDao<T>
{
    /**
     * ����һ������
     * 
     * @param o     ������Ķ���
     * @return
     */
    public Serializable save(T o);

    /**
     * ɾ��һ������
     * 
     * @param o     ��ɾ���Ķ���
     */
    public void delete(T o);

    /**
     * ����һ������
     * 
     * @param o     �����µĶ���
     */
    public void update(T o);

    /**
     * �������¶���
     * 
     * @param o     ���������µĶ���
     */
    public void saveOrUpdate(T o);

    /**
     * HQL��ѯ
     * 
     * @param hql   HQL��ѯ���
     * @return  �����ѯ�����Ķ����б�
     */
    public List<T> find(String hql);

    /**
     * ����HQL��ѯ���Ͳ�������ѯ���������Ķ����б�
     * 
     * @param hql   HQL��ѯ���
     * @param param ��ѯ��������
     * @return  �����ѯ�����Ķ����б�
     */
    public List<T> find(String hql, Object[] param);

    /**
     * ����HQL��ѯ���Ͳ�������ѯ���������Ķ����б�
     * 
     * @param hql   HQL��ѯ���
     * @param param ��ѯ�����б�
     * @return  �����ѯ�����Ķ����б�
     */
    public List<T> find(String hql, List<Object> param);

    /**
     * ����HQL��ѯ���Ͳ�������ѯָ��ҳ���������Ķ����б�(����ҳ)
     * 
     * @param hql       HQL��ѯ���
     * @param param     ��ѯ��������
     * @param page      ��ѯ�ڼ�ҳ
     * @param rows      ÿҳ��ʾ������¼
     * @return  �����ѯ�����Ķ����б�
     */
    public List<T> find(String hql, Object[] param, Integer page, Integer rows);

    /**
     * ����HQL��ѯ���Ͳ�������ѯָ��ҳ���������Ķ����б�(����ҳ)
     * 
     * @param hql       HQL��ѯ���
     * @param param     ��ѯ�����б�
     * @param page      ��ѯ�ڼ�ҳ
     * @param rows      ÿҳ��ʾ������¼
     * @return  �����ѯ�����Ķ����б�
     */
    public List<T> find(String hql, List<Object> param, Integer page, Integer rows);

    /**
     * ���һ������
     * 
     * @param c         ��������
     * @param id        ����
     * @return Object   ָ�������Ķ���
     */
    public T get(Class<T> c, Serializable id);

    /**
     * ����HQL��ѯ���Ͳ�������ѯ��һ�����������Ķ���
     * 
     * @param hql   HQL��ѯ���
     * @param param ��ѯ��������
     * @return  ��һ�������ѯ�����Ķ���
     */
    public T get(String hql, Object[] param);

    /**
     * ����HQL��ѯ���Ͳ�������ѯ��һ�����������Ķ���
     * 
     * @param hql   HQL��ѯ���
     * @param param ��ѯ�����б�
     * @return  ��һ�������ѯ�����Ķ���
     */
    public T get(String hql, List<Object> param);

    /**
     * select count(*) from ��
     * 
     * @param hql   HQL��ѯ���
     * @return  �����ѯ�����ļ�¼��
     */
    public Long count(String hql);

    /**
     * select count(*) from ��
     * 
     * @param hql   HQL��ѯ���
     * @param param ��ѯ��������
     * @return  �����ѯ�����ļ�¼��
     */
    public Long count(String hql, Object[] param);

    /**
     * select count(*) from ��
     * 
     * @param hql   HQL��ѯ���
     * @param param ��ѯ�����б�
     * @return  �����ѯ�����ļ�¼��
     */
    public Long count(String hql, List<Object> param);

    /**
     * ִ��HQL���
     * 
     * @param hql       ��ִ�е�HQL���
     * @return ��Ӧ��Ŀ
     */
    public Integer executeHql(String hql);

    /**
     * ִ��HQL���
     * 
     * @param hql       ��ִ�е�HQL���
     * @param param     ��������
     * @return ��Ӧ��Ŀ
     */
    public Integer executeHql(String hql, Object[] param);

    /**
     * ִ��HQL���
     * 
     * @param hql       ��ִ�е�HQL���
     * @param param     �����б�
     * @return ��Ӧ��Ŀ
     */
    public Integer executeHql(String hql, List<Object> param);

}
