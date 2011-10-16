package com.ecyrd.simplejmx.core;

import java.lang.management.ManagementFactory;

import javax.management.*;

import com.ecyrd.simplejmx.MBean;

public class MBeanBuilder
{
    private static boolean isMBean(Object o)
    {
        return o.getClass().isAnnotationPresent(MBean.class);
    }
    
    public static SimpleMBean createMBean(Object mbean) throws NotCompliantMBeanException
    {
        if( !isMBean(mbean) ) return null;
        
        return new SimpleMBean( mbean );
    }
    
    public static void registerMBean( Object mbean ) throws MBeanException, NotCompliantMBeanException, InstanceAlreadyExistsException, MalformedObjectNameException, NullPointerException
    {
        MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
        
        SimpleMBean smb = createMBean(mbean);
        ObjectName  on  = smb.getObjectName();
        
        if( !mbeanServer.isRegistered(on) )
        {
            mbeanServer.registerMBean( smb, on );
        }
    }
    
    public static void unregisterMBean( Object mbean )
    {
        MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();

        try
        {
            ObjectName on = SimpleMBean.getObjectName( mbean );
            
            mbeanServer.unregisterMBean(on);
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
    }
}
