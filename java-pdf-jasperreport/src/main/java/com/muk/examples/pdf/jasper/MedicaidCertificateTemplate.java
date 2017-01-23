package com.muk.examples.pdf.jasper;

import java.util.Map;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.SimpleJasperReportsContext;
import net.sf.jasperreports.engine.design.JRCompiler;

public class MedicaidCertificateTemplate {
  
  private static final String JRXML_FILE = "MedicaidCertificate.jrxml";
  private static JasperReport JASPER_REPORT;
  
  private Map<String, Object> parameters;

  public MedicaidCertificateTemplate(Map<String, Object> params) {
    this.parameters = params;
    init();
  }
  
  public void save(String fileName) {
    try {
      JasperExportManager.exportReportToPdfFile(generateReport(), fileName);
      JasperExportManager.exportReportToHtmlFile(generateReport(), "target/report.html");
    } catch (JRException e) {
      throw new RuntimeException("Error in JasperExportManager", e);
    }
  }
  
  private JasperPrint generateReport() {
    try {
      return JasperFillManager.fillReport(JASPER_REPORT, parameters, new JREmptyDataSource());
    } catch (JRException e) {
      throw new RuntimeException("Error in JasperFillManager", e);
    }
  }
  
  private static synchronized void init() {
    if (JASPER_REPORT == null) {
      ClassLoader cl = MedicaidCertificateTemplate.class.getClassLoader();
      try {
        SimpleJasperReportsContext context = new SimpleJasperReportsContext();
        context.setProperty(JRCompiler.COMPILER_TEMP_DIR, "target");
        // context.setProperty(JRCompiler.COMPILER_KEEP_JAVA_FILE, "true");
        JasperCompileManager reportCompiler = JasperCompileManager.getInstance(context);
        JASPER_REPORT = reportCompiler.compile(cl.getResourceAsStream(JRXML_FILE));
      } catch (JRException e) {
        throw new RuntimeException("Error in JasperCompileManager", e);
      }
    }
  }
}
