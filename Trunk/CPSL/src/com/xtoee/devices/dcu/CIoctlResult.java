package com.xtoee.devices.dcu;

/**
 * ¿ØÖÆÃüÁîÖ´ĞĞ½á¹û
 * @author zgm
 *
 */
public class CIoctlResult
{
    protected enumErrorCode         m_eErrorCode;               // ´íÎóÂë
    
    
    /**
     * »ñµÃ´íÎóÂë
     * @return  ´íÎóÂë
     */
    public enumErrorCode getErrorCode()
    {
        return m_eErrorCode;
    }
    
    /**
     * ÉèÖÃ´íÎóÂë
     * @param eErrorCode    ´íÎóÂë
     */
    public void setErrorCode(enumErrorCode eErrorCode)
    {
        m_eErrorCode = eErrorCode;
    }
}
