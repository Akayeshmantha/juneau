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
package org.apache.juneau.utils;

import static org.apache.juneau.TestUtils.*;
import static org.apache.juneau.internal.StringUtils.*;
import static org.junit.Assert.*;

import java.math.*;
import java.util.*;
import java.util.concurrent.atomic.*;

import org.apache.juneau.*;
import org.apache.juneau.json.*;
import org.apache.juneau.parser.*;
import org.apache.juneau.serializer.*;
import org.apache.juneau.transforms.*;
import org.junit.*;

@SuppressWarnings("javadoc")
public class StringUtilsTest {

	//====================================================================================================
	// isNumeric(String,Class)
	// parseNumber(String,Class)
	//====================================================================================================
	@Test
	public void testParseNumber() throws Exception {

		// Integers
		assertTrue(isNumeric("123"));
		assertEquals(123, parseNumber("123", null));
		assertEquals(123, parseNumber("123", Integer.class));
		assertEquals((short)123, parseNumber("123", Short.class));
		assertEquals((long)123, parseNumber("123", Long.class));

		assertTrue(isNumeric("0123"));
		assertEquals(0123, parseNumber("0123", null));

		assertTrue(isNumeric("-0123"));
		assertEquals(-0123, parseNumber("-0123", null));

		// Hexadecimal
		assertTrue(isNumeric("0x123"));
		assertEquals(0x123, parseNumber("0x123", null));

		assertTrue(isNumeric("-0x123"));
		assertEquals(-0x123, parseNumber("-0x123", null));

		assertTrue(isNumeric("0X123"));
		assertEquals(0X123, parseNumber("0X123", null));

		assertTrue(isNumeric("-0X123"));
		assertEquals(-0X123, parseNumber("-0X123", null));

		assertTrue(isNumeric("#123"));
		assertEquals(0x123, parseNumber("#123", null));

		assertTrue(isNumeric("-#123"));
		assertEquals(-0x123, parseNumber("-#123", null));

		assertFalse(isNumeric("x123"));
		assertFalse(isNumeric("0x123x"));

		// Decimal
		assertTrue(isNumeric("0.123"));
		assertEquals(0.123f, parseNumber("0.123", null));

		assertTrue(isNumeric("-0.123"));
		assertEquals(-0.123f, parseNumber("-0.123", null));

		assertTrue(isNumeric(".123"));
		assertEquals(.123f, parseNumber(".123", null));

		assertTrue(isNumeric("-.123"));
		assertEquals(-.123f, parseNumber("-.123", null));

		assertFalse(isNumeric("0.123.4"));

		// Scientific notation
		assertTrue(isNumeric("1e1"));
		assertEquals(1e1f, parseNumber("1e1", null));

		assertTrue(isNumeric("1e+1"));
		assertEquals(1e+1f, parseNumber("1e+1", null));

		assertTrue(isNumeric("1e-1"));
		assertEquals(1e-1f, parseNumber("1e-1", null));

		assertTrue(isNumeric("1.1e1"));
		assertEquals(1.1e1f, parseNumber("1.1e1", null));

		assertTrue(isNumeric("1.1e+1"));
		assertEquals(1.1e+1f, parseNumber("1.1e+1", null));

		assertTrue(isNumeric("1.1e-1"));
		assertEquals(1.1e-1f, parseNumber("1.1e-1", null));

		assertTrue(isNumeric(".1e1"));
		assertEquals(.1e1f, parseNumber(".1e1", null));

		assertTrue(isNumeric(".1e+1"));
		assertEquals(.1e+1f, parseNumber(".1e+1", null));

		assertTrue(isNumeric(".1e-1"));
		assertEquals(.1e-1f, parseNumber(".1e-1", null));

		// Hexadecimal + scientific
		assertTrue(isNumeric("0x123e1"));
		assertEquals(0x123e1, parseNumber("0x123e1", null));

		try {
			parseNumber("x", Number.class);
			fail();
		} catch (ParseException e) {
			assertTrue(e.getCause() instanceof NumberFormatException);
		}

		try {
			parseNumber("x", null);
			fail();
		} catch (ParseException e) {
			assertTrue(e.getCause() instanceof NumberFormatException);
		}

		try {
			parseNumber("x", BadNumber.class);
			fail();
		} catch (ParseException e) {
			assertTrue(e.getLocalizedMessage().startsWith("Unsupported Number type"));
		}
	}

	@SuppressWarnings("serial")
	private abstract static class BadNumber extends Number {}

	//====================================================================================================
	// parseNumber(ParserReader,Class)
	//====================================================================================================
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testParseNumberFromReader() throws Exception {
		ParserReader in;
		Number n;

		for (Class c : new Class[]{ Integer.class, Double.class, Float.class, Long.class, Short.class, Byte.class, BigInteger.class, BigDecimal.class, Number.class, AtomicInteger.class, AtomicLong.class}) {
			in = new ParserReader("123'");
			n = parseNumber(in, c);
			assertTrue(c.isInstance(n));
			assertEquals(123, n.intValue());
		}
	}

	//====================================================================================================
	// test - Basic tests
	//====================================================================================================
	@Test
	public void testNumberRanges() throws Exception {
		String s;

		// An integer range is -2,147,483,648 to 2,147,483,647

		assertFalse(isNumeric(null));
		assertFalse(isNumeric(""));
		assertFalse(isNumeric("x"));

		s = "-2147483648";
		assertTrue(isNumeric(s));
		assertTrue(parseNumber(s, null) instanceof Integer);
		assertEquals(-2147483648, parseNumber(s, null));

		s = "2147483647";
		assertTrue(isNumeric(s));
		assertTrue(parseNumber(s, null) instanceof Integer);
		assertEquals(2147483647, parseNumber(s, null));

		s = "-2147483649";
		assertTrue(isNumeric(s));
		assertTrue(parseNumber(s, null) instanceof Long);
		assertEquals(-2147483649L, parseNumber(s, null));

		s = "2147483648";
		assertTrue(isNumeric(s));
		assertTrue(parseNumber(s, null) instanceof Long);
		assertEquals(2147483648L, parseNumber(s, null));

		// An long range is -9,223,372,036,854,775,808 to +9,223,372,036,854,775,807

		s = "-9223372036854775808";
		assertTrue(isNumeric(s));
		assertTrue(parseNumber(s, null) instanceof Long);
		assertEquals(-9223372036854775808L, parseNumber(s, null));

		s = "9223372036854775807";
		assertTrue(isNumeric(s));
		assertTrue(parseNumber(s, null) instanceof Long);
		assertEquals(9223372036854775807L, parseNumber(s, null));

		// Anything that falls outside this range should be a double.

		s = "-9223372036854775809";
		assertTrue(isNumeric(s));
		assertTrue(parseNumber(s, null) instanceof Double);
		assertEquals(-9223372036854775808L, parseNumber(s, null).longValue());
		assertEquals(-9.223372036854776E18, parseNumber(s, null));

		s = "9223372036854775808";
		assertTrue(isNumeric(s));
		assertTrue(parseNumber(s, null) instanceof Double);
		assertEquals(9223372036854775807L, parseNumber(s, null).longValue());
		assertEquals(9.223372036854776E18, parseNumber(s, null));

		// Check case where string is longer than 20 characters since it's a different code path.

		s = "-123456789012345678901";
		assertTrue(isNumeric(s));
		assertTrue(parseNumber(s, null) instanceof Double);
		assertEquals(-9223372036854775808L, parseNumber(s, null).longValue());
		assertEquals(-1.2345678901234568E20, parseNumber(s, null));

		s = "123456789012345678901";
		assertTrue(isNumeric(s));
		assertTrue(parseNumber(s, null) instanceof Double);
		assertEquals(9223372036854775807L, parseNumber(s, null).longValue());
		assertEquals(1.2345678901234568E20, parseNumber(s, null));

		// Autodetected floating point numbers smaller than Float.MAX_VALUE
		s = String.valueOf(Float.MAX_VALUE / 2);
		assertTrue(isNumeric(s));
		assertTrue(parseNumber(s, null) instanceof Float);
		assertEquals(1.7014117E38f, parseNumber(s, null));

		s = String.valueOf((-Float.MAX_VALUE) / 2);
		assertTrue(isNumeric(s));
		assertTrue(parseNumber(s, null) instanceof Float);
		assertEquals(-1.7014117E38f, parseNumber(s, null));

		// Autodetected floating point numbers larger than Float.MAX_VALUE
		s = String.valueOf((double)Float.MAX_VALUE * 2);
		assertTrue(isNumeric(s));
		assertTrue(parseNumber(s, null) instanceof Double);
		assertEquals("6.805646932770577E38", parseNumber(s, null).toString());

		s = String.valueOf((double)Float.MAX_VALUE * -2);
		assertTrue(isNumeric(s));
		assertTrue(parseNumber(s, null) instanceof Double);
		assertEquals("-6.805646932770577E38", parseNumber(s, null).toString());

		s = String.valueOf("214748364x");
		assertFalse(isNumeric(s));
		try {
			parseNumber(s, Number.class);
		} catch (ParseException e) {}

		s = String.valueOf("2147483640x");
		assertFalse(isNumeric(s));
		try {
			parseNumber(s, Long.class);
		} catch (ParseException e) {}


	}

	//====================================================================================================
	// testReplaceVars
	//====================================================================================================
	@Test
	public void testReplaceVars() throws Exception {
		ObjectMap m = new ObjectMap("{a:'A',b:1,c:true,d:'{e}',e:'E{f}E',f:'F',g:'{a}',h:'a',i:null}");

		String s = "xxx";
		assertEquals("xxx", replaceVars(s, m));

		s = "{a}";
		assertEquals("A", replaceVars(s, m));
		s = "{a}{a}";
		assertEquals("AA", replaceVars(s, m));
		s = "x{a}x";
		assertEquals("xAx", replaceVars(s, m));
		s = "x{a}x{a}x";
		assertEquals("xAxAx", replaceVars(s, m));

		s = "{b}";
		assertEquals("1", replaceVars(s, m));
		s = "{b}{b}";
		assertEquals("11", replaceVars(s, m));
		s = "x{b}x";
		assertEquals("x1x", replaceVars(s, m));
		s = "x{b}x{b}x";
		assertEquals("x1x1x", replaceVars(s, m));

		s = "{c}";
		assertEquals("true", replaceVars(s, m));
		s = "{c}{c}";
		assertEquals("truetrue", replaceVars(s, m));
		s = "x{c}x{c}x";
		assertEquals("xtruextruex", replaceVars(s, m));

		s = "{d}";
		assertEquals("EFE", replaceVars(s, m));
		s = "{d}{d}";
		assertEquals("EFEEFE", replaceVars(s, m));
		s = "x{d}x";
		assertEquals("xEFEx", replaceVars(s, m));
		s = "x{d}x{d}x";
		assertEquals("xEFExEFEx", replaceVars(s, m));

		s = "{g}";
		assertEquals("A", replaceVars(s, m));
		s = "{g}{g}";
		assertEquals("AA", replaceVars(s, m));
		s = "x{g}x";
		assertEquals("xAx", replaceVars(s, m));
		s = "x{g}x{g}x";
		assertEquals("xAxAx", replaceVars(s, m));

		s = "{x}";
		assertEquals("{x}", replaceVars(s, m));
		s = "{x}{x}";
		assertEquals("{x}{x}", replaceVars(s, m));
		s = "x{x}x{x}x";
		assertEquals("x{x}x{x}x", replaceVars(s, m));

		s = "{{g}}";
		assertEquals("{A}", replaceVars(s, m));
		s = "{{h}}";
		assertEquals("A", replaceVars(s, m));

		s = "{{i}}";
		assertEquals("{}", replaceVars(s, m));
		s = "{}";
		assertEquals("{}", replaceVars(s, m));
	}

	//====================================================================================================
	// isFloat(String)
	//====================================================================================================
	@Test
	public void testisFloat() throws Exception {
		String[] valid = {
			"+1.0",
			"-1.0",
			".0",
			"NaN",
			"Infinity",
			"1e1",
			"-1e-1",
			"+1e+1",
			"-1.1e-1",
			"+1.1e+1",
			"1.1f",
			"1.1F",
			"1.1d",
			"1.1D",
			"0x1.fffffffffffffp1023",
			"0x1.FFFFFFFFFFFFFP1023",
		};
		for (String s : valid)
			assertTrue(isFloat(s));

		String[] invalid = {
			null,
			"",
			"a",
			"+",
			"-",
			".",
			"a",
			"+a",
			"11a",
		};
		for (String s : invalid)
			assertFalse(isFloat(s));
	}

	//====================================================================================================
	// isDecimal(String)
	//====================================================================================================
	@Test
	public void testisDecimal() throws Exception {
		String[] valid = {
			"+1",
			"-1",
			"0x123",
			"0X123",
			"0xdef",
			"0XDEF",
			"#def",
			"#DEF",
			"0123",
		};
		for (String s : valid)
			assertTrue(isDecimal(s));

		String[] invalid = {
			null,
			"",
			"a",
			"+",
			"-",
			".",
			"0xdeg",
			"0XDEG",
			"#deg",
			"#DEG",
			"0128",
			"012A",
		};
		for (String s : invalid)
			assertFalse(isDecimal(s));
	}

	//====================================================================================================
	// join(Object[],String)
	// join(int[],String)
	// join(Collection,String)
	// join(Object[],char)
	// join(int[],char)
	// join(Collection,char)
	//====================================================================================================
	@Test
	public void testJoin() throws Exception {
		assertNull(join((Object[])null, ","));
		assertEquals("1", join(new Object[]{1}, ","));
		assertEquals("1,2", join(new Object[]{1,2}, ","));

		assertNull(join((int[])null, ","));
		assertEquals("1", join(new int[]{1}, ","));
		assertEquals("1,2", join(new int[]{1,2}, ","));

		assertNull(join((Collection<?>)null, ","));
		assertEquals("1", join(Arrays.asList(new Integer[]{1}), ","));
		assertEquals("1,2", join(Arrays.asList(new Integer[]{1,2}), ","));

		assertNull(join((Object[])null, ','));
		assertEquals("1", join(new Object[]{1}, ','));
		assertEquals("1,2", join(new Object[]{1,2}, ','));

		assertNull(join((int[])null, ','));
		assertEquals("1", join(new int[]{1}, ','));
		assertEquals("1,2", join(new int[]{1,2}, ','));

		assertNull(join((Collection<?>)null, ','));
		assertEquals("1", join(Arrays.asList(new Integer[]{1}), ','));
		assertEquals("1,2", join(Arrays.asList(new Integer[]{1,2}), ','));
	}

	//====================================================================================================
	// split(String,char)
	//====================================================================================================
	@Test
	public void testSplit() throws Exception {
		String[] r;

		assertNull(split((String)null, ','));
		assertObjectEquals("[]", split("", ','));
		assertObjectEquals("['1']", split("1", ','));
		assertObjectEquals("['1','2']", split("1,2", ','));
		assertObjectEquals("['1,2']", split("1\\,2", ','));

		r = split("1\\\\,2", ',');
		assertEquals("1\\", r[0]);
		assertEquals("2", r[1]);

		r = split("1\\\\\\,2", ',');
		assertEquals(1, r.length);
		assertEquals("1\\,2", r[0]);

		r = split("1,2\\", ',');
		assertEquals("2\\", r[1]);

		r = split("1,2\\\\", ',');
		assertEquals("2\\", r[1]);

		r = split("1,2\\,", ',');
		assertEquals("2,", r[1]);

		r = split("1,2\\\\,", ',');
		assertEquals("2\\", r[1]);
		assertEquals("", r[2]);
	}

	//====================================================================================================
	// nullIfEmpty(String)
	//====================================================================================================
	@Test
	public void testNullIfEmpty() throws Exception {
		assertNull(nullIfEmpty(null));
		assertNull(nullIfEmpty(""));
		assertNotNull(nullIfEmpty("x"));
	}

	//====================================================================================================
	// unescapeChars(String,char[],char)
	//====================================================================================================
	@Test
	public void testUnescapeChars() throws Exception {
		char[] toEscape = {'\\',',','|'};
		char escape = '\\';

		assertNull(unEscapeChars(null, toEscape, escape));
		assertEquals("xxx", unEscapeChars("xxx", new char[0], escape));
		assertEquals("xxx", unEscapeChars("xxx", null, escape));
		assertEquals("xxx", unEscapeChars("xxx", toEscape, (char)0));

		assertEquals("xxx", unEscapeChars("xxx", toEscape, escape));
		assertEquals("x,xx", unEscapeChars("x\\,xx", toEscape, escape));
		assertEquals("x\\xx", unEscapeChars("x\\xx", toEscape, escape));
		assertEquals("x\\,xx", unEscapeChars("x\\\\,xx", toEscape, escape));
		assertEquals("x\\,xx", unEscapeChars("x\\\\\\,xx", toEscape, escape));
		assertEquals("\\", unEscapeChars("\\", toEscape, escape));
		assertEquals(",", unEscapeChars("\\,", toEscape, escape));
		assertEquals("|", unEscapeChars("\\|", toEscape, escape));

		toEscape = new char[] {',','|'};
		assertEquals("x\\\\xx", unEscapeChars("x\\\\xx", toEscape, escape));
	}

	//====================================================================================================
	// decodeHex(String)
	//====================================================================================================
	@Test
	public void testDecodeHex() throws Exception {
		assertNull(decodeHex(null));
		assertEquals("19azAZ", decodeHex("19azAZ"));
		assertEquals("[0][1][ffff]", decodeHex("\u0000\u0001\uFFFF"));
	}

	//====================================================================================================
	// startsWith(String,char)
	//====================================================================================================
	@Test
	public void testStartsWith() throws Exception {
		assertFalse(startsWith(null, 'a'));
		assertFalse(startsWith("", 'a'));
		assertTrue(startsWith("a", 'a'));
		assertTrue(startsWith("ab", 'a'));
	}

	//====================================================================================================
	// endsWith(String,char)
	//====================================================================================================
	@Test
	public void testEndsWith() throws Exception {
		assertFalse(endsWith(null, 'a'));
		assertFalse(endsWith("", 'a'));
		assertTrue(endsWith("a", 'a'));
		assertTrue(endsWith("ba", 'a'));
	}

	//====================================================================================================
	// base64EncodeToString(String)
	// base64DecodeToString(String)
	//====================================================================================================
	@Test
	public void testBase64EncodeToString() throws Exception {
		String s = null;

		assertEquals(s, base64DecodeToString(base64EncodeToString(s)));
		s = "";
		assertEquals(s, base64DecodeToString(base64EncodeToString(s)));
		s = "foobar";
		assertEquals(s, base64DecodeToString(base64EncodeToString(s)));
		s = "\u0000\uffff";
		assertEquals(s, base64DecodeToString(base64EncodeToString(s)));

		try {
			base64Decode("a");
			fail();
		} catch (IllegalArgumentException e) {
			assertEquals("Invalid BASE64 string length.  Must be multiple of 4.", e.getLocalizedMessage());
			// OK.
		}
		try {
			base64Decode("aaa");
			fail();
		} catch (IllegalArgumentException e) {
			// OK.
		}
	}

	//====================================================================================================
	// generateUUID(String)
	//====================================================================================================
	@Test
	public void testGenerateUUID() throws Exception {
		for (int i = 0; i < 10; i++) {
			String s = generateUUID(i);
			assertEquals(i, s.length());
			for (char c : s.toCharArray())
				assertTrue(Character.isLowerCase(c) || Character.isDigit(c));
		}
	}

	//====================================================================================================
	// trim(String)
	//====================================================================================================
	@Test
	public void testTrim() throws Exception {
		assertNull(trim(null));
		assertEquals("", trim(""));
		assertEquals("", trim("  "));
	}

	//====================================================================================================
	// parseISO8601Date(String)
	//====================================================================================================
	@Test
	public void testParseISO8601Date() throws Exception {
		WriterSerializer s = new JsonSerializer.Simple().addPojoSwaps(DateSwap.ISO8601DTPNZ.class);

		assertNull(parseISO8601Date(null));
		assertNull(parseISO8601Date(""));

		assertEquals("'2000-01-01T00:00:00.000'", s.serialize(parseISO8601Date("2000")));
		assertEquals("'2000-02-01T00:00:00.000'", s.serialize(parseISO8601Date("2000-02")));
		assertEquals("'2000-02-03T00:00:00.000'", s.serialize(parseISO8601Date("2000-02-03")));
		assertEquals("'2000-02-03T04:00:00.000'", s.serialize(parseISO8601Date("2000-02-03T04")));
		assertEquals("'2000-02-03T04:05:00.000'", s.serialize(parseISO8601Date("2000-02-03T04:05")));
		assertEquals("'2000-02-03T04:05:06.000'", s.serialize(parseISO8601Date("2000-02-03T04:05:06")));
		assertEquals("'2000-02-03T04:00:00.000'", s.serialize(parseISO8601Date("2000-02-03 04")));
		assertEquals("'2000-02-03T04:05:00.000'", s.serialize(parseISO8601Date("2000-02-03 04:05")));
		assertEquals("'2000-02-03T04:05:06.000'", s.serialize(parseISO8601Date("2000-02-03 04:05:06")));

		// ISO8601 doesn't support milliseconds, so it gets trimmed.
		assertEquals("'2000-02-03T04:05:06.000'", s.serialize(parseISO8601Date("2000-02-03 04:05:06,789")));
	}

	//====================================================================================================
	// pathStartsWith(String, String)
	//====================================================================================================
	@Test
	public void testPathStartsWith() throws Exception {
		assertTrue(pathStartsWith("foo", "foo"));
		assertTrue(pathStartsWith("foo/bar", "foo"));
		assertFalse(pathStartsWith("foo2/bar", "foo"));
		assertFalse(pathStartsWith("foo2", "foo"));
		assertFalse(pathStartsWith("foo2", ""));
	}

	//====================================================================================================
	// getField(int, String, char)
	//====================================================================================================
	@Test
	public void testGetField() {
		String in = "0,1,2";
		assertEquals("0", getField(0, in, ','));
		assertEquals("1", getField(1, in, ','));
		assertEquals("2", getField(2, in, ','));
		assertEquals("", getField(3, in, ','));

		in = ",1,,3,";
		assertEquals("", getField(0, in, ','));
		assertEquals("1", getField(1, in, ','));
		assertEquals("", getField(2, in, ','));
		assertEquals("3", getField(3, in, ','));
		assertEquals("", getField(4, in, ','));
		assertEquals("", getField(5, in, ','));

		in = "";
		assertEquals("", getField(0, in, ','));

		in = null;
		assertEquals("", getField(0, in, ','));
	}
}
