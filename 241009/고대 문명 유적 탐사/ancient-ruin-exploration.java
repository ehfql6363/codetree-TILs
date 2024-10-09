import java.io.*;
import java.util.*;

public class Main {
	static int k;
	static int m;
	static int[][] map;
	static Queue<Integer> numbers;
	
	static int[] rows8 = {-1, -1, -1, 0, 1, 1, 1, 0};
	static int[] cols8 = {-1, 0, 1, 1, 1, 0, -1, -1};
	static int[] rows4 = {-1, 1, 0, 0};
	static int[] cols4 = {0, 0, -1, 1};
	
	static int[] rotationArr;
	static int index;
	static int ans;
	
	static List<int[]> finalBag;
	static List<int[]> subBag;
	
	static class Record {
		int[] position = new int[2];
		int dir;
		
		public Record(int[] position, int dir) {
			this.position[0] = position[0];
			this.position[1] = position[1];
			this.dir = dir;
		}
	}
	
	static Record record = new Record(new int[] {-1, -1}, -1);
	public static void main(String[] agrs) throws Exception{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		StringBuilder sb = new StringBuilder();
		
		input(br, st);
		
		while(k-- > 0) {
			ans = 0;
			process();
			
			if(ans == 0) break;
			
			sb.append(ans + " ");
		}
		
		System.out.println(sb);
	}
	
	static void input(BufferedReader br, StringTokenizer st) throws Exception {
		k = Integer.parseInt(st.nextToken());
		m = Integer.parseInt(st.nextToken());
		map = new int[5][5];
		numbers = new ArrayDeque<>();
		rotationArr = new int[8];
		index = 0;
		ans = 0;
		
		for(int i = 0; i < 5; i++) {
			st = new StringTokenizer(br.readLine());
			for(int j = 0; j < 5; j++) {
				map[i][j] = Integer.parseInt(st.nextToken());
			}
		}
		
		st = new StringTokenizer(br.readLine());
		for(int i = 0; i < m; i++) {
			numbers.offer(Integer.parseInt(st.nextToken()));			
		}
	}
	
	static void process() {
		finalBag = new ArrayList<>();
		find();
		
		boolean first = true;
		
		while(!finalBag.isEmpty()) {
			ans += finalBag.size();
			
			Collections.sort(finalBag, (o1, o2) -> {
				if(o1[1] == o2[1]) return Integer.compare(o2[0], o1[0]);
				return Integer.compare(o1[1], o2[1]);
			});
			
			if(first) {
				setArr(record.position[0], record.position[1]);
				setAnswer();
				first = false;
			}
			
			fill();
			
			reSearch();
		}
	}
	
	static void find() {
		for(int i = 1; i < 4; i++) {
			for(int j = 1; j < 4; j++) {
				setArr(i, j);
				
				int dir = 0;
				while(dir++ < 3) {
					rotate(i, j);
					search(i, j, dir);
				}
				rotate(i, j); // 원상복구용
			}
		}
	}
	
 	static void setArr(int row, int col) {
		for(int i = 0; i < 8; i++) {
			rotationArr[i] = map[row + rows8[i]][col + cols8[i]];
		}
	}
	
	static void setMap(int row, int col) {
		for(int i = 0; i < 8; i++) {
			map[row + rows8[i]][col + cols8[i]] = rotationArr[(index + i) % 8];
		}
	}
	
	static void rotate(int row, int col) { // 90도 회전시키는 함수
		index += 6;
		
		setMap(row, col);
	}
	
	static void search(int row, int col, int dir) {
		subBag = new ArrayList<>();
		
		boolean[][] visited = new boolean[5][5];
		for(int i = 0; i < 5; i++) {
			for(int j = 0; j < 5; j++) {
				bfs(i, j, visited);
			}
		}
		
		if(shouldChange(row, col, dir)) change(row, col, dir);
	}
	
	static void reSearch() {
		subBag = new ArrayList<>();
		
		boolean[][] visited = new boolean[5][5];
		for(int i = 0; i < 5; i++) {
			for(int j = 0; j < 5; j++) {
				bfs(i, j, visited);
			}
		}
		
		finalBag = new ArrayList<>();
		putIn(subBag, finalBag);
	}
	
	static void bfs(int row, int col, boolean[][] visited) {
		Queue<int[]> q = new ArrayDeque<>();
		q.offer(new int[] {row, col});
		
		int target = map[row][col];
		
		visited[row][col] = true;
		
		List<int[]> list = new ArrayList<>();
		
		while(!q.isEmpty()) {
			int[] cur = q.poll();
			int r = cur[0];
			int c = cur[1];
			
			if(target == map[r][c]) list.add(new int[] {r, c});
			
			for(int i = 0; i < 4; i++) {
				int dr = r + rows4[i];
				int dc = c + cols4[i];
				
				if(isRange(dr, dc) && target == map[dr][dc] && !visited[dr][dc]) {
					q.offer(new int[] {dr, dc});
					visited[dr][dc] = true;
				}
			}
		}
		
		if(list.size() >= 3) putIn(list, subBag);
	}
	
	static boolean shouldChange(int row, int col, int dir) {
		if (finalBag.size() < subBag.size()) {
	        return true;
	    }
	    
	    if (finalBag.size() == subBag.size()) {
	        if (record.dir == dir) {
	            if (record.position[1] == col) {
	                return record.position[0] > row;
	            }
	            return record.position[1] > col;
	        }
	        return record.dir > dir;
	    }

	    return false;
	}
	
	static void change(int row, int col, int dir) {
		record.position[0] = row;
		record.position[1] = col;
		record.dir = dir;
		
		finalBag = new ArrayList<>();
		putIn(subBag, finalBag);
	}
	
 	static void putIn(List<int[]> from, List<int[]> to) {
		for(int[] pos : from) to.add(new int[] {pos[0], pos[1]});
	}
	
 	static void setAnswer() {
 		int row = record.position[0];
 		int col = record.position[1];
 		int dir = record.dir;
 		
 		for(int i = 0; i < 8; i++) {
			map[row + rows8[i]][col + cols8[i]] = rotationArr[(dir * 6 + i) % 8];
		}
 	}
 	
	static void fill() {
		for(int[] pos : finalBag) {
			int row = pos[0];
			int col = pos[1];
			
			map[row][col] = numbers.poll();
		}
	}

	static boolean isRange(int dr, int dc) {
		return dr >= 0 && dc >= 0 && dr < 5 && dc < 5;
	}
}