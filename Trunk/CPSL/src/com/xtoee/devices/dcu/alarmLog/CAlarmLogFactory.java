package com.xtoee.devices.dcu.alarmLog;

/**
 * 告警日志工厂类
 * @author zgm
 *
 */
public class CAlarmLogFactory
{
    public static final int         AT_ArchiveParamCleared          = 0x0200;   // 档案参数清零
    public static final int         AT_ArchiveParamModified         = 0x0201;   // 档案参数变更
    public static final int         AT_SettingRectifierVoltage      = 0x0202;   // 整流器电压设置
    public static final int         AT_ManualSettingCircuitVoltage  = 0x0203;   // 手动控制（回路电压设置）
    public static final int         AT_SwitchRectifier              = 0x0208;   // 整流器开关控制
    public static final int         AT_ManualSettingCircuitSwitch   = 0x020A;   // 手动控制（回路通断）
    public static final int         AT_SwitchSubLoop                = 0x020D;   // 子回路通断控制
    public static final int         AT_RectifierOffline             = 0x020F;   // CPR掉线
    public static final int         AT_TaskSettingCircuitVoltage    = 0x0215;   // 任务控制（回路电压设置）
    public static final int         AT_TaskSettingCircuitSwitch     = 0x0216;   // 任务控制（回路开关控制）
    public static final int         AT_LightControlCircuitVoltage   = 0x0301;   // 光控任务（回路电压设置）
    public static final int         AT_LightControlFinish           = 0x0302;   // 光控任务结束
    public static final int         AT_LightControlCircuitSwitch    = 0x0303;   // 光控任务（回路开关）
    public static final int         AT_ConstantLightStart           = 0x0310;   // 恒照任务开始
    public static final int         AT_ConstantLightFinish          = 0x0311;   // 恒照任务结束
    
    private static CAlarmLogFactory s_alarmLogFactory;
    
    
    /**
     * 私有化默认构造函数
     */
    private CAlarmLogFactory()
    {
    }
    
    /**
     * 创建工厂单例
     * @return  工厂单例对象
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
     * 创建指定类型的告警日志对象
     * @param iAlarmType 告警类型
     * @return 告警日志对象
     */
    public CAlarmLogBase CreateAlarmLog(int iAlarmType)
    {
        CAlarmLogBase   alarmLog = null;
        
        
        switch (iAlarmType)
        {
        // 档案参数清零
        case AT_ArchiveParamCleared:
            alarmLog = new CAlarmLog_ArchiveParamCleared();
            break;
            
        // 档案参数变更
        case AT_ArchiveParamModified:
            alarmLog = new CAlarmLog_ArchiveParamModified();
            break;
            
        // 整流器电压设置
        case AT_SettingRectifierVoltage:
            alarmLog = new CAlarmLog_SettingRectifierVoltage();
            break;
            
        // 手动控制（回路电压设置）
        case AT_ManualSettingCircuitVoltage:
            alarmLog = new CAlarmLog_ManualSettingCircuitVoltage();
            break;
            
        // 整流器开关控制
        case AT_SwitchRectifier:
            alarmLog = new CAlarmLog_SwitchRectifier();
            break;
            
        // 手动控制（回路通断）
        case AT_ManualSettingCircuitSwitch:
            alarmLog = new CAlarmLog_ManualSettingCircuitSwitch();
            break;
            
        // 子回路通断控制
        case AT_SwitchSubLoop:
            alarmLog = new CAlarmLog_SwitchSubLoop();
            break;
            
        // 整流器掉线
        case AT_RectifierOffline:
            alarmLog = new CAlarmLog_RectifierOffline();
            break;
            
        // 任务控制（回路电压设置）
        case AT_TaskSettingCircuitVoltage:
            alarmLog = new CAlarmLog_TaskSettingCircuitVoltage();
            break;
            
        // 任务控制（回路开关控制）
        case AT_TaskSettingCircuitSwitch:
            alarmLog = new CAlarmLog_TaskSettingCircuitSwitch();
            break;
 
        // 光控任务（回路电压设置）
        case AT_LightControlCircuitVoltage:
            alarmLog = new CAlarmLog_LightControlCircuitVoltage();
            break;
            
        // 光控任务结束
        case AT_LightControlFinish:
            alarmLog = new CAlarmLog_LightControlFinish();
            break;
            
        // 光控任务（回路开关）
        case AT_LightControlCircuitSwitch:
            alarmLog = new CAlarmLog_LightControlCircuitSwitch();
            break;
        
        // 恒照任务开始
        case AT_ConstantLightStart:
            alarmLog = new CAlarmLog_ConstantLightStart();
            break;
            
        // 恒照任务结束
        case AT_ConstantLightFinish:
            alarmLog = new CAlarmLog_ConstantLightFinish();
            break;

        default:
            break;
        }
        
        return alarmLog;
    }
}
