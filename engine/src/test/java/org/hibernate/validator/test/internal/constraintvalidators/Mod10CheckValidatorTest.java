/*
* JBoss, Home of Professional Open Source
* Copyright 2012, Red Hat, Inc. and/or its affiliates, and individual contributors
* by the @authors tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* http://www.apache.org/licenses/LICENSE-2.0
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.hibernate.validator.test.internal.constraintvalidators;

import org.testng.annotations.Test;

import org.hibernate.validator.constraints.Mod10Check;
import org.hibernate.validator.internal.constraintvalidators.Mod10CheckValidator;
import org.hibernate.validator.internal.util.annotationfactory.AnnotationDescriptor;
import org.hibernate.validator.internal.util.annotationfactory.AnnotationFactory;
import org.hibernate.validator.testutil.TestForIssue;

import static org.testng.Assert.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

/**
 * Tests for the {@code Mod10CheckValidator}.
 *
 * @author Hardy Ferentschik
 * @author Victor Rezende dos Santos
 */
public class Mod10CheckValidatorTest {

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testInvalidStartIndex() {
		Mod10CheckValidator validator = new Mod10CheckValidator();
		Mod10Check modCheck = createMod10CheckAnnotation( -1, Integer.MAX_VALUE, -1, false );
		validator.initialize( modCheck );
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testInvalidEndIndex() {
		Mod10CheckValidator validator = new Mod10CheckValidator();
		Mod10Check modCheck = createMod10CheckAnnotation( 0, -1, -1, false );
		validator.initialize( modCheck );
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testEndIndexLessThanStartIndex() {
		Mod10CheckValidator validator = new Mod10CheckValidator();
		Mod10Check modCheck = createMod10CheckAnnotation( 5, 0, -1, false );
		validator.initialize( modCheck );
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testInvalidCheckDigitPosition() {
		Mod10CheckValidator validator = new Mod10CheckValidator();
		Mod10Check modCheck = createMod10CheckAnnotation( 0, 10, 5, false );
		validator.initialize( modCheck );
	}

	@Test
	public void testFailOnNonNumeric() throws Exception {
		Mod10CheckValidator validator = new Mod10CheckValidator();
		Mod10Check modCheck = createMod10CheckAnnotation( 0, Integer.MAX_VALUE, -1, false );
		validator.initialize( modCheck );

		assertFalse( validator.isValid( new MyCustomStringImpl( "A79927398712" ), null ) );
	}

	@Test
	public void testIgnoreNonNumeric() throws Exception {
		Mod10CheckValidator validator = new Mod10CheckValidator();
		Mod10Check modCheck = createMod10CheckAnnotation( 0, Integer.MAX_VALUE, -1, true );
		validator.initialize( modCheck );

		assertTrue( validator.isValid( new MyCustomStringImpl( "A79927398712" ), null ) );
	}

	@Test
	public void testValidMod10() throws Exception {
		Mod10CheckValidator validator = new Mod10CheckValidator();
		Mod10Check modCheck = createMod10CheckAnnotation( 0, Integer.MAX_VALUE, -1, false );
		validator.initialize( modCheck );

		assertTrue( validator.isValid( "79927398712", null ) );
	}

	@Test
	public void testValidMod10WithGivenRange() throws Exception {
		Mod10CheckValidator validator = new Mod10CheckValidator();
		Mod10Check modCheck = createMod10CheckAnnotation( 3, 14, -1, true );
		validator.initialize( modCheck );

		assertTrue( validator.isValid( "123-7992739871-2", null ) );
	}

	@Test
	public void testValidMod10WithGivenRangeAndCheckDigitIndex() throws Exception {
		Mod10CheckValidator validator = new Mod10CheckValidator();
		Mod10Check modCheck = createMod10CheckAnnotation( 3, 13, 13, true );
		validator.initialize( modCheck );

		assertTrue( validator.isValid( "123-7992739871-2-456", null ) );
	}

	@Test
	public void testInvalidMod10() throws Exception {
		Mod10CheckValidator validator = new Mod10CheckValidator();
		Mod10Check modCheck = createMod10CheckAnnotation( 0, Integer.MAX_VALUE, -1, false );
		validator.initialize( modCheck );

		assertFalse( validator.isValid( new MyCustomStringImpl( "79927398713" ), null ) );
	}

	@Test
	@TestForIssue(jiraKey = "HV-813")
	public void testValidEAN_GTIN_13() throws Exception {
		Mod10CheckValidator validator = new Mod10CheckValidator();
		Mod10Check modCheck = createMod10CheckAnnotation( 0, Integer.MAX_VALUE, -1, true );
		validator.initialize( modCheck );

		assertTrue( validator.isValid( new MyCustomStringImpl( "4 007630 00011 6" ), null ) );
		assertTrue( validator.isValid( new MyCustomStringImpl( "1 234567 89012 8" ), null ) );
	}

	@Test
	@TestForIssue(jiraKey = "HV-813")
	public void testValidEAN_GTIN_14() throws Exception {
		Mod10CheckValidator validator = new Mod10CheckValidator();
		Mod10Check modCheck = createMod10CheckAnnotation( 0, Integer.MAX_VALUE, -1, true );
		validator.initialize( modCheck );

		assertTrue( validator.isValid( new MyCustomStringImpl( "0 40 07630 00011 6" ), null ) );
		assertTrue( validator.isValid( new MyCustomStringImpl( "3 07 12345 00001 0" ), null ) );
	}

	@Test
	@TestForIssue(jiraKey = "HV-813")
	public void testValidEAN_14WithEAN_128() throws Exception {
		Mod10CheckValidator validator = new Mod10CheckValidator();
		Mod10Check modCheck = createMod10CheckAnnotation( 2, 16, -1, true );
		validator.initialize( modCheck );
		assertTrue( validator.isValid( new MyCustomStringImpl( "(01) 1 23 45678 90123 1" ), null ) );
	}

	@Test
	@TestForIssue(jiraKey = "HV-813")
	public void testValidISBN_13() throws Exception {
		Mod10CheckValidator validator = new Mod10CheckValidator();
		Mod10Check modCheck = createMod10CheckAnnotation( 0, Integer.MAX_VALUE, -1, true );
		validator.initialize( modCheck );

		assertTrue( validator.isValid( new MyCustomStringImpl( "978-85-61411-03-9" ), null ) );
		assertTrue( validator.isValid( new MyCustomStringImpl( "978-1-4302-1957-6" ), null ) );
	}

	@Test
	@TestForIssue(jiraKey = "HV-813")
	public void testValidIdentcode() throws Exception {
		Mod10CheckValidator validator = new Mod10CheckValidator();
		Mod10Check modCheck = createMod10CheckAnnotation( 0, Integer.MAX_VALUE, -1, true, 4, 9 );
		validator.initialize( modCheck );

		assertTrue( validator.isValid( new MyCustomStringImpl( "56.310 243.031 3" ), null ) );
	}

	private Mod10Check createMod10CheckAnnotation(int start, int end, int checkDigitIndex, boolean ignoreNonDigits, int multiplier, int weight) {
		AnnotationDescriptor<Mod10Check> descriptor = new AnnotationDescriptor<Mod10Check>( Mod10Check.class );
		descriptor.setValue( "startIndex", start );
		descriptor.setValue( "endIndex", end );
		descriptor.setValue( "checkDigitPosition", checkDigitIndex );
		descriptor.setValue( "ignoreNonDigitCharacters", ignoreNonDigits );
		descriptor.setValue( "multiplier", multiplier );
		descriptor.setValue( "weight", weight );

		return AnnotationFactory.create( descriptor );
	}

	private Mod10Check createMod10CheckAnnotation(int start, int end, int checkDigitIndex, boolean ignoreNonDigits) {
		return this.createMod10CheckAnnotation( start, end, checkDigitIndex, ignoreNonDigits, 3, 1 );
	}

	private Mod10Check createMod10CheckAnnotation(boolean ignoreNonDigits, int multiplier, int weight) {
		return this.createMod10CheckAnnotation( 0, Integer.MAX_VALUE, -1, ignoreNonDigits, multiplier, weight );
	}
}
