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
package org.apache.juneau.rest.response;

import org.apache.juneau.http.annotation.*;

/**
 * Represents an <code>HTTP 204 No Content</code> response.
 *
 * <p>
 * The server successfully processed the request and is not returning any content.
 */
@Response(code=204, example="'No Content'")
public class NoContent extends HttpResponse {

	/** Reusable instance. */
	public static final NoContent INSTANCE = new NoContent();

	/**
	 * Constructor using HTTP-standard message.
	 */
	public NoContent() {
		this("No Content");
	}

	/**
	 * Constructor using custom message.
	 * @param message Message to send as the response.
	 */
	public NoContent(String message) {
		super(message);
	}
}