package com.xtoee.devices.dcu;

/**
 * ��������
 * 
 * @author zgm
 *
 */
public enum enumDataItem
{
    ReadAlarmLog(0x0019),                                       // ���澯��־
    AdminPhone(0x010D),                                         // ����Ա�绰
    CombinedLamp1(0x0111),                                      // ��ϵƷ���1
    CombinedLamp2(0x0112),                                      // ��ϵƷ���2
    CombinedLamp3(0x0113),                                      // ��ϵƷ���3
    RemoteControl(0x0201),                                      // Զ�̿�����·
    ReadAllRelay(0x0202),                                       // �����м̵���
    ReadSingleRectifier(0x0401),                                // ������������
    ReadAllRectifier(0x040F),                                   // ������������
    WriteControlTask(0x0501),                                   // �̿�����
    ReadSingleControlTask_1(0x0511),                            // �������̿�����
    ReadSingleControlTask_2(0x0512),                            // �������̿�����
    ReadSingleControlTask_3(0x0513),                            // �������̿�����
    ReadSingleControlTask_4(0x0514),                            // �������̿�����
    ReadSingleControlTask_5(0x0515),                            // �������̿�����
    ReadSingleControlTask_6(0x0516),                            // �������̿�����
    ReadSingleControlTask_7(0x0517),                            // �������̿�����
    ReadSingleControlTask_8(0x0518),                            // �������̿�����
    ReadSingleControlTask_9(0x0519),                            // �������̿�����
    ReadSingleControlTask_10(0x051A),                           // �������̿�����
    ReadSingleControlTask_11(0x051B),                           // �������̿�����
    ReadSingleControlTask_12(0x051C),                           // �������̿�����
    ReadAllControlTask(0x051f),                                 // �����еĳ̿�����
    AddControlTasks(0x052F),                                    // ���������̿�����
    ModifyControlTasks(0x053F),                                 // �����޸ĳ̿�����
    DeleteControlTasks(0x054F),                                 // ����ɾ���̿�����
    Timing(0x8030),                                             // Уʱ
    AddArchives(0x895F),                                        // ���������豸����
    DeleteArchives(0x896F),                                     // ����ɾ���豸����
    ReadAllArchives(0x897F);                                    // �����豸������Ϣ
    private int m_iValue;                                       // ö�ٱ�����ֵ

    
    /**
     * ���캯��
     * 
     * @param iValue
     *            ö�ٱ�����ֵ
     */
    private enumDataItem(int iValue)
    {
        m_iValue = iValue;
    }

    /**
     * ���ö��ֵ
     * 
     * @return ö�ٱ�����ֵ
     */
    public int getValue()
    {
        return m_iValue;
    }
}
