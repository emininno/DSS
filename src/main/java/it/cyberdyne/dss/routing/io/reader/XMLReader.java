package it.cyberdyne.dss.routing.io.reader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class XMLReader
{
  public static final String FILE_EXTENSION = ".xml";
  protected static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
  protected static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
  protected static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
  protected int m_callId;
  protected Document m_document = null;
  protected ArrayList m_list;
  protected String m_filepath;
  
  public XMLReader(int call_id)
  {
    this.m_callId = call_id;
  }
  
  public XMLReader(ArrayList list)
  {
    if ((list != null) && (!list.isEmpty())) {
      this.m_list = new ArrayList(list);
    }
  }

  public void read()
    throws Exception
  {}

  protected Document parse(File xmlFile, InputStream schemaStream)
    throws Exception
  {
    this.m_document = null;
    try
    {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      
      boolean validate = false;
      if (schemaStream != null) {
        System.out.println("DEBUG: parse - attivata validazione");
        factory.setValidating(true);
        factory.setNamespaceAware(true);
        factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
        factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaSource", schemaStream);
        validate = true;
      }
      else {
        factory.setValidating(false);
      }
      DocumentBuilder builder = factory.newDocumentBuilder();
      if (validate)
        builder.setErrorHandler(new ReadErrorHandler());
      this.m_document = builder.parse(xmlFile);
      System.out.println("Parsing OK");

    }
    catch (SAXException sxe)
    {

      throw sxe;

    }
    catch (ParserConfigurationException pce)
    {
      pce.printStackTrace();
      throw pce;

    }
    catch (IOException ioe)
    {
      ioe.printStackTrace();
      throw ioe;
    }
    return this.m_document;
  }
  

  public String getFileName()
  {
    File f = new File(this.m_filepath);
    return f.getName();
  }
  
  public final void printArrayList()
  {
    if (this.m_list != null)
    {
      int sz = this.m_list.size();
      
      for (int i = 0; i < this.m_list.size(); i++) {
        System.out.println(this.m_list.get(i).toString());
      }
    }
  }
}