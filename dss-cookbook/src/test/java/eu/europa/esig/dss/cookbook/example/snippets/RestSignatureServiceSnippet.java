package eu.europa.esig.dss.cookbook.example.snippets;

import java.io.File;

import eu.europa.esig.dss.cookbook.example.CookbookTools;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.enumerations.SignatureLevel;
import eu.europa.esig.dss.enumerations.SignaturePackaging;
import eu.europa.esig.dss.model.FileDocument;
import eu.europa.esig.dss.model.SignatureValue;
import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.token.SignatureTokenConnection;
import eu.europa.esig.dss.utils.Utils;
import eu.europa.esig.dss.ws.converter.DTOConverter;
import eu.europa.esig.dss.ws.dto.RemoteCertificate;
import eu.europa.esig.dss.ws.dto.RemoteDocument;
import eu.europa.esig.dss.ws.dto.SignatureValueDTO;
import eu.europa.esig.dss.ws.dto.ToBeSignedDTO;
import eu.europa.esig.dss.ws.signature.dto.DataToSignOneDocumentDTO;
import eu.europa.esig.dss.ws.signature.dto.ExtendDocumentDTO;
import eu.europa.esig.dss.ws.signature.dto.SignOneDocumentDTO;
import eu.europa.esig.dss.ws.signature.dto.TimestampOneDocumentDTO;
import eu.europa.esig.dss.ws.signature.dto.parameters.RemoteSignatureParameters;
import eu.europa.esig.dss.ws.signature.dto.parameters.RemoteTimestampParameters;
import eu.europa.esig.dss.ws.signature.rest.RestDocumentSignatureServiceImpl;
import eu.europa.esig.dss.ws.signature.rest.client.RestDocumentSignatureService;

public class RestSignatureServiceSnippet extends CookbookTools {
	
	@SuppressWarnings("unused")
	public void demo() throws Exception {

		try (SignatureTokenConnection signingToken = getPkcs12Token()) {

			DSSPrivateKeyEntry privateKey = signingToken.getKeys().get(0);
			
			// tag::demo[]
			
			// Initializes the rest client
			RestDocumentSignatureService restClient = new RestDocumentSignatureServiceImpl();
			
			// Defines RemoteSignatureParameters
			RemoteSignatureParameters parameters = new RemoteSignatureParameters();
			parameters.setSignatureLevel(SignatureLevel.PAdES_BASELINE_B);
			parameters.setSigningCertificate(new RemoteCertificate(privateKey.getCertificate().getEncoded()));
			parameters.setSignaturePackaging(SignaturePackaging.ENVELOPING);
			parameters.setDigestAlgorithm(DigestAlgorithm.SHA256);
			
			// Initialize a RemoteDocument object to be signed
			FileDocument fileToSign = new FileDocument(new File("src/test/resources/sample.xml"));
			RemoteDocument toSignDocument = new RemoteDocument(Utils.toByteArray(fileToSign.openStream()), fileToSign.getName());
			
			// computes the digest to be signed
			ToBeSignedDTO dataToSign = restClient.getDataToSign(new DataToSignOneDocumentDTO(toSignDocument, parameters));
	
			// Creates a SignOneDocumentDTO
			SignatureValue signatureValue = signingToken.sign(DTOConverter.toToBeSigned(dataToSign), DigestAlgorithm.SHA256, privateKey);
			SignOneDocumentDTO signDocument = new SignOneDocumentDTO(toSignDocument, parameters,
					new SignatureValueDTO(signatureValue.getAlgorithm(), signatureValue.getValue()));
			
			// Adds the signature value to the document
			RemoteDocument signedDocument = restClient.signDocument(signDocument);
	
			// Define the extention parameters
			RemoteSignatureParameters extendParameters = new RemoteSignatureParameters();
			extendParameters.setSignatureLevel(SignatureLevel.PAdES_BASELINE_T);
	
			// Extends the existing signature
			RemoteDocument extendedDocument = restClient.extendDocument(new ExtendDocumentDTO(signedDocument, extendParameters));
			
			// Defines timestamp parameters
			RemoteTimestampParameters remoteTimestampParameters = new RemoteTimestampParameters();
			remoteTimestampParameters.setDigestAlgorithm(DigestAlgorithm.SHA256);
			
			// Defines a Timestamp document DTO
			TimestampOneDocumentDTO timestampOneDocumentDTO = new TimestampOneDocumentDTO(extendedDocument, remoteTimestampParameters);
			
			// Timestamps a provided document (available for PDF, ASiC-E and ASiC-S container formats)
			RemoteDocument timestampedDocument = restClient.timestampDocument(timestampOneDocumentDTO);
			
			// end::demo[]
		}
		
	}

}
