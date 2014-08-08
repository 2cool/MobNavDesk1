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


// git git git


public interface INET_MAPS {    	
	//public int cnt=0;
	public String png=".png",jpg=".jpg";
	public String getReferer();
	public String getName();
	public String getExt();
	public Image getIcon();
	public String getUrl(final int x,final int y,final int z);
        public void   OkVersion();
        public void   incVersion();
        public void   loadVersion();
        public void   saveVersion();
}
