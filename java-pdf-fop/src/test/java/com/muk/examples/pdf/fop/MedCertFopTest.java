package com.muk.examples.pdf.fop;

import org.junit.Test;

public class MedCertFopTest {

  @Test
  public void testReport() throws Exception {
    MedCertFop report = new MedCertFop();
    report.save("target/MedCertFop.pdf");
  }
}
