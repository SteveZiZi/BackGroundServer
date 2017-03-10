package com.xtoee.devices.dcu.alarmLog;

/**
 * �澯��־������
 * @author zgm
 *
 */
public class CAlarmLogFactory
{
    public static final int         AT_ArchiveParamCleared          = 0x0200;   // ������������
    public static final int         AT_ArchiveParamModified         = 0x0201;   // �����������
    public static final int         AT_SettingRectifierVoltage      = 0x0202;   // ��������ѹ����
    public static final int         AT_ManualSettingCircuitVoltage  = 0x0203;   // �ֶ����ƣ���·��ѹ���ã�
    public static final int         AT_SwitchRectifier              = 0x0208;   // ���������ؿ���
    public static final int         AT_ManualSettingCircuitSwitch   = 0x020A;   // �ֶ����ƣ���·ͨ�ϣ�
    public static final int         AT_SwitchSubLoop                = 0x020D;   // �ӻ�·ͨ�Ͽ���
    public static final int         AT_RectifierOffline             = 0x020F;   // CPR����
    public static final int         AT_TaskSettingCircuitVoltage    = 0x0215;   // ������ƣ���·��ѹ���ã�
    public static final int         AT_TaskSettingCircuitSwitch     = 0x0216;   // ������ƣ���·���ؿ��ƣ�
    public static final int         AT_LightControlCircuitVoltage   = 0x0301;   // ������񣨻�·��ѹ���ã�
    public static final int         AT_LightControlFinish           = 0x0302;   // ����������
    public static final int         AT_LightControlCircuitSwitch    = 0x0303;   // ������񣨻�·���أ�
    public static final int         AT_ConstantLightStart           = 0x0310;   // ��������ʼ
    public static final int         AT_ConstantLightFinish          = 0x0311;   // �����������
    
    private static CAlarmLogFactory s_alarmLogFactory;
    
    
    /**
     * ˽�л�Ĭ�Ϲ��캯��
     */
    private CAlarmLogFactory()
    {
    }
    
    /**
     * ������������
     * @return  ������������
     */
    public static CAlarmLogFactory getInstance()
    {
        if (null == s_alarmLogFactory)
        {
            s_alarmLogFactory = new CAlarmLogFactory();
        }
        
        return s_alarmLogFactory;
    }
    
    /**
     * ����ָ�����͵ĸ澯��־����
     * @param iAlarmType �澯����
     * @return �澯��־����
     */
    public CAlarmLogBase CreateAlarmLog(int iAlarmType)
    {
        CAlarmLogBase   alarmLog = null;
        
        
        switch (iAlarmType)
        {
        // ������������
        case AT_ArchiveParamCleared:
            alarmLog = new CAlarmLog_ArchiveParamCleared();
            break;
            
        // �����������
        case AT_ArchiveParamModified:
            alarmLog = new CAlarmLog_ArchiveParamModified();
            break;
            
        // ��������ѹ����
        case AT_SettingRectifierVoltage:
            alarmLog = new CAlarmLog_SettingRectifierVoltage();
            break;
            
        // �ֶ����ƣ���·��ѹ���ã�
        case AT_ManualSettingCircuitVoltage:
            alarmLog = new CAlarmLog_ManualSettingCircuitVoltage();
            break;
            
        // ���������ؿ���
        case AT_SwitchRectifier:
            alarmLog = new CAlarmLog_SwitchRectifier();
            break;
            
        // �ֶ����ƣ���·ͨ�ϣ�
        case AT_ManualSettingCircuitSwitch:
            alarmLog = new CAlarmLog_ManualSettingCircuitSwitch();
            break;
            
        // �ӻ�·ͨ�Ͽ���
        case AT_SwitchSubLoop:
            alarmLog = new CAlarmLog_SwitchSubLoop();
            break;
            
        // ����������
        case AT_RectifierOffline:
            alarmLog = new CAlarmLog_RectifierOffline();
            break;
            
        // ������ƣ���·��ѹ���ã�
        case AT_TaskSettingCircuitVoltage:
            alarmLog = new CAlarmLog_TaskSettingCircuitVoltage();
            break;
            
        // ������ƣ���·���ؿ��ƣ�
        case AT_TaskSettingCircuitSwitch:
            alarmLog = new CAlarmLog_TaskSettingCircuitSwitch();
            break;
 
        // ������񣨻�·��ѹ���ã�
        case AT_LightControlCircuitVoltage:
            alarmLog = new CAlarmLog_LightControlCircuitVoltage();
            break;
            
        // ����������
        case AT_LightControlFinish:
            alarmLog = new CAlarmLog_LightControlFinish();
            break;
            
        // ������񣨻�·���أ�
        case AT_LightControlCircuitSwitch:
            alarmLog = new CAlarmLog_LightControlCircuitSwitch();
            break;
        
        // ��������ʼ
        case AT_ConstantLightStart:
            alarmLog = new CAlarmLog_ConstantLightStart();
            break;
            
        // �����������
        case AT_ConstantLightFinish:
            alarmLog = new CAlarmLog_ConstantLightFinish();
            break;

        default:
            break;
        }
        
        return alarmLog;
    }
}
