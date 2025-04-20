package com.bimser.eImza.controller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Base64;
import java.util.Calendar;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bimser.eImza.SmartCardManager;
import com.bimser.eImza.requests.PadesSignRequest;
import com.bimser.eImza.responses.PadesSignResponse;
import com.bimser.eImza.SampleBase;
import tr.gov.tubitak.uekae.esya.api.asn.x509.ECertificate;
import tr.gov.tubitak.uekae.esya.api.common.crypto.BaseSigner;
import tr.gov.tubitak.uekae.esya.api.signature.ContainerValidationResult;
import tr.gov.tubitak.uekae.esya.api.signature.Signature;
import tr.gov.tubitak.uekae.esya.api.signature.SignatureContainer;
import tr.gov.tubitak.uekae.esya.api.signature.SignatureFactory;
import tr.gov.tubitak.uekae.esya.api.signature.SignatureFormat;
import tr.gov.tubitak.uekae.esya.api.signature.config.Config;
import tr.gov.tubitak.uekae.esya.api.pades.pdfbox.PAdESContext;

@RestController
@RequestMapping("/pades")
public class PadesController extends SampleBase {
    PadesSignResponse response = new PadesSignResponse();

    public PAdESContext createContext() {

        PAdESContext c = new PAdESContext(new File(getTestDataFolder()).toURI());

        c.setConfig(new Config(getRootDir() + "/config/esya-signature-config.xml"));

        return c;
    }

    @PostMapping("/sign")
    public ResponseEntity<PadesSignResponse> signn(@RequestBody PadesSignRequest request) {

        try {

            SignatureContainer signatureContainer = SignatureFactory.readContainer(SignatureFormat.PAdES,
                    new FileInputStream(request.getFilePath()), createContext());

            ECertificate eCertificate = SmartCardManager.getInstance().getSignatureCertificate(isQualified());
            BaseSigner signer = SmartCardManager.getInstance().getSigner(getPin(), eCertificate);

            Signature signature = signatureContainer.createSignature(eCertificate);
            signature.setSigningTime(Calendar.getInstance());
            signature.sign(signer);

            signatureContainer.write(new FileOutputStream(getTestDataFolder() + "signed-bes.pdf"));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            signatureContainer.write(baos);
            byte[] signedData = baos.toByteArray();
            String encodedSignedData = Base64.getEncoder().encodeToString(signedData);
            response.setMassage("İmzalama işlemi başarılı");
            response.setEncodedSignedDocument(encodedSignedData);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            response.setMassage("İmzalama işlemi sırasında bir hata oluştu  " + e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verify(@RequestBody PadesSignRequest request) {
        try {

            SignatureContainer pc = SignatureFactory.readContainer(SignatureFormat.PAdES,
                    new FileInputStream(request.getFilePath()), createContext());

            ContainerValidationResult cvr = pc.verifyAll();
            response.setMassage("Doğrulama işlemi başarılı   " + cvr);

            return ResponseEntity.ok(response.getMassage());
        } catch (Exception e) {
            e.printStackTrace();
            response.setMassage("Doğrulama işlemi sırasında bir hata oluştu   " + e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response.getMassage());
        }
    }
}
