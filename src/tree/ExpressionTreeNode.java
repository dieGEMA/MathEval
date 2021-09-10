package tree;

import java.util.Iterator;
import java.util.LinkedList;

public class ExpressionTreeNode<T> {

	T content;
	ExpressionTreeNode<T> parent;
	ExpressionTreeNode<T> leftChild;
	ExpressionTreeNode<T> rightChild;
	
	public ExpressionTreeNode(T content) {
	    this.content = content;
	}
	
	public T getContent() {
		return this.content;
	}
	
	public ExpressionTreeNode<T> getParent(){
		return this.parent;
	}
	
	public ExpressionTreeNode<T> getLeftChild() {
		return this.leftChild;
	}
	
	public ExpressionTreeNode<T> getRightChild() {
		return this.rightChild;
	}
	
	public void setContent(T content) {
		this.content = content;
	}
	
	public void setParent(ExpressionTreeNode<T> parent) {
		this.parent = parent;
	}
	
	public void setLeftChild(ExpressionTreeNode<T> leftChild) {
		this.leftChild = leftChild;
	}
	
	public void setRightChild(ExpressionTreeNode<T> rightChild) {
		this.rightChild = rightChild;
	}
	
	 public String toString() {
	        StringBuilder buffer = new StringBuilder(50);
	        print(buffer, "", "");
	        return buffer.toString();
	 }
	 
	 private void print(StringBuilder buffer, String prefix, String childrenPrefix) {
	        buffer.append(prefix);
	        buffer.append(this.content);
	        buffer.append('\n');
	        LinkedList<ExpressionTreeNode<T>> children = new LinkedList<ExpressionTreeNode<T>>();
	        if(leftChild != null) {
	        	children.add(leftChild);
	        }
	        if(rightChild != null) {
	        	children.add(rightChild);
	        }
	        for (Iterator<ExpressionTreeNode<T>> iter = children.iterator(); iter.hasNext();) {
	        	ExpressionTreeNode<T> next = iter.next();
	            if (iter.hasNext()) {
	                next.print(buffer, childrenPrefix + "├── ", childrenPrefix + "│   ");
	            } else {
	                next.print(buffer, childrenPrefix + "└── ", childrenPrefix + "    ");
	            }
	        }
	    }
	
}