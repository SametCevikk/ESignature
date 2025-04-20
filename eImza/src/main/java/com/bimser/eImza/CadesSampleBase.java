package com.bimser.eImza;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import tr.gov.tubitak.uekae.esya.api.certificate.validation.policy.PolicyReader;
import tr.gov.tubitak.uekae.esya.api.certificate.validation.policy.ValidationPolicy;
import tr.gov.tubitak.uekae.esya.api.common.ESYAException;
import tr.gov.tubitak.uekae.esya.api.common.ESYARuntimeException;

public class CadesSampleBase extends SampleBase {

    private static ValidationPolicy validationPolicy;

    public synchronized ValidationPolicy getPolicy() throws ESYAException {

        if (validationPolicy == null) {
            try {
                validationPolicy = PolicyReader.readValidationPolicy(new FileInputStream(getPolicyFile()));
            } catch (FileNotFoundException e) {
                throw new ESYARuntimeException("Policy dosyası bulunamıyor", e);
            }
        }
        return validationPolicy;
    }

}
