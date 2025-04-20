package com.bimser.eImza;

import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.gov.tubitak.uekae.esya.api.common.util.LicenseUtil;
import tr.gov.tubitak.uekae.esya.api.common.util.VersionUtil;


public class SampleBase {
    
  protected static Logger logger = LoggerFactory.getLogger(SampleBase.class);

   
    private static String ROOT_DIR = "C:/Bimser/eImza";

    
    private static final boolean IS_QUALIFIED = true;

    private static final String PIN_SMARTCARD = "12345";
    
    private static String testDataFolder = getRootDir() + "/testdata";
    private static String policyFile = getRootDir() + "/config/certval-policy.xml";
   
  

    static {

        try {

           

            LicenseUtil.setLicenseXml(new FileInputStream(ROOT_DIR + "/lisans/lisans.xml"));
            Date expirationDate = LicenseUtil.getExpirationDate();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            System.out.println("License expiration date :" + dateFormat.format(expirationDate));
            System.out.println("MA3 API version: " + VersionUtil.getAPIVersion());

        
        
        } catch (Exception e) {
            logger.error("Error in SampleBase", e);
        }
    }

    /**
     * Gets the bundle root directory of project
     *
     * @return the root dir
     */
    protected static String getRootDir() {
        return ROOT_DIR;
    }

    protected static String getTestDataFolder(){
        return testDataFolder;
    }

    protected static String getPolicyFile(){
        return policyFile;
    }

   protected static String getPin(){
    return PIN_SMARTCARD;
   }
        
    /**
     * The parameter to choose the qualified certificates in smart card
     *
     * @return the
     */
    protected static boolean isQualified() {
        return IS_QUALIFIED;
    }
}
