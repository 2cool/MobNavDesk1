/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mobnavdesk;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Iterator;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;

/**
 *
 * @author 2cool
 */
public class ImgCompressor {
        static public float compression=2;
        private boolean jpgType=false;
        private ImageWriter writer=null;
        private ImageWriteParam iwp;
    public  ImgCompressor(final ImageOutputStream o){
        if (jpgType=compression<=1){
            Iterator iter = ImageIO.getImageWritersByFormatName("jpg");
            writer = (ImageWriter)iter.next();
            iwp = writer.getDefaultWriteParam();
            iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            iwp.setCompressionQuality(compression);
            writer.setOutput(o);
        }
    }
    public int write(final BufferedImage img, final int tileSize, final int x, final int y, final ImageOutputStream o) throws IOException{        
        return (jpgType)?writeJpg(img,tileSize,x,y,o):writePng(img,tileSize,x,y,o);
    }
    public void dispose(){
        if (writer!=null)
            writer.dispose();
    }
    private int writeJpg(final BufferedImage img, final int tileSize, final int x, final int y, final ImageOutputStream o) throws IOException{
        BufferedImage oimg =new BufferedImage(tileSize, tileSize, BufferedImage.TYPE_INT_RGB);                            
        Graphics g = oimg.createGraphics();
        g.drawImage(img, x,y , null);      
        long oldFP=o.getStreamPosition();                
        IIOImage image = new IIOImage(oimg, null, null);
        writer.write(null, image, iwp);                                                                    
        int size=(int)(o.getStreamPosition()-oldFP);               
        return size;
    }
    private int writePng(final BufferedImage img, final int tileSize, final int x, final int y, final ImageOutputStream o) throws IOException{
        BufferedImage oimg =new BufferedImage(tileSize, tileSize, BufferedImage.TYPE_INT_RGB);                            
        Graphics g = oimg.createGraphics();
        g.drawImage(img, x,y , null); 
        long oldFP=o.getStreamPosition();
        ImageIO.write(oimg, "png", o);       
        int size=(int)(o.getStreamPosition()-oldFP);        
        return size;
    }
    
}
