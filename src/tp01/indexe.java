
package tp01;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileNotFoundException;

import java.io.FileReader;

import java.io.IOException;
import static java.lang.Math.log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.StringTokenizer;



/**
 *
 * @author Chakib
 */

public class indexe {
   public static  ArrayList<String> stopWords_tokens;
   public static String separateur="%:._+– *&\"\'!*^/()?,;[]<>?!=+|{}\"&-";
   public static String stopWordPath="stopwords.txt";
   public static String dataPath="data";
   public static String IndexResultPath="fichier_invers//postingList.txt";
   public static String DocsPath="fichier_invers\\DocsId.txt";
   public static String TermPath="fichier_invers\\lexycon.txt";
   public static String PostingPath="fichier_invers\\postingList.txt";
   public static int totalDoc=0;
   public final static int TRUNCATE_SIZE=6;
   public static ArrayList<HashMap<String, Integer>> listeDoc;
   
   public  static HashMap<String, Integer> lexyconeTermId;
   public  static HashMap<Integer, String> LexyconeIdTerm;
   public  static HashMap <Integer, Integer>lexyconeIdDf;
   public  static HashMap <Integer, Document>Documents;
   public  static HashMap<String,String> stopWordsHash;
   public static HashMap<Integer, ArrayList<Integer>> PostingList;
///activitie 1
   public static void init() throws IOException{
    lexyconeTermId=new HashMap<String, Integer>() ;
    LexyconeIdTerm=new HashMap<Integer, String>();
    lexyconeIdDf= new HashMap<Integer, Integer>();
    Documents=new HashMap<Integer,Document>();
    stopWordsHash=new HashMap<>();
    PostingList=new HashMap<Integer, ArrayList<Integer>>();
     tokenize_StopWords();
   }
   
   //lecture d'un fichier a partire d'un path
   public static String readDoc(String path )throws FileNotFoundException, IOException{
       //System.out.println("file : \n"+path);
       String data=new String();
       data="";
        BufferedReader br = new BufferedReader(new FileReader(path)); 
       try{
        String line = br.readLine(); 
        while (line != null) {
            data+= " "+line;
            line = br.readLine(); 
       }
    }
      
  finally{
       br.close();
       }

  return data.toLowerCase();
       
   
}
 public static void tokenize_StopWords() throws IOException{
     var stopWords =indexe.readDoc(indexe.stopWordPath);
      //stopWords_tokens=indexe.tokenize(stopWords);
      StringTokenizer st = new StringTokenizer(stopWords);
      HashMap<String,String> stA=new HashMap<String,String>();  
        while(st.hasMoreTokens()){
            String word=st.nextToken();
            stopWordsHash.put(word,word);
        }
       // System.out.println("size of stopWord "+stopWordsHash.size());
    
 }
  

///activitie 3
   //remove stope word from liste of words
   public static   ArrayList<String> stopwords_removal(ArrayList<String> st) throws IOException{
       ArrayList<String> liste_without_stop=new   ArrayList<String>();
        for (String elm:st){
            if(!stopWordsHash.containsKey(elm))
                liste_without_stop.add(elm);
        }
        return liste_without_stop;
   }
  
   
   ////activitie 2
   // get liste of tokenze from String text 
   public static ArrayList<String> tokenize(String file){
        StringTokenizer st = new StringTokenizer(file,separateur);
        ArrayList<String> stA=new ArrayList<String>();  
        while(st.hasMoreTokens()){
            String word=st.nextToken();
            stA.add(word);
        }
        //System.out.println("the size is of liste of tokens : "+stA.size());
        return stA;
   
   }
   
   //truncate word to K first letters
   public static String truncate (String s){
     String st=s.substring(0, Math.min(s.length(),TRUNCATE_SIZE));
     return st;
   }
   //steming words 
    static public ArrayList<String> stemming(ArrayList<String> liste_words){
        ArrayList<String> liste_stems=new ArrayList<String>();
        for(String elm:liste_words){
            String s=truncate(elm);
            liste_stems.add(s);
        }
        return liste_stems;
    }
    
    
//activitie 5
    ///calcule la frequence de chaque mot in Liste of words 
    public static HashMap<String, Integer> term_frequency_computing(  ArrayList<String>list_of_stems ){
      HashMap<String, Integer> occs = new HashMap<String, Integer>();
      for (String elm :list_of_stems){
         if (occs.get(elm) == null){
            Integer oc=0;
            for (int i=elm.indexOf(elm)+1;i<list_of_stems.size();i++){
                if (elm.equalsIgnoreCase(list_of_stems.get(i)))
                    oc++;
            }
            occs.put(elm, oc);
         }
      }
      return occs;
    }
    
    //calcule le TF d'un motId donne avec une occurece donne 
    public static double Term_weighting(int id,double occ){
        if (lexyconeIdDf.get(id)!=0)
         return (1+log(occ));
        else return 0;
    }

    
   //create hashMap of one Documment that contains word with  his occurency in the Document
    public static HashMap<String, Integer> indexFile (String path){
         String File = new String();
         File="";
        try {
             File=readDoc(path);
             ArrayList<String> st=tokenize(File);
            // System.out.println("donne tokenize");
             ArrayList<String> list_of_tokens_without_stopwords =stopwords_removal(st);  
             //System.out.println("donne stopWord");
            // System.out.println("the size of  is list_of_tokens_without_stopwords : "+list_of_tokens_without_stopwords.size());
             ArrayList<String>list_of_stems=stemming(list_of_tokens_without_stopwords);
            // System.out.println("donne stem");
             HashMap<String, Integer> occs = term_frequency_computing(list_of_stems);
            // System.out.println("donne frequ");
           return occs;
        }
       catch(Exception e) {
           System.out.println(e);
           return null;
        }
    }
    
//Faire la Mise a jour des Liste de Lexycone pour un nouveau document
public static void updateListes(int idDoc,HashMap<String, Integer>doc){
    // System.out.println("\nUpdate ");
     int idterm=lexyconeIdDf.size()+1;
     for (Entry e :doc.entrySet()){
                String word =(String) e.getKey();
                ArrayList<Integer> docIds=new ArrayList<>();
                int idf=1;
                if(indexe.lexyconeTermId.get(word) == null) {
                    docIds.add(idDoc);
                    docIds.add(doc.get(word));
                // System.out.println(idterm+"word: "+word+" "+1);
                PostingList.put(idterm, docIds);
                indexe.lexyconeIdDf.put(idterm, idf);
                indexe.LexyconeIdTerm.put(idterm,word);
                indexe.lexyconeTermId.put(word,idterm);
                idterm++;
               }else {
                    int df=indexe.lexyconeIdDf.get(lexyconeTermId.get(word));
                    indexe.lexyconeIdDf.put(lexyconeTermId.get(word),df+1);
                    int idT=lexyconeTermId.get(word);
                   // System.out.println(idT+"word: "+word+" "+df);
                    docIds=indexe.PostingList.get(idT);
                    docIds.add(idDoc);
                    docIds.add(doc.get(word));
                    PostingList.put(idT, docIds);
                }        
            }
}

//calcule de Invert document occurence 
public static  double Idf(int id){
    if(lexyconeIdDf.containsKey(id))
        return log((double)indexe.totalDoc/(double)lexyconeIdDf.get(id));
    else return 0;
}

//Generate Id for Document 
public static int generateIdDoc(){
  // System.out.println(Documents.size()+1);
    return Documents.size()+1;
}


//Faire l'indexation de repertoire de DATA 
public static void indexRepertory(String path) throws IOException{
       File f = new File(path);
       String[] pathnames = f.list();
       int id=1;
       for(String doc :pathnames){ 
          HashMap<String, Integer> elm =indexFile(path+"\\"+doc);
          id=generateIdDoc();
          Document d=new Document(id,path+"\\"+doc,elm.size());
          indexe.Documents.put(id, d);
          updateListes(id,elm);
          
       }
       indexe.totalDoc=Documents.size();
        //indexe.listeDoc=listeDoc;
        //IDF(listeDoc);
        System.out.println("size of lexycon : "+indexe.lexyconeTermId.size());
    
        //save indexes in repertory data/inverce_file
       save_index_to_disk.InverseFile();
 
    }

    
  
    
}
