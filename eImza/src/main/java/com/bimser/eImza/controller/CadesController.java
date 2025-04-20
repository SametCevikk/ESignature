package com.bimser.eImza.controller;

import java.io.FileInputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Hashtable;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bimser.eImza.CadesSampleBase;
import com.bimser.eImza.requests.CadesSignRequest;
import com.bimser.eImza.responses.CadesSignResponse;
import com.bimser.eImza.SmartCardManager;

import tr.gov.tubitak.uekae.esya.api.asn.x509.ECertificate;
import tr.gov.tubitak.uekae.esya.api.certificate.validation.policy.PolicyReader;
import tr.gov.tubitak.uekae.esya.api.certificate.validation.policy.ValidationPolicy;
import tr.gov.tubitak.uekae.esya.api.cmssignature.ISignable;
import tr.gov.tubitak.uekae.esya.api.cmssignature.SignableByteArray;
import tr.gov.tubitak.uekae.esya.api.cmssignature.attribute.EParameters;
import tr.gov.tubitak.uekae.esya.api.cmssignature.signature.BaseSignedData;
import tr.gov.tubitak.uekae.esya.api.cmssignature.signature.ESignatureType;
import tr.gov.tubitak.uekae.esya.api.cmssignature.validation.SignedDataValidation;
import tr.gov.tubitak.uekae.esya.api.cmssignature.validation.SignedDataValidationResult;
import tr.gov.tubitak.uekae.esya.api.cmssignature.validation.SignedData_Status;
import tr.gov.tubitak.uekae.esya.api.common.crypto.BaseSigner;

@RestController
@RequestMapping("/cades")
public class CadesController extends CadesSampleBase {
    CadesSignResponse response = new CadesSignResponse();

    @PostMapping("/sign")
    public ResponseEntity<CadesSignResponse> sign(@RequestBody CadesSignRequest request) {

        try {

            BaseSignedData baseSignedData = new BaseSignedData();
            ISignable content = new SignableByteArray(request.getFile().getBytes());

            baseSignedData.addContent(content);

            HashMap<String, Object> params = new HashMap<>();

            params.put(EParameters.P_VALIDATE_CERTIFICATE_BEFORE_SIGNING, true);

            params.put(EParameters.P_CERT_VALIDATION_POLICY, getPolicy());

            ECertificate cert = SmartCardManager.getInstance().getSignatureCertificate(isQualified());

            BaseSigner signer = SmartCardManager.getInstance().getSigner(getPin(), cert);

            baseSignedData.addSigner(ESignatureType.TYPE_BES, cert, signer, null, params);

            SmartCardManager.getInstance().logout();

            byte[] signedDocument = baseSignedData.getEncoded();

            String encodedSignedDocument = Base64.getEncoder().encodeToString(signedDocument);

            response.setMassage("İmzalama işlemi başarılı ");
            response.setEncodedSignedDocument(encodedSignedDocument);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.setMassage("İmzalama işlemi sırasında bir hata oluştu  " + e.toString());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);

        }

    }

    @PostMapping("/verify")
    public ResponseEntity<String> verify(@RequestBody CadesSignRequest request) {
        try {
            byte[] signedData = request.getFile().getBytes();
            ValidationPolicy policy = PolicyReader.readValidationPolicy(new FileInputStream(getPolicyFile()));

            Hashtable<String, Object> params = new Hashtable<String, Object>();
            params.put(EParameters.P_CERT_VALIDATION_POLICY, policy);

            SignedDataValidation sdv = new SignedDataValidation();

            SignedDataValidationResult sdvr = sdv.verify(signedData, params);

            if (sdvr.getSDStatus() != SignedData_Status.ALL_VALID) {
                response.setMassage("İmzaların hepsi doğrulanamadı");
                return ResponseEntity.ok(response.getMassage());
            }
            response.setMassage(sdvr.toString());
            return ResponseEntity.ok(response.getMassage());

        } catch (Exception e) {
            e.printStackTrace();
            response.setMassage("İmza doğrulama sırasında bir hata oluştu " + e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response.getMassage());
        }
    }

}
