package com.xtoee.devices.dcu;

/**
 * 数据项编号
 * 
 * @author zgm
 *
 */
public enum enumDataItem
{
    ReadAlarmLog(0x0019),                                       // 读告警日志
    AdminPhone(0x010D),                                         // 管理员电话
    CombinedLamp1(0x0111),                                      // 组合灯方案1
    CombinedLamp2(0x0112),                                      // 组合灯方案2
    CombinedLamp3(0x0113),                                      // 组合灯方案3
    RemoteControl(0x0201),                                      // 远程控制子路
    ReadAllRelay(0x0202),                                       // 读所有继电器
    ReadSingleRectifier(0x0401),                                // 读单个整流器
    ReadAllRectifier(0x040F),                                   // 读所有整流器
    WriteControlTask(0x0501),                                   // 程控任务
    ReadSingleControlTask_1(0x0511),                            // 读单个程控任务
    ReadSingleControlTask_2(0x0512),                            // 读单个程控任务
    ReadSingleControlTask_3(0x0513),                            // 读单个程控任务
    ReadSingleControlTask_4(0x0514),                            // 读单个程控任务
    ReadSingleControlTask_5(0x0515),                            // 读单个程控任务
    ReadSingleControlTask_6(0x0516),                            // 读单个程控任务
    ReadSingleControlTask_7(0x0517),                            // 读单个程控任务
    ReadSingleControlTask_8(0x0518),                            // 读单个程控任务
    ReadSingleControlTask_9(0x0519),                            // 读单个程控任务
    ReadSingleControlTask_10(0x051A),                           // 读单个程控任务
    ReadSingleControlTask_11(0x051B),                           // 读单个程控任务
    ReadSingleControlTask_12(0x051C),                           // 读单个程控任务
    ReadAllControlTask(0x051f),                                 // 读所有的程控任务
    AddControlTasks(0x052F),                                    // 批量新增程控任务
    ModifyControlTasks(0x053F),                                 // 批量修改程控任务
    DeleteControlTasks(0x054F),                                 // 批量删除程控任务
    Timing(0x8030),                                             // 校时
    AddArchives(0x895F),                                        // 批量增加设备档案
    DeleteArchives(0x896F),                                     // 批量删除设备档案
    ReadAllArchives(0x897F);                                    // 抄收设备档案信息
    private int m_iValue;                                       // 枚举变量的值

    
    /**
     * 构造函数
     * 
     * @param iValue
     *            枚举变量的值
     */
    private enumDataItem(int iValue)
    {
        m_iValue = iValue;
    }

    /**
     * 获得枚举值
     * 
     * @return 枚举变量的值
     */
    public int getValue()
    {
        return m_iValue;
    }
}
