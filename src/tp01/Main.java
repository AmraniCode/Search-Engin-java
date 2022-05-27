
package tp01;

import GraphicInterface.MainPage;
import java.io.IOException;

/**
 *
 * @author chakib
 */
/*
les repertoire utiliser sont DATA et fichier_inv
*/

public class Main {
    
       
public static void main(String[] args) throws IOException   {
      indexe.init();    
//if you want to indexe repertory DATA without interface uncomment the lines below  and comment Mainpage p
     // indexe.indexRepertory("data");
    //indexManipulator manip=new indexManipulator();  
    //manip.read_index_from_disk();
//    String query = "preven Allah God";
//    var elm=manip.Evaluate_Query(query);
//    System.out.println(elm);
//    manip.Sort(elm);
//    var elm2=manip.Evaluate_Query_BM25(query);
//    System.out.println(elm2);
//    manip.Sort(elm2);
//if you want to use graphic interface use the lines below 
       MainPage p=new MainPage();
        p.setVisible(true);
      
}

}
