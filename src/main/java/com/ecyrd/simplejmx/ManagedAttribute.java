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

/**
 *  Define a JMX managed attribute.  Use this declaration on either
 *  a setter or a getter on any class which has the {@link MBean} annotation.  
 *  Example:
 *  
 *  <pre>
 *    @ManagedAttribute(description="How many times images have been fetched.")
 *    public long getFetchedImageCount()
 *    {
 *    ...
 *    }
 *  </pre>
 *  
 *  Later on, if you refactor the method name but want to keep the JMX attribute name
 *  similar, use
 *  
 *  <pre>
 *    @ManagedAttribute(description="How many times images have been fetched.",
 *                      name="FetchedImageCount")
 *    public long getNewNameForThisGetter()
 *    {
 *    ...
 *    }
 *  </pre>
 *  
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ManagedAttribute
{
    /**
     *  The name of the attribute under which it should appear in the
     *  JMX console.  This value is optional, in which case the name
     *  will be determined from the getter/setter name by simply removing
     *  the "get" or "set" from the image name.  For example, a getter
     *  named "getImageCount" would appear as JMX attribute "ImageCount".
     *  
     *  @return
     */
    String name() default "";
    
    /**
     *  A description which will also appear in the JMX console.
     *  This value is optional.
     *  
     *  @return A plain text string.
     */
    String description() default "";
}
