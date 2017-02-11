package com.muk.examples.pdf.pdfbox;

import static com.muk.examples.pdf.pdfbox.MedCertTemplate.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class MedCertTemplateTest {

  @Test
  public void generateMedicaidCertificate() throws IOException {
    Map<String, String> parameters = new HashMap<>();
    parameters.put(LF_TRANSACTION_ID, "LLAP12345");
    parameters.put(LF_MEM_FIRST_NAME, "Jane");
    parameters.put(LF_MEM_LAST_NAME, "Smith");
    parameters.put(LF_MEM_DOB, "1976-01-01");
    
    parameters.put(LF_PAYER_NAME, "UNITEDHEALTHCARE");
    parameters.put(LF_PLAN_TYPE, "Medicaid");
    parameters.put(LF_PLAN_DESC, "MI UHC MEDICAID");
    parameters.put(LF_GROUP_DESC, "");
    parameters.put(LF_COVERAGE_STATUS, "ACTIVE");
    
    parameters.put(LF_DATETIME, "2017-03-23 19:08:30");
    parameters.put(LF_ACTIVITY_ID, "877fa074c16711e6ba460242ac1100");
    
    MedCertTemplate rptTemplate = new MedCertTemplate(parameters);
    
    rptTemplate.save("target/Med_Cert.pdf");
    
    rptTemplate.close();
  }
}
