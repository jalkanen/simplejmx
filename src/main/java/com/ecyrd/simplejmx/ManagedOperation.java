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
 *  Marks a method to be exposed as a JMX operation for any POJO object
 *  which has the {@link MBean} annotation.
 *  
 *  @see {@link MBean}, {@link ManagedAttribute}.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ManagedOperation
{
    /**
     *  Sets the name of the method to expose.  You typically use this
     *  to make sure that refactorings don't change the JMX interface.  For example,
     *  
     *  <pre>
     *     @ManagedOperation
     *     public void doStuff()
     *     { ... }
     *  </pre>
     *  
     *  If you refactor the doStuff() you might want to do this
     *  
     *  <pre>
     *     @ManagedOperation(name="doStuff")
     *     public void refactoredName()
     *     { ... }
     *  </pre>
     *  
     *  @return A text string for the method name to expose.
     */
    String name() default "";
    
    /**
     *  A textual description for this ManagedOperation which should then be visible
     *  in the management interface.  For example
     *  
     *  <pre>
     *    @ManagedOperation(description="Calculates the statistics.  Be warned, this may take a long time.")
     *    public void calcStats()
     *    { ... }
     *  </pre>
     * @return
     */
    String description() default "";
}
