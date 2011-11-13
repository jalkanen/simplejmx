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
package com.ecyrd.simplejmx;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.management.ObjectName;

/**
 *  A marker for classes which are to be exposed as JMX MBeans.  Typically,
 *  you would just annotate your classes with @MBean and they would be
 *  exposable by the MBeanBuilder class.
 *  <p>
 *  Example:
 *  <pre>
 *  
 *  @MBean(name="com.ecyrd.simplejmx:name=SampleMBean")
 *  public class Sampler()
 *  {
 *      private long counter = 0;
 *      
 *      public void increment() { counter++; }
 *   
 *      @ManagedAttribute
 *      public long getCounter() { return counter; }
 *  }
 *  
 *  public static void main(String[] argv)
 *  {
 *      Sampler s = new Sampler();
 *      MBeanBuilder.registerMBean(s);
 *      
 *      //  Do your processing here.
 *      
 *      ...
 *  }
 *  
 *  </pre>
 *  
 *  This code would register an instance of the class Sampler as an MBean under
 *  the name "com.ecyrd.simplejmx:name=SampleMBean" and expose a singular attribute
 *  "Counter".
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface MBean
{
    /**
     *  A description for the MBean, which will be visible in the UI.  This
     *  attribute is optional.
     *  
     *  @return A valid textual description of this MBean.
     */
    String description() default "";
    
    /**
     *  The name of this MBean.  When present, this attribute must
     *  conform to the {@link ObjectName} specification.  For example,
     *  you might want to use something like 
     *  <pre>
     *    @MBean(name="com.ecyrd.simplejmx:name=TestMBean")
     *    public class MyTestClass { ... }
     *  </pre> 
     *  
     *  <p>
     *  
     *  If you do not specify a name, then an ObjectName is constructed
     *  by taking the package and the class name, e.g. annotating the class "com.ecyrd.simplejmx.Test"
     *  would result in an ObjectName "com.ecyrd.simplejmx:name=Test".  However, in practice, you will
     *  probably want to construct your own hierarchies using the ObjectName notation.
     *  
     *  @return The object name.
     */
    String name() default "";
}
