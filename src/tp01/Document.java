/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tp01;


/**
 *
 * @author chakib
 */
public class Document implements Comparable< Document> {
    public String path;
    public int nbrTerm;
    public int id;
    public double score=0;

    public Document(int id ,String path,int n) {
    this.path=path;
    this.id=id;
    this.nbrTerm=n;
    }
    public void setScore(double s){
        this.score=s;
    }
    public double getScore(){
        return this.score;
    }

    @Override
    public int compareTo(Document o) {
    return  Double.compare(this.getScore(),o.getScore());
    }

    
}
