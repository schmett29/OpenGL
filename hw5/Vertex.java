package hw5;

import java.util.*;

class Vertex {
    public float x,y,z; // x,y,z components
    public float nx,ny,nz;

    Vertex(float a,float b,float c){
	x=a; y=b; z=c;
    }

    public void setNormal(Vertex n){
	nx = n.x;
	ny = n.y;
	nz = n.z;
    }

    public void computeNormal(List<Face> faces){
	double sx=0,sy=0,sz=0;
	int num=0;
	for(Face f : faces){
	    num++;
	    double[] normal = f.getNormal();
	    sx += normal[0];
	    sy += normal[1];
	    sz += normal[2];
	}
	nx = (float)sx/num;
	ny = (float)sy/num;
	nz = (float)sz/num;
    }

    public String toString(){
	return "("+x+","+y+","+z+")";
    }
}

