import java.io.*;
import java.util.*;

public class Main {
	static int n, m, k;
	static Turret[][] map;
	
	static boolean[][] attacked;
	static List<Turret> attackers;
	static List<Turret> defencers;
	
	static int[] rows4 = {0, 1, 0, -1};
	static int[] cols4 = {1, 0, -1, 0};
	static int[] rows8 = {-1, -1, -1, 0, 1, 1, 1, 0};
	static int[] cols8 = {-1, 0, 1, 1, 1, 0, -1, -1};
	
	static class Turret{
		int row, col;
		int power;
		int seq;
		
		public Turret(int row, int col, int power, int seq) {
			this.row = row;
			this.col = col;
			this.power = power;
			this.seq = seq;
		}
		
		@Override
		public String toString() {
			return "[" + row + ", " + col + "] " + power + " " + seq;
		}
	}
	
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		
		n = Integer.parseInt(st.nextToken());
		m = Integer.parseInt(st.nextToken());
		k = Integer.parseInt(st.nextToken());
		
		map = new Turret[n][m];
		attacked = new boolean[n][m];
		attackers = new ArrayList<>();
		defencers = new ArrayList<>();
		
		for(int i = 0; i < n; i++) {
			st = new StringTokenizer(br.readLine());
			for(int j = 0; j < m; j++) {
				int power = Integer.parseInt(st.nextToken());
				
				Turret turret = new Turret(i, j, power, 1);
				map[i][j] = turret;
				
				if(power > 0) {
					attackers.add(turret);
					defencers.add(turret);
				}
			}
		}
		
		for(int i = 0; i < k; i++) {
			game();
		}
		
//		printMap();
		
		System.out.println(findStrongest().power);
	}
	
	static void printMap() {
		for(Turret[] arr : map) {
			for(Turret t : arr) System.out.print(t.power + " ");
			System.out.println();
		}
	}
	
	static void game() {
		Turret attacker = findWeakest();
		Turret defencer = findStrongest();
		
		attacker.power += n + m;
		
//		System.out.println("공격 전");
//		printMap();
//		System.out.println();
		
		attack(attacker, defencer);
//		System.out.println("공격 후");
//		printMap();
//		System.out.println();
		
		repair();
//		System.out.println("정비 후");
//		printMap();
//		System.out.println();
		
		increaseSeq(attacker);
		
	}
	
	static int weakest(Turret o1, Turret o2) {
		int r1 = o1.row, c1 = o1.col;
		int r2 = o2.row, c2 = o2.col;
		
		if(o1.power == o2.power) {
			if(o1.seq == o2.seq) {
				if(r1 + c1 == r2 + c2) return c2 - c1;
				return (r2 + c2) - (r1 + c1);
			}
			return o1.seq - o2.seq;
		}
		return o1.power - o2.power;
	}
	
	static int strongest(Turret o1, Turret o2) {
		int r1 = o1.row, c1 = o1.col;
		int r2 = o2.row, c2 = o2.col;
		
		if(o2.power == o1.power) {
			if(o2.seq == o1.seq) {
				if(r1 + c1 == r2 + c2) return c1 - c2;
				return (r1 + c1) - (r2 + c2);
			}
			return o2.seq - o1.seq;
		}
		return o2.power - o1.power;
	}
	
	static Turret findWeakest() {
		Collections.sort(attackers, (o1, o2) -> weakest(o1, o2));
//		System.out.println("공격 리스트" + attackers);
		Turret attacker = attackers.get(0);
		return attacker;
	}
	
	static Turret findStrongest() {
		Collections.sort(defencers, (o1, o2) -> strongest(o1, o2));
//		System.out.println("수비 리스트" + defencers);
		Turret defencer = defencers.get(0);
		return defencer;
	}
	
	static void attack(Turret attacker, Turret defencer) {
		boolean laserAttack = laser(attacker, defencer);
//		System.out.println("레이저 공격 성고 여부 : " + laserAttack);
		if(!laserAttack) boom(attacker, defencer);
	}
	
	static boolean laser(Turret attacker, Turret defencer) {
		int[] from = new int[n * m]; // 경로를 담는 배열
		Arrays.fill(from, -2); // 초기화
		from[attacker.row * m + attacker.col] = -1; // 시작 위치
		
		findDirection(from, attacker, defencer);
		
		if(from[defencer.row * m + defencer.col] == -2) return false;
		
		descPower(from, attacker, defencer);
		
		return true;
	}
	
	static void findDirection(int[] from, Turret attacker, Turret defencer) {
		Queue<Turret> q = new ArrayDeque<>();
		q.offer(attacker);
		
		boolean[][] visited = new boolean[n][m];
		visited[attacker.row][attacker.col] = true;
		
		while(!q.isEmpty()) {
			Turret cur = q.poll();
			
			if(cur.row == defencer.row && cur.col == defencer.col) return;
			
			for(int i = 0; i < 4; i++) {
				int nr = (cur.row + rows4[i] + n) % n;
				int nc = (cur.col + cols4[i] + m) % m;
				
				if(isRange(nr, nc) && !visited[nr][nc] && from[nr * m + nc] == -2) {
					q.offer(map[nr][nc]);
					from[nr * m + nc] = cur.row * m + cur.col;
				}
			}
		}
	}
	
	static void descPower(int[] from, Turret attacker, Turret defencer) {
		int row = defencer.row;
		int col = defencer.col;
		while(from[row * m + col] > -1) {
			Turret target = map[row][col];
			
			if(defencer.row == target.row && defencer.col == target.col)
				target.power -= attacker.power;
			else 
				target.power -= attacker.power / 2;
			
			if(target.power <= 0) {
				boolean rd = defencers.remove(target);
				boolean ra = attackers.remove(target);
				target.power = 0;
//				System.out.print("포탑 삭제 : ");
//				System.out.println(rd + " " + ra);
			}
			
			attacked[row][col] = true;
			
			int r = from[row * m + col] / m;
			int c = from[row * m + col] % m;
			
			row = r;
			col = c;
		}
		attacked[row][col] = true;
	}
	
	static void boom(Turret attacker, Turret defencer) {
		int row = defencer.row;
		int col = defencer.col;
		
		map[row][col].power -= attacker.power;
		if(map[row][col].power <= 0) {
			map[row][col].power = 0;
			defencers.remove(map[row][col]);
			attackers.remove(map[row][col]);
		}
		
		attacked[row][col] = true;
		
		for(int i = 0; i < 8; i++) {
			int nr = (row + rows8[i] + n) % n;
			int nc = (col + cols8[i] + m) % m;
			
			if(isRange(nr, nc) && !(attacker.row == nr && attacker.col == nc)) {
				map[nr][nc].power -= attacker.power / 2;
				if(map[nr][nc].power <= 0) {
					defencers.remove(map[nr][nc]);
					attackers.remove(map[nr][nc]);
				}
				attacked[nr][nc] = true;
			}
		}
	}
	
	static boolean isRange(int nr, int nc) {
		return map[nr][nc].power > 0;
	}
	
	static void repair() {
		for(int i = 0; i < n; i++) {
			for(int j = 0; j < m; j++) {
				if(attacked[i][j]) continue;
				if(map[i][j].power == 0) continue;
				
				map[i][j].power++;
			}
		}
	}

	static void increaseSeq(Turret attacker) {
		for(int i = 0; i < n; i++) {
			for(int j = 0; j < m; j++) {
				if(map[i][j].power == 0) continue;
				if(map[i][j] == attacker) continue;
				map[i][j].seq++;
			}
		}
	}
}