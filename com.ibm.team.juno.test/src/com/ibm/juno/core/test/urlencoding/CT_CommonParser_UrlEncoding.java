/*******************************************************************************
 * Licensed Materials - Property of IBM
 * © Copyright IBM Corporation 2014, 2015. All Rights Reserved.
 *
 * Note to U.S. Government Users Restricted Rights:
 * Use, duplication or disclosure restricted by GSA ADP Schedule
 * Contract with IBM Corp.
 *******************************************************************************/
package com.ibm.juno.core.test.urlencoding;

import static com.ibm.juno.core.BeanContextProperties.*;
import static com.ibm.juno.core.serializer.SerializerProperties.*;
import static com.ibm.juno.core.test.TestUtils.*;
import static org.junit.Assert.*;

import java.util.*;

import org.junit.*;

import com.ibm.juno.core.*;
import com.ibm.juno.core.parser.*;
import com.ibm.juno.core.serializer.*;
import com.ibm.juno.core.urlencoding.*;

@SuppressWarnings({"rawtypes","hiding","serial"})
public class CT_CommonParser_UrlEncoding {

	ReaderParser p = UrlEncodingParser.DEFAULT.clone().setClassLoader(getClass().getClassLoader());

	//====================================================================================================
	// testFromSerializer
	//====================================================================================================
	@Test
	public void testFromSerializer() throws Exception {
		Map m = null;
		String in;

		in = "a=$n(1)";
		m = (Map)p.parse(in, Object.class);
		assertEquals(1, m.get("a"));

		in = "a=$n(1)&b=foo+bar";
		m = (Map)p.parse(in, Object.class);
		assertEquals(1, m.get("a"));
		assertEquals("foo bar", m.get("b"));

		in = "a=$n(1)&b=foo+bar&c=$b(false)";
		m = (Map)p.parse(in, Object.class);
		assertEquals(1, m.get("a"));
		assertEquals("foo bar", m.get("b"));
		assertEquals(false, m.get("c"));

		in = "a=$n(1)&b=foo%20bar&c=$b(false)";
		m = (Map)p.parse(in, Object.class);
		assertEquals(1, m.get("a"));
		assertEquals("foo bar", m.get("b"));
		assertEquals(false, m.get("c"));

		ObjectMap jm = (ObjectMap)p.parse("x=$a($o(attribute=value),$o(attribute='value'))", Object.class);
		assertEquals("value", jm.getObjectList("x").getObjectMap(0).getString("attribute"));
		assertEquals("'value'", jm.getObjectList("x").getObjectMap(1).getString("attribute"));

		ObjectList jl = (ObjectList)p.parse("_value=$a($o(attribute=value),$o(attribute='value'))", Object.class);
		assertEquals("value", jl.getObjectMap(0).getString("attribute"));
		assertEquals("'value'", jl.getObjectMap(1).getString("attribute"));

		A1 b = new A1();
		A2 tl = new A2();
		tl.add(new A3("name0","value0"));
		tl.add(new A3("name1","value1"));
		b.list = tl;

		in = new UrlEncodingSerializer().setProperty(SERIALIZER_addClassAttrs, true).serialize(b);
		b = (A1)p.parse(in, Object.class);
		assertEquals("value1", b.list.get(1).value);

		in = UrlEncodingSerializer.DEFAULT.serialize(b);
		b = p.parse(in, A1.class);
		assertEquals("value1", b.list.get(1).value);
	}

	public static class A1 {
		public A2 list;
	}

	public static class A2 extends LinkedList<A3> {
	}

	public static class A3 {
		public String name, value;
		public A3(){}
		public A3(String name, String value) {
			this.name = name;
			this.value = value;
		}
	}

	//====================================================================================================
	// Correct handling of unknown properties.
	//====================================================================================================
	@Test
	public void testCorrectHandlingOfUnknownProperties() throws Exception {
		ReaderParser p = new UrlEncodingParser().setProperty(BEAN_ignoreUnknownBeanProperties, true);
		B t;

		String in =  "a=1&unknown=3&b=2";
		t = p.parse(in, B.class);
		assertEquals(t.a, 1);
		assertEquals(t.b, 2);

		try {
			p = new UrlEncodingParser();
			p.parse(in, B.class);
			fail("Exception expected");
		} catch (ParseException e) {}
	}

	public static class B {
		public int a, b;
	}

	//====================================================================================================
	// Writing to Collection properties with no setters.
	//====================================================================================================
	@Test
	public void testCollectionPropertiesWithNoSetters() throws Exception {

		ReaderParser p = UrlEncodingParser.DEFAULT;

		String json = "ints=(1,2,3)&beans=((a=1,b=2))";
		C t = p.parse(json, C.class);
		assertEquals(t.getInts().size(), 3);
		assertEquals(t.getBeans().get(0).b, 2);
	}

	public static class C {
		private Collection<Integer> ints = new LinkedList<Integer>();
		private List<B> beans = new LinkedList<B>();
		public Collection<Integer> getInts() {
			return ints;
		}
		public List<B> getBeans() {
			return beans;
		}
	}

	//====================================================================================================
	// Parser listeners.
	//====================================================================================================
	@Test
	public void testParserListeners() throws Exception {
		final List<String> events = new LinkedList<String>();
		UonParser p = new UrlEncodingParser().setProperty(BEAN_ignoreUnknownBeanProperties, true);
		p.addListener(
			new ParserListener() {
				@Override /* ParserListener */
				public <T> void onUnknownProperty(String propertyName, Class<T> beanClass, T bean, int line, int col) {
					events.add(propertyName + "," + line + "," + col);
				}
			}
		);

		String in = "a=1&unknownProperty=foo&b=2";
		p.parse(in, B.class);
		assertEquals(1, events.size());
		assertEquals("unknownProperty,1,4", events.get(0));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testCollections() throws Exception {
		WriterSerializer s = new UrlEncodingSerializer().setProperty(UonSerializerProperties.UON_simpleMode, true);
		ReaderParser p = new UrlEncodingParser();

		List l = new ObjectList("foo","bar");
		assertEquals("0=foo&1=bar", s.serialize(l));

		String in =  "0=foo&1=bar";
		ClassMeta<LinkedList<String>> cm = p.getBeanContext().getCollectionClassMeta(LinkedList.class, String.class);
		l = p.parse(in, cm);
		assertObjectEquals("['foo','bar']",l);
	}
}
