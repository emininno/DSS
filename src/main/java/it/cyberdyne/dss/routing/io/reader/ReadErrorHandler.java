 package it.cyberdyne.dss.routing.io.reader;
 
 import org.xml.sax.ErrorHandler;
 import org.xml.sax.SAXException;
 import org.xml.sax.SAXParseException;
 
 
 
 
 
 
 
 
 
 public class ReadErrorHandler
   implements ErrorHandler
 {
   public void warning(SAXParseException spe)
     throws SAXException
   {
     System.err.println("WARNING (" + getClass().getName() + "). " + spe);
     throw new SAXException("Warning originato da " + message(spe));
   }
   
   public void error(SAXParseException spe) throws SAXException
   {
     System.err.println("ERROR (" + getClass().getName() + "). " + spe);
     throw new SAXException("Error orginato da " + message(spe));
   }
   
   public void fatalError(SAXParseException spe) throws SAXException
   {
     System.err.println("FATAL ERROR (" + getClass().getName() + "). " + spe);
     throw new SAXException("Fatal error originato da " + message(spe));
   }
   
   private String message(SAXParseException spe) {
     return "SAXParseException su " + spe.getSystemId() + "; linea = " + spe.getLineNumber() + "; motivo = [" + spe.getMessage() + "]";
   }
 }
