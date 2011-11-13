/*
   Copyright 2011 Janne Jalkanen

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
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
