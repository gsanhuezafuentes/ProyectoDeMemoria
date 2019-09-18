package epanet.uielements;

import java.util.ArrayList;
import java.util.List;

public abstract class Link {
	private List<Point> vertices;
	
	public Link() {
		this.vertices = new ArrayList<Point>();
	}

	public final List<Point> getVertices(){
		return vertices;
	}
}
