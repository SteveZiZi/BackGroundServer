package com.xtoee.util;

public class NumberUtil
{
	/**
	 * int����ת��Ϊ4�ֽڵ�byte����
	 * 
	 * @param i ����
	 * @return byte����
	 */
	public static byte[] intToByte4(int i)
	{
		byte[] targets = new byte[4];
		
		
		targets[0] = (byte) (i & 0xFF);
		targets[1] = (byte) (i >> 8 & 0xFF);
		targets[2] = (byte) (i >> 16 & 0xFF);
		targets[3] = (byte) (i >> 24 & 0xFF);
		
		return targets;
	}
	
	/**
	 * byte����ת��Ϊint����
	 * 
	 * @param bytes byte����
	 * @param off ��ʼλ��
	 * @return int����
	 */
	public static int byte4ToInt(byte[] bytes, int off)
	{
		int b0 = bytes[off] & 0xFF;
		int b1 = bytes[off + 1] & 0xFF;
		int b2 = bytes[off + 2] & 0xFF;
		int b3 = bytes[off + 3] & 0xFF;
		
		return (b3 << 24) | (b2 << 16) | (b1 << 8) | b0;
	}
	
	/**
     * ������ת��4�ֽڵ�BCD����
     * @param iValue   ����ֵ
     * @return BCD����
     */
    public static byte[] intToBcdByte4(int iValue)
    {
        // ����ַ�������ȷ���䳤��Ϊ8�ֽ�
        String strValue = String.valueOf(iValue);
        for (int i = strValue.length(); i < 8; i++)
        {
            strValue = "0" + strValue;
        }
        strValue = strValue.substring(0, 8);
        
        // ����BCD����
        return StringToBcdArray(strValue);
    }

	/**
	 * long����ת��Ϊ8�ֽڵ�byte����
	 * 
	 * @param lo long����
	 * @return byte����
	 */
	public static byte[] longToByte8(long lo)
	{
		byte[] targets = new byte[8];
		
		
		for (int i = 0; i < 8; i++)
		{
			int offset = i * 8;
			targets[i] = (byte) ((lo >>> offset) & 0xFF);
		}
		
		return targets;
	}
	
	/**
	 * 8�ֽڵ�byte����ת����Long����
	 * @param bytes	byte����
	 * @return	long����
	 */
	public static long Byte8ToLong(byte[] bytes)
	{
		return Byte8ToLong(bytes, 0);
	}
	
	/**
	 * 8�ֽڵ�byte����ת����Long����
	 * @param bytes	byte����
	 * @param iOff	��ʼλ��
	 * @return	long����
	 */
	public static long Byte8ToLong(byte[] bytes, int iOff)
	{
		int b0 = bytes[iOff] & 0xFF;
		int b1 = bytes[iOff + 1] & 0xFF;
		int b2 = bytes[iOff + 2] & 0xFF;
		int b3 = bytes[iOff + 3] & 0xFF;
		int b4 = bytes[iOff + 4] & 0xFF;
		int b5 = bytes[iOff + 5] & 0xFF;
		int b6 = bytes[iOff + 6] & 0xFF;
		int b7 = bytes[iOff + 7] & 0xFF;
		
		return (b7 << 56) | (b6 << 48) | (b5 << 40) | (b4 << 32) | (b3 << 24) | (b2 << 16) | (b1 << 8) | b0;
	}

	/**
	 * byte����ת��Ϊ�޷���short����
	 * 
	 * @param bytes byte����
	 * @return short����
	 */
	public static int byte2ToUnsignedShort(byte[] bytes)
	{
		return byte2ToUnsignedShort(bytes, 0);
	}
	
	/**
	 * byte����ת��Ϊ�޷���short����
	 * 
	 * @param bytes byte����
	 * @param off ��ʼλ��
	 * @return short����
	 */
	public static int byte2ToUnsignedShort(byte[] bytes, int off)
	{
		int low = bytes[off];
		int high = bytes[off + 1];
		
		return (high << 8 & 0xFF00) | (low & 0xFF);
	}
	
	/**
	 * short����ת��ΪBCD�ֽ�����
	 * @param sValue   short����
	 * @return byte����
	 */
	public static byte[] ShortToBcdByte2(int sValue)
	{
	    // ����ַ�������ȷ���䳤��Ϊ4�ֽ�
        String strValue = String.valueOf(sValue);
        for (int i = strValue.length(); i < 4; i++)
        {
            strValue = "0" + strValue;
        }
        strValue = strValue.substring(0, 4);
        
        // ����BCD����
        return StringToBcdArray(strValue);
	}
	
	/**
	 * short����ת��Ϊ2�ֽڵ�byte����
	 * 
	 * @param s short����
	 * @return byte����
	 */
	public static byte[] ShortToByte2(int s)
	{
		byte[] targets = new byte[2];
		
		
		targets[0] = (byte) (s & 0xFF);
		targets[1] = (byte) (s >> 8 & 0xFF);

		return targets;
	}
	
	/**
	 * BCD�ֽ�����ת��Ϊshort����
	 * @param bytes	bytes����
	 * @return	short����
	 */
	public static int BcdByte2ToShort(byte[] bytes)
	{
		return BcdByte2ToShort(bytes, 0);
	}
	
	/**
	 * BCD�ֽ�����ת��Ϊshort����
	 * @param bytes	bytes����
	 * @param iOff	��ʼλ��
	 * @return	short����
	 */
	public static int BcdByte2ToShort(byte[] bytes, int iOff)
	{
		StringBuffer	sb 		= new StringBuffer();
		
		
		// ���ֽ�
		sb.append(Integer.toHexString((bytes[iOff] & 0x000000FF) | 0xFFFFFF00).substring(6));
        // ���ֽ�
		sb.append(Integer.toHexString((bytes[iOff + 1] & 0x000000FF) | 0xFFFFFF00).substring(6));
		
		// �ϳ�����
		return Integer.parseInt(sb.toString());
	}

	/**
	 * ���ַ���ת��BCD����
	 * @param strValue �ַ���
	 * @return BCD����
	 */
    public static byte[] StringToBcdArray(String strValue)
    {
        byte[]          bytes = null;
        
        
        // ����������
        if ((null == strValue) || (strValue.length() == 0))
        {
            return null;
        }
        
        // ����ַ������Ȳ�Ϊż������ô����ǰ�渽��һ��0
        if (strValue.length() % 2 != 0)
        {
            strValue = strValue + "0";
        }
        
        // ѭ�������ַ���
        bytes = new byte[strValue.length() / 2];
        for (int i = 0; i < bytes.length; i++)
        {
            String strSegment = strValue.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte)Integer.parseInt(strSegment, 16);
        }
        
        return bytes;
    }
	
	/**
	 * ��BCD������ת���ַ���
	 * @param bytes    BCD������
	 * @param iOffset  ����������ʼλ��
	 * @param iCount   ���������ֽ���
	 * @return �ַ���
	 */
	public static String BcdArrayToString(byte[] bytes, int iOffset, int iCount)
    {
	    StringBuffer   sb      = new StringBuffer();
	    
	    
	    // ����������
	    if ((null == bytes) || (iOffset < 0) || (iOffset + iCount > bytes.length))
        {
            return null;
        }
	    
	    // ѭ������ÿһ��BCD�ֽ�
	    for (int i = 0; i < iCount; i++)
        {
	        sb.append(Integer.toHexString((bytes[iOffset++] & 0x000000FF) | 0xFFFFFF00).substring(6));
        }
	    
	    return sb.toString();
    }
	
	/**
	 * ��һ��BCD�ֽ�ת������ͨ���ֽ�
	 * @param ucValue  BCD�ֽ�
	 * @return ��ͨ�ֽ�
	 */
	public static byte BcdByteToNormalByte(byte ucValue)
    {
        return Byte.parseByte((Integer.toHexString((ucValue & 0x000000FF) | 0xFFFFFF00).substring(6)));
    }
}
