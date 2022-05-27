/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tp01;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import java.util.Map.Entry;


/**
 *
 * @author chaki
 */


//Class for saving indexe with defrent method  
public class save_index_to_disk {
    public static int getIdTerm(String word){
       return indexe.lexyconeTermId.get(word);
    }
     public static String getTermById(int id){
       return indexe.LexyconeIdTerm.get(id);
    }
     
     //Save indexes to disk with inverse file method 
    public static void InverseFile() throws FileNotFoundException, IOException{
        //create docs file 
        try (DataOutputStream lex = new DataOutputStream (new FileOutputStream(indexe.DocsPath))) {
            File f = new File(indexe.dataPath);
            String[] pathnames = f.list();
            int id =0; 
            for(Entry e :indexe.Documents.entrySet()){
                Document doc=(Document) e.getValue();
                lex.writeInt(doc.id);
                lex.writeUTF(doc.path);
                lex.writeInt(doc.nbrTerm);
                id++;
            }
           indexe.totalDoc=id+1;
            }catch(Exception ex){
                System.err.println("error dans fichier DocsId");
            }
        //create termes file ;
       try ( DataOutputStream lex = new DataOutputStream (new FileOutputStream(indexe.TermPath))) {
           
           for (int key :indexe.lexyconeIdDf.keySet()){
               String term=getTermById(key);
               lex.writeInt(key);
               lex.writeUTF(term);
               lex.writeInt(indexe.lexyconeIdDf.get(key));
               
          //     System.out.println("word : "+key);
           }
       }
       catch(Exception e){
           System.err.println("error d'ecriture de lexycon");
       }
       
       // create posting Liste file
        DataOutputStream dos = new DataOutputStream (new FileOutputStream(indexe.IndexResultPath));
      for (int id : indexe.lexyconeIdDf.keySet()){
          int idTerm =id;
          ArrayList<Integer> docIds=indexe.PostingList.get(idTerm);
          dos.writeInt(idTerm);
          //System.out.println(idTerm);
          dos.writeInt(docIds.size()/2); 
       for (int i=0;i<docIds.size();i+=2){
            //System.err.println("term : "+getTermById(idTerm)+" "+idTerm+" idDoc : "+docIds.get(i).toString());
            dos.writeInt(docIds.get(i));
            dos.writeInt(docIds.get(i+1));
            }
        }

       // System.out.println(getIdTerm("natura"));
        dos.close();
            
        System.out.println("indexing with inverse file is donne");
    }

}
