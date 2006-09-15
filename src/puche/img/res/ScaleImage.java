/*
 * ScaleImage.java
 *
 * Created on 9 de septiembre de 2006, 0:09
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package puche.img.res;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import javax.imageio.ImageIO;

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
  
  public void setSize(int width, int height) {
    
    this.width = width;
    this.height = height;
  }
  
  public void setWidth(int width) {
    
    this.width = width;
  }
  
  public void setHeight(int height) {
    
    this.height = height;
  }
  
  public void setRelativeWidth(double alfa) {
    
    width = (int)(((double)srcWidth) * alfa);
  }
  
  public void setRelativeHeight(double alfa) {
    
    height = (int)(((double)srcHeight) * alfa);
  }
  
  public void generate(OutputStream out) throws IOException {
    
    BufferedImage im = new BufferedImage(width, height,
                                         BufferedImage.TYPE_INT_RGB);
    Graphics2D g = (Graphics2D)im.getGraphics();
    
    g.scale((double)width / (double)srcWidth,
            (double)height / (double)srcHeight);
    g.translate(-x, -y);
    g.drawImage(src, 0, 0, null);
    ImageIO.write(im, "jpg", out);
  }
}