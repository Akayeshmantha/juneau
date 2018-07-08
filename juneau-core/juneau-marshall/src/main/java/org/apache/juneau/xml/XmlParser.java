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
package org.apache.juneau.xml;

import javax.xml.stream.*;
import javax.xml.stream.util.*;

import org.apache.juneau.*;
import org.apache.juneau.parser.*;

/**
 * Parses text generated by the {@link XmlSerializer} class back into a POJO model.
 *
 * <h5 class='topic'>Media types</h5>
 *
 * Handles <code>Content-Type</code> types:  <code><b>text/xml</b></code>
 *
 * <h5 class='topic'>Description</h5>
 *
 * See the {@link XmlSerializer} class for a description of Juneau-generated XML.
 */
public class XmlParser extends ReaderParser {

	//-------------------------------------------------------------------------------------------------------------------
	// Configurable properties
	//-------------------------------------------------------------------------------------------------------------------

	private static final String PREFIX = "XmlParser.";

	/**
	 * Configuration property:  XML event allocator.
	 *
	 * <h5 class='section'>Property:</h5>
	 * <ul>
	 * 	<li><b>Name:</b>  <js>"XmlParser.eventAllocator.c"</js>
	 * 	<li><b>Data type:</b>  <code>Class&lt;? <jk>extends</jk> {@link XMLEventAllocator}&gt;</code>
	 * 	<li><b>Default:</b>  <jk>null</jk>
	 * 	<li><b>Session-overridable:</b>  <jk>true</jk>
	 * 	<li><b>Methods:</b>
	 * 		<ul>
	 * 			<li class='jm'>{@link XmlParserBuilder#eventAllocator(XMLEventAllocator)}
	 * 		</ul>
	 * </ul>
	 *
	 * <h5 class='section'>Description:</h5>
	 * <p>
	 * Associates an {@link XMLEventAllocator} with this parser.
	 */
	public static final String XML_eventAllocator = PREFIX + "eventAllocator.c";

	/**
	 * Configuration property:  Preserve root element during generalized parsing.
	 *
	 * <h5 class='section'>Property:</h5>
	 * <ul>
	 * 	<li><b>Name:</b>  <js>"XmlParser.preserveRootElement.b"</js>
	 * 	<li><b>Data type:</b>  <code>Boolean</code>
	 * 	<li><b>Default:</b>  <jk>false</jk>
	 * 	<li><b>Session-overridable:</b>  <jk>false</jk>
	 * 	<li><b>Methods:</b>
	 * 		<ul>
	 * 			<li class='jm'>{@link XmlParserBuilder#preserveRootElement(boolean)}
	 * 			<li class='jm'>{@link XmlParserBuilder#preserveRootElement()}
	 * 		</ul>
	 * </ul>
	 *
	 * <h5 class='section'>Description:</h5>
	 * <p>
	 * If <jk>true</jk>, when parsing into a generic {@link ObjectMap}, the map will contain a single entry whose key
	 * is the root element name.
	 *
	 * <h5 class='section'>Example:</h5>
	 * <p class='bcode'>
	 * 	<jc>// Parser with preserve-root-element.</jc>
	 * 	ReaderParser p1 = XmlParser
	 * 		.<jsm>create</jsm>()
	 * 		.preserveRootElement(<jk>true</jk>)
	 * 		.build();
	 *
	 * 	<jc>// Parser without preserve-root-element (the default behavior).</jc>
	 * 	ReaderParser p2 = XmlParser
	 * 		.<jsm>create</jsm>()
	 * 		.preserveRootElement(<jk>false</jk>)
	 * 		.build();
	 *
	 * 	String xml = <js>"&lt;root&gt;&lt;a&gt;foobar&lt;/a&gt;&lt;/root&gt;"</js>;
	 *
	 * 	<jc>// Produces:  "{ root: { a:'foobar' }}"</jc>
	 * 	ObjectMap m1 = p1.parse(xml, ObjectMap.<jk>class</jk>);
	 *
	 * 	<jc>// Produces:  "{ a:'foobar' }"</jc>
	 * 	ObjectMap m2 = p2.parse(xml, ObjectMap.<jk>class)</jk>;
	 * </p>
	 */
	public static final String XML_preserveRootElement = PREFIX + "preserveRootElement.b";

	/**
	 * Configuration property:  XML reporter.
	 *
	 * <h5 class='section'>Property:</h5>
	 * <ul>
	 * 	<li><b>Name:</b>  <js>"XmlParser.reporter.c"</js>
	 * 	<li><b>Data type:</b>  <code>Class&lt;? <jk>extends</jk> {@link XMLReporter}&gt;</code>
	 * 	<li><b>Default:</b>  <jk>null</jk>
	 * 	<li><b>Session-overridable:</b>  <jk>false</jk>
	 * 	<li><b>Methods:</b>
	 * 		<ul>
	 * 			<li class='jm'>{@link XmlParserBuilder#reporter(XMLReporter)}
	 * 		</ul>
	 * </ul>
	 *
	 * <h5 class='section'>Description:</h5>
	 * <p>
	 * Associates an {@link XMLReporter} with this parser.
	 *
	 * <h5 class='section'>Notes:</h5>
	 * <ul class='spaced-list'>
	 * 	<li>
	 * 		Reporters are not copied to new parsers during a clone.
	 * </ul>
	 */
	public static final String XML_reporter = PREFIX + "reporter.c";

	/**
	 * Configuration property:  XML resolver.
	 *
	 * <h5 class='section'>Property:</h5>
	 * <ul>
	 * 	<li><b>Name:</b>  <js>"XmlParser.resolver.c"</js>
	 * 	<li><b>Data type:</b>  <code>Class&lt;? <jk>extends</jk> {@link XMLResolver}&gt;</code>
	 * 	<li><b>Default:</b>  <jk>null</jk>
	 * 	<li><b>Session-overridable:</b>  <jk>false</jk>
	 * 	<li><b>Methods:</b>
	 * 		<ul>
	 * 			<li class='jm'>{@link XmlParserBuilder#resolver(XMLResolver)}
	 * 		</ul>
	 * </ul>
	 *
	 * <h5 class='section'>Description:</h5>
	 * <p>
	 * Associates an {@link XMLResolver} with this parser.
	 */
	public static final String XML_resolver = PREFIX + "resolver.c";

	/**
	 * Configuration property:  Enable validation.
	 *
	 * <h5 class='section'>Property:</h5>
	 * <ul>
	 * 	<li><b>Name:</b>  <js>"XmlParser.validating.b"</js>
	 * 	<li><b>Data type:</b>  <code>Boolean</code>
	 * 	<li><b>Default:</b>  <jk>false</jk>
	 * 	<li><b>Session-overridable:</b>  <jk>false</jk>
	 * 	<li><b>Methods:</b>
	 * 		<ul>
	 * 			<li class='jm'>{@link XmlParserBuilder#validating(boolean)}
	 * 			<li class='jm'>{@link XmlParserBuilder#validating()}
	 * 		</ul>
	 * </ul>
	 *
	 * <h5 class='section'>Description:</h5>
	 * <p>
	 * If <jk>true</jk>, XML document will be validated.
	 *
	 * <p>
	 * See {@link XMLInputFactory#IS_VALIDATING} for more info.
	 */
	public static final String XML_validating = PREFIX + "validating.b";


	//-------------------------------------------------------------------------------------------------------------------
	// Predefined instances
	//-------------------------------------------------------------------------------------------------------------------

	/** Default parser, all default settings.*/
	public static final XmlParser DEFAULT = new XmlParser(PropertyStore.DEFAULT);


	//-------------------------------------------------------------------------------------------------------------------
	// Instance
	//-------------------------------------------------------------------------------------------------------------------

	private final boolean
		validating,
		preserveRootElement;
	private final XMLReporter reporter;
	private final XMLResolver resolver;
	private final XMLEventAllocator eventAllocator;

	/**
	 * Constructor.
	 *
	 * @param ps
	 * 	The property store containing all the settings for this object.
	 */
	public XmlParser(PropertyStore ps) {
		this(ps, "text/xml", "application/xml");
	}

	/**
	 * Constructor.
	 *
	 * @param ps
	 * 	The property store containing all the settings for this object.
	 * @param consumes
	 * 	The list of media types that this parser consumes (e.g. <js>"application/json"</js>, <js>"*&#8203;/json"</js>).
	 */
	public XmlParser(PropertyStore ps, String...consumes) {
		super(ps, consumes);
		validating = getBooleanProperty(XML_validating, false);
		preserveRootElement = getBooleanProperty(XML_preserveRootElement, false);
		reporter = getInstanceProperty(XML_reporter, XMLReporter.class, null);
		resolver = getInstanceProperty(XML_resolver, XMLResolver.class, null);
		eventAllocator = getInstanceProperty(XML_eventAllocator, XMLEventAllocator.class, null);
	}

	@Override /* Context */
	public XmlParserBuilder builder() {
		return new XmlParserBuilder(getPropertyStore());
	}

	/**
	 * Instantiates a new clean-slate {@link XmlParserBuilder} object.
	 *
	 * <p>
	 * This is equivalent to simply calling <code><jk>new</jk> XmlParserBuilder()</code>.
	 *
	 * <p>
	 * Note that this method creates a builder initialized to all default settings, whereas {@link #builder()} copies
	 * the settings of the object called on.
	 *
	 * @return A new {@link XmlParserBuilder} object.
	 */
	public static XmlParserBuilder create() {
		return new XmlParserBuilder();
	}

	@Override /* Parser */
	public ReaderParserSession createSession(ParserSessionArgs args) {
		return new XmlParserSession(this, args);
	}

	//-----------------------------------------------------------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------------------------------------------------------

	/**
	 * Configuration property:  Enable validation.
	 *
	 * @see #XML_validating
	 * @return
	 * 	<jk>true</jk> if XML document will be validated.
	 */
	protected final boolean isValidating() {
		return validating;
	}

	/**
	 * Configuration property:  Preserve root element during generalized parsing.
	 *
	 * @see #XML_preserveRootElement
	 * @return
	 * 	<jk>true</jk> if when parsing into a generic {@link ObjectMap}, the map will contain a single entry whose key
	 * 	is the root element name.
	 */
	protected final boolean isPreserveRootElement() {
		return preserveRootElement;
	}

	/**
	 * Configuration property:  XML reporter.
	 *
	 * @see #XML_reporter
	 * @return
	 * 	The {@link XMLReporter} associated with this parser, or <jk>null</jk> if there isn't one.
	 */
	protected final XMLReporter getReporter() {
		return reporter;
	}

	/**
	 * Configuration property:  XML resolver.
	 *
	 * @see #XML_resolver
	 * @return
	 * 	The {@link XMLResolver} associated with this parser, or <jk>null</jk> if there isn't one.
	 */
	protected final XMLResolver getResolver() {
		return resolver;
	}

	/**
	 * Configuration property:  XML event allocator.
	 *
	 * @see #XML_eventAllocator
	 * @return
	 * 	The {@link XMLEventAllocator} associated with this parser, or <jk>null</jk> if there isn't one.
	 */
	protected final XMLEventAllocator getEventAllocator() {
		return eventAllocator;
	}

	@Override /* Context */
	public ObjectMap asMap() {
		return super.asMap()
			.append("XmlParser", new ObjectMap()
				.append("validating", validating)
				.append("preserveRootElement", preserveRootElement)
				.append("reporter", reporter)
				.append("resolver", resolver)
				.append("eventAllocator", eventAllocator)
			);
	}
}
