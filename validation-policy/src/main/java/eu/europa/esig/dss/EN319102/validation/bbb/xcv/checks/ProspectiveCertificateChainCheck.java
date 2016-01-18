package eu.europa.esig.dss.EN319102.validation.bbb.xcv.checks;

import eu.europa.esig.dss.MessageTag;
import eu.europa.esig.dss.EN319102.validation.ChainItem;
import eu.europa.esig.dss.EN319102.wrappers.CertificateWrapper;
import eu.europa.esig.dss.EN319102.wrappers.DiagnosticData;
import eu.europa.esig.dss.jaxb.detailedreport.XmlXCV;
import eu.europa.esig.dss.validation.policy.rules.Indication;
import eu.europa.esig.dss.validation.policy.rules.SubIndication;
import eu.europa.esig.jaxb.policy.LevelConstraint;

public class ProspectiveCertificateChainCheck extends ChainItem<XmlXCV> {

	private final CertificateWrapper certificate;
	private final DiagnosticData diagnosticData;

	public ProspectiveCertificateChainCheck(XmlXCV result, CertificateWrapper certificate, DiagnosticData diagnosticData, LevelConstraint constraint) {
		super(result, constraint);
		this.certificate = certificate;
		this.diagnosticData = diagnosticData;
	}

	@Override
	protected boolean process() {
		if (certificate.isTrusted()) {
			return true;
		}
		String lastChainCertId = certificate.getLastChainCertificateId();
		final CertificateWrapper lastChainCertificate = diagnosticData.getUsedCertificateByIdNullSafe(lastChainCertId);
		return lastChainCertificate.isTrusted();
	}

	@Override
	protected MessageTag getMessageTag() {
		return MessageTag.BBB_XCV_CCCBB;
	}

	@Override
	protected MessageTag getErrorMessageTag() {
		return MessageTag.BBB_XCV_CCCBB_ANS;
	}

	@Override
	protected Indication getFailedIndicationForConclusion() {
		return Indication.INDETERMINATE;
	}

	@Override
	protected SubIndication getFailedSubIndicationForConclusion() {
		return SubIndication.NO_CERTIFICATE_CHAIN_FOUND;
	}

}
