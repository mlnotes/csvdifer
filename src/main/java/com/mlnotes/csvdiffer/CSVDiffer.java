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
			dist[oStart][mStart] = 1 + Math.min(Math.min(
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
			dist[i][n] = m-i;
		}
		for(int j = 0; j <= n; ++j) {
			dist[m][j] = n-j;
		}
		int delta = distance(original, modified, 0, 0, dist);
		System.out.printf("Distance:%d%n", delta);
		
		List<Pair<Integer, Integer>> changes = 
				new ArrayList<Pair<Integer, Integer>>();
		
		//output(dist);
		
		findChanges(dist, 0, 0, changes);
		for(Pair<Integer, Integer> entry : changes) {
			System.out.printf("%d => %d%n", entry.getLeft(), entry.getRight());
		}
	}
	
	private void findChanges(int[][] dist, int x, int y,
			List<Pair<Integer, Integer>> changes) {
		// TODO how to find changes chain !
		int m = dist.length - 1;
		int n = dist[0].length - 1;
		
		// find the eage
		if(x == m-1 && y == n-1) {
			if(dist[x][y] == 1){
				changes.add(new Pair<Integer, Integer>(x, y));
			}
			return;
		}else if(x == m-1) {
			if(dist[x][y+1] != -1) {
				changes.add(new Pair<Integer, Integer>(-1, y));
				y++;
			} else {
				for(int i = dist[x][y]; i > 0; --i) {
					changes.add(new Pair<Integer, Integer>(-1, n-i));
				}
				return;
			}
		} else if(y == n-1) {
			if(dist[x+1][y] != -1) {
				changes.add(new Pair<Integer, Integer>(x, -1));
				x++;
			} else {
				for(int i = dist[x][y]; i > 0; --i) {
					changes.add(new Pair<Integer, Integer>(m-i, -1));
				}
				return;
			}
		} else if(dist[x][y] == dist[x+1][y+1]+1) {
			changes.add(new Pair<Integer, Integer>(x, y));
			x++;
			y++;
		} else if(dist[x][y] == dist[x+1][y]+1) {
			changes.add(new Pair<Integer, Integer>(x, -1));
			x++;
		} else if(dist[x][y] == dist[x][y+1]+1) {
			changes.add(new Pair<Integer, Integer>(-1, y));
			y++;
		} else if(dist[x][y] == dist[x+1][y+1]) {
			x++;
			y++;
		}
		
		findChanges(dist, x, y, changes);
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
	
	private void output(int[][] dist) {
		for(int i = 0; i < dist.length; ++i) {
			for(int j = 0; j < dist[i].length; ++j) {
				System.out.print(dist[i][j] + ", ");
			}
			System.out.println();
		}
	}
	
	public static void main(String[] args) throws IOException {
		CSVDiffer differ = new CSVDiffer();
		
		String original = "/usr/local/google/home/hanfeng/Downloads/name.csv";
		String modified = "/usr/local/google/home/hanfeng/Downloads/name2.csv";
		//String original = "data1/1.txt";
		//String modified = "data1/2.txt";

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
