/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mobnavdesk;

import java.awt.Image;

/**
 *
 * @author 2cool
 */
public class MyTile {
    public Image img;
    public int   size;
    public MyTile(){
        img=null;
        size=0;
    }
    public void flush(){
        if (img!=null){
            img.flush();
            img=null;
            size=0;
        }
    }
    
}
