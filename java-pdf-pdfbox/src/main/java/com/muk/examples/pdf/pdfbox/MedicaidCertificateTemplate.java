package com.muk.examples.pdf.pdfbox;

import java.awt.Color;
import java.io.IOException;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.Cell;
import be.quodlibet.boxable.HorizontalAlignment;
import be.quodlibet.boxable.Row;
import be.quodlibet.boxable.Table;
import be.quodlibet.boxable.VerticalAlignment;

public class MedicaidCertificateTemplate {
  
  public static final String PARAM_LF_TRANSACTION_ID = "LF_TRANSACTION_ID";
  public static final String PARAM_LF_MEM_FIRST_NAME = "LF_MEM_FIRST_NAME";
  public static final String PARAM_LF_MEM_LAST_NAME = "LF_MEM_LAST_NAME";
  public static final String PARAM_LF_MEM_DOB = "LF_MEM_DOB";
  
  public static final String PARAM_LF_PAYER_NAME = "LF_PAYER_NAME";
  public static final String PARAM_LF_PLAN_TYPE = "LF_PLAN_TYPE";
  public static final String PARAM_LF_PLAN_DESC = "LF_PLAN_DESC";
  public static final String PARAM_LF_GROUP_DESC = "LF_GROUP_DESC";
  public static final String PARAM_LF_COVERAGE_STATUS = "LF_COVERAGE_STATUS";
  
  public static final String PARAM_LF_DATETIME = "LF_DATETIME";
  public static final String PARAM_LF_ACTIVITY_ID = "LF_ACTIVITY_ID";

  private Map<String, String> parameters;
  
  public MedicaidCertificateTemplate(Map<String, String> parameters) {
    this.parameters = parameters;
  }
  
  public void save(String fileName) throws IOException {
    try(
        PDDocument doc = generateReport();
    ) {
      doc.save(fileName);
    }
  }
  
  private PDDocument generateReport() throws IOException {
    PDDocument doc = new PDDocument();
    PDPage page = new PDPage();
    doc.addPage(page);
    
    page.setMediaBox(new PDRectangle(PDRectangle.A4.getWidth(), PDRectangle.A4.getHeight()));
    
    float tableTopMargin = 20;
    float tableLeftMargin = 80;
    float pageWidth = page.getMediaBox().getWidth();
    float pageHeight = page.getMediaBox().getHeight();
    
    float tableWidth = pageWidth - (2 * tableLeftMargin);
    float yNewPage = pageHeight - tableTopMargin;
    float yStart = yNewPage;
    
    float bottomMargin = 0;
    boolean drawLines = true;
    boolean drawContent = true;
    
    BaseTable table = new BaseTable(yStart, yNewPage, bottomMargin, tableWidth, tableLeftMargin, doc, page, drawLines, 
        drawContent);
    
    createTableHeader(table, "CERTIFICATE OF RECEIPT OF MEDICAID BENEFITS");    
    
    createDetailRow(table, "Lifeline Application Transaction ID", data(PARAM_LF_TRANSACTION_ID));
    
    createSectionHeader(table, "Member Identifiers");
    createDetailRow(table, "Member’s First Name", data(PARAM_LF_MEM_FIRST_NAME));
    createDetailRow(table, "Member’s Last Name", data(PARAM_LF_MEM_LAST_NAME));
    createDetailRow(table, "Member’s DOB", data(PARAM_LF_MEM_DOB));
    
    createSectionHeader(table, "Insurance Plan Coverage");
    createDetailRow(table, "Insurance Payer Name", data(PARAM_LF_PAYER_NAME));
    createDetailRow(table, "Insurance Plan Type", data(PARAM_LF_PLAN_TYPE));
    createDetailRow(table, "Insurance Plan Description", data(PARAM_LF_PLAN_DESC));
    createDetailRow(table, "Insurance Plan Group Description", data(PARAM_LF_GROUP_DESC));
    createDetailRow(table, "Insurance Coverage Status", data(PARAM_LF_COVERAGE_STATUS));
    
    createSectionHeader(table, "X12 Eligibility Transaction");
    createDetailRow(table, "Date-Time of x12 270/271 Transaction", data(PARAM_LF_DATETIME));
    createDetailRow(table, "X12 Activity ID", data(PARAM_LF_ACTIVITY_ID));
    
    table.draw();
    
    return doc;
  }
  
  private String data(String paramName) {
    return this.parameters.getOrDefault(paramName, "");
  }
  
  private Row<PDPage> createTableHeader(Table<PDPage> table, String title) {
    Row<PDPage> row = table.createRow(0f);
    Cell<PDPage> cell = row.createCell(100f, "CERTIFICATE OF RECEIPT OF MEDICAID BENEFITS", HorizontalAlignment.CENTER, 
        VerticalAlignment.BOTTOM);
    cell.setFont(PDType1Font.HELVETICA_BOLD);
    cell.setFontSize(14);
    cell.setFillColor(new Color(144, 195, 212));
    return row;
  }
  
  private Row<PDPage> createSectionHeader(Table<PDPage> table, String title) {
    Row<PDPage> row = table.createRow(0f);
    Cell<PDPage> cell = row.createCell(100f, title, HorizontalAlignment.CENTER, 
        VerticalAlignment.BOTTOM);
    //cell.setTextRotated(true);
    cell.setFont(PDType1Font.HELVETICA_BOLD);
    //cell.setFontSize(8);
    cell.setFillColor(new Color(175, 212, 224));
    return row;
  }
  
  private Row<PDPage> createDetailRow(Table<PDPage> table, String left, String right) {
    Row<PDPage> row = table.createRow(0f);
    Cell<PDPage> cell = row.createCell(50f, left, HorizontalAlignment.LEFT, 
        VerticalAlignment.BOTTOM);
    cell.setFont(PDType1Font.HELVETICA_BOLD);
    //cell.setFontSize(8);
    
    Cell<PDPage> cell2 = row.createCell(50f, right, HorizontalAlignment.LEFT, 
        VerticalAlignment.BOTTOM);
    cell2.setFont(PDType1Font.HELVETICA);
    //cell2.setFontSize(8);

    return row;
  }
}
