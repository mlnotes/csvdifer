package com.mlnotes.csvdiffer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVDiffer {
	public void compare(String fileA, String fileB) throws IOException {
		List<String> linesA = fileToLines(fileA);
		List<String> linesB = fileToLines(fileB);
		List<Pair> modifiedRows = diff(linesA, linesB);
		
		for(Pair pair : modifiedRows) {
			System.out.printf("[%d <=> %d]%n", pair.getLeft(), 
					pair.getRight());
			
			if(pair.left != -1 && pair.right != -1) {
				List<Pair> modifiedCells = diff(
						splitCSVRow(linesA.get(pair.getLeft())),
						splitCSVRow(linesB.get(pair.getRight())));
				
				for(Pair p : modifiedCells) {
					System.out.printf("  [%d <=> %d],", p.getLeft(), 
							p.getRight());
				}
				System.out.println();
			}
		}
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
	
	private List<Pair> diff(List<String> original, 
			List<String> modified) {
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
		//int delta = distance(original, modified, 0, 0, dist);
		//System.out.printf("Distance: %d%n", delta);
		// TODO add some log
		distance(original, modified, 0, 0, dist);
		
		List<Pair> changes = 
				new ArrayList<Pair>();
		
		findChanges(dist, 0, 0, changes);
		
		return changes;
	}
	
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
	
	private void findChanges(int[][] dist, int x, int y,
			List<Pair> changes) {
		if(dist[x][y] == 0) {
			return;
		}
		
		int m = dist.length - 1;
		int n = dist[0].length - 1;
		
		// find the eage
		if(x == m-1 && y == n-1) {
			if(dist[x][y] == 1){
				changes.add(new Pair(x, y));
			}
			return;
		}else if(x == m-1) {
			if(dist[x][y+1] != -1) {
				changes.add(new Pair(-1, y));
				y++;
			} else {
				for(int i = dist[x][y]; i > 0; --i) {
					changes.add(new Pair(-1, n-i));
				}
				return;
			}
		} else if(y == n-1) {
			if(dist[x+1][y] != -1) {
				changes.add(new Pair(x, -1));
				x++;
			} else {
				for(int i = dist[x][y]; i > 0; --i) {
					changes.add(new Pair(m-i, -1));
				}
				return;
			}
		} else if(dist[x][y] == dist[x+1][y+1]+1) {
			changes.add(new Pair(x, y));
			x++;
			y++;
		} else if(dist[x][y] == dist[x+1][y]+1) {
			changes.add(new Pair(x, -1));
			x++;
		} else if(dist[x][y] == dist[x][y+1]+1) {
			changes.add(new Pair(-1, y));
			y++;
		} else if(dist[x][y] == dist[x+1][y+1]) {
			x++;
			y++;
		}
		
		findChanges(dist, x, y, changes);
	}
	
	private List<String> splitCSVRow(String row) {
		List<String> cells = new ArrayList<String>();
		String[] parts = row.split(",");
		for(String part : parts) {
			cells.add(part);
		}
		// TODO how handle csv row splitting better
		// for example if there is comma in cell
		return cells;
	}
	
	public static void main(String[] args) throws IOException {
		CSVDiffer differ = new CSVDiffer();
		
		String original = "/usr/local/google/home/hanfeng/Downloads/test/name.csv";
		String modified = "/usr/local/google/home/hanfeng/Downloads/test/name2.csv";

		differ.compare(original, modified);
	}
	
	public static class Pair {
		private int left;
		private int right;
		public Pair(int left, int right) {
			this.left = left;
			this.right = right;
		}
		public int getLeft() {
			return left;
		}
		public int getRight() {
			return right;
		}
	}
}
