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
package org.apache.juneau.encoders;

import java.io.*;

/**
 * Used for enabling decompression on requests and compression on responses, such as support for GZIP compression.
 *
 *
 * <h6 class='topic'>Description</h6>
 * <p>
 * 	Used to wrap input and output streams withing compression/decompression streams.
 * <p>
 * 	Encoders are registered with <code>RestServlets</code> through the <ja>@RestResource.encoders()</ja> annotation.
 *
 *
 * @author James Bognar (james.bognar@salesforce.com)
 */
public abstract class Encoder {

	/**
	 * Converts the specified compressed input stream into an uncompressed stream.
	 *
	 * @param is The compressed stream.
	 * @return The uncompressed stream.
	 * @throws IOException If any errors occur, such as on a stream that's not a valid GZIP input stream.
	 */
	public abstract InputStream getInputStream(InputStream is) throws IOException;

	/**
	 * Converts the specified uncompressed output stream into an uncompressed stream.
	 *
	 * @param os The uncompressed stream.
	 * @return The compressed stream stream.
	 * @throws IOException If any errors occur.
	 */
	public abstract OutputStream getOutputStream(OutputStream os) throws IOException;

	/**
	 * Returns the codings in <code>Content-Encoding</code> and <code>Accept-Encoding</code> headers
	 * 	that this encoder handles (e.g. <js>"gzip"</js>).
	 *
	 * @return The codings that this encoder handles.
	 */
	public abstract String[] getCodings();
}
