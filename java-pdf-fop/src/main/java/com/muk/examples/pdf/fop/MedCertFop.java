package com.muk.examples.pdf.fop;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.FormattingResults;
import org.apache.fop.apps.MimeConstants;
import org.apache.fop.apps.PageSequenceResults;

public class MedCertFop {

  private FopFactory fopFactory;
  private TransformerFactory transformerFactory;
  
  public MedCertFop() {
    fopFactory = FopFactory.newInstance(new File(".").toURI());
    transformerFactory = TransformerFactory.newInstance();
  }
  
  public void save(File outFile) throws Exception {
    FileOutputStream fos = new FileOutputStream(outFile);
    BufferedOutputStream baos = new BufferedOutputStream(fos);
    save(baos);
    baos.flush();
    baos.close();
  }
  
  public void save(String fileName) throws Exception {
    save(new File(fileName));
  }
  
  public void save(OutputStream out) throws Exception {
    FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
    
    Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, out);
    
    Transformer transformer = transformerFactory.newTransformer(); // identity transformer
    
    InputStream fo = MedCertFop.class.getResourceAsStream("/MedCert.fo");
    Source src = new StreamSource(fo);
    
    Result res = new SAXResult(fop.getDefaultHandler());
    
    transformer.transform(src, res);
    
    FormattingResults foResults = fop.getResults();
    List<?> pageSequences = foResults.getPageSequences();
    for (Iterator<?> it = pageSequences.iterator(); it.hasNext();) {
        PageSequenceResults pageSequenceResults = (PageSequenceResults)it.next();
        System.out.println("PageSequence "
                + (String.valueOf(pageSequenceResults.getID()).length() > 0
                        ? pageSequenceResults.getID() : "<no id>")
                + " generated " + pageSequenceResults.getPageCount() + " pages.");
    }
    System.out.println("Generated " + foResults.getPageCount() + " pages in total.");
    
    fo.close();
  }
  
}
