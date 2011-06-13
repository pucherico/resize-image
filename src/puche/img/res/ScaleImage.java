/*
 * ScaleImage.java
 *
 * Created on 9 de septiembre de 2006, 0:09
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package puche.img.res;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 *
 * @author puche
 */
public class ScaleImage {
  
  private final Image src;
  private final int   x, y; // left-top coord. from which to start copying.
  private final int   srcWidth, srcHeight;
  private int         width, height;
  
  public ScaleImage(Image src) {
    
    this(src, 0, 0, src.getWidth(null), src.getHeight(null));
  }
  
  public ScaleImage(Image src, int x, int y, int srcWidth, int srcHeight) {
    
    this.src = src;
    this.x = x;
    this.y = y;
    this.srcWidth = this.width = srcWidth;
    this.srcHeight = this.height = srcHeight;
  }
  
  protected void checkProportions() {
  
    if ((width == 0) && (height != 0))
      setProportionalWidth();
    else if ((height == 0) && (width != 0))
      setProportionalHeight();
  }
  
  /** 
   * it's allowed a zero-width or zero-height (but not both). This means that
   * the corresponding data will be calculated asuring to keep the rate of the
   * target image.
   */
  public void setSize(int width, int height) {
    
    this.width = width;
    this.height = height;
    checkProportions();
  }
  /*
  public void setWidth(int width) {
    
    this.width = width;
    checkProportions();
  }
  
  public void setHeight(int height) {
    
    this.height = height;
    checkProportions();
  }
  */
  
  public void setRelativeSize(double w, boolean relativeWidth,
                              double h, boolean relativeHeight) {
    if (relativeWidth)
      this.width = (int)(((double)srcWidth) * w);
    else
      this.width = (int)w;
    
    if (relativeHeight)
      this.height = (int)(((double)srcHeight) * h);
    else
      this.height = (int)h;
    
    checkProportions();
  }
  
  /**
   * define target width keeping the rate of the image knowing the target height
   * and src image dimension
   */
  private void setProportionalWidth() {
   
    this.width = srcWidth * height / srcHeight;
  }
  
  /** 
   * define target height keeping the rate of the image knowing the target width
   * and src image dimension
   */
  private void setProportionalHeight() {
   
    this.height = srcHeight * width / srcWidth;
  }
  
  public BufferedImage generate() throws IOException {
    
    BufferedImage im = new BufferedImage(width, height,
                                         BufferedImage.TYPE_INT_RGB);
    Graphics2D g = (Graphics2D)im.getGraphics();
    
    g.scale((double)width / (double)srcWidth,
            (double)height / (double)srcHeight);
    g.translate(-x, -y);
    g.drawImage(src, 0, 0, null);
    return im;
  }
}