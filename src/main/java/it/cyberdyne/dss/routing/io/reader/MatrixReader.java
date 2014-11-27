/*     */ package it.cyberdyne.dss.routing.io.reader;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileReader;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.util.StringTokenizer;
/*     */ 
/*     */ public class MatrixReader
/*     */ {
/*     */   Double[][] m_matrix;
/*     */   int m_rows;
/*     */   int m_cols;
/*     */   String m_filepath;
/*     */ 
/*     */   public MatrixReader(String filepath)
/*     */   {
/*  32 */     this.m_filepath = filepath;
/*     */   }
/*     */ 
/*     */   public MatrixReader(Double[][] matrix)
/*     */   {
/*  37 */     if (matrix != null) {
/*  38 */       this.m_rows = matrix.length;
/*  39 */       this.m_cols = matrix[0].length;
/*  40 */       this.m_matrix = new Double[this.m_rows][this.m_cols];
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getRowCount()
/*     */   {
/*  47 */     return this.m_rows;
/*     */   }
/*     */ 
/*     */   public int getColumnCount() {
/*  51 */     return this.m_cols;
/*     */   }
/*     */ 
/*     */   public Double at(int row, int col)
/*     */     throws Exception
/*     */   {
/*  64 */     Double d = null;
/*  65 */     if ((row > -1) && (col > -1) && (row < this.m_rows) && (col < this.m_cols)) {
/*  66 */       d = this.m_matrix[row][col];
/*     */     }
/*     */     else {
/*  69 */       throw new RuntimeException("Errore: indici fuori limite!");
/*     */     }
/*  71 */     return d;
/*     */   }
/*     */ 
/*     */   public Double[][] getMatrix()
/*     */   {
/*  78 */     return this.m_matrix;
/*     */   }
/*     */ 
/*     */   public Double[] getRow(int index)
/*     */   {
/*  87 */     if (index < this.m_rows) {
/*  88 */       return this.m_matrix[index];
/*     */     }
/*  90 */     return null;
/*     */   }
/*     */ 
/*     */   public Double[] getColumn(int index)
/*     */   {
/*  97 */     if (index < this.m_cols) {
/*  98 */       Double[] v = new Double[this.m_rows];
/*  99 */       for (int i = 0; i < this.m_rows; i++)
/* 100 */         v[i] = this.m_matrix[i][index];
/* 101 */       return v;
/*     */     }
/*     */ 
/* 104 */     return null;
/*     */   }
/*     */ 
/*     */   public void read()
/*     */     throws FileNotFoundException, IOException
/*     */   {
/* 129 */     BufferedReader br = null;
/*     */     try
/*     */     {
/* 132 */       FileReader fr = new FileReader(this.m_filepath);
/* 133 */       br = new BufferedReader(fr);
/*     */ 
/* 135 */       scoutsDimensions(br);
/*     */ 
/* 137 */       this.m_matrix = new Double[this.m_rows][this.m_cols];
/*     */ 
/* 139 */       br.close();
/* 140 */       br = null;
/* 141 */       br = new BufferedReader(new FileReader(this.m_filepath));
/* 142 */       fillMatrix(br);
/*     */     }
/*     */     catch (FileNotFoundException fnfe) {
/* 145 */       throw fnfe;
/*     */     }
/*     */     catch (IOException ioe) {
/* 148 */       throw ioe;
/*     */     }
/*     */     finally {
/* 151 */       if (br != null)
/* 152 */         br.close();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void scoutsDimensions(BufferedReader br)
/*     */     throws IOException
/*     */   {
/* 164 */     int rowCnt = 0;
/* 165 */     int colCnt = 0;
/*     */     String line;
/* 168 */     while ((line = br.readLine()) != null)
/*     */     {
/* 172 */       StringTokenizer lineTk = new StringTokenizer(line, ";");
/*     */ 
/* 174 */       rowCnt += lineTk.countTokens();
/* 175 */       while (lineTk.hasMoreTokens()) {
/* 176 */         String rowStr = lineTk.nextToken();
/*     */ 
/* 178 */         StringTokenizer rowTk = new StringTokenizer(rowStr, ",");
/* 179 */         if (colCnt < rowTk.countTokens()) {
/* 180 */           colCnt = rowTk.countTokens();
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 185 */     this.m_rows = rowCnt;
/* 186 */     this.m_cols = colCnt;
/*     */   }
/*     */ 
/*     */   private void fillMatrix(BufferedReader br)
/*     */     throws IOException
/*     */   {
/* 194 */     int i = 0; int j = 0;
/*     */     String line;
/* 196 */     while ((line = br.readLine()) != null) {
/* 197 */       StringTokenizer lineTk = new StringTokenizer(line, ";");
/*     */ 
/* 200 */       while ((lineTk.hasMoreTokens()) && (i < this.m_rows)) {
/* 201 */         String rowStr = lineTk.nextToken();
/*     */ 
/* 204 */         StringTokenizer rowTk = new StringTokenizer(rowStr, ",");
/*     */ 
/* 206 */         j = 0;
/* 207 */         while ((rowTk.hasMoreTokens()) && (j < this.m_cols)) {
/* 208 */           this.m_matrix[i][j] = Double.valueOf(Double.parseDouble(rowTk.nextToken()));
/*     */ 
/* 210 */           j++;
/*     */         }
/*     */ 
/* 214 */         for (int k = j; k < this.m_cols; k++)
/* 215 */           this.m_matrix[i][k] = Double.valueOf(0.0D);
/* 216 */         i++;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void printMatrix()
/*     */   {
/* 226 */     for (int i = 0; i < this.m_rows; i++) {
/* 227 */       for (int j = 0; j < this.m_cols; j++)
/* 228 */         System.out.print(this.m_matrix[i][j] + "  ");
/* 229 */       System.out.println();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/ern/Desktop/installazione/lib/TourFinder_v1_2.jar
 * Qualified Name:     it.cyberdyne.dss.routing.io.reader.MatrixReader
 * JD-Core Version:    0.6.2
 */