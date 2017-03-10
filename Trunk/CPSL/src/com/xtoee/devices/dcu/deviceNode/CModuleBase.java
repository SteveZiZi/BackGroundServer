package com.xtoee.devices.dcu.deviceNode;

import java.util.ArrayList;
import java.util.List;

import org.omg.CORBA.IntHolder;

import com.xtoee.util.NumberUtil;

/**
 * �����豸�ڵ�
 * @author zgm
 *
 */
public class CModuleBase
{
    /**
     * ��������
     */
    public static final int         FT_RESERVE      = 0;        // ����
    public static final int         FT_CONTROL      = 1;        // ��������
    public static final int         FT_DISPLAY      = 2;        // �����ʾ����
    
    /**
     * �豸������
     */
    public static final int         MT_DCU          = 0;        // DCU��ģ��
    public static final int         MT_Aircon       = 1;        // �յ�ģ��
    public static final int         MT_8RoadRelay   = 2;        // 8·�̵���ģ��
    public static final int         MT_4RoadLight   = 3;        // 4·�����
    public static final int         MT_2RoadRelay   = 4;        // 2·�̵���ģ��
    public static final int         MT_3RoadLed     = 5;        // 3·LED�����
    public static final int         MT_DoorPlate    = 6;        // ������ʾģ��
    public static final int         MT_RFID         = 7;        // RFID�忨ȡ��ģ��
    public static final int         MT_20Keys       = 8;        // 20����ģ��
    public static final int         MT_Rectifier    = 9;        // ������
    public static final int         MT_LightSensor  = 10;       // �����
    
    /**
     * ͨѶ����
     */
    public static final int         CP_1200         = 1;        // 1200
    public static final int         CP_2400         = 2;        // 2400
    public static final int         CP_4800         = 3;        // 4800
    public static final int         CP_9600         = 4;        // 9600
    public static final int         CP_19200        = 5;        // 19200
    
    private int                     m_iModuleId;                // �豸�ڵ���
    private String                  m_strModuleAddress;         // �豸�ڵ��ַ
    private int                     m_iClosedCircuitId;         // ��·��
    private int                     m_iFunctionType;            // ��������
    private int                     m_iModuleType;              // �豸������
    private int                     m_iCommParam;               // ͨѶ����
    private List<CSubRoad>          m_lstRelateDevices;         // �����豸
    
    
    /**
     * ���캯��
     */
    public CModuleBase()
    {
        super();
        m_lstRelateDevices = new ArrayList<CSubRoad>();
    }

    /**
     * ���캯��
     * @param moduleId          �豸�ڵ���
     * @param moduleAddress     �豸�ڵ��ַ
     * @param closedCircuitId   ��·��
     * @param functionType      ��������
     * @param moduleType        �豸������
     * @param commParam         ͨѶ����
     */
    public CModuleBase(int moduleId, String moduleAddress,
            int closedCircuitId, int functionType, int moduleType, int commParam)
    {
        super();
        m_iModuleId = moduleId;
        m_strModuleAddress = moduleAddress;
        m_iClosedCircuitId = closedCircuitId;
        m_iFunctionType = functionType;
        m_iModuleType = moduleType;
        m_iCommParam = commParam;
        m_lstRelateDevices = new ArrayList<CSubRoad>();
    }

    /**
     * ����豸�ڵ���
     * @return  �豸�ڵ���
     */
    public int getModuleId()
    {
        return m_iModuleId;
    }
    
    /**
     * �����豸�ڵ���
     * @param moduleId  �豸�ڵ���
     */
    public void setModuleId(int moduleId)
    {
        m_iModuleId = moduleId;
    }
    
    /**
     * ����豸�ڵ��ַ
     * @return  �豸�ڵ��ַ
     */
    public String getModuleAddress()
    {
        return m_strModuleAddress;
    }
    
    /**
     * �����豸�ڵ��ַ
     * @param moduleAddress �豸�ڵ��ַ
     */
    public void setModuleAddress(String moduleAddress)
    {
        m_strModuleAddress = moduleAddress;
    }
    
    /**
     * ��û�·��
     * @return  ��·��
     */
    public int getClosedCircuitId()
    {
        return m_iClosedCircuitId;
    }
    
    /**
     * ���û�·��
     * @param closedCircuitId   ��·��
     */
    public void setClosedCircuitId(int closedCircuitId)
    {
        m_iClosedCircuitId = closedCircuitId;
    }
    
    /**
     * ��ù�������
     * @return  ��������
     */
    public int getFunctionType()
    {
        return m_iFunctionType;
    }
    
    /**
     * ���ù�������
     * @param functionType  ��������
     */
    public void setFunctionType(int functionType)
    {
        m_iFunctionType = functionType;
    }
    
    /**
     * ��ù����������Ӧ�������ַ���
     * @param iFuncType ����������
     * @return  �����������Ӧ�������ַ���
     */
    public static String getFunctionTypeDesc(int iFuncType)
    {
        String          strRet = "δ֪";
        
        
        switch (iFuncType)
        {
        case FT_RESERVE:
            strRet = "����";
            break;
            
        case FT_CONTROL:
            strRet = "��������";
            break;
            
        case FT_DISPLAY:
            strRet = "�����ʾ����";
            break;

        default:
            break;
        }
        
        return strRet;
    }
    
    /**
     * ����豸������
     * @return  �豸������
     */
    public int getModuleType()
    {
        return m_iModuleType;
    }
    
    /**
     * �����豸������
     * @param moduleType    �豸������
     */
    public void setModuleType(int moduleType)
    {
        m_iModuleType = moduleType;
    }
    
    /**
     * ����豸�����ֶ�Ӧ�������ַ���
     * @param iModuleType   �豸������
     * @return  �豸�����ֶ�Ӧ�������ַ���
     */
    public static String getModuleTypeDesc(int iModuleType)
    {
        String          strRet = "δ֪";
        
        
        switch (iModuleType)
        {
        case MT_DCU:
            strRet = "DCU��ģ��";
            break;
            
        case MT_Aircon:
            strRet = "�յ�ģ��";
            break;
            
        case MT_8RoadRelay:
            strRet = "8·�̵���ģ��";
            break;
            
        case MT_4RoadLight:
            strRet = "4·�����";
            break;
            
        case MT_2RoadRelay:
            strRet = "2·�̵���ģ��";
            break;
            
        case MT_3RoadLed:
            strRet = "3·LED�����";
            break;
            
        case MT_DoorPlate:
            strRet = "������ʾģ��";
            break;
            
        case MT_RFID:
            strRet = "RFID�忨ȡ��ģ��";
            break;
            
        case MT_20Keys:
            strRet = "20����ģ��";
            break;
            
        case MT_Rectifier:
            strRet = "������";
            break;
            
        case MT_LightSensor:
            strRet = "�����";
            break;

        default:
            break;
        }
        
        return strRet;
    }
    
    /**
     * ���ͨѶ����
     * @return  ͨѶ����
     */
    public int getCommParam()
    {
        return m_iCommParam;
    }
    
    /**
     * ����ͨѶ����
     * @param commParam ͨѶ����
     */
    public void setCommParam(int commParam)
    {
        m_iCommParam = commParam;
    }
    
    /**
     * ���ͨѶ������Ӧ�������ַ���
     * @param iCommParam    ͨѶ����
     * @return  ͨѶ������Ӧ�������ַ���
     */
    public static String getCommParamDesc(int iCommParam)
    {
        String          strRet = "δ֪";
        
        
        switch (iCommParam)
        {
        case CP_1200:
            strRet = "1200";
            break;
            
        case CP_2400:
            strRet = "2400";
            break;
            
        case CP_4800:
            strRet = "4800";
            break;
            
        case CP_9600:
            strRet = "9600";
            break;
            
        case CP_19200:
            strRet = "19200";
            break;

        default:
            break;
        }
        
        return strRet;
    }
    
    /**
     * ��ù����豸
     * @return  �����豸
     */
    public List<CSubRoad> getRelateDevices()
    {
        return m_lstRelateDevices;
    }
    
    /**
     * ���ù����豸
     * @param relateDevices �����豸
     */
    public void setRelateDevices(List<CSubRoad> relateDevices)
    {
        m_lstRelateDevices = relateDevices;
    }
    
    /**
     * ����
     * @param ucBuffer      ��ű������Ļ�����
     * @param iOffset       ��ű���������ʼ����
     * @param iEncodeBytes  �������ĳ���
     * @return  �ɹ�����true
     */
    public boolean Encode(byte[] ucBuffer, int iOffset, IntHolder iEncodeBytes)
    {
        int             iOldOffset = iOffset;
        
        
        // �豸�ڵ���
        ucBuffer[iOffset++] = (byte)m_iModuleId;
        
        // ����
        ucBuffer[iOffset++] = 0x00;
        
        // �豸�ڵ��ַ
        byte[] maBytes = NumberUtil.StringToBcdArray(m_strModuleAddress);
        for (int i = maBytes.length - 1; i >= 0; i--)
        {
            ucBuffer[iOffset++] = maBytes[i];
        }
        
        // ��·��
        ucBuffer[iOffset++] = (byte)m_iClosedCircuitId;
        
        // ��������
        ucBuffer[iOffset++] = (byte)m_iFunctionType;
        
        // �豸������
        ucBuffer[iOffset++] = (byte)m_iModuleType;
        
        // ͨѶ����
        ucBuffer[iOffset++] = (byte)m_iCommParam;
        
        // �����豸��
        ucBuffer[iOffset++] = (byte)m_lstRelateDevices.size();
        
        // �����豸
        for (int i = 0; i < m_lstRelateDevices.size(); i++)
        {
            CSubRoad subRoad = m_lstRelateDevices.get(i);
            IntHolder iSubRoadEncodeBytes = new IntHolder(0);
            
            // ������·
            if (!subRoad.Encode(ucBuffer, iOffset, iSubRoadEncodeBytes))
            {
                return false;
            }

            // �����ѱ�����ֽ���
            iOffset += iSubRoadEncodeBytes.value;
        }
        
        // ���ر�����ֽ���
        if (null != iEncodeBytes)
        {
            iEncodeBytes.value = iOffset - iOldOffset;
        }
        
        return true;
    }
    
    /**
     * �����豸�ڵ㱨��
     * @param ucBuffer      ����豸�ڵ㱨�ĵ�����
     * @param iOffset       �豸�ڵ㱨�ĵ���ʼ����
     * @param iParseBytes   �������ֽ���
     * @return  �����ɹ�����true
     */
    public boolean parseFrame(byte[] ucBuffer, int iOffset, IntHolder iParseBytes)
    {
        int             iOldOffset = 0;
        
        
        // �����������ʼλ��
        iOldOffset = iOffset;
        
        // �豸�ڵ���
        m_iModuleId = (ucBuffer[iOffset++] & 0xff);
        
        // ����
        @SuppressWarnings("unused")
        int iValue = ucBuffer[iOffset++];
        
        // �豸�ڵ��ַ
        m_strModuleAddress = "";
        for (int i = 0; i < 6; i++)
        {
            m_strModuleAddress = NumberUtil.BcdArrayToString(ucBuffer, iOffset++, 1) + m_strModuleAddress;
        }
        
        // ��·��
        m_iClosedCircuitId = ucBuffer[iOffset++];
        
        // ��������
        m_iFunctionType = ucBuffer[iOffset++];
        
        // �豸������
        m_iModuleType = ucBuffer[iOffset++];
        
        // ͨѶ����
        m_iCommParam = ucBuffer[iOffset++];
        
        // �����豸��
        int iRelateDeviceNum = ucBuffer[iOffset++];
        
        // �����豸
        m_lstRelateDevices.clear();
        for (int i = 0; i < iRelateDeviceNum; i++)
        {
            CSubRoad subRoad = new CSubRoad();
            IntHolder iSubRoadParseBytes = new IntHolder(0);
            
            // ������·����
            if (!subRoad.parseFrame(ucBuffer, iOffset, iSubRoadParseBytes))
            {
                return false;
            }
            
            // ������·����
            m_lstRelateDevices.add(subRoad);
            
            // �����ѽ������ֽ���
            iOffset += iSubRoadParseBytes.value;
        }
        
        // ���ؽ������ֽ���
        if (null != iParseBytes)
        {
            iParseBytes.value = iOffset - iOldOffset;
        }
        
        return true;
    }
}
