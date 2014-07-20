/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mobnavdesk;

import java.awt.Point;
import java.io.IOException;
import javax.imageio.stream.ImageOutputStream;

/**
 *
 * @author 2cool
 */
public class SubSet {
   // int mapType=-1; 
    public Point p0, p1;
    public int[] sizes;
    public String datum;
    public void save(ImageOutputStream o, int[]a) throws IOException
    {
        o.writeInt(p0.x);
        o.writeInt(p0.y);
        o.writeInt(p1.x);
        o.writeInt(p1.y);       
        for (int i = 0; i < sizes.length; i++)
        {
            o.writeInt(a[0]);
            a[0] += sizes[i];
        }
        o.writeInt(a[0]);
    }

 
    
    
    
}
