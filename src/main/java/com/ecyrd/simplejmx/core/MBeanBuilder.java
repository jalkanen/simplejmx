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
    
    /**
     *  Registers an MBean.  If the MBean with the matching ObjectName already exists, removes it prior to 
     *  registering this one.
     *  
     *  @param mbean Object to register.  It's expected to have an MBean annotation.
     *  @throws MBeanException
     *  @throws NotCompliantMBeanException
     *  @throws InstanceAlreadyExistsException
     *  @throws MalformedObjectNameException
     *  @throws NullPointerException
     */
    public static void registerMBean( Object mbean ) throws MBeanException, NotCompliantMBeanException, InstanceAlreadyExistsException, MalformedObjectNameException, NullPointerException
    {
        MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
        
        SimpleMBean smb = createMBean(mbean);
        ObjectName  on  = smb.getObjectName();
        
        if( mbeanServer.isRegistered(on) )
        {
            try
            {
                mbeanServer.unregisterMBean(on);
            }
            catch( InstanceNotFoundException e ) {};
        }
        
        mbeanServer.registerMBean( smb, on );
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
