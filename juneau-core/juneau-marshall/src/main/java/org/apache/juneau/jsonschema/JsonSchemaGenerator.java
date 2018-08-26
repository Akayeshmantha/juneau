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
package org.apache.juneau.jsonschema;

import java.util.*;

import org.apache.juneau.*;
import org.apache.juneau.annotation.*;
import org.apache.juneau.json.*;

/**
 * Generates JSON-schema metadata about POJOs.
 */
public class JsonSchemaGenerator extends BeanTraverseContext {

	//-------------------------------------------------------------------------------------------------------------------
	// Configurable properties
	//-------------------------------------------------------------------------------------------------------------------

	private static final String PREFIX = "JsonSchemaGenerator.";

	/**
	 * Configuration property:  Add descriptions to types.
	 *
	 * <h5 class='section'>Property:</h5>
	 * <ul>
	 * 	<li><b>Name:</b>  <js>"JsonSchemaSerializer.addDescriptionsTo.s"</js>
	 * 	<li><b>Data type:</b>  <code>String</code>
	 * 	<li><b>Default:</b>  Empty string.
	 * 	<li><b>Session property:</b>  <jk>false</jk>
	 * 	<li><b>Methods:</b>
	 * 		<ul>
	 * 			<li class='jm'>{@link JsonSchemaSerializerBuilder#addDescriptionsTo(String)}
	 * 		</ul>
	 * </ul>
	 *
	 * <h5 class='section'>Description:</h5>
	 * <p>
	 * Identifies which categories of types that descriptions should be automatically added to generated schemas.
	 * <p>
	 * The description is the result of calling {@link ClassMeta#getReadableName()}.
	 * <p>
	 * The format is a comma-delimited list of any of the following values:
	 *
	 * <ul class='doctree'>
	 * 	<li class='jf'>{@link TypeCategory#BEAN BEAN}
	 * 	<li class='jf'>{@link TypeCategory#COLLECTION COLLECTION}
	 * 	<li class='jf'>{@link TypeCategory#ARRAY ARRAY}
	 * 	<li class='jf'>{@link TypeCategory#MAP MAP}
	 * 	<li class='jf'>{@link TypeCategory#STRING STRING}
	 * 	<li class='jf'>{@link TypeCategory#NUMBER NUMBER}
	 * 	<li class='jf'>{@link TypeCategory#BOOLEAN BOOLEAN}
	 * 	<li class='jf'>{@link TypeCategory#ANY ANY}
	 * 	<li class='jf'>{@link TypeCategory#OTHER OTHER}
	 * </ul>
	 */
	public static final String JSONSCHEMA_addDescriptionsTo = PREFIX + "addDescriptionsTo.s";

	/**
	 * Configuration property:  Add examples.
	 *
	 * <h5 class='section'>Property:</h5>
	 * <ul>
	 * 	<li><b>Name:</b>  <js>"JsonSchemaSerializer.addExamplesTo.s"</js>
	 * 	<li><b>Data type:</b>  <code>String</code>
	 * 	<li><b>Default:</b>  Empty string.
	 * 	<li><b>Session property:</b>  <jk>false</jk>
	 * 	<li><b>Methods:</b>
	 * 		<ul>
	 * 			<li class='jm'>{@link JsonSchemaSerializerBuilder#addExamplesTo(String)}
	 * 		</ul>
	 * </ul>
	 *
	 * <h5 class='section'>Description:</h5>
	 * <p>
	 * Identifies which categories of types that examples should be automatically added to generated schemas.
	 * <p>
	 * The examples come from calling {@link ClassMeta#getExample(BeanSession)} which in turn gets examples
	 * from the following:
	 * <ul class='doctree'>
	 * 	<li class='ja'>{@link Example}
	 * 	<li class='jf'>{@link BeanContext#BEAN_examples}
	 * </ul>
	 *
	 * <p>
	 * The format is a comma-delimited list of any of the following values:
	 *
	 * <ul class='doctree'>
	 * 	<li class='jf'>{@link TypeCategory#BEAN BEAN}
	 * 	<li class='jf'>{@link TypeCategory#COLLECTION COLLECTION}
	 * 	<li class='jf'>{@link TypeCategory#ARRAY ARRAY}
	 * 	<li class='jf'>{@link TypeCategory#MAP MAP}
	 * 	<li class='jf'>{@link TypeCategory#STRING STRING}
	 * 	<li class='jf'>{@link TypeCategory#NUMBER NUMBER}
	 * 	<li class='jf'>{@link TypeCategory#BOOLEAN BOOLEAN}
	 * 	<li class='jf'>{@link TypeCategory#ANY ANY}
	 * 	<li class='jf'>{@link TypeCategory#OTHER OTHER}
	 * </ul>
	 */
	public static final String JSONSCHEMA_addExamplesTo = PREFIX + "addExamplesTo.s";

	/**
	 * Configuration property:  Allow nested descriptions.
	 *
	 * <h5 class='section'>Property:</h5>
	 * <ul>
	 * 	<li><b>Name:</b>  <js>"JsonSchemaSerializer.allowNestedDescriptions.b"</js>
	 * 	<li><b>Data type:</b>  <code>Boolean</code>
	 * 	<li><b>Default:</b>  <jk>false</jk>
	 * 	<li><b>Session property:</b>  <jk>false</jk>
	 * 	<li><b>Methods:</b>
	 * 		<ul>
	 * 			<li class='jm'>{@link JsonSchemaSerializerBuilder#allowNestedDescriptions()}
	 * 		</ul>
	 * </ul>
	 *
	 * <h5 class='section'>Description:</h5>
	 * <p>
	 * Identifies whether nested descriptions are allowed in schema definitions.
	 */
	public static final String JSONSCHEMA_allowNestedDescriptions = PREFIX + "allowNestedDescriptions.b";

	/**
	 * Configuration property:  Allow nested examples.
	 *
	 * <h5 class='section'>Property:</h5>
	 * <ul>
	 * 	<li><b>Name:</b>  <js>"JsonSchemaSerializer.allowNestedExamples.b"</js>
	 * 	<li><b>Data type:</b>  <code>Boolean</code>
	 * 	<li><b>Default:</b>  <jk>false</jk>
	 * 	<li><b>Session property:</b>  <jk>false</jk>
	 * 	<li><b>Methods:</b>
	 * 		<ul>
	 * 			<li class='jm'>{@link JsonSchemaSerializerBuilder#allowNestedExamples()}
	 * 		</ul>
	 * </ul>
	 *
	 * <h5 class='section'>Description:</h5>
	 * <p>
	 * Identifies whether nested examples are allowed in schema definitions.
	 */
	public static final String JSONSCHEMA_allowNestedExamples = PREFIX + "allowNestedExamples.b";

	/**
	 * Configuration property:  Bean schema definition mapper.
	 *
	 * <h5 class='section'>Property:</h5>
	 * <ul>
	 * 	<li><b>Name:</b>  <js>"JsonSchemaSerializer.beanDefMapper.o"</js>
	 * 	<li><b>Data type:</b>  {@link BeanDefMapper}
	 * 	<li><b>Default:</b>  {@link BasicBeanDefMapper}
	 * 	<li><b>Session property:</b>  <jk>false</jk>
	 * 	<li><b>Methods:</b>
	 * 		<ul>
	 * 			<li class='jm'>{@link JsonSchemaSerializerBuilder#beanDefMapper(Class)}
	 * 			<li class='jm'>{@link JsonSchemaSerializerBuilder#beanDefMapper(BeanDefMapper)}
	 * 		</ul>
	 * </ul>
	 *
	 * <h5 class='section'>Description:</h5>
	 * <p>
	 * Interface to use for converting Bean classes to definition IDs and URIs.
	 * <p>
	 * Used primarily for defining common definition sections for beans in Swagger JSON.
	 * <p>
	 * This setting is ignored if {@link #JSONSCHEMA_useBeanDefs} is not enabled.
	 */
	public static final String JSONSCHEMA_beanDefMapper = PREFIX + "beanDefMapper.o";

	/**
	 * Configuration property:  Default schemas.
	 *
	 * <h5 class='section'>Property:</h5>
	 * <ul>
	 * 	<li><b>Name:</b>  <js>"JsonSchemaSerializer.defaultSchema.smo"</js>
	 * 	<li><b>Data type:</b>  <code>Map&lt;String,ObjectMap&gt;</code>
	 * 	<li><b>Default:</b>  Empty map.
	 * 	<li><b>Session property:</b>  <jk>false</jk>
	 * 	<li><b>Methods:</b>
	 * 		<ul>
	 * 			<li class='jm'>{@link JsonSchemaSerializerBuilder#defaultSchema(Class,ObjectMap)}
	 * 		</ul>
	 * </ul>
	 *
	 * <h5 class='section'>Description:</h5>
	 * <p>
	 * Allows you to override or provide custom schema information for particular class types.
	 * <p>
	 * Keys are full class names.
	 */
	public static final String JSONSCHEMA_defaultSchemas = PREFIX + "defaultSchemas.smo";

	/**
	 * Configuration property:  Use bean definitions.
	 *
	 * <h5 class='section'>Property:</h5>
	 * <ul>
	 * 	<li><b>Name:</b>  <js>"JsonSchemaSerializer.useBeanDefs.o"</js>
	 * 	<li><b>Data type:</b>  <code>Boolean</code>
	 * 	<li><b>Default:</b>  <jk>false</jk>
	 * 	<li><b>Methods:</b>
	 * 		<ul>
	 * 			<li class='jm'>{@link JsonSchemaSerializerBuilder#useBeanDefs()}
	 * 		</ul>
	 * </ul>
	 *
	 * <h5 class='section'>Description:</h5>
	 * <p>
	 * When enabled, schemas on beans will be serialized as the following:
	 * <p class='bcode w800'>
	 * 	{
	 * 		type: <js>'object'</js>,
	 * 		<js>'$ref'</js>: <js>'#/definitions/TypeId'</js>
	 * 	}
	 * </p>
	 *
	 * <p>
	 * The definitions can then be retrieved from the session using {@link JsonSchemaGeneratorSession#getBeanDefs()}.
	 * <p>
	 * Definitions can also be added programmatically using {@link JsonSchemaGeneratorSession#addBeanDef(String, ObjectMap)}.
	 */
	public static final String JSONSCHEMA_useBeanDefs = PREFIX + "useBeanDefs.b";


	//-------------------------------------------------------------------------------------------------------------------
	// Predefined instances
	//-------------------------------------------------------------------------------------------------------------------

	/** Default serializer, all default settings.*/
	public static final JsonSchemaGenerator DEFAULT = new JsonSchemaGenerator(PropertyStore.DEFAULT);


	//-------------------------------------------------------------------------------------------------------------------
	// Instance
	//-------------------------------------------------------------------------------------------------------------------

	private final boolean useBeanDefs, allowNestedExamples, allowNestedDescriptions;
	private final BeanDefMapper beanDefMapper;
	private final Set<TypeCategory> addExamplesTo, addDescriptionsTo;
	private final Map<String,ObjectMap> defaultSchemas;
	private final JsonSerializer jsonSerializer;

	/**
	 * Constructor.
	 *
	 * @param ps Initialize with the specified config property store.
	 */
	public JsonSchemaGenerator(PropertyStore ps) {
		super(ps);

		useBeanDefs = getBooleanProperty(JSONSCHEMA_useBeanDefs, false);
		allowNestedExamples = getBooleanProperty(JSONSCHEMA_allowNestedExamples, false);
		allowNestedDescriptions = getBooleanProperty(JSONSCHEMA_allowNestedDescriptions, false);
		beanDefMapper = getInstanceProperty(JSONSCHEMA_beanDefMapper, BeanDefMapper.class, BasicBeanDefMapper.class);
		addExamplesTo = TypeCategory.parse(getStringProperty(JSONSCHEMA_addExamplesTo, null));
		addDescriptionsTo = TypeCategory.parse(getStringProperty(JSONSCHEMA_addDescriptionsTo, null));
		defaultSchemas = getMapProperty(JSONSCHEMA_defaultSchemas, ObjectMap.class);
		jsonSerializer = new JsonSerializer(ps);
	}

	@Override /* Context */
	public JsonSchemaGeneratorBuilder builder() {
		return new JsonSchemaGeneratorBuilder(getPropertyStore());
	}

	/**
	 * Instantiates a new clean-slate {@link JsonSerializerBuilder} object.
	 *
	 * <p>
	 * This is equivalent to simply calling <code><jk>new</jk> JsonSerializerBuilder()</code>.
	 *
	 * @return A new {@link JsonSerializerBuilder} object.
	 */
	public static JsonSchemaGeneratorBuilder create() {
		return new JsonSchemaGeneratorBuilder();
	}

	@Override
	public JsonSchemaGeneratorSession createSession(BeanSessionArgs args) {
		return new JsonSchemaGeneratorSession(this, args);
	}

	@Override
	public JsonSchemaGeneratorSession createSession() {
		return new JsonSchemaGeneratorSession(this, null);
	}

	JsonSerializer getJsonSerializer() {
		return jsonSerializer;
	}

	//-----------------------------------------------------------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------------------------------------------------------

	/**
	 * Configuration property:  Use bean definitions.
	 *
	 * @see #JSONSCHEMA_useBeanDefs
	 * @return
	 * 	<jk>true</jk> if schemas on beans will be serialized with <js>'$ref'</js> tags.
	 */
	protected final boolean isUseBeanDefs() {
		return useBeanDefs;
	}

	/**
	 * Configuration property:  Allow nested examples.
	 *
	 * @see #JSONSCHEMA_allowNestedExamples
	 * @return
	 * 	<jk>true</jk> if nested examples are allowed in schema definitions.
	 */
	protected final boolean isAllowNestedExamples() {
		return allowNestedExamples;
	}

	/**
	 * Configuration property:  Allow nested descriptions.
	 *
	 * @see #JSONSCHEMA_allowNestedDescriptions
	 * @return
	 * 	<jk>true</jk> if nested descriptions are allowed in schema definitions.
	 */
	protected final boolean isAllowNestedDescriptions() {
		return allowNestedDescriptions;
	}

	/**
	 * Configuration property:  Bean schema definition mapper.
	 *
	 * @see #JSONSCHEMA_beanDefMapper
	 * @return
	 * 	Interface to use for converting Bean classes to definition IDs and URIs.
	 */
	protected final BeanDefMapper getBeanDefMapper() {
		return beanDefMapper;
	}

	/**
	 * Configuration property:  Add examples.
	 *
	 * @see #JSONSCHEMA_addExamplesTo
	 * @return
	 * 	Set of categories of types that examples should be automatically added to generated schemas.
	 */
	protected final Set<TypeCategory> getAddExamplesTo() {
		return addExamplesTo;
	}

	/**
	 * Configuration property:  Add descriptions to types.
	 *
	 * @see #JSONSCHEMA_addDescriptionsTo
	 * @return
	 * 	Set of categories of types that descriptions should be automatically added to generated schemas.
	 */
	protected final Set<TypeCategory> getAddDescriptionsTo() {
		return addDescriptionsTo;
	}

	/**
	 * Configuration property:  Default schemas.
	 *
	 * @see #JSONSCHEMA_defaultSchemas
	 * @return
	 * 	Custom schema information for particular class types.
	 */
	protected final Map<String,ObjectMap> getDefaultSchemas() {
		return defaultSchemas;
	}
}