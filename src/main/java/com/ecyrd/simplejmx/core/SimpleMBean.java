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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import javax.management.*;

import com.ecyrd.simplejmx.MBean;
import com.ecyrd.simplejmx.ManagedAttribute;
import com.ecyrd.simplejmx.ManagedOperation;
import com.ecyrd.simplejmx.SimpleJMXException;

public class SimpleMBean implements DynamicMBean
{
    private Object m_object;
    private MBean  m_mbean;
    private HashMap<String,SimpleAttribute> m_attributes = new HashMap<String,SimpleAttribute>();
    private HashMap<String,SimpleMethod>    m_operations    = new HashMap<String,SimpleMethod>();
    private MBeanInfo m_beanInfo;
    
    public SimpleMBean( Object mbean ) throws NotCompliantMBeanException
    {
        m_object = mbean;
        m_mbean  = mbean.getClass().getAnnotation(MBean.class);
        
        if( m_mbean == null ) throw new NotCompliantMBeanException("Must annotate object with @MBean");
        
        for( Method m : mbean.getClass().getMethods() )
        {
            ManagedAttribute a = m.getAnnotation( ManagedAttribute.class );
            
            if( a != null )
            {
                createNewAttribute( m, a );
            }
            
            ManagedOperation mm = m.getAnnotation( ManagedOperation.class );
            
            if( mm != null ) createNewMethod( m, mm );
        }
    }
    
    private void createNewAttribute( Method m, ManagedAttribute a ) throws NotCompliantMBeanException
    {
        SimpleAttribute sa = new SimpleAttribute(m, a);
        
        m_attributes.put( sa.getName(), sa );
    }
    
    private void createNewMethod( Method m, ManagedOperation mm ) throws NotCompliantMBeanException
    {
        SimpleMethod sm = new SimpleMethod( m, mm );
        m_operations.put( sm.getName(), sm );
    }
    
    public Object getAttribute(String name) throws AttributeNotFoundException, MBeanException, ReflectionException
    {
        SimpleAttribute sa = m_attributes.get(name);
        
        if( sa == null ) throw new AttributeNotFoundException("Attribute "+name+" not found from object "+m_object.getClass());
        
        try
        {
            Attribute a = sa.getValue();
            
            return a.getValue();
        }
        catch( Exception e )
        {
            throw new MBeanException(e);
        }
    }

    public AttributeList getAttributes(String[] attributes)
    {
        AttributeList list = new AttributeList(attributes.length);
        
        for( String attr : attributes )
        {
            try
            {
                SimpleAttribute sa = m_attributes.get(attr);
            
                if( sa != null )
                    list.add( sa.getValue() );
            }
            catch( Exception e )
            {
                e.printStackTrace(); // TODO
            }
        }
        
        return list;
    }

    public MBeanInfo getMBeanInfo()
    {
        if( m_beanInfo == null )
        {
            try
            {
                buildMBeanInfo();
            }
            catch( IntrospectionException e )
            {
                e.printStackTrace(); // TODO:
            }
        }
        return m_beanInfo;
    }

    // FIXME: Currently ignores signature
    public Object invoke(String actionName, Object[] params, String[] signature) throws MBeanException, ReflectionException
    {
        SimpleMethod sm = m_operations.get(actionName);
        
        if( sm == null ) throw new ReflectionException(null,"No such method");
        
        try
        {
            return sm.invoke(params);
        }
        catch( Exception e )
        {
            throw new MBeanException( e );
        }
    }

    public void setAttribute(Attribute attribute)
                                            throws AttributeNotFoundException,
                                                InvalidAttributeValueException,
                                                MBeanException,
                                                ReflectionException
    {
        SimpleAttribute sa = m_attributes.get( attribute.getName() );
        
        if( sa == null ) throw new AttributeNotFoundException();
        
        Method setter = sa.getSetter();
        
        if( setter == null ) throw new AttributeNotFoundException("This attribute does not have a setter");
        
        try
        {
            setter.invoke(m_object, attribute.getValue());
        }
        catch( Exception e )
        {
            throw new MBeanException(e);
        }
    }

    public AttributeList setAttributes(AttributeList attributes)
    {
        AttributeList success = new AttributeList();
        for( Attribute a : attributes.asList() )
        {
            try
            {
                setAttribute(a);
                success.add(a);
            }
            catch( Exception e )
            {
                e.printStackTrace();
            }
        }
        return success;
    }

    private void buildMBeanInfo() throws IntrospectionException
    {
        MBeanAttributeInfo[]    attributes    = new MBeanAttributeInfo[m_attributes.size()];
        MBeanOperationInfo[]    operations    = new MBeanOperationInfo[m_operations.size()];
        MBeanConstructorInfo[]  constructors  = null;
        MBeanNotificationInfo[] notifications = null;

        //
        //  Create attributes
        //
        int idx = 0;
        
        for( SimpleAttribute sa : m_attributes.values() )
        {
            attributes[idx++] = new MBeanAttributeInfo( sa.getName(), sa.getDescription(), sa.getGetter(), sa.getSetter() );
        }

        idx = 0;
        for( SimpleMethod sm : m_operations.values() )
        {
            operations[idx++] = new MBeanOperationInfo( sm.getDescription(), sm.getMethod() );
        }
        
        //      
        //  Create the actual BeanInfo instance.
        //

        m_beanInfo = new MBeanInfo( getClass().getName(),
                                    m_mbean.description(),
                                    attributes,
                                    constructors,
                                    operations,
                                    notifications );
    }
    
    private class SimpleMethod
    {
        private Method m_method;
        private ManagedOperation m_managed;
        
        public SimpleMethod( Method m, ManagedOperation mm ) throws NotCompliantMBeanException
        {
            m_method = m;
            m_managed = mm;
        }
        
        public Method getMethod()
        {
            return m_method;
        }

        public String getName()
        {
            if( m_managed.name().length() > 0 ) return m_managed.name();
            
            return m_method.getName();
        }
        
        public String getDescription()
        {
            return m_managed.description();
        }
        
        public Object invoke(Object... args) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
        {
            return m_method.invoke(m_object, args);
        }
    }
    
    private class SimpleAttribute
    {
        private Method  m_getter;
        private Method  m_setter;
        private ManagedAttribute m_attribute;
        private String  m_name;
        
        public SimpleAttribute(Method m, ManagedAttribute a) throws NotCompliantMBeanException
        {
            m_attribute = a;
            setSetterAndGetter(m);
        }

        public String getName()
        {
            return m_name;
        }

        public String getDescription()
        {
            return m_attribute.description();
        }
        
        
        public Method getGetter()
        {
            return m_getter;
        }
        
        public Method getSetter()
        {
            return m_setter;
        }
        
        public Attribute getValue()
        {
            Object value;
            try
            {
                value = m_getter.invoke(m_object);
                
                return new Attribute( m_name, value );
            }
            catch (Exception e)
            {
                throw new SimpleJMXException("Unable to invoke method: ",e);
            }
        }
        
        private void setSetterAndGetter(Method m)
        {
            String methodName = m.getName();
            
            if( methodName.startsWith("set") )
            {
                m_setter = m;
                m_name = methodName.substring(3);
                try
                {
                    m_getter = m_object.getClass().getMethod("get"+m_name);
                }
                catch( NoSuchMethodException e )
                {
                    try
                    {
                        m_getter = m_object.getClass().getMethod("is"+m_name);
                    }
                    catch( NoSuchMethodException ex ) {}
                }
            }
            else if( methodName.startsWith("get") )
            {
                m_getter = m;
                m_name = methodName.substring(3);
                Class<?> returnedType = m.getReturnType();

                try
                {
                    m_setter = m_object.getClass().getMethod("set"+m_name, returnedType);
                }
                catch( NoSuchMethodException e ) 
                {
                }
            }
            else if( methodName.startsWith("is") )
            {
                m_getter = m;
                Class<?> returnedType = m.getReturnType();
                m_name = methodName.substring(2);

                try
                {
                    m_setter = m_object.getClass().getMethod("set"+m_name, returnedType);
                }
                catch( NoSuchMethodException e ) 
                {
                }

            }
        }        
    }

    public static ObjectName getObjectName( Object o ) throws NotCompliantMBeanException
    {
        MBean mb = o.getClass().getAnnotation( MBean.class );
        if( mb != null )
        {
            try
            {
                return new ObjectName( mb.name() );
            }
            catch( Exception e ) {}
        }
        
        throw new NotCompliantMBeanException("Object must be annotated with @MBean and have a proper name attribute");
    }
    
    public ObjectName getObjectName() throws MalformedObjectNameException, NullPointerException
    {
        if( m_mbean.name() == null ) return new ObjectName( m_object.getClass().getName() );
        return new ObjectName(m_mbean.name());
    }
}
