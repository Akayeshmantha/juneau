// ***************************************************************************************************************************
// * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements.  See the NOTICE file *
// * distributed with this work for additional information regarding copyright ownership.  The ASF licenses this file        *
// * to you under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance            *
// * with the License.  You may obtain a copy of the License at                                                              *
// *                                                                                                                         *
// *  http://www.apache.org/licenses/LICENSE-2.0                                                                             *
// *                                                                                                                         *
// * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an  *
// * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the        *
// * specific language governing permissions and limitations under the License.                                              *
// ***************************************************************************************************************************
package org.apache.juneau.remoteable;

import static org.apache.juneau.internal.ClassUtils.*;
import static org.apache.juneau.internal.ReflectionUtils.*;
import static org.apache.juneau.remoteable.ReturnValue.*;

import java.lang.reflect.*;

import org.apache.juneau.http.annotation.*;
import org.apache.juneau.httppart.*;

/**
 * Represents the metadata about the returned object of a method on a remote proxy interface.
 *
 * <h5 class='section'>See Also:</h5>
 * <ul class='doctree'>
 * 	<li class='link'><a class='doclink' href='../../../../overview-summary.html#juneau-rest-client.3rdPartyProxies'>Overview &gt; juneau-rest-client &gt; Interface Proxies Against 3rd-party REST Interfaces</a>
 * </ul>
 */
public final class RemoteMethodReturn {

	private final HttpPartParser parser;
	private final HttpPartSchema schema;
	private final Type returnType;
	private final ReturnValue returnValue;

	RemoteMethodReturn(Method m, ReturnValue returnValue) {
		this.returnType = m.getGenericReturnType();
		if (hasAnnotation(Body.class, m)) {
			this.schema = HttpPartSchema.create(Body.class, m);
			this.parser = newInstance(HttpPartParser.class, schema.getParser());
		} else {
			this.schema = null;
			this.parser = null;
		}
		if (returnValue == null) {
			if (m.getReturnType() == void.class)
				returnValue = NONE;
			else
				returnValue = BODY;
		}
		this.returnValue = returnValue;
	}

	/**
	 * Returns the parser to use for parsing this part.
	 *
	 * @return The parser to use for parsing this part, or <jk>null</jk> if not specified.
	 */
	public HttpPartParser getParser() {
		return parser;
	}

	/**
	 * Returns schema information about the HTTP part.
	 *
	 * @return Schema information about the HTTP part, or <jk>null</jk> if not found.
	 */
	public HttpPartSchema getSchema() {
		return schema;
	}

	/**
	 * Returns the class type of the method return.
	 *
	 * @return The class type of the method return.
	 */
	public Type getReturnType() {
		return returnType;
	}

	/**
	 * Specifies whether the return value is the body of the request or the HTTP status.
	 *
	 * @return The type of value returned.
	 */
	public ReturnValue getReturnValue() {
		return returnValue;
	}
}