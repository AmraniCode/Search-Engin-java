/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tp01;


import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import static java.lang.Math.log;
import java.util.ArrayList;

import java.util.Collections;

import java.util.HashMap;

import java.util.Map.Entry;
/**
 *
 * @author chaki
 */

//indexManipulator est la class qui permet de manipuler les fichier indexe 
public class indexManipulator {
    
    private ArrayList <Document>DocListe=new ArrayList<>();
    private ArrayList <Term>TermListe=new ArrayList<>();
    
    
    private void Init_Posting_List_on_Memory() throws FileNotFoundException, IOException{
       DataInputStream PostReader = new DataInputStream (new FileInputStream(indexe.PostingPath));
        while(PostReader.available()>0){
            int id= PostReader.readInt();
            int nbrDoc= PostReader.readInt();
            ArrayList<Integer>liste=new ArrayList<>();
            for (int i=0;i<nbrDoc;i++){
                int idDoc=PostReader.readInt();
                int occ=PostReader.readInt();
                liste.add(idDoc);
                liste.add(occ);
            }
            
           indexe.PostingList.put(id, liste);
        }
        //indexe.PostingList=PostingList;
    }
    private void Init_Doc_Index_on_Memory() throws FileNotFoundException, IOException{
      DataInputStream DocReader = new DataInputStream (new FileInputStream(indexe.DocsPath));
      int nbrDoc=0;  
      while(DocReader.available()>0){
            int id= DocReader.readInt();
            String docPath=DocReader.readUTF();
            int nbrTerm=DocReader.readInt();
            Document d=new Document( id,docPath, nbrTerm);
            DocListe.add(d);
            indexe.Documents.put(id, d);
            nbrDoc++;
        }
      indexe.totalDoc=nbrDoc;
      //indexe.Documents=Documents;
    }
    private void Init_Term_Index_on_Memory() throws FileNotFoundException, IOException{
    DataInputStream TermReader = new DataInputStream (new FileInputStream(indexe.TermPath));
        
        while(TermReader.available()>0){          
            int id= TermReader.readInt();
            String term=TermReader.readUTF();
            int occ=TermReader.readInt();
            Term d=new Term( id,term, occ);
            TermListe.add(d);
            indexe.LexyconeIdTerm.put(id, term);
            indexe.lexyconeTermId.put(term, id);
            indexe.lexyconeIdDf.put(id, occ);
           // System.out.println("term : "+term);
        }
//        indexe.lexyconeIdDf=LexyconeIdDf;
//        indexe.lexyconeTermId=lexyconeTermId;
//        indexe.LexyconeIdTerm=LexyconeIdTerm;
    }
    
    public void read_index_from_disk() throws FileNotFoundException, IOException {
        Init_Doc_Index_on_Memory();
        Init_Term_Index_on_Memory();
        Init_Posting_List_on_Memory();
    }
    

    //get id  with term =nom 
    public int getIdTerm(String nom){
        if (indexe.lexyconeTermId.get(nom)!=null)
        return indexe.lexyconeTermId.get(nom);
        else return -1;
    }
    
    //get Term by id
     public String getTermById(int id){
      return indexe.LexyconeIdTerm.get(id);
    }
    public Document getDocById(int id){
      return indexe.Documents.get(id);
    }
    

//test if word exist in List 
private boolean exist(String word,ArrayList<String>list){
        for (String elm : list){
            if(elm.equalsIgnoreCase(word))
                return true ;
        }
        return false;
    }


//for indexing the query
public ArrayList<String> Index_Query(String query) throws IOException{
       String queryNormalized=query.toLowerCase();
       ArrayList<String> tokens=new ArrayList<>();
       tokens=indexe.tokenize(queryNormalized);
       ArrayList<String> tokensWithoutStopWord=indexe.stopwords_removal(tokens);
       ArrayList<String>stems=indexe.stemming(tokensWithoutStopWord);
       ArrayList<String>validWords=new ArrayList<>();
       for (String word :stems){
           if(indexe.lexyconeTermId.get(word)!=null && !exist(word,validWords))
               validWords.add(word);
       }
       return validWords;
    }
    

//create posting liste of query 
   public  HashMap<Integer,ArrayList<Integer>> Init_Posting_List_of_Query_on_Memory(ArrayList<String> valid){
       HashMap<Integer,ArrayList<Integer>>queryMap=new HashMap<>();
       for(String word :valid){
           int idWord=getIdTerm(word);
           ArrayList<Integer> liste=indexe.PostingList.get(idWord);
           queryMap.put(idWord, liste);
       }
      return queryMap;
   }
   
   
   
   public HashMap<Integer,Double> Evaluate_Query(String query) throws IOException{
       HashMap<Integer,Double>Query_Doc_Scores=new HashMap<>();
       ArrayList<String> valid=this.Index_Query(query);
       HashMap<Integer,ArrayList<Integer>> queryMap=this.Init_Posting_List_of_Query_on_Memory(valid);
      for (Entry e:queryMap.entrySet()){
          ArrayList<Integer> Docs=(ArrayList<Integer>) e.getValue();
          double idf=Docs.size()/2;
          //calcule de idf de term courant 
          int idTerm=(int) e.getKey();
          idf= indexe.Idf(idTerm);
          for(int i=0;i<Docs.size();i+=2){
              int docId=Docs.get(i);
              double tf=Docs.get(i+1);
              tf= indexe.Term_weighting(idTerm,tf);
//calcule de tf de term courant 
              if(Query_Doc_Scores.containsKey(docId)){
                  double docrsv=Query_Doc_Scores.get(docId)+idf*tf;
                  Query_Doc_Scores.put(docId, docrsv);
              }else Query_Doc_Scores.put(docId,tf*idf);

          }
      }
      return Query_Doc_Scores;
   }

 private double AVG_DL(){
    double total=indexe.Documents.size(),res=0;
    for(Document d:indexe.Documents.values()){
        res+=d.nbrTerm;
    }
    res=res/total;
    return res;
}
   public double IDF_V2(int idTerm){
       double N=indexe.Documents.size();
       double nq=indexe.lexyconeIdDf.get(idTerm);
       double res=log((N-nq+0.5)/(nq+0.5));
       return res;
       
   }
   
   public void printScores(){
       for (Entry e : indexe.Documents.entrySet()){
           Document d=(Document) e.getValue();
           System.out.println("doc "+d.path+" score"+d.score);
       }
   }
   public ArrayList<Document> Sort(HashMap<Integer,Double> listeDoc){
       ArrayList<Document> list=new ArrayList<>();
       for(Entry e:listeDoc.entrySet()){
           double value=(double) e.getValue();
           int idDoc=(int) e.getKey();
           Document Document = indexe.Documents.get(idDoc);
           Document.setScore(value);
          
           list.add(Document);
       }
     Collections.sort(list,Collections.reverseOrder());  
      
       return list;
   }
   public double Frequency(int idTerm,int idDoc){
       ArrayList<Integer>ListDoc=indexe.PostingList.get(idTerm);
       for(int i=0;i<ListDoc.size();i+=2){
           if(ListDoc.get(i)== idDoc)
               return ListDoc.get(i+1);
       }
       return 0;
   }
   
   
   //fonction de calcule de score avec method bm25 in one documment 
   public double SCORE_BM25(int idDoc,ArrayList<String> validQueryWords,double K1) throws IOException{
     double score=0;
     double B=0.75;
     int sizeDoc=indexe.Documents.get(idDoc).nbrTerm;
     double avregDocSize=AVG_DL();
     for (String word:validQueryWords){
        int idTermQuery=indexe.lexyconeTermId.get(word);
        double F=Frequency(idTermQuery, idDoc);
        score+=IDF_V2(idTermQuery)*(F*(K1+1))/(F+K1*(1-B+B*sizeDoc/avregDocSize));
     }   
     return score;
   }

    public HashMap<Integer,Double> Evaluate_Query_BM25(String query) throws IOException{
         HashMap<Integer,Double>Query_Doc_Scores=new HashMap<>();
         ArrayList<String> validQueryWord=this.Index_Query(query);
         HashMap<Integer,ArrayList<Integer>> queryMap=this.Init_Posting_List_of_Query_on_Memory(validQueryWord);
         double K1= (1.2 + (Math.random() * (2.2 - 1.2)));
         for (Entry e:queryMap.entrySet()){
             ArrayList<Integer>ListDocs=(ArrayList<Integer>) e.getValue();
             for (int i=0;i<ListDocs.size();i+=2){
                 int idDoc=ListDocs.get(i);
                 if(Query_Doc_Scores.get(idDoc)==null){
                    double score=SCORE_BM25(idDoc, validQueryWord,K1);
                    Query_Doc_Scores.put(idDoc, score);
                 }
             }
         }
         return Query_Doc_Scores;
    }
//les deux fonction getNbrOfPrevios and  getDocsOfTerm sont des fonction non demander dans le TP 
//permetre de lire Les posting liste from disk d'une facon optimal
   
private int getNbrOfPrevios(int id){
       if(indexe.LexyconeIdTerm.get(id)!=null){
        int total=0;
        for(Term term:TermListe){
            int ids=term.id;
               if(id == ids)
                   return total;
               total+=indexe.lexyconeIdDf.get(getTermById(ids))*2+2;
           }
       
    }
    return -1;
    }
    
   
public ArrayList<Document>getDocsOfTerm(String nom) throws FileNotFoundException, IOException{
        int term = getIdTerm(nom.toLowerCase());
        if(term >=0){
             File file = new File(indexe.PostingPath);
             RandomAccessFile raf = new RandomAccessFile(file, "rw");
             int dep=getNbrOfPrevios(term);
             raf.seek(dep*4);
             int idT=raf.readInt();
             int NbrDoc=raf.readInt();
             //System.out.println("term searched "+getTermById(idT)+" is in docs :");
             for(int i=0;i<NbrDoc;i++){
                 int idoc =raf.readInt();
                 int occ =raf.readInt();
                 System.out.println("DocId :"+idoc+" occ :"+occ);
                 System.out.println("path : Local\\"+getDocById(idoc).path);
             }
             raf.close();
//             ArrayList<Integer>l=PostingList.get(idT);
//             for (var elm:l){
//                 System.out.println(elm);
//             }
        }else {
            System.out.println("le term n'est pas indexer");
        }
        return null;
}
    
}


