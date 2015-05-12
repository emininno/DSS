package it.cyberdyne.dss.routing.io.reader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.StringTokenizer;


public class MatrixReader
{
  Double[][] m_matrix;
  int m_rows;
  int m_cols;
  String m_filepath;
  
  public MatrixReader(String filepath)
  {
    this.m_filepath = filepath;
  }
  
  public MatrixReader(Double[][] matrix)
  {
    if (matrix != null) {
      this.m_rows = matrix.length;
      this.m_cols = matrix[0].length;
      this.m_matrix = new Double[this.m_rows][this.m_cols];
      for (int i=0;i<this.m_rows;i++)
          System.arraycopy(matrix[i], 0, this.m_matrix[i], 0, this.m_cols);
    }
    else
          System.err.println("MATRICE VUOTA!!");
  }
  

  public int getRowCount()
  {
    return this.m_rows;
  }
  
  public int getColumnCount() {
    return this.m_cols;
  }
  
  public Double at(int row, int col)
    throws Exception
  {
    Double d = null;
    if ((row > -1) && (col > -1) && (row < this.m_rows) && (col < this.m_cols)) {
      d = this.m_matrix[row][col];
    }
    else {
      throw new RuntimeException("Errore: indici fuori limite!");
    }
    return d;
  }
  

  public Double[][] getMatrix()
  {
    return this.m_matrix;
  }
  
  public Double[] getRow(int index)
  {
    if (index < this.m_rows) {
      return this.m_matrix[index];
    }
    return null;
  }
  
  public Double[] getColumn(int index)
  {
    if (index < this.m_cols) {
      Double[] v = new Double[this.m_rows];
      for (int i = 0; i < this.m_rows; i++)
        v[i] = this.m_matrix[i][index];
      return v;
    }
    
    return null;
  }
  
  public void read()
    throws FileNotFoundException, IOException
  {
    BufferedReader br = null;
    try
    {
      FileReader fr = new FileReader(this.m_filepath);
      br = new BufferedReader(fr);
      
      scoutsDimensions(br);
      
      this.m_matrix = new Double[this.m_rows][this.m_cols];
      
      br.close();
      br = null;
      br = new BufferedReader(new FileReader(this.m_filepath));
      fillMatrix(br);
    }
    catch (FileNotFoundException fnfe) {
      throw fnfe;
    }
    catch (IOException ioe) {
      throw ioe;
    }
    finally {
      if (br != null) {
        br.close();
      }
    }
  }
  
  private void scoutsDimensions(BufferedReader br)
    throws IOException
  {
    int rowCnt = 0;
    int colCnt = 0;
    
    String line;
    while ((line = br.readLine()) != null)
    {


      StringTokenizer lineTk = new StringTokenizer(line, ";");
      
      rowCnt += lineTk.countTokens();
      while (lineTk.hasMoreTokens()) {
        String rowStr = lineTk.nextToken();
        
        StringTokenizer rowTk = new StringTokenizer(rowStr, ",");
        if (colCnt < rowTk.countTokens()) {
          colCnt = rowTk.countTokens();
        }
      }
    }
    
    this.m_rows = rowCnt;
    this.m_cols = colCnt;
  }
  


  private void fillMatrix(BufferedReader br)
    throws IOException
  {
    int i = 0;int j = 0;
    String line;
    while ((line = br.readLine()) != null) {
      StringTokenizer lineTk = new StringTokenizer(line, ";");
      

      while ((lineTk.hasMoreTokens()) && (i < this.m_rows)) {
        String rowStr = lineTk.nextToken();
        

        StringTokenizer rowTk = new StringTokenizer(rowStr, ",");
        
        j = 0;
        while ((rowTk.hasMoreTokens()) && (j < this.m_cols)) {
          this.m_matrix[i][j] = Double.valueOf(Double.parseDouble(rowTk.nextToken()));
          
          j++;
        }
        

        for (int k = j; k < this.m_cols; k++)
          this.m_matrix[i][k] = Double.valueOf(0.0D);
        i++;
      }
    }
  }
  
  public void printMatrix()
  {
    for (int i = 0; i < this.m_rows; i++) {
      for (int j = 0; j < this.m_cols; j++)
        System.out.print(this.m_matrix[i][j] + "  ");
      System.out.println();
    }
  }
}