package com.mlnotes.csvdiffer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVDiffer {
	
	private int distance(List<String> original, List<String> modified, 
			int oStart, int mStart, int[][] dist) {
		if(dist[oStart][mStart] != -1) {
			return dist[oStart][mStart];
		}
		
		if(original.get(oStart).equals(modified.get(mStart))) {
			dist[oStart][mStart] = 
					distance(original, modified, oStart+1, mStart+1, dist);
		} else {
			dist[oStart][mStart] = Math.min(Math.min(
					distance(original, modified, oStart+1, mStart, dist),
					distance(original, modified, oStart, mStart+1, dist)),
					distance(original, modified, oStart+1, mStart+1, dist));			
		}
		
		return dist[oStart][mStart];
	}
	
	private void diff(List<String> original, List<String> modified) {
		int m = original.size();
		int n = modified.size();
		int[][] dist = new int[m+1][n+1];
		for(int i = 0; i < m; ++i) {
			for(int j = 0; j < n; ++j) {
				dist[i][j] = -1;
			}
		}
		for(int i = 0; i <= m; ++i) {
			dist[i][n] = 0;
		}
		for(int j = 0; j <= n; ++j) {
			dist[m][j] = 0;
		}
		int delta = distance(original, modified, 0, 0, dist);
		System.out.printf("Distance:%d%n", delta);
		
		List<Pair<Integer, Integer>> changes = 
				new ArrayList<Pair<Integer, Integer>>();
		findChanges(dist, 0, 0, changes);
		for(Pair<Integer, Integer> entry : changes) {
			System.out.printf("%d => %d%n", entry.getLeft(), entry.getRight());
		}
	}
	
	private void findChanges(int[][] dist, int i, int j,
			List<Pair<Integer, Integer>> changes) {
		// TODO compute changes basing on dist table
		if(dist.length == 0 || i >= dist.length || j >= dist[0].length) {
			return;
		}
		
		
		
		
	}
	
	public void compare(String fileA, String fileB) throws IOException {
		List<String> linesA = fileToLines(fileA);
		List<String> linesB = fileToLines(fileB);
		
		diff(linesA, linesB);
	}
	
	private List<String> fileToLines(String fileName) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(fileName));		
		List<String> lines = new ArrayList<String>();
		String line;
		while((line = reader.readLine()) != null) {
			lines.add(line);
		}
		
		reader.close();
		return lines;
	}
	
	public static void main(String[] args) throws IOException {
		CSVDiffer differ = new CSVDiffer();
		
		String original = "D:\\workspaces\\java\\a.txt";
		String modified = "D:\\workspaces\\java\\b.txt";
		
		differ.compare(original, modified);
	}
	
	public static class Pair<A, B> {
		private A left;
		private B right;
		public Pair(A left, B right) {
			this.left = left;
			this.right = right;
		}
		public A getLeft() {
			return left;
		}
		public B getRight() {
			return right;
		}
	}
}
