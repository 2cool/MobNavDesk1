/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mobnavdesk;

import java.awt.Point;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author 2cool
 */






public class MAP_CREATOR {
    final  public UPD_MON updMon=new UPD_MON();
     InetMaps inetMaps=new InetMaps(updMon);
     
     public MapSet []mapSet=null;
     public int tiles2Load=1,tilesSaved=0,tilesSavedFull,zoomAsembled=0,maxZoom;
     public boolean jobDone=false;
     public boolean pause=false;
  
 public int cnt=10000000,tiles;
    private  final int MAX_ERR=5;
    public int subsetN=0;
    public int subSetSize=0;
    private  int loadTiles(final MapSet ms){
        updMon.zoom=ms.zoom;
        //System.out.println("ZOOM="+ms.zoom);
        subSetSize=ms.subsets.length;
        for (subsetN=0; subsetN<ms.subsets.length; subsetN++){
            int err=inetMaps.cnt=0;
            inetMaps=new InetMaps(updMon);
           // System.out.println("SET="+i);
            Point p0=ms.subsets[subsetN].p0;
            Point p1=ms.subsets[subsetN].p1;
            inetMaps.type=InetMaps.getType(ms.subsets[subsetN].datum);
            cnt=(p1.x-p0.x)*(p1.y-p0.y);
            
            do{
                tiles=inetMaps.cnt=0;                              
                for (int y=p0.y; y<p1.y; y++){
                    for (int x=p0.x; x<p1.x; x++){
                        boolean imgExist=inetMaps.tileExistOrLoad(x, y, ms.zoom);
                        if (imgExist)
                            tiles++;
                    }
                }                   
                synchronized(updMon){
                                 
                    if (inetMaps.cnt+tiles<cnt){                      
                        try {
                            //System.out.println("------------WAIT0 "+tiles+" < "+cnt);
                            if (inetMaps.thrRunning>0)
                                updMon.wait();//20000);
                            //System.out.println("------------WAIT1");
                             
                        } catch (InterruptedException ex) {
                            Logger.getLogger(CMAPJFrame.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    
                    tiles+=inetMaps.cnt;
                }                
            }while(tiles<cnt && ++err<=MAX_ERR);
            if (err>MAX_ERR)System.out.println("===========ERROR "+(cnt-tiles));
            
            if (tiles<cnt)
              ;//  errF+= cnt-tiles;
            else      System.out.println("_______ALL DONE-------------");                            
            cntFull+=tiles;   
            downloadWorkDone[0]++;
        } 
        return inetMaps.errors;
    }
     private  void correctMapSets(final int zoom){
        
    }
     
     // #######################################################################

     //-----------------------------------------------------------
     
     
     
     
    
    
    
    public int cntFull,errFull;
    public int  [] downloadWorkDone=new int[1];
    
    
     private void downloadTiles(){
         
        cntFull=0;
        errFull=0;
         for (int i=0; i<mapSet.length; i++){
             correctMapSets(mapSet[i].zoom);
              errFull+=loadTiles(mapSet[i]);                        
        }            
     }                  
     private void mapInit(){
         
        int m_cnt=0;
        for (int zoom=0; zoom<=19; zoom++){            
            if (MAPS_SET.zooms[zoom]!=null){
                m_cnt++;
                maxZoom=zoom;
            }
        }
        mapSet=new MapSet[m_cnt];
         m_cnt=0;
         for (int zoom=0; zoom<=19; zoom++){            
            if (MAPS_SET.zooms[zoom]!=null){
                
                MapSet ms=mapSet[m_cnt++]=new MapSet();
                ms.zoom=zoom;                
                int ccnt=MAPS_SET.zooms[zoom].getChildCount();
                ms.subsets=new SubSet[ccnt];
                for (int n=0; n<ccnt; n++){
                    SubSet ss=ms.subsets[n]=new SubSet();
                    String  str=MAPS_SET.zooms[zoom].getChildAt(n).toString().trim();
                    String f[]=str.split(MAPS_SET.devider);                   
                    ss.p0=new Point(Integer.parseInt(f[1].trim()),Integer.parseInt(f[2].trim()));
                    ss.p1=new Point(Integer.parseInt(f[3].trim()),Integer.parseInt(f[4].trim()));                            
                    ss.datum=f[0];                    
              }
            }
        }
     }
     // ############################# S T A R T ###############################
     static public String mapFileName=null;
     public Map map=null;
     public void start() throws FileNotFoundException, IOException{         
         mapFileName=MobNavJFrame.mapFileName;
         if (mapFileName==null)
             return;
        mapInit();
        map=new Map(this);
         new Thread(new Runnable() {            
            @Override
                public void run() {                
                       downloadTiles();
                }}).start();
          new Thread(new Runnable() {            
            @Override
                public void run() {                
                        map.DO(mapSet,downloadWorkDone);
                }}).start();

     }

}
