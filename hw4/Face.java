package hw4;

import java.util.*;

class Face {
    List<Vertex> vlist;
    Face(){
	vlist = new ArrayList<Vertex>();
    }

    public void addVertex(Vertex v){
	vlist.add(v);
    }

    public List<Vertex> getVlist(){
	return vlist;
    }

    public String toString(){
	return vlist.toString();
    }
}
