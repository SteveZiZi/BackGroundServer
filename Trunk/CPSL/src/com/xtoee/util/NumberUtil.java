package com.xtoee.util;

public class NumberUtil
{
	/**
	 * int整数转换为4字节的byte数组
	 * 
	 * @param i 整数
	 * @return byte数组
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
	 * byte数组转换为int整数
	 * 
	 * @param bytes byte数组
	 * @param off 开始位置
	 * @return int整数
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
     * 将整数转成4字节的BCD数组
     * @param iValue   整数值
     * @return BCD数组
     */
    public static byte[] intToBcdByte4(int iValue)
    {
        // 获得字符串，并确保其长度为8字节
        String strValue = String.valueOf(iValue);
        for (int i = strValue.length(); i < 8; i++)
        {
            strValue = "0" + strValue;
        }
        strValue = strValue.substring(0, 8);
        
        // 返回BCD数组
        return StringToBcdArray(strValue);
    }

	/**
	 * long整数转换为8字节的byte数组
	 * 
	 * @param lo long整数
	 * @return byte数组
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
	 * 8字节的byte数组转换成Long整数
	 * @param bytes	byte数组
	 * @return	long整数
	 */
	public static long Byte8ToLong(byte[] bytes)
	{
		return Byte8ToLong(bytes, 0);
	}
	
	/**
	 * 8字节的byte数组转换成Long整数
	 * @param bytes	byte数组
	 * @param iOff	开始位置
	 * @return	long整数
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
	 * byte数组转换为无符号short整数
	 * 
	 * @param bytes byte数组
	 * @return short整数
	 */
	public static int byte2ToUnsignedShort(byte[] bytes)
	{
		return byte2ToUnsignedShort(bytes, 0);
	}
	
	/**
	 * byte数组转换为无符号short整数
	 * 
	 * @param bytes byte数组
	 * @param off 开始位置
	 * @return short整数
	 */
	public static int byte2ToUnsignedShort(byte[] bytes, int off)
	{
		int low = bytes[off];
		int high = bytes[off + 1];
		
		return (high << 8 & 0xFF00) | (low & 0xFF);
	}
	
	/**
	 * short整数转换为BCD字节数组
	 * @param sValue   short整数
	 * @return byte数组
	 */
	public static byte[] ShortToBcdByte2(int sValue)
	{
	    // 获得字符串，并确保其长度为4字节
        String strValue = String.valueOf(sValue);
        for (int i = strValue.length(); i < 4; i++)
        {
            strValue = "0" + strValue;
        }
        strValue = strValue.substring(0, 4);
        
        // 返回BCD数组
        return StringToBcdArray(strValue);
	}
	
	/**
	 * short整数转换为2字节的byte数组
	 * 
	 * @param s short整数
	 * @return byte数组
	 */
	public static byte[] ShortToByte2(int s)
	{
		byte[] targets = new byte[2];
		
		
		targets[0] = (byte) (s & 0xFF);
		targets[1] = (byte) (s >> 8 & 0xFF);

		return targets;
	}
	
	/**
	 * BCD字节数组转换为short整数
	 * @param bytes	bytes数组
	 * @return	short整数
	 */
	public static int BcdByte2ToShort(byte[] bytes)
	{
		return BcdByte2ToShort(bytes, 0);
	}
	
	/**
	 * BCD字节数组转换为short整数
	 * @param bytes	bytes数组
	 * @param iOff	开始位置
	 * @return	short整数
	 */
	public static int BcdByte2ToShort(byte[] bytes, int iOff)
	{
		StringBuffer	sb 		= new StringBuffer();
		
		
		// 高字节
		sb.append(Integer.toHexString((bytes[iOff] & 0x000000FF) | 0xFFFFFF00).substring(6));
        // 低字节
		sb.append(Integer.toHexString((bytes[iOff + 1] & 0x000000FF) | 0xFFFFFF00).substring(6));
		
		// 合成整数
		return Integer.parseInt(sb.toString());
	}

	/**
	 * 将字符串转成BCD数组
	 * @param strValue 字符串
	 * @return BCD数组
	 */
    public static byte[] StringToBcdArray(String strValue)
    {
        byte[]          bytes = null;
        
        
        // 检查输入参数
        if ((null == strValue) || (strValue.length() == 0))
        {
            return null;
        }
        
        // 如果字符串长度不为偶数，那么在它前面附加一个0
        if (strValue.length() % 2 != 0)
        {
            strValue = strValue + "0";
        }
        
        // 循环解析字符串
        bytes = new byte[strValue.length() / 2];
        for (int i = 0; i < bytes.length; i++)
        {
            String strSegment = strValue.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte)Integer.parseInt(strSegment, 16);
        }
        
        return bytes;
    }
	
	/**
	 * 将BCD码数组转成字符串
	 * @param bytes    BCD码数组
	 * @param iOffset  待解析的起始位置
	 * @param iCount   待解析的字节数
	 * @return 字符串
	 */
	public static String BcdArrayToString(byte[] bytes, int iOffset, int iCount)
    {
	    StringBuffer   sb      = new StringBuffer();
	    
	    
	    // 检查输入参数
	    if ((null == bytes) || (iOffset < 0) || (iOffset + iCount > bytes.length))
        {
            return null;
        }
	    
	    // 循环解析每一个BCD字节
	    for (int i = 0; i < iCount; i++)
        {
	        sb.append(Integer.toHexString((bytes[iOffset++] & 0x000000FF) | 0xFFFFFF00).substring(6));
        }
	    
	    return sb.toString();
    }
	
	/**
	 * 将一个BCD字节转换成普通的字节
	 * @param ucValue  BCD字节
	 * @return 普通字节
	 */
	public static byte BcdByteToNormalByte(byte ucValue)
    {
        return Byte.parseByte((Integer.toHexString((ucValue & 0x000000FF) | 0xFFFFFF00).substring(6)));
    }
}
