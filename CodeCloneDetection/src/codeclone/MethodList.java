package codeclone;

import java.util.ArrayList;
import java.util.List;

public class MethodList {
	
	public List<MethodVector> methodList = new ArrayList<MethodVector>();

	// add function
	public void addMethodVector(MethodVector mv) {
		methodList.add(mv);
	}
	
	// getMethodVector
	public MethodVector getMethodVector(int index) {
		return methodList.get(index);
	}
	
	// size
	public int size() {
		return methodList.size();
	}
	
	// print
	public void print() {
		for(int i = 0; i < methodList.size(); i++)
			methodList.get(i).print();
	}
}
