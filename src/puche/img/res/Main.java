/*
 * Main.java
 *
 * Created on 7 de septiembre de 2006, 21:15
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package puche.img.res;

import java.awt.image.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.*;
import javax.imageio.ImageIO;

/**
 *
 * @author puche
 */
public class Main {
  
  private final File imgSrc, imgTarget;
  private final int widthTarget, heightTarget;
  private final boolean wRelative, hRelative;
  
  public Main(File imgSrc, File imgTarget, int w, int h,
              boolean wRelative, boolean hRelative) {
    
    this.imgSrc = imgSrc;
    this.imgTarget = imgTarget;
    this.widthTarget = w;
    this.heightTarget = h;
    this.wRelative = wRelative;
    this.hRelative = hRelative;
  }
  
  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) throws IOException {
    
    if (args.length < 1) {
      System.out.println("SINTAX ERROR");
      System.out.println("OPTION i:");
      System.out.println("\tto scale: Main <img-src> <img-target> <width-target> <height-target>");
      System.out.println("OPTION ii:");
      System.out.println("\tto cut: Main <img-src> <xpos-src> <ypos-src> <img-target> <width-target> <height-target>");
      System.out.println();
      System.out.println("Note: width and height could be absolute or relative to source image. e.g. 50%");
      System.exit(0);
    } else if (args.length == 4) {
      File src = new File(args[0]);
      File target = new File(args[1]);
      int w = Integer.parseInt(args[2].endsWith("%")?
                               args[2].substring(0, args[2].length() - 1)
                               : args[2]);
      int h = Integer.parseInt(args[3].endsWith("%")?
                               args[3].substring(0, args[3].length() - 1)
                               : args[3]);
      Main app = new Main(src, target,
                          w, h, args[2].endsWith("%"), args[2].endsWith("%"));
      app.run();
    }
  }
    
  public void run() throws IOException {
    
    ScaleImage model = new ScaleImage(ImageIO.read(imgSrc));
    OutputStream out =new BufferedOutputStream(new FileOutputStream(imgTarget));
    if (wRelative)
      model.setRelativeWidth(((double)widthTarget) / 100.0);
    else
      model.setWidth(widthTarget);
    if (hRelative)
      model.setRelativeHeight(((double)heightTarget) / 100.0);
    else
      model.setHeight(heightTarget);
    model.generate(out);
    out.close();
  }
}