package hw5;

import java.util.*;

class Face {
    List<Vertex> vlist;
    double[] normal = new double[3];

    Face(){
	vlist = new ArrayList<Vertex>();
    }

    public void computeNormal(){
	Vertex v0 = vlist.get(0);
	Vertex v1 = vlist.get(1);
	Vertex v2 = vlist.get(2);
	double x0 = v1.x-v0.x;
	double x1 = v2.x-v1.x;
	double y0 = v1.y-v0.y;
	double y1 = v2.y-v1.y;
	double z0 = v1.z-v0.z;
	double z1 = v2.z-v1.z;
	normal[0] = (y0*z1-y1*z0);
	normal[1] = (z0*x1-z1*x0);
	normal[2] = (x0*y1-x1*y0);
    }

    public void addVertex(Vertex v){
	vlist.add(v);
    }

    public void addVertex(Vertex v, Vertex normal){
	vlist.add(v);
    }

    public double[] getNormal(){
	return normal;
    }

    public List<Vertex> getVlist(){
	return vlist;
    }

    public String toString(){
	return vlist.toString()+"("+normal[0]+","+normal[1]+","+normal[2]+")";
    }
}
