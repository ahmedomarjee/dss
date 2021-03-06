/**
 * DSS - Digital Signature Services
 * Copyright (C) 2015 European Commission, provided under the CEF programme
 * 
 * This file is part of the "DSS - Digital Signature Services" project.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package eu.europa.esig.dss.xades.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import eu.europa.esig.dss.diagnostic.DiagnosticData;
import eu.europa.esig.dss.diagnostic.SignatureWrapper;
import eu.europa.esig.dss.diagnostic.jaxb.XmlDigestMatcher;
import eu.europa.esig.dss.diagnostic.jaxb.XmlSignatureScope;
import eu.europa.esig.dss.enumerations.DigestMatcherType;
import eu.europa.esig.dss.enumerations.SignatureScopeType;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.FileDocument;
import eu.europa.esig.dss.model.InMemoryDocument;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import eu.europa.esig.dss.validation.SignedDocumentValidator;
import eu.europa.esig.dss.validation.reports.Reports;

public class DSS1770Test {
	
	@Test
	public void test() {
		DSSDocument doc = new FileDocument("src/test/resources/validation/dss1770/dss1770.xml");
		SignedDocumentValidator validator = SignedDocumentValidator.fromDocument(doc);
		validator.setCertificateVerifier(new CommonCertificateVerifier());
		
		Reports reports = validator.validateDocument();
		// reports.print();
		assertNotNull(reports);
		
		DiagnosticData diagnosticData = reports.getDiagnosticData();
		List<SignatureWrapper> signatures = diagnosticData.getSignatures();
		assertEquals(1, signatures.size());
		
		SignatureWrapper signatureWrapper = signatures.get(0);
		List<XmlSignatureScope> signatureScopes = signatureWrapper.getSignatureScopes();
		assertEquals(1, signatureScopes.size());
		XmlSignatureScope xmlSignatureScope = signatureScopes.get(0);
		assertEquals(SignatureScopeType.FULL, xmlSignatureScope.getScope());
		assertEquals("Full XML File", xmlSignatureScope.getName());
		List<XmlDigestMatcher> digestMatchers = signatureWrapper.getDigestMatchers();
		assertEquals(3, digestMatchers.size());
		boolean refRootFound = false;
		for (XmlDigestMatcher digestMatcher : digestMatchers) {
			if (DigestMatcherType.REFERENCE.equals(digestMatcher.getType())) {
				assertTrue(digestMatcher.isDataFound());
				assertTrue(digestMatcher.isDataIntact());
				refRootFound = true;
			}
		}
		assertTrue(refRootFound);
	}
	
	@Test
	public void rootAndRefsCoveredEnvelopedSigTest() {
		DSSDocument doc = new FileDocument("src/test/resources/validation/dss1770/dss1770rootAndRefs.xml");
		SignedDocumentValidator validator = SignedDocumentValidator.fromDocument(doc);
		validator.setCertificateVerifier(new CommonCertificateVerifier());
		
		Reports reports = validator.validateDocument();
		// reports.print();
		assertNotNull(reports);
		
		DiagnosticData diagnosticData = reports.getDiagnosticData();
		List<SignatureWrapper> signatures = diagnosticData.getSignatures();
		assertEquals(1, signatures.size());
		
		SignatureWrapper signatureWrapper = signatures.get(0);
		List<XmlSignatureScope> signatureScopes = signatureWrapper.getSignatureScopes();
		assertEquals(3, signatureScopes.size());
		
		int fullScopeCounter = 0;
		int partialScopeCounter = 0;
		for (XmlSignatureScope signatureScope : signatureScopes) {
			if (SignatureScopeType.FULL.equals(signatureScope.getScope())) {
				assertEquals("Full XML File", signatureScope.getName()); // the whole current file is covered
				fullScopeCounter++;
			} else if (SignatureScopeType.PARTIAL.equals(signatureScope.getScope())) {
				partialScopeCounter++;
			}
		}
		assertEquals(1, fullScopeCounter);
		assertEquals(2, partialScopeCounter);
		List<XmlDigestMatcher> digestMatchers = signatureWrapper.getDigestMatchers();
		assertEquals(4, digestMatchers.size());
		boolean refRootFound = false;
		for (XmlDigestMatcher digestMatcher : digestMatchers) {
			if ("REF-ROOT".equals(digestMatcher.getName())) {
				assertTrue(digestMatcher.isDataFound());
				assertTrue(digestMatcher.isDataIntact());
				refRootFound = true;
			}
		}
		assertTrue(refRootFound);
	}
	
	@Test
	public void detachedContentTest() {
		DSSDocument doc = new FileDocument("src/test/resources/validation/dss1770/dss1770rootAndRefs.xml");
		SignedDocumentValidator validator = SignedDocumentValidator.fromDocument(doc);
		validator.setCertificateVerifier(new CommonCertificateVerifier());
		validator.setDetachedContents(Arrays.asList(new FileDocument("src/test/resources/sample.png")));
		
		Reports reports = validator.validateDocument();
		// reports.print();
		assertNotNull(reports);
		
		DiagnosticData diagnosticData = reports.getDiagnosticData();
		List<SignatureWrapper> signatures = diagnosticData.getSignatures();
		assertEquals(1, signatures.size());
		
		SignatureWrapper signatureWrapper = signatures.get(0);
		List<XmlSignatureScope> signatureScopes = signatureWrapper.getSignatureScopes();
		assertEquals(3, signatureScopes.size());
		
		int fullScopeCounter = 0;
		int partialScopeCounter = 0;
		for (XmlSignatureScope signatureScope : signatureScopes) {
			if (SignatureScopeType.FULL.equals(signatureScope.getScope())) {
				assertEquals("Full XML File", signatureScope.getName());
				fullScopeCounter++;
			} else if (SignatureScopeType.PARTIAL.equals(signatureScope.getScope())) {
				partialScopeCounter++;
			}
		}
		assertEquals(1, fullScopeCounter);
		assertEquals(2, partialScopeCounter);
		List<XmlDigestMatcher> digestMatchers = signatureWrapper.getDigestMatchers();
		assertEquals(4, digestMatchers.size());
		boolean refRootFound = false;
		for (XmlDigestMatcher digestMatcher : digestMatchers) {
			if ("REF-ROOT".equals(digestMatcher.getName())) {
				assertTrue(digestMatcher.isDataFound());
				assertTrue(digestMatcher.isDataIntact());
				refRootFound = true;
			}
		}
		assertTrue(refRootFound);
	}
	
	@Test
	public void detachedContentWithEmptyNameTest() {
		DSSDocument doc = new FileDocument("src/test/resources/validation/dss1770/dss1770rootAndRefs.xml");
		SignedDocumentValidator validator = SignedDocumentValidator.fromDocument(doc);
		validator.setCertificateVerifier(new CommonCertificateVerifier());
		FileDocument fileDocument = new FileDocument("src/test/resources/sample.png");
		fileDocument.setName("");
		validator.setDetachedContents(Arrays.asList(fileDocument));
		
		Reports reports = validator.validateDocument();
		// reports.print();
		assertNotNull(reports);
		
		DiagnosticData diagnosticData = reports.getDiagnosticData();
		List<SignatureWrapper> signatures = diagnosticData.getSignatures();
		assertEquals(1, signatures.size());
		
		SignatureWrapper signatureWrapper = signatures.get(0);
		List<XmlSignatureScope> signatureScopes = signatureWrapper.getSignatureScopes();
		assertEquals(3, signatureScopes.size());
		
		int fullScopeCounter = 0;
		int partialScopeCounter = 0;
		for (XmlSignatureScope signatureScope : signatureScopes) {
			if (SignatureScopeType.FULL.equals(signatureScope.getScope())) {
				assertEquals("Full XML File", signatureScope.getName());
				fullScopeCounter++;
			} else if (SignatureScopeType.PARTIAL.equals(signatureScope.getScope())) {
				partialScopeCounter++;
			}
		}
		assertEquals(1, fullScopeCounter);
		assertEquals(2, partialScopeCounter);
		List<XmlDigestMatcher> digestMatchers = signatureWrapper.getDigestMatchers();
		assertEquals(4, digestMatchers.size());
		boolean refRootFound = false;
		for (XmlDigestMatcher digestMatcher : digestMatchers) {
			if ("REF-ROOT".equals(digestMatcher.getName())) {
				assertTrue(digestMatcher.isDataFound());
				assertTrue(digestMatcher.isDataIntact());
				refRootFound = true;
			}
		}
		assertTrue(refRootFound);
	}
	
	@Test
	public void refUriRemovedTest() {
		DSSDocument doc = new FileDocument("src/test/resources/validation/dss1770/dss1770refUriRemoved.xml");
		SignedDocumentValidator validator = SignedDocumentValidator.fromDocument(doc);
		validator.setCertificateVerifier(new CommonCertificateVerifier());
		
		Reports reports = validator.validateDocument();
		// reports.print();
		assertNotNull(reports);
		
		DiagnosticData diagnosticData = reports.getDiagnosticData();
		List<SignatureWrapper> signatures = diagnosticData.getSignatures();
		assertEquals(1, signatures.size());
		
		SignatureWrapper signatureWrapper = signatures.get(0);
		List<XmlSignatureScope> signatureScopes = signatureWrapper.getSignatureScopes();
		assertEquals(2, signatureScopes.size());
		
		int fullScopeCounter = 0;
		int partialScopeCounter = 0;
		for (XmlSignatureScope signatureScope : signatureScopes) {
			if (SignatureScopeType.FULL.equals(signatureScope.getScope())) {
				fullScopeCounter++;
			} else if (SignatureScopeType.PARTIAL.equals(signatureScope.getScope())) {
				partialScopeCounter++;
			}
		}
		assertEquals(0, fullScopeCounter);
		assertEquals(2, partialScopeCounter);
		
		List<XmlDigestMatcher> digestMatchers = signatureWrapper.getDigestMatchers();
		assertEquals(4, digestMatchers.size());
		boolean refRootFound = false;
		for (XmlDigestMatcher digestMatcher : digestMatchers) {
			if ("REF-ROOT".equals(digestMatcher.getName())) {
				assertFalse(digestMatcher.isDataFound());
				assertFalse(digestMatcher.isDataIntact());
				refRootFound = true;
			}
		}
		assertTrue(refRootFound);
	}
	
	@Test
	public void nullUriWithDetachedContentTest() {
		DSSDocument doc = new FileDocument("src/test/resources/validation/dss1770/dss1770refUriRemoved.xml");
		SignedDocumentValidator validator = SignedDocumentValidator.fromDocument(doc);
		validator.setCertificateVerifier(new CommonCertificateVerifier());
		FileDocument detachedDocument = new FileDocument("src/test/resources/sample.png");
		validator.setDetachedContents(Arrays.asList(detachedDocument));
		
		Reports reports = validator.validateDocument();
		// reports.print();
		assertNotNull(reports);
		
		DiagnosticData diagnosticData = reports.getDiagnosticData();
		List<SignatureWrapper> signatures = diagnosticData.getSignatures();
		assertEquals(1, signatures.size());
		
		SignatureWrapper signatureWrapper = signatures.get(0);
		List<XmlSignatureScope> signatureScopes = signatureWrapper.getSignatureScopes();
		assertEquals(3, signatureScopes.size());
		
		int fullScopeCounter = 0;
		int partialScopeCounter = 0;
		for (XmlSignatureScope signatureScope : signatureScopes) {
			if (SignatureScopeType.FULL.equals(signatureScope.getScope())) {
				assertEquals(detachedDocument.getName(), signatureScope.getName());
				fullScopeCounter++;
			} else if (SignatureScopeType.PARTIAL.equals(signatureScope.getScope())) {
				partialScopeCounter++;
			}
		}
		assertEquals(1, fullScopeCounter);
		assertEquals(2, partialScopeCounter);
		List<XmlDigestMatcher> digestMatchers = signatureWrapper.getDigestMatchers();
		assertEquals(4, digestMatchers.size());
		boolean refRootFound = false;
		for (XmlDigestMatcher digestMatcher : digestMatchers) {
			if ("REF-ROOT".equals(digestMatcher.getName())) {
				assertTrue(digestMatcher.isDataFound());
				assertFalse(digestMatcher.isDataIntact());
				refRootFound = true;
			}
		}
		assertTrue(refRootFound);
	}
	
	@Test
	public void nullUriWithMultipleDetachedContentTest() {
		DSSDocument doc = new FileDocument("src/test/resources/validation/dss1770/dss1770refUriRemoved.xml");
		SignedDocumentValidator validator = SignedDocumentValidator.fromDocument(doc);
		validator.setCertificateVerifier(new CommonCertificateVerifier());
		List<DSSDocument> detachedDocuments = new ArrayList<>();
		detachedDocuments.add(new FileDocument("src/test/resources/sample.png"));
		detachedDocuments.add(new FileDocument("src/test/resources/sample.xml"));
		validator.setDetachedContents(detachedDocuments);
		
		Reports reports = validator.validateDocument();
		// reports.print();
		assertNotNull(reports);
		
		DiagnosticData diagnosticData = reports.getDiagnosticData();
		List<SignatureWrapper> signatures = diagnosticData.getSignatures();
		assertEquals(1, signatures.size());
		
		SignatureWrapper signatureWrapper = signatures.get(0);
		List<XmlSignatureScope> signatureScopes = signatureWrapper.getSignatureScopes();
		assertEquals(2, signatureScopes.size());
		
		int fullScopeCounter = 0;
		int partialScopeCounter = 0;
		for (XmlSignatureScope signatureScope : signatureScopes) {
			if (SignatureScopeType.FULL.equals(signatureScope.getScope())) {
				fullScopeCounter++;
			} else if (SignatureScopeType.PARTIAL.equals(signatureScope.getScope())) {
				partialScopeCounter++;
			}
		}
		assertEquals(0, fullScopeCounter);
		assertEquals(2, partialScopeCounter);
		List<XmlDigestMatcher> digestMatchers = signatureWrapper.getDigestMatchers();
		assertEquals(4, digestMatchers.size());
		boolean refRootFound = false;
		for (XmlDigestMatcher digestMatcher : digestMatchers) {
			if ("REF-ROOT".equals(digestMatcher.getName())) {
				assertFalse(digestMatcher.isDataFound());
				assertFalse(digestMatcher.isDataIntact());
				refRootFound = true;
			}
		}
		assertTrue(refRootFound);
	}
	
	@Test
	public void nullUriWithDetachedContentNullNameTest() {
		DSSDocument doc = new FileDocument("src/test/resources/validation/dss1770/dss1770refUriRemoved.xml");
		SignedDocumentValidator validator = SignedDocumentValidator.fromDocument(doc);
		validator.setCertificateVerifier(new CommonCertificateVerifier());
		validator.setDetachedContents(Arrays.asList(new InMemoryDocument(new byte[] {1, 2, 3})));
		
		Reports reports = validator.validateDocument();
		// reports.print();
		assertNotNull(reports);
		
		DiagnosticData diagnosticData = reports.getDiagnosticData();
		List<SignatureWrapper> signatures = diagnosticData.getSignatures();
		assertEquals(1, signatures.size());
		
		SignatureWrapper signatureWrapper = signatures.get(0);
		List<XmlSignatureScope> signatureScopes = signatureWrapper.getSignatureScopes();
		assertEquals(3, signatureScopes.size());
		
		int fullScopeCounter = 0;
		int partialScopeCounter = 0;
		for (XmlSignatureScope signatureScope : signatureScopes) {
			if (SignatureScopeType.FULL.equals(signatureScope.getScope())) {
				assertNull(signatureScope.getName());
				fullScopeCounter++;
			} else if (SignatureScopeType.PARTIAL.equals(signatureScope.getScope())) {
				partialScopeCounter++;
			}
		}
		assertEquals(1, fullScopeCounter);
		assertEquals(2, partialScopeCounter);
		List<XmlDigestMatcher> digestMatchers = signatureWrapper.getDigestMatchers();
		assertEquals(4, digestMatchers.size());
		boolean refRootFound = false;
		for (XmlDigestMatcher digestMatcher : digestMatchers) {
			if ("REF-ROOT".equals(digestMatcher.getName())) {
				assertTrue(digestMatcher.isDataFound());
				assertFalse(digestMatcher.isDataIntact());
				refRootFound = true;
			}
		}
		assertTrue(refRootFound);
	}

}
