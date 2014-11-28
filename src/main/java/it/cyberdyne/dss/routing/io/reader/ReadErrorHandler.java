/*    */ package it.cyberdyne.dss.routing.io.reader;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ import org.xml.sax.ErrorHandler;
/*    */ import org.xml.sax.SAXException;
/*    */ import org.xml.sax.SAXParseException;
/*    */ 
/*    */ public class ReadErrorHandler
/*    */   implements ErrorHandler
/*    */ {
/*    */   public void warning(SAXParseException spe)
/*    */     throws SAXException
/*    */   {
/* 22 */     System.err.println("WARNING (" + getClass().getName() + "). " + spe);
/* 23 */     throw new SAXException("Warning originato da " + message(spe));
/*    */   }
/*    */ 
/*    */   public void error(SAXParseException spe) throws SAXException
/*    */   {
/* 28 */     System.err.println("ERROR (" + getClass().getName() + "). " + spe);
/* 29 */     throw new SAXException("Error orginato da " + message(spe));
/*    */   }
/*    */ 
/*    */   public void fatalError(SAXParseException spe) throws SAXException
/*    */   {
/* 34 */     System.err.println("FATAL ERROR (" + getClass().getName() + "). " + spe);
/* 35 */     throw new SAXException("Fatal error originato da " + message(spe));
/*    */   }
/*    */ 
/*    */   private String message(SAXParseException spe) {
/* 39 */     return "SAXParseException su " + spe.getSystemId() + "; linea = " + spe.getLineNumber() + "; motivo = [" + spe.getMessage() + "]";
/*    */   }
/*    */ }

/* Location:           /home/ern/Desktop/installazione/lib/TourFinder_v1_2.jar
 * Qualified Name:     it.cyberdyne.dss.routing.io.reader.ReadErrorHandler
 * JD-Core Version:    0.6.2
 */