/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mobnavdesk;


import java.io.IOException;
import javax.imageio.stream.ImageOutputStream;

/**
 *
 * @author 2cool
 */
public class MapSet {

    public int zoom;
    public SubSet[] subsets; // in cur zoom

    public void save(ImageOutputStream o, int []a) throws IOException
    {
        int nSubsets = subsets.length;

        o.writeByte((byte)zoom);
        o.writeInt(nSubsets);
        for (int i = 0; i < nSubsets; i++)
            subsets[i].save(o,a);
    }
        
    
}
