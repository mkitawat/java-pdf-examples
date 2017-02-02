package com.muk.examples.pdf.pdfbox;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.CCITTFactory;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.Cell;
import be.quodlibet.boxable.HorizontalAlignment;
import be.quodlibet.boxable.Row;
import be.quodlibet.boxable.Table;
import be.quodlibet.boxable.VerticalAlignment;
import be.quodlibet.boxable.line.LineStyle;
import be.quodlibet.boxable.utils.FontUtils;

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
    PDDocument doc = null;
    try {
      doc = generateReport();
      doc.save(fileName);
    } finally {
      if (doc != null) doc.close();
    }
  }
  
  private PDDocument generateReport() throws IOException {
    PDDocument doc = new PDDocument();
    PDPage page = new PDPage();
    doc.addPage(page);
    PDImageXObject headerImage = imageObject("/wp_logo_blue.png", doc);
    page.setMediaBox(new PDRectangle(PDRectangle.A4.getWidth(), PDRectangle.A4.getHeight()));
    
    float pageWidth = page.getMediaBox().getWidth();
    float pageHeight = page.getMediaBox().getHeight();
    
    System.out.printf("pageWidth=%.2f, pageHeight=%.2f%n", pageWidth, pageHeight);
    
    float imageTopMargin = 20;
    float imageHeight = 60;
    float imageWidth = headerImage.getWidth() * (imageHeight / headerImage.getHeight());
    float imageX = (pageWidth - imageWidth) / 2;
    float imageY = pageHeight - imageTopMargin - imageHeight;
    
    System.out.printf("iw=%.2f, ih=%.2f, ix=%.2f, iy=%.2f, itop=%.2f%n", imageWidth, imageHeight, imageX, imageY, imageTopMargin);
    
    PDPageContentStream contentStream = new PDPageContentStream(doc, page);
    contentStream.drawImage(headerImage, imageX, imageY, imageWidth, imageHeight);
    contentStream.close();
    
    float tableTopMargin = 20;
    float tableLeftMargin = 80;
    float tableWidth = pageWidth - (2 * tableLeftMargin);
    float yNewPage = pageHeight - tableTopMargin;
    float yStart = yNewPage - imageTopMargin - imageHeight;
    
    System.out.printf("tw=%.2f, th=?, tx=%.2f, ty=%.2f, ttop=%.2f%n", tableWidth, tableLeftMargin, yStart, tableTopMargin);
    
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
    createEmptyBorderlessFooterRow(table);
    createEmptyFooterRowWithBottomBorder(table);
    createFooterRow(table, "This information was obtained by Wellpass for purposes of verifying the applicant's/dependent’s Medicaid coverage and this document will be submitted to <Lifeline Carrier Name> as proof of eligibility for Lifeline service.");
    
    float tableEndY = table.draw();
    
    System.out.printf("footerY=%.2f%n", tableEndY);
    
    drawFooter(tableEndY, 
        pageWidth, 
        new String[] {"This information was obtained by Wellpass for purposes of verifying the applicant's/dependent’s Medicaid coverage and",
      "this document will be submitted to <Lifeline Carrier Name> as proof of eligibility for Lifeline service."},
        page, doc);
    
    return doc;
  }
  
  private String data(String paramName) {
    return this.parameters.getOrDefault(paramName, "");
  }
  
  private void drawFooter(float tableEndY, float pageWidth, String[] text, PDPage page, PDDocument doc) throws IOException {
    PDPageContentStream stream = new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, true);
    
    float lineStartX = 80;
    float lineStartY = tableEndY - 20;
    float lineEndX = pageWidth - lineStartX;
    float lineEndY = lineStartY;
    
    Color lineColor = new Color(224, 224, 224);
    stream.setNonStrokingColor(lineColor);
    stream.setStrokingColor(lineColor);
    stream.setLineWidth(1);
    stream.setLineCapStyle(0);
    //stream.setLineDashPattern(new float[] {}, 0f);
    stream.setLineDashPattern(new float[] {}, 0.0f);
    stream.moveTo(lineStartX, lineStartY);
    stream.lineTo(lineEndX, lineEndY);
    stream.stroke();
    stream.closePath();

    stream.setNonStrokingColor(Color.BLACK);
    stream.setFont(PDType1Font.HELVETICA, 8);
    float fontHeight = FontUtils.getHeight(PDType1Font.HELVETICA, 8);
    float textStartX = lineStartX;
    float textStartY = lineStartY - 5 - fontHeight;
    for (String line : text) {
      stream.beginText();
      stream.newLineAtOffset(textStartX, textStartY);
      stream.showText(line);
      stream.endText();
      stream.closePath();
      textStartY -= fontHeight;
    }
    stream.close();
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
  
  private Row<PDPage> createEmptyBorderlessFooterRow(Table<PDPage> table) {
    Row<PDPage> row = table.createRow(0f);
    Cell<PDPage> cell = row.createCell(100f, "", HorizontalAlignment.CENTER, 
        VerticalAlignment.BOTTOM);
    cell.setBorderStyle(null);
    return row;
  }
  
  private Row<PDPage> createEmptyFooterRowWithBottomBorder(Table<PDPage> table) {
    Row<PDPage> row = table.createRow(0f);
    Cell<PDPage> cell = row.createCell(100f, "", HorizontalAlignment.CENTER, 
        VerticalAlignment.BOTTOM);
    cell.setBorderStyle(null);
    cell.setBottomBorderStyle(new LineStyle(new Color(224, 224, 224), 1));
    return row;
  }
  
  private Row<PDPage> createFooterRow(Table<PDPage> table, String text) {
    Row<PDPage> row = table.createRow(0f);
    Cell<PDPage> cell = row.createCell(100f, text, HorizontalAlignment.LEFT, 
        VerticalAlignment.TOP);
    cell.setBorderStyle(null);
    cell.setFont(PDType1Font.HELVETICA);
    cell.setFontSize(8);
    cell.setLeftPadding(2);
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
  
  public PDImageXObject imageObject(String resourceName, PDDocument doc) throws IOException {
    BufferedImage awtImage = getBufferedImage(resourceName);
    String lcaseResNm = resourceName.toLowerCase();
    if (lcaseResNm.endsWith(".png")) {
      return LosslessFactory.createFromImage(doc, awtImage);
    } else if (lcaseResNm.endsWith("jpg") || lcaseResNm.endsWith("jpeg")) {
      return JPEGFactory.createFromImage(doc, awtImage);
    } else if (lcaseResNm.endsWith("tif") || lcaseResNm.endsWith("tiff")) {
      return CCITTFactory.createFromImage(doc, awtImage);
    } else {
      return LosslessFactory.createFromImage(doc, awtImage);
    }
  }
  
  private BufferedImage getBufferedImage(String resourceName) {
    return awtImages.computeIfAbsent(resourceName, this::readBufferedImage);
  }
  
  private BufferedImage readBufferedImage(String resourceName) {
    InputStream imageStream = this.getClass().getResourceAsStream(resourceName);
    try {
      return ImageIO.read(imageStream);
    } catch(IOException ex) {
      throw new RuntimeException(ex);
    } finally {
      try {
        imageStream.close();
      } catch (IOException e) {}
    }
  }
  
  private Map<String, BufferedImage> awtImages = new ConcurrentHashMap<>();
}
