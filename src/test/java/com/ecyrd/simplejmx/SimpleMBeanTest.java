package com.ecyrd.simplejmx;

import static org.junit.Assert.*;

import javax.management.*;

import org.junit.Test;

import com.ecyrd.simplejmx.core.SimpleMBean;

public class SimpleMBeanTest
{
    @Test
    public void testSimpleClass() throws NotCompliantMBeanException, AttributeNotFoundException, MBeanException, ReflectionException
    {
        TestMBean tmb = new TestMBean();
        SimpleMBean smb = new SimpleMBean(tmb);
        
        assertEquals(3, smb.getMBeanInfo().getAttributes().length);
        
        assertEquals( -1L, smb.getAttribute("LValue") );
        
        assertEquals( 1, smb.getAttribute("IValue") );
        
        assertEquals( false, smb.getAttribute("Boolean") );
        
        try
        {
            smb.getAttribute("XXX");
            fail("Didn't get exception");
        }
        catch( AttributeNotFoundException e ) {}
    }
    
    @Test
    public void testSetter() throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException, NotCompliantMBeanException
    {
        TestMBean tmb = new TestMBean();
        SimpleMBean smb = new SimpleMBean(tmb);
        
        Attribute a = new Attribute( "LValue", new Long(5) );
        
        smb.setAttribute(a);
        
        assertEquals( 5L, tmb.lvalue );
    }
    
    @Test
    public void testOperation() throws MBeanException, ReflectionException, NotCompliantMBeanException
    {
        TestMBean tmb = new TestMBean();
        SimpleMBean smb = new SimpleMBean(tmb);

        Object value = smb.invoke("call", new Object[] { "blob" }, new String[] { "String" } );
        
        assertTrue( (Boolean)value );
        
        assertEquals( "blob", tmb.callableValue );
    }
    
    @MBean(description="This is a test bean", 
           name="com.ecyrd.simplejmx:name=TestMBean")
    public static class TestMBean
    {
        public int  ivalue = 1;
        public long lvalue = -1L;
        public String svalue = "Foo";
        public boolean bvalue = false;
        
        public String callableValue = null;
        
        @ManagedAttribute
        public int getIValue()
        {
            return ivalue;
        }
        
        public long getLValue()
        {
            return lvalue;
        }
        
        @ManagedAttribute
        public void setLValue(long value)
        {
            lvalue = value;
        }
        
        @ManagedAttribute
        public boolean isBoolean()
        {
            return bvalue;
        }
        
        public void setBoolean(boolean value)
        {
            bvalue = value;
        }
    
        @ManagedOperation
        public boolean call( String tst )
        {
            callableValue = tst;
            return true;
        }
    }
}
