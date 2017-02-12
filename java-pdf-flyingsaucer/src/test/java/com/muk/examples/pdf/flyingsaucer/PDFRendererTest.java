package com.muk.examples.pdf.flyingsaucer;

import org.junit.Test;
import org.xhtmlrenderer.simple.PDFRenderer;

public class PDFRendererTest {

  @Test
  public void testRender2PDF() throws Exception {
    PDFRenderer.renderToPDF(
        this.getClass().getResource("/MedCertFS.xhtml").toString(), "target/MedCertFS.pdf");
  }

}
