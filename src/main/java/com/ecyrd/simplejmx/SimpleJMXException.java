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

/**
 *  A Runtime exception which may be thrown in case there's something
 *  wrong with the MBean.  Typically, this method wraps any JMX exceptions
 *  that we might encounter.
 *  <p>
 *  The reason why this is a RuntimeException is that libraries shouldn't
 *  expose complicated Exception management to applications.  This will result
 *  in cleaner code.
 */
public class SimpleJMXException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    public SimpleJMXException(String string, Exception e)
    {
        super(string, e);
    }

}
