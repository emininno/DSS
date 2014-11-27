/*     */ package it.cyberdyne.dss.routing.io.reader;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import javax.xml.parsers.DocumentBuilder;
/*     */ import javax.xml.parsers.DocumentBuilderFactory;
/*     */ import javax.xml.parsers.ParserConfigurationException;
/*     */ import org.w3c.dom.Document;
/*     */ import org.xml.sax.SAXException;
/*     */ 
/*     */ public class XMLReader
/*     */ {
/*     */   public static final String FILE_EXTENSION = ".xml";
/*     */   protected static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
/*     */   protected static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
/*     */   protected static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
/*     */   protected int m_callId;
/*  43 */   protected Document m_document = null;
/*     */   protected ArrayList m_list;
/*     */   protected String m_filepath;
/*     */ 
/*     */   public XMLReader(int call_id)
/*     */   {
/*  53 */     this.m_callId = call_id;
/*     */   }
/*     */ 
/*     */   public XMLReader(ArrayList list)
/*     */   {
/*  59 */     if ((list != null) && (!list.isEmpty()))
/*  60 */       this.m_list = new ArrayList(list);
/*     */   }
/*     */ 
/*     */   public void read()
/*     */     throws Exception
/*     */   {
/*     */   }
/*     */ 
/*     */   protected Document parse(File xmlFile, InputStream schemaStream)
/*     */     throws Exception
/*     */   {
/*  88 */     this.m_document = null;
/*     */     try
/*     */     {
/*  91 */       DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
/*     */ 
/*  93 */       boolean validate = false;
/*  94 */       if (schemaStream != null) {
/*  95 */         System.out.println("DEBUG: parse - attivata validazione");
/*  96 */         factory.setValidating(true);
/*  97 */         factory.setNamespaceAware(true);
/*  98 */         factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
/*  99 */         factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaSource", schemaStream);
/* 100 */         validate = true;
/*     */       }
/*     */       else {
/* 103 */         factory.setValidating(false);
/*     */       }
/* 105 */       DocumentBuilder builder = factory.newDocumentBuilder();
/* 106 */       if (validate)
/* 107 */         builder.setErrorHandler(new ReadErrorHandler());
/* 108 */       this.m_document = builder.parse(xmlFile);
/* 109 */       System.out.println("Parsing OK");
/*     */     }
/*     */     catch (SAXException sxe)
/*     */     {
/* 116 */       throw sxe;
/*     */     }
/*     */     catch (ParserConfigurationException pce)
/*     */     {
/* 121 */       pce.printStackTrace();
/* 122 */       throw pce;
/*     */     }
/*     */     catch (IOException ioe)
/*     */     {
/* 127 */       ioe.printStackTrace();
/* 128 */       throw ioe;
/*     */     }
/* 130 */     return this.m_document;
/*     */   }
/*     */ 
/*     */   public String getFileName()
/*     */   {
/* 138 */     File f = new File(this.m_filepath);
/* 139 */     return f.getName();
/*     */   }
/*     */ 
/*     */   public final void printArrayList()
/*     */   {
/* 147 */     if (this.m_list != null)
/*     */     {
/* 149 */       int sz = this.m_list.size();
/*     */ 
/* 151 */       for (int i = 0; i < this.m_list.size(); i++)
/* 152 */         System.out.println(this.m_list.get(i).toString());
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/ern/Desktop/installazione/lib/TourFinder_v1_2.jar
 * Qualified Name:     it.poliba.lca.routing.io.reader.XMLReader
 * JD-Core Version:    0.6.2
 */