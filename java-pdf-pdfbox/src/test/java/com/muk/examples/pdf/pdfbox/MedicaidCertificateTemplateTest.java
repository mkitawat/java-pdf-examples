package com.muk.examples.pdf.pdfbox;

import static com.muk.examples.pdf.pdfbox.MedicaidCertificateTemplate.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class MedicaidCertificateTemplateTest {

  @Test
  public void generateMedicaidCertificate() throws IOException {
    Map<String, String> parameters = new HashMap<>();
    parameters.put(PARAM_LF_TRANSACTION_ID, "LLAP12345");
    parameters.put(PARAM_LF_MEM_FIRST_NAME, "Jane");
    parameters.put(PARAM_LF_MEM_LAST_NAME, "Smith");
    parameters.put(PARAM_LF_MEM_DOB, "1976-01-01");
    
    parameters.put(PARAM_LF_PAYER_NAME, "UNITEDHEALTHCARE");
    parameters.put(PARAM_LF_PLAN_TYPE, "Medicaid");
    parameters.put(PARAM_LF_PLAN_DESC, "MI UHC MEDICAID");
    parameters.put(PARAM_LF_GROUP_DESC, "");
    parameters.put(PARAM_LF_COVERAGE_STATUS, "ACTIVE");
    
    parameters.put(PARAM_LF_DATETIME, "2017-03-23 19:08:30");
    parameters.put(PARAM_LF_ACTIVITY_ID, "877fa074c16711e6ba460242ac1100");
    
    MedicaidCertificateTemplate rptTemplate = new MedicaidCertificateTemplate(parameters);
    
    rptTemplate.save("target/Medicaid_Participation_Certificate.pdf");
    //rptTemplate.save("target/Medicaid_Participation_Certificate_01.pdf");
    //rptTemplate.save("target/Medicaid_Participation_Certificate_02.pdf");
    
  }
}
