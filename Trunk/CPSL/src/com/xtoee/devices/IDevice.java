package com.xtoee.devices;

import java.util.concurrent.ScheduledFuture;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * ʱƵ�豸�ӿ�
 * @author zgm
 *
 */
public interface IDevice extends Runnable
{
    /**
     * ���ʱƵ�豸������
     * @return  �豸����
     */
    public enumDeviceType GetDeviceType();
    
    /**
     * ����ʱƵ�豸������
     * @param eType �豸����
     */
    public void SetDeviceType(enumDeviceType eType);
    
    /**
     * ����豸ID
     * @return  �豸ID
     */
    public int getID();
    
    /**
     * �����豸ID
     * @param nID �豸ID
     */
    public void setID(int nID);
    
    /**
     * ����豸����
     * @return  �豸����
     */
    public String getDeviceName();
    
    /**
     * �����豸����
     * @param strName   �豸����
     */
    public void SetDeviceName(String strName);
    
    /**
     * ����豸IP��ַ
     * @return  �豸IP��ַ
     */
    public String GetDeviceIP();
    
    /**
     * �����豸IP��ַ
     * @param strIP �豸IP��ַ
     */
    public void SetDeviceIP(String strIP);
    
    /**
     * ����豸�߼���ַ
     * @return  �߼���ַ
     */
    public String getLogicAddress();

    /**
     * �����豸�߼���ַ
     * @param strLogicAddress   �߼���ַ
     */
    public void setLogicAddress(String strLogicAddress);
    
    /**
     * ����豸״̬
     * @return  �豸״̬
     */
    public enumDeviceState GetDeviceState();
    
    /**
     * �����豸״̬
     * @param eNewState �豸״̬
     */
    public void SetDeviceState(enumDeviceState eNewState);
    
    /**
     * ��������ַ���
     * @return  �����ַ���
     */
    public String toString();
    
    /**
     * ��������
     * @param xnDevice  �豸��Ϣ�����ڵ�
     * @return  ���ڳɹ�����true
     */
    public boolean LoadConfig(Element xnDevice);
    
    /**
     * ��������
     * @param xmlDoc    XML�ĵ�����
     * @param xnDevice  XML�豸�ڵ����
     * @return  ����ɹ�����true
     */
    public boolean SaveConfig(Document xmlDoc, Element xnDevice);
    
    /**
     * ����豸״̬��ѯ����
     * @return  ״̬��ѯ����
     */
    public ScheduledFuture<?> GetScheduledFuture();
    
    /**
     * �����豸״̬��ѯ����
     * @param sf    ״̬��ѯ����
     */
    public void SetScheduledFuture(ScheduledFuture<?> sf);
}
