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
package org.apache.juneau.internal;

import java.util.*;

import org.apache.juneau.*;

/**
 * Represents a wrapped {@link Collection} where entries in the list can be removed or reordered without
 * 	affecting the underlying list.
 *
 * @author James Bognar (james.bognar@salesforce.com)
 * @param <T> The class type of the wrapped bean.
 */
public class DelegateList<T extends Collection<?>> extends ObjectList implements Delegate<T> {
	private static final long serialVersionUID = 1L;

	private transient ClassMeta<T> classMeta;

	/**
	 * Constructor.
	 *
	 * @param classMeta The metadata object that created this delegate list.
	 */
	public DelegateList(ClassMeta<T> classMeta) {
		this.classMeta = classMeta;
	}

	@Override /* Delegate */
	public ClassMeta<T> getClassMeta() {
		return classMeta;
	}
}
