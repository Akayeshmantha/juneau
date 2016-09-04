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
package org.apache.juneau;

import java.io.*;

/**
 * Interface that identifies that an object can be serialized directly to a writer.
 * <p>
 * 	Instances must identify the media type of the content by implementing the
 * 	{@link #getMediaType()} method.
 * </p>
 *
 * @author James Bognar (james.bognar@salesforce.com)
 */
public interface Writable {

	/**
	 * Serialize this object to the specified writer.
	 *
	 * @param w The writer to write to.
	 * @throws IOException
	 */
	void writeTo(Writer w) throws IOException;

	/**
	 * Returns the serialized media type for this resource (e.g. <js>"text/html"</js>)
	 *
	 * @return The media type, or <jk>null</jk> if the media type is not known.
	 */
	String getMediaType();
}
