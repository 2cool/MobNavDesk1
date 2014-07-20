/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mobnavdesk;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;

/**
 *
 * @author 2cool
 */
class MakeImg{
    public ImageOutputStream  ios=null;  
    public SubSet ss=null;
    public int ssI=0;
    public MakeImg(final SubSet s, final int i){ss=s;ssI=i;}
}
public class Map {
    public int thrRunning=0;
    private boolean recreate;
    private int tiles,ctiles,tileSize,t2t,t2tb,a[]=new int[1];
    private double rtiles;
    public int acum;
    private MAP_CREATOR mc=null;
    ImgCompressor imgC=null;
    private ImageOutputStream o=null;
    private File f=null;
    RandomAccessFile ro=null;
    public   Map(final MAP_CREATOR mc) throws FileNotFoundException, IOException{
        f = new File(MAP_CREATOR.mapFileName);
        if (f.exists())
            f.delete();
       // ro=new RandomAccessFile(f,"rw");                      
        o=new FileImageOutputStream(f);
        imgC=new ImgCompressor(o);
        this.mc=mc;
          recreate = MobNavJFrame.recreateTiles;
          if (recreate){
               tileSize = MobNavJFrame.tileSize;
                if (tileSize == 128){
                t2t = 2;
                t2tb = 1;
            }else            
                t2t = t2tb = 0;
            }else{
              tileSize=256;
              t2t=t2tb=0;
          }
            tiles = (int)MAPS_SET.tiles<<t2t;
            rtiles = 1.0 / (double)tiles;
            ctiles = 0;

           
            
                       
            a[0] = 13;
            //-------------------------------
           
            
            
        
    }
    final Integer monitor=new Integer(0);
    private void sizesR(final MakeImg out, final int type, final int x,final int y, final int zoom){
        
        thrRunning++;
       // new Thread(new Runnable() {
       // @Override
       // public void run() {                                            
         int ry=y>>t2tb;           
         int rx=x>>t2tb; 
         try {                      
         String fn=InetMaps.getTileFName(type,rx,ry,zoom);
                File lf=new File(fn);                                 
                int size;                
                if (lf.exists() && (size=(int)lf.length())>0){
                                            
                        FileInputStream is=new FileInputStream(lf);
                        byte []inb=new byte[size];
                        is.read(inb);
                        is.close();
                        InputStream in = new ByteArrayInputStream(inb);
                        BufferedImage img = ImageIO.read(in);
                        ImgCompressor ic=new ImgCompressor(out.ios);
                        out.ss.sizes[out.ssI]=ic.write(img, tileSize, ((rx<<t2tb)-x)<<7,((ry<<t2tb)-y)<<y, out.ios);                                                                                          
                        ic.dispose();
                }else{
                    byte []outb={126,126,126};
                    out.ios.write(outb);
                    out.ss.sizes[out.ssI]=3;
                }
         
         }catch(Exception ex) {
            ERROR=true;
            System.out.println("E R R O R "+ex.toString());
            Logger.getLogger(Map.class.getName()).log(Level.SEVERE, null, ex);
        }
            thrRunning--;
            synchronized(monitor){                
                monitor.notify();
            }
      //}}).start();  
    }
    
    
    private void sizesR_dontWork(final ImageOutputStream o, final SubSet ss, final int zoom) {
        
        int type=InetMaps.getType(ss.datum);
        int si=0;
        MakeImg []mk={new MakeImg(ss,0),new MakeImg(ss,1),new MakeImg(ss,2),new MakeImg(ss,3)};
        thrRunning=0;
        int thr=0;
        for (int y=ss.p0.y; y<ss.p1.y; y++){
            for (int x=ss.p0.x; x<ss.p1.x; x++){                                
                if (mc.pause)
                    try {Thread.sleep(1000);} catch (InterruptedException ex) {} 
                 //mk[thr].ss=ss;
                 mk[thr].ssI=si++;
                 
                    try {
                        mk[thr].ios=ImageIO.createImageOutputStream(new ByteArrayOutputStream());
                    } catch (IOException ex) {}
                    
                 sizesR(mk[thr], type, x,y,zoom);
                 thr++;
                 if (thr==4 || si==ss.sizes.length ){
                     while (thrRunning>0){
                        synchronized(monitor){
                        try {
                            monitor.wait(1000);
                        } catch (InterruptedException ex) { }
                        }
                     }
                     try {
                        for (int i=0; i<thr; i++){
                            byte[]b=new byte[ss.sizes[mk[i].ssI]];                    
                            mk[i].ios.read(b);                    
                            o.write(b);
                            mk[i].ios.close();                            
                        }
                        mc.tilesSavedFull+=(thr>>t2t);
                        mc.tilesSaved+=thr;
                        thr=0;
                     } catch (IOException ex) {}
                     
                     
                 }
    
            }
        }
    }
    /// ###################################################################
    private void sizesR(final ImageOutputStream o, final SubSet ss, final int zoom) {
        
        int type=InetMaps.getType(ss.datum);
        int si=0;
        for (int y=ss.p0.y; y<ss.p1.y; y++){
            int ry=y>>t2tb;
            int yImgOff=((ry<<t2tb)-y)<<7;
            for (int x=ss.p0.x; x<ss.p1.x; x++){                                
                if (mc.pause)
                    try {Thread.sleep(1000);} catch (InterruptedException ex) {}                
                int rx=x>>t2tb;                                                 
                String fn=InetMaps.getTileFName(type,rx,ry,zoom);
                File lf=new File(fn);                                 
                int size;                
                if (lf.exists() && (size=(int)lf.length())>0){
                    try {                        
                        FileInputStream is=new FileInputStream(lf);
                        byte[]b=new byte[size];
                        is.read(b);
                        is.close();
                        InputStream in = new ByteArrayInputStream(b);
                        BufferedImage img = ImageIO.read(in);                         
                        ss.sizes[si++]=imgC.write(img, tileSize, ((rx<<t2tb)-x)<<7,yImgOff, o);                                                                                                                                                                                                                   
                    }catch(Exception ex) {
                        ERROR=true;
                        System.out.println("E R R O R "+ex.toString());
                        Logger.getLogger(Map.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }else{
                    ss.sizes[si++]=3;
                    byte []b={126,126,126};
                    try {o.write(b);} catch (IOException ex) {ERROR=true;}
                }
                if (t2tb==0 || (x&1)==0 && (y&1)==0)
                    mc.tilesSavedFull++;
                mc.tilesSaved++;
                
            }
        }
    }
   public boolean ERROR=false;
    public MapSet[]map=null;
    private void sizes(final ImageOutputStream o, final SubSet ss, final int zoom){
        int type=InetMaps.getType(ss.datum);
        int si=0;
        for (int y=ss.p0.y; y<ss.p1.y; y++){
            for (int x=ss.p0.x; x<ss.p1.x; x++){
                 if (mc.pause)
                    try {Thread.sleep(1000);} catch (InterruptedException ex) {}
                File lf=new File(InetMaps.getTileFName(type,x,y,zoom));
                int size;
                if (lf.exists() && (size=(int)lf.length())>0){
                    try {
                        FileInputStream is=new FileInputStream(lf);
                        byte[]b=new byte[size];
                        is.read(b);
                        is.close();
                        o.write(b);
                        ss.sizes[si++]=size;                        
                        //System.out.println("TileSaved="+mc.tilesSaved);
                    } catch (Exception ex) {
                        ERROR=true;
                        Logger.getLogger(Map.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }else{
                    ss.sizes[si++]=3;
                    byte []b={126,126,126};
                    try {o.write(b);} catch (IOException ex) {ERROR=true;}
                }
                mc.tilesSaved++;
                mc.tilesSavedFull++;
            }
        }
    }
    public int subsets=0,subset;
    public  void DO(final MapSet []map, final int[] loadedStage){

         try{
            int stage=0;
           // MapSet[] map = CreateMaps();
            int nMaps = map.length;
            if (nMaps == 0)
                return;                           
           
            int foo = 0x2C001;
            if (tileSize != 256)
                foo |= 1 << 20;
            o.writeInt(foo);//4
            o.writeByte((byte)map.length);//1
            o.writeInt(tiles);//4
            o.writeInt(0);//4 for indexBegin
            //13 
            mc.tilesSavedFull=0;
            for (int i = 0; i < nMaps; i++){
                mc.zoomAsembled=map[i].zoom;
                subsets=map[i].subsets.length;
                for (subset = 0; subset < map[i].subsets.length; subset++){
                   // string datum = zoomNodezoomNode[map[i].zoom].Nodes[j].                    
                    while (stage==loadedStage[0]){
                        try{
                            Thread.sleep(1000);
                        }  catch (InterruptedException ex) {}
                    }
                    stage++;
                    int x0=map[i].subsets[subset].p0.x<<=t2tb;
                    int y0=map[i].subsets[subset].p0.y<<=t2tb;
                    int x1=map[i].subsets[subset].p1.x<<=t2tb;
                    int y1=map[i].subsets[subset].p1.y<<=t2tb;
                    map[i].subsets[subset].sizes=new int[mc.tiles2Load=(x1-x0)*(y1-y0)];               
                    mc.tilesSaved=0;
                    if (recreate)
                        sizesR(o, map[i].subsets[subset], map[i].zoom);                       
                    else
                        sizes(o, map[i].subsets[subset], map[i].zoom);
                }
            }
            int sizesBeginPos = (int)o.getStreamPosition();
            //------------------------------------------------------------------
            imgC.dispose();
            //-------------------------------------------------
             for (int i = 0; i < map.length; i++)               
                    map[i].save(o,a);                
                //-------------------------------------------------
                long fp0=o.getStreamPosition();
                
                o.close();
               // ro.close();                
                ro=new RandomAccessFile(f,"rw"); 
                long fp1=ro.getFilePointer();
                ro.seek(5);//##########################################  
                long fp2=ro.getFilePointer();
                int sizebeg = sizesBeginPos;
                ro.writeInt(tiles);
                ro.writeInt(sizebeg);
                ro.close();
                mc.jobDone=true;
              //  SetButton_("DONE");
            }
           catch (Exception ex) {
                ERROR=true;            
           }
    }
}
