package com.xtoee.service;

import java.util.Date;

/**
 * ���ù���ӿ�
 * @author zgm
 *
 */
public interface ConfigManageService
{
    /**
     * ��ù���Ա�ֻ���
     * @param logicAddress  �豸�߼���ַ
     * @return  �ֻ�����
     */
    public String getAdminPhone(String logicAddress);
    
    /**
     * ���ù���Ա�ֻ���
     * @param logicAddress  �豸�߼���ַ
     * @param adminPhone    ����Ա�ֻ���
     * @return  �ɹ�����true
     */
    public boolean setAdminPhone(String logicAddress, String adminPhone);
    
    /**
     * �����豸ʱ��
     * @param logicAddress  �豸�߼���ַ
     * @param newDatetime   �豸ʱ��
     * @return  �ɹ�����true
     */
    public boolean setDeviceTime(String logicAddress, Date newDatetime);
}
