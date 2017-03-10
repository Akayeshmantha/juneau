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

import java.util.*;

/**
 * Builder class for creating instances of {@link EncoderGroup}.
 */
public class EncoderGroupBuilder {

	private final List<Encoder> encoders;

	/**
	 * Create an empty encoder group builder.
	 */
	public EncoderGroupBuilder() {
		this.encoders = new ArrayList<Encoder>();
	}

	/**
	 * Clone an existing encoder group builder.
	 * @param copyFrom The encoder group that we're copying settings and encoders from.
	 */
	public EncoderGroupBuilder(EncoderGroup copyFrom) {
		this.encoders = new ArrayList<Encoder>(Arrays.asList(copyFrom.encoders));
	}

	/**
	 * Registers the specified encoders with this group.
	 *
	 * @param e The encoders to append to this group.
	 * @return This object (for method chaining).
	 */
	public EncoderGroupBuilder append(Class<?>...e) {
		for (Class<?> ee : e) {
			try {
				encoders.add((Encoder)((Class<?>)ee).newInstance());
			} catch (Exception x) {
				throw new RuntimeException(x);
			}
		}
		return this;
	}

	/**
	 * Registers the specified encoders with this group.
	 *
	 * @param e The encoders to append to this group.
	 * @return This object (for method chaining).
	 */
	public EncoderGroupBuilder append(Encoder...e) {
		encoders.addAll(Arrays.asList(e));
		return this;
	}

	/**
	 * Registers the specified encoders with this group.
	 *
	 * @param e The encoders to append to this group.
	 * @return This object (for method chaining).
	 */
	public EncoderGroupBuilder append(Collection<Encoder> e) {
		encoders.addAll(e);
		return this;
	}

	/**
	 * Registers the encoders in the specified group with this group.
	 *
	 * @param eg The encoders to append to this group.
	 * @return This object (for method chaining).
	 */
	public EncoderGroupBuilder append(EncoderGroup eg) {
		append(eg.encoders);
		return this;
	}

	/**
	 * Creates a new {@link EncoderGroup} object using a snapshot of the settings defined in this builder.
	 * <p>
	 * This method can be called multiple times to produce multiple encoder groups.
	 *
	 * @return A new {@link EncoderGroup} object.
	 */
	@SuppressWarnings("unchecked")
	public EncoderGroup build() {
		try {
			List<Encoder> l = new ArrayList<Encoder>();
			for (Object e : encoders) {
				l.add(e instanceof Class ? ((Class<? extends Encoder>)e).getConstructor().newInstance() : (Encoder)e);
			}
			Collections.reverse(l);
			return new EncoderGroup(l.toArray(new Encoder[l.size()]));
		} catch (Exception x) {
			throw new RuntimeException("Could not instantiate encoder.", x);
		}
	}
}
