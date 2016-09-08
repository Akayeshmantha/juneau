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
package org.apache.juneau.jena;

import org.apache.juneau.*;
import org.apache.juneau.jena.annotation.*;

/**
 * Metadata on beans specific to the RDF serializers and parsers pulled from the {@link Rdf @Rdf} annotation on the class.
 *
 * @author James Bognar (james.bognar@salesforce.com)
 */
public class RdfBeanMeta extends BeanMetaExtended {

	// RDF related fields
	private final BeanPropertyMeta beanUriProperty;    // Bean property that identifies the URI of the bean.

	/**
	 * Constructor.
	 *
	 * @param beanMeta The metadata on the bean that this metadata applies to.
	 */
	public RdfBeanMeta(BeanMeta<?> beanMeta) {
		super(beanMeta);

		BeanPropertyMeta t_beanUriProperty = null;
		for (BeanPropertyMeta p : beanMeta.getPropertyMetas()) {
			RdfBeanPropertyMeta bpm = p.getExtendedMeta(RdfBeanPropertyMeta.class);
			if (bpm.isBeanUri())
				t_beanUriProperty = p;
		}

		this.beanUriProperty = t_beanUriProperty;
	}

	/**
	 * Returns <jk>true</jk> if one of the properties on this bean is annotated with {@link Rdf#beanUri()} as <jk>true</jk>
	 *
	 * @return <jk>true</jk> if there is a URI property associated with this bean.
	 */
	public boolean hasBeanUri() {
		return beanUriProperty != null;
	}

	/**
	 * Returns the bean property marked as the URI for the bean (annotated with {@link Rdf#beanUri()} as <jk>true</jk>).
	 *
	 * @return The URI property, or <jk>null</jk> if no URI property exists on this bean.
	 */
	public BeanPropertyMeta getBeanUriProperty() {
		return beanUriProperty;
	}
}
