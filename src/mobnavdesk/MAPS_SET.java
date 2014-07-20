/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mobnavdesk;



import java.awt.Point;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 *
 * @author 2cool
 */

public class MAPS_SET {
  public static long tiles=0;
  public static DefaultMutableTreeNode myMap = new DefaultMutableTreeNode("My Map");
  public static DefaultMutableTreeNode zooms[]={null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null};
  public static void rename(final String str){   
      myMap.setUserObject(str);

  }
  
  static public void clear(){
      tiles=0;
      myMap=new DefaultMutableTreeNode("My Map");
     for (int i=0; i<zooms.length; i++)
         zooms[i]=null;
     
     
  }
  
  final static public String devider="-";
  
  static public long pasreSize(final String str){
      String []a=str.split(devider);
      return ( Long.parseLong(a[3])-Long.parseLong(a[1]) )*( Long.parseLong(a[4])-Long.parseLong(a[2]) );
                    
      
  }
  static public void delite(){

      if (MobNavJFrame.selectionPaths!=null)
          for (int i=0; i<MobNavJFrame.selectionPaths.length; i++){
              String a[]=MobNavJFrame.selectionPaths[i].toString().split(",");
              if (a.length==3){
                int zoom=Integer.parseInt(a[1].trim());
                String name=a[2].substring(0, a[2].length()-1).trim();
                for (int n=0; n<zooms[zoom].getChildCount(); n++){
                    String  str=zooms[zoom].getChildAt(n).toString().trim();
                    if (str.endsWith(name)){
                        zooms[zoom].remove(n);                         
                        tiles-=pasreSize(str);
                    }                
                }
                if (zooms[zoom].getChildCount()==0){
                        myMap.remove(zooms[zoom]);
                        zooms[zoom]=null;
                }
            }else{
                  Object[] options = {"Yes","No"};
                    int n = JOptionPane.showOptionDialog(null,
                        "Delite "+MobNavJFrame.selectionPaths[i].toString(),
                        "!",
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        null);
                    if (n==0){
                        if (a.length==2){
                             int zoom=Integer.parseInt(a[1].replace("]", "").trim());
                             for (n=0; n<zooms[zoom].getChildCount(); n++){
                                String  str=zooms[zoom].getChildAt(n).toString().trim();
                                zooms[zoom].remove(n);                         
                                tiles-=pasreSize(str);
                             }
                             myMap.remove(zooms[zoom]);
                             zooms[zoom]=null;
                         }
                        if (a.length==1){
                            tiles=0;
                            for (int zoom=0; zoom<20; zoom++){
                                if (zooms[zoom]!=null){
                                    myMap.remove(zooms[zoom]);
                                    zooms[zoom]=null;
                                }
                            }
                            return;
                        }
                     }                                                                             
              }
          }
  }
  static public Point inTiles(final int zoom, final Point p1){
       final int dz=8+19-zoom;                  
       return new Point(p1.x>>dz,p1.y>>dz);
  }
  static private void addZoom(final int zoom){
      if (zooms[zoom]==null){
        zooms[zoom]=(new DefaultMutableTreeNode(zoom)); 
        myMap.add(MAPS_SET.zooms[zoom]);
    }  
  }
  static public void add(final int zoom, final String str){
    addZoom(zoom);                        
    zooms[zoom].add( new DefaultMutableTreeNode(str));                            
    
  }
  static public int add(final boolean z[], final Point p1, final Point p2){
      int n=0;
      
      for (int zoom=0; zoom<z.length; zoom++){
          if (z[zoom]){
              addZoom(zoom);  
              Point pp1=inTiles(zoom,p1);              
              Point pp2=inTiles(zoom,p2);
              if (MobNavJFrame.greed==-1){
                  pp2.x++;
                  pp2.y++;
              }
              
              tiles+=(long)(pp2.x-pp1.x)*(long)(pp2.y-pp1.y);
             
              zooms[zoom].add( new DefaultMutableTreeNode(MobNavJFrame.inetMaps.im[MobNavJFrame.inetMaps.type].getName()+devider+pp1.x+devider+pp1.y+devider+pp2.x+devider+pp2.y));     
              n++;
          }
      } 
      return n;
  }
 
    
}
