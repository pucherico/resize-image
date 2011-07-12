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
import java.io.*;
import java.util.Hashtable;
import java.util.Locale;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.FileImageOutputStream;

/**
 *
 * @author puche
 */
public class Main {

  private final File imgSrc, imgTarget;
  private final int xSrc, ySrc, widthSrc, heightSrc;
  private final boolean cut; // if src data specified
  private final int widthTarget, heightTarget;
  private final boolean wRelative, hRelative;

  public Main(File imgSrc, File imgTarget, int w, int h,
          boolean wRelative, boolean hRelative) {

    this.imgSrc = imgSrc;
    this.xSrc = this.ySrc = this.widthSrc = this.heightSrc = 0; // no use
    this.cut = false;
    this.imgTarget = imgTarget;
    this.widthTarget = w;
    this.heightTarget = h;
    this.wRelative = wRelative;
    this.hRelative = hRelative;
  }

  public Main(File imgSrc, int xSrc, int ySrc, int wSrc, int hSrc,
          File imgTarget, int w, int h,
          boolean wRelative, boolean hRelative) {

    this.imgSrc = imgSrc;
    this.xSrc = xSrc;
    this.ySrc = ySrc;
    this.widthSrc = wSrc;
    this.heightSrc = hSrc;
    this.cut = true;
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

    if ((args.length != 4) && (args.length != 8)) {
      System.out.println("SINTAX:");
      System.out.println();
      System.out.println("\tjava -jar ResizeImage <img-src> [<xpos-src> <ypos-src> <width-src> <height-src>] <img-target> <width-target> <height-target>");
      System.out.println("\tor");
      System.out.println("\tjava -jar ResizeImage <dir-src> [<xpos-src> <ypos-src> <width-src> <height-src>] <dir-target> <width-target> <height-target>");
      System.out.println();
      System.out.println("Note: width and height could be absolute or relative to source image. e.g. 50%");
      System.out.println("Note also that either width or height could be zero to mean proportional");
      System.exit(0);
    } else if (args.length == 4) {
      File src = new File(args[0]);
      File target = new File(args[1]);
      int w = Integer.parseInt(args[2].endsWith("%")
              ? args[2].substring(0, args[2].length() - 1)
              : args[2]);
      int h = Integer.parseInt(args[3].endsWith("%")
              ? args[3].substring(0, args[3].length() - 1)
              : args[3]);
      Main app = new Main(src, target,
              w, h, args[2].endsWith("%"), args[3].endsWith("%"));
      app.run();
    } else {
      File src = new File(args[0]);
      int xSrc = Integer.parseInt(args[1]);
      int ySrc = Integer.parseInt(args[2]);
      int wSrc = Integer.parseInt(args[3]);
      int hSrc = Integer.parseInt(args[4]);
      File target = new File(args[5]);
      int w = Integer.parseInt(args[6].endsWith("%")
              ? args[6].substring(0, args[6].length() - 1)
              : args[6]);
      int h = Integer.parseInt(args[7].endsWith("%")
              ? args[7].substring(0, args[7].length() - 1)
              : args[7]);
      Main app = new Main(src, xSrc, ySrc, wSrc, hSrc,
              target,
              w, h, args[6].endsWith("%"), args[7].endsWith("%"));
      app.run();
    }
  }

  public void run() throws IOException {

    if (imgSrc.isDirectory()) {
      File[] imgs = imgSrc.listFiles(new FileFilter() {

        public boolean accept(File pathname) {
          return pathname.getName().toUpperCase().endsWith("JPG");
        }
      });
      imgTarget.mkdirs();
      for (File f : imgs) {
        runImage(f, new File(imgTarget, f.getName()));
      }
    } else {
      runImage(imgSrc, imgTarget);
    }
  }

  public void runImage(File imgSrc, File imgTarget) throws IOException {
    FileImageInputStream fis = new FileImageInputStream(imgSrc);
    ImageReader ir = ImageIO.getImageReaders(fis).next();
    ir.setInput(fis);
    IIOMetadata m = ir.getImageMetadata(0);
    RenderedImage i = ir.read(0);

    ImageWriter iw = ImageIO.getImageWriter(ir);
    FileImageOutputStream fos = new FileImageOutputStream(imgTarget);
    iw.setOutput(fos);
    JPEGImageWriteParam param = new JPEGImageWriteParam(Locale.getDefault());
    //param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
    //param.setCompressionQuality(0.9f);
    param.setOptimizeHuffmanTables(true);

    BufferedImage biSrc = convertRenderedImage(i); //ImageIO.read(imgSrc);
    ScaleImage model = cut ? new ScaleImage(biSrc, xSrc, ySrc, widthSrc, heightSrc)
                           : new ScaleImage(biSrc);

    double w = wRelative ? ((double) widthTarget) / 100.0 : (double) widthTarget;
    double h = hRelative ? ((double) heightTarget) / 100.0 : (double) heightTarget;
    model.setRelativeSize(w, wRelative, h, hRelative);
    BufferedImage biGen = model.generate();

    // Bug? Demos excluir los metadatos (m) en imágenes verticales (si no sale mal la imagen!!!)
    iw.write(null, new IIOImage(biGen, null, biSrc.getWidth() > biSrc.getHeight()? m : null), param);
    fos.close(); // really needed or done by iw.dispose()?
    iw.dispose();
    ir.dispose();

    System.out.println("Processed: " + imgSrc);
  }

  public static BufferedImage convertRenderedImage(RenderedImage img) {
    if (img instanceof BufferedImage) {
      return (BufferedImage) img;
    }
    ColorModel cm = img.getColorModel();
    int width = img.getWidth();
    int height = img.getHeight();
    WritableRaster raster = cm.createCompatibleWritableRaster(width, height);
    boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
    Hashtable properties = new Hashtable();
    String[] keys = img.getPropertyNames();
    if (keys != null) {
      for (int i = 0; i < keys.length; i++) {
        properties.put(keys[i], img.getProperty(keys[i]));
      }
    }
    BufferedImage result = new BufferedImage(cm, raster, isAlphaPremultiplied, properties);
    img.copyData(raster);
    return result;
  }
}