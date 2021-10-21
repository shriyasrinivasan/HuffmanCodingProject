

// Group Project by: Shriya Srinivasan, Jonathan Phan, Thien-Huong Ninh, Mason Pamarang, Abhinav Balla


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;



/*
Write a program to compress a source file into a target file using the 
Huffman coding method. First, use ObjectOutputStream to output the Huffman 
codes into the target file, then use BitOutputStream from Assignment #2 
to output the encoded binary contents to the target file. Pass the files 
from the command line using something like the following command:

C:\Users\.... > java Compress_a_File sourceFile.txt compressedFile.txt
 */
class Heap<E extends Comparable<E>> {
	private java.util.ArrayList<E> list = new java.util.ArrayList<>();

	/** Create a default heap */
	public Heap() {
	}

	/** Create a heap from an array of objects */
	public Heap(E[] objects) {
		for (int i = 0; i < objects.length; i++)
			add(objects[i]);
	}

	/** Add a new object into the heap */
	public void add(E newObject) {
		list.add(newObject); // Append to the heap
		int currentIndex = list.size() - 1; // The index of the last node

		while (currentIndex > 0) {
			int parentIndex = (currentIndex - 1) / 2;
			// Swap if the current object is greater than its parent
			if (list.get(currentIndex).compareTo(
					list.get(parentIndex)) > 0) {
				E temp = list.get(currentIndex);
				list.set(currentIndex, list.get(parentIndex));
				list.set(parentIndex, temp);
			}
			else
				break; // the tree is a heap now

			currentIndex = parentIndex;
		}
	}

	/** Remove the root from the heap */
	public E remove() {
		if (list.size() == 0) return null;

		E removedObject = list.get(0);
		list.set(0, list.get(list.size() - 1));
		list.remove(list.size() - 1);

		int currentIndex = 0;
		while (currentIndex < list.size()) {
			int leftChildIndex = 2 * currentIndex + 1;
			int rightChildIndex = 2 * currentIndex + 2;

			// Find the maximum between two children
			if (leftChildIndex >= list.size()) break; // The tree is a heap
			int maxIndex = leftChildIndex;
			if (rightChildIndex < list.size()) {
				if (list.get(maxIndex).compareTo(
						list.get(rightChildIndex)) < 0) {
					maxIndex = rightChildIndex;
				}
			}

			// Swap if the current node is less than the maximum
			if (list.get(currentIndex).compareTo(
					list.get(maxIndex)) < 0) {
				E temp = list.get(maxIndex);
				list.set(maxIndex, list.get(currentIndex));
				list.set(currentIndex, temp);
				currentIndex = maxIndex;
			}
			else
				break; // The tree is a heap
		}
		return removedObject;
	}

	/** Get the number of nodes in the tree */
	public int getSize() {
		return list.size();
	}

	/** Return true if heap is empty */
	public boolean isEmpty() {
		return list.size() == 0;
	}
}


class HuffmanCode {
	public static void main(String[] args) {
		Scanner input = new Scanner(System.in);
		System.out.print("Enter a text: ");
		String text = input.nextLine();

		int[] counts = getCharacterFrequency(text); // Count frequency

		System.out.printf("%-15s%-15s%-15s%-15s\n",
				"ASCII Code", "Character", "Frequency", "Code");  

		Tree tree = getHuffmanTree(counts); // Create a Huffman tree
		String[] codes = getCode(tree.root); // Get codes

		for (int i = 0; i < codes.length; i++)
			if (counts[i] != 0) // (char)i is not in text if counts[i] is 0
				System.out.printf("%-15d%-15s%-15d%-15s\n", 
						i, (char)i + "", counts[i], codes[i]);
		input.close();
	}

	/** Get Huffman codes for the characters 
	 * This method is called once after a Huffman tree is built
	 */
	public static String[] getCode(Tree.Node root) {
		if (root == null) return null;    
		String[] codes = new String[2 * 128];
		assignCode(root, codes);
		return codes;
	}

	/* Recursively get codes to the leaf node */
	private static void assignCode(Tree.Node root, String[] codes) {
		if (root.left != null) {
			root.left.code = root.code + "0";
			assignCode(root.left, codes);

			root.right.code = root.code + "1";
			assignCode(root.right, codes);
		}
		else {
			codes[(int)root.element] = root.code;
		}
	}

	/** Get a Huffman tree from the codes */  
	public static Tree getHuffmanTree(int[] counts) {
		// Create a heap to hold trees
		Heap<Tree> heap = new Heap<>(); // Defined in Listing 24.10
		for (int i = 0; i < counts.length; i++) {
			if (counts[i] > 0)
				heap.add(new Tree(counts[i], (char)i)); // A leaf node tree
		}

		while (heap.getSize() > 1) { 
			Tree t1 = heap.remove(); // Remove the smallest weight tree
			Tree t2 = heap.remove(); // Remove the next smallest weight 
			heap.add(new Tree(t1, t2)); // Combine two trees
		}

		return heap.remove(); // The final tree
	}

	/** Get the frequency of the characters */
	public static int[] getCharacterFrequency(String text) {
		int[] counts = new int[256]; // 256 ASCII characters

		for (int i = 0; i < text.length(); i++)
			counts[(int)text.charAt(i)]++; // Count the character in text

		return counts;
	}

	/** Define a Huffman coding tree */
	static class Tree implements Comparable<Tree> {
		Node root; // The root of the tree

		/** Create a tree with two subtrees */
		public Tree(Tree t1, Tree t2) {
			root = new Node();
			root.left = t1.root;
			root.right = t2.root;
			root.weight = t1.root.weight + t2.root.weight;
		}

		/** Create a tree containing a leaf node */
		public Tree(int weight, char element) {
			root = new Node(weight, element);
		}

		@Override /** Compare trees based on their weights */
		public int compareTo(Tree t) {
			if (root.weight < t.root.weight) // Purposely reverse the order
				return 1;
			else if (root.weight == t.root.weight)
				return 0;
			else
				return -1;
		}

		class Node {
			char element; // Stores the character for a leaf node
			int weight; // weight of the subtree rooted at this node
			Node left; // Reference to the left subtree
			Node right; // Reference to the right subtree
			String code = ""; // The code of this node from the root

			/** Create an empty node */
			public Node() {
			}

			/** Create a node with the specified weight and character */
			public Node(int weight, char element) {
				this.weight = weight;
				this.element = element;
			}
		}
	}  
}

class BitOutputStream implements AutoCloseable {
	private ObjectOutputStream output;
	private int bits;
	private int position;

	public BitOutputStream(File file) throws IOException {
		// we want to write to targetfile so we wrapped it in an ObjectOutputStream
		output = new ObjectOutputStream (new FileOutputStream(file));
	}

	public void writeObject(Object object) throws IOException {
		// wrap ObjectOutputStream's writeObject method
		output.writeObject(object);
	}

	public void writeInt(int n) throws IOException {
		// wrap ObjectOutputStream's writeInt method
		output.writeInt(n);
	}

	public void writeBit(char bit) throws IOException {
		bits = bits << 1;

		if (bit == '1') {
			bits = bits | 1;
		}

		position++;

		if (position == 8) {
			output.write(bits);
			bits = 0;
			position = 0;
		}	
	}

	public void writeBit(String bitString) throws IOException {
		for (int i = 0; i < bitString.length(); i++) {
			writeBit(bitString.charAt(i));
		}
	}

	public void close() throws IOException {
		if (bits != 0) {
			bits = bits << (8 - position);
			output.write(bits);
		}
		output.close();
	}
}

public class CompressFinal {
	public static void main(String[] args) throws IOException {
		if (args.length != 2) {
			System.out.println("Usage: java groupProject sourceFile targetFile");
			System.exit(0);
		}

		File sourceFile = new File (args[0]);
		if (!sourceFile.exists()) {
			System.out.println(sourceFile.getName() + " does not exist");
			System.exit(1);		
		}

		File targetFile = new File(args[1]);
		if (targetFile.exists()) {
			System.out.println(targetFile.getName() + " exists");
			System.exit(2);
		}

		// obtaining text from the sourceFile
		String sbText = new String(Files.readAllBytes(Paths.get(args[0])));

		// generate Huffman Code using HuffmanCode Class for the text
		int[] counts = HuffmanCode.getCharacterFrequency(sbText);
		HuffmanCode.Tree tree = HuffmanCode.getHuffmanTree(counts);
		String [] codes = HuffmanCode.getCode(tree.root);

		// encoding text using HuffmanCode class
		StringBuilder sbEncoded = new StringBuilder();
		for (int i = 0; i < sbText.length(); i++) {
			char c = sbText.charAt(i);
			sbEncoded.append(codes[(int)c]);
		}

		// writing codes to targetFile using BitOutputStream class
		try (
				BitOutputStream bitOut = new BitOutputStream(targetFile);
				) {
			bitOut.writeObject(codes);
			bitOut.writeBit(sbEncoded.toString());
		}
	} 
}
