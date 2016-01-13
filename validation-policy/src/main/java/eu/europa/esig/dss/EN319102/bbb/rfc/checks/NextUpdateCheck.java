package eu.europa.esig.dss.EN319102.bbb.rfc.checks;

import java.util.Date;

import eu.europa.esig.dss.EN319102.bbb.ChainItem;
import eu.europa.esig.dss.jaxb.detailedreport.XmlRFC;
import eu.europa.esig.dss.validation.RevocationWrapper;
import eu.europa.esig.dss.validation.policy.rules.Indication;
import eu.europa.esig.dss.validation.policy.rules.MessageTag;
import eu.europa.esig.dss.validation.policy.rules.SubIndication;
import eu.europa.esig.jaxb.policy.TimeConstraint;

public class NextUpdateCheck extends ChainItem<XmlRFC> {

	private final RevocationWrapper revocationData;
	private final Date validationDate;

	public NextUpdateCheck(XmlRFC result, RevocationWrapper revocationData, Date validationDate, TimeConstraint constraint) {
		super(result, constraint);

		this.revocationData = revocationData;
		this.validationDate = validationDate;
	}

	@Override
	protected boolean process() {
		if (revocationData != null) {
			Date nextUpdate = revocationData.getNextUpdate();

			if (nextUpdate == null) {
				return false;
			}

			if (validationDate.after(nextUpdate)) {
				return false;
			}

			return true;
		}
		return false;
	}

	@Override
	protected MessageTag getMessageTag() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected MessageTag getErrorMessageTag() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Indication getFailedIndicationForConclusion() {
		return Indication.INVALID;
	}

	@Override
	protected SubIndication getFailedSubIndicationForConclusion() {
		return null;
	}

}
