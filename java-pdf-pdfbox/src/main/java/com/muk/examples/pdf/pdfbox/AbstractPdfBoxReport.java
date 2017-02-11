package com.muk.examples.pdf.pdfbox;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.graphics.image.CCITTFactory;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

// @ThreadUnsafe
public abstract class AbstractPdfBoxReport implements AutoCloseable {
  private PDDocument doc;

  protected abstract PDDocument generatePDDocument();

  private PDDocument getPDDocument() {
    if (doc == null) {
      this.doc = generatePDDocument();
    }
    return this.doc;
  }

  public void save(OutputStream ostream) throws IOException {
    PDDocument pdf = getPDDocument();
    pdf.save(ostream);
  }
  
  public void save(File file) throws IOException {
    PDDocument pdf = getPDDocument();
    pdf.save(file);
  }
  
  public void save(String fileName) throws IOException {
    this.save(new File(fileName));
  }

  public String getContentType() {
    return "application/pdf";
  }

  public int hintByteSize() {
    return 1024;
  }

  @Override
  public void close() {
    if (doc != null) {
      try {
        doc.close();
      } catch (Exception ignore) {
      }
    }
  }
  
  protected static PDImageXObject imageObject(BufferedImage awtImage, String resourceName, PDDocument doc) 
      throws IOException {
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
  
  protected static BufferedImage readBufferedImage(String resourceName) throws IOException {
    InputStream imageStream = null;
    try {
      imageStream = AbstractPdfBoxReport.class.getResourceAsStream(resourceName);
      return ImageIO.read(imageStream);
    } finally {
      try {
        if (imageStream != null) {
          imageStream.close();
        }
      } catch (IOException e) {}
    }
  }
  
  protected static BufferedImage readBufferedImageUnchecked(String resourceName) {
    try {
      return readBufferedImage(resourceName);
    } catch(Exception ignore) {
      return null;
    }
  }

  protected static class BoxParams {
    public float x;
    public float y;
    public float width;
    public float height;
    public float topMargin;
    public float bottomMargin;
    
    public BoxParams() {}
  }

}
