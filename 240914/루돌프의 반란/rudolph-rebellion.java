import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.StringTokenizer;

public class Main {
    static int n; // 게임판 크기
    static int m; // 게임 턴 수
    static int p; // 산타의 수
    static int c; // 루돌프의 힘
    static int d; // 산타의 힘
    static int[][] map;
    static int[] rows = {-1, 0, 1, 0, -1, -1, 1, 1};
    static int[] cols = {0, 1, 0, -1, -1, 1, 1, -1};
    static int[] scores;
    static boolean[] retire;
    static class Rudolf{
        int row;
        int col; 
        public Rudolf(int row, int col) {
            this.row = row;
            this.col = col;
        }
    }
    static class Santa{
        int no;
        int row;
        int col;
        int isStun = 0;
        
        public Santa(int no, int row, int col) {
            this.no = no;
            this.row = row;
            this.col = col;
        }
        @Override
        public String toString() {
            return no + " " + row + " " + col + " " + isStun;
        }
    }
    public static void main(String[] args) throws Exception{
        // 여기에 코드를 작성해주세요.
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        StringBuilder sb = new StringBuilder();

        n = Integer.parseInt(st.nextToken());
        m = Integer.parseInt(st.nextToken());
        p = Integer.parseInt(st.nextToken());
        c = Integer.parseInt(st.nextToken());
        d = Integer.parseInt(st.nextToken());
        
        map = new int[n][n];
        
        st = new StringTokenizer(br.readLine());
        Rudolf rudolf = new Rudolf(Integer.parseInt(st.nextToken()) - 1, 
                                   Integer.parseInt(st.nextToken()) - 1);
        
        map[rudolf.row][rudolf.col] = -1;
        
        Santa[] santas = new Santa[p + 1];
        for(int i = 0; i < p; i++) {
            st = new StringTokenizer(br.readLine());
            int no = Integer.parseInt(st.nextToken());
            santas[no] = new Santa(no,
                                  Integer.parseInt(st.nextToken()) - 1,
                                  Integer.parseInt(st.nextToken()) - 1);
            
            map[santas[no].row][santas[no].col] = no;
        }
        
        retire = new boolean[p + 1];
        scores = new int[p + 1];
        
        play(santas, rudolf);
        
        for(int i = 1; i < p + 1; i++) sb.append(scores[i] + " ");
        System.out.println(sb);
    }
    static void play(Santa[] santas, Rudolf rudolf) {
        for(int round = 0; round < m; round++) {
            // 가장 가까이 있는 산타 정하기
            Santa target = getNearSanta(santas, rudolf);
            
//            System.out.println("=============== " + (round + 1) + " ==================");
            
            // 없으면 나가기
            if(target == null) return;
//            System.out.println("가장 가까운 산타 : " + target.toString());
            
            
            // 루돌프 : 가장 가까운 산타쪽으로 이동
            moveToSanta(santas, target, rudolf);
            
            // 디버깅
//            System.out.println("루돌프 움직이고 난 뒤");
//            for(int[] a : map) System.out.println(Arrays.toString(a));
//            System.out.println();
            
            // 산타 : 루돌프쪽으로 이동
            moveToRudolf(santas, rudolf);
            
            // 디버깅
//            System.out.println("산타 움직이고 난 뒤");
//            for(int[] a : map) System.out.println(Arrays.toString(a));
//            System.out.println();
            // 디버깅
//            for(int[] a : map) System.out.println(Arrays.toString(a));
            
            // 루돌프와 산타가 이동을 마치면
            // 살아 남은 산타에게 점수 1점 추가
            getPoint();
//            System.out.println("retire : " + Arrays.toString(retire));
//            System.out.println("score : " + Arrays.toString(scores));
        }
    }
    static void getPoint() {
        for(int i = 1; i < p + 1; i++) {
            if(retire[i]) continue;
            scores[i]++;
        }
    }
    static Santa getNearSanta(Santa[] santas, Rudolf rudolf) {
        int dist = Integer.MAX_VALUE;
        Santa target = null;
        for(int i = 1; i < p + 1; i++) {
            if(retire[i]) continue;
            Santa santa = santas[i];
            
            int d = (int) (Math.pow(santa.row - rudolf.row, 2) + 
                           Math.pow(santa.col - rudolf.col, 2));
            
            if(dist < d) continue;
            if(dist == d) {
                if(target.row > santa.row) continue;
                if(target.row < santa.row) {
                    target = santa;
                    continue;
                }
                
                if(target.col > santa.col) continue;
                target = santa;
            }
            
            target = santa;
            dist = d;
        }
        
        return target;
    }
    
    static void moveToSanta(Santa[] santas, Santa target, Rudolf rudolf) {
        int row = 0; // 최소 거리가 되는 row
        int col=  0; // 최소 거리가 되는 col
        
        double dist = Double.MAX_VALUE; // 최소 거리
        
        for(int i = 0; i < 8; i++) {
            int dr = rudolf.row + rows[i]; // 현재 루돌프 위치에서 한 칸 이동할 때 row
            int dc = rudolf.col + cols[i]; // 현재 루돌프 위치에서 한 칸 이동할 때 col
            
            if(!isInRange(dr, dc)) continue; // 장외면 패스
            if(dr == target.row && dc == target.col) { // 산타랑 부딪히면
//                System.out.println("dir = " + i);
                bump(santas, target, i, dr, dc, 2, c); // 충돌 메서드 실행
                row = dr;
                col = dc;
                break;
            }
            
            double dd = Math.pow(target.row - dr, 2) + 
                        Math.pow(target.col - dc, 2);
            
            if(dist <= dd) continue;
            dist = dd;
            row = dr;
            col = dc;
        }
        
        map[row][col] = -1;
        map[rudolf.row][rudolf.col] = 0;
        rudolf.row = row;
        rudolf.col = col;
    }
    
    static void moveToRudolf(Santa[] santas, Rudolf rudolf) {
        for(int i = 1; i < p + 1; i++) { // 모든 산타를 돌면서
            Santa santa = santas[i];
            
            if(retire[santa.no]) continue; // 장외면 패스
            if(santa.isStun > 0) { // 스턴 상태면
                santa.isStun--; // 풀어주고
                continue; // 패스
            }
            
//            System.out.println(i + " : " + santa.no);
            int row = santa.row; // 이동 전 row 저장
            int col = santa.col; // 이동 후 col 저장
            double dist = Math.pow(row - rudolf.row, 2) + 
                              Math.pow(col - rudolf.col, 2); // 이동 전 루돌프까지의 거리 및 최소 거리
            
            for(int j = 0; j < 4; j++) { // 상우하좌 순
                int dr = row + rows[j]; // 이동 할 row
                int dc = col + cols[j]; // 이동 할 col
                
                if(!isInRange(dr, dc)) continue;
                if(map[dr][dc] > 0) continue;
                if(map[dr][dc] == -1) { // 루돌프랑 부딪히면
                    bump(santas, santa, (j + 2) % 4, dr, dc, 1, d); // 부딪히는 함수
                    break; // 주변 탐색 안해도 됨.
                }
                
                // 이동 할 위치와 루돌프 사이의 거리
                double dd = Math.pow(dr - rudolf.row, 2) + 
                            Math.pow(dc - rudolf.col, 2);
                
                if(dist <= dd) continue; // 멀어지거나 이전 최소 거리와 같으면 패스
                dist = dd; // 최소 거리 갱신
                santa.row = dr; // 이동 할 row 갱신
                santa.col = dc; // 이동 할 col 갱신
            }
            
            map[row][col] = 0; // 이전 히스토리 삭제
            map[santa.row][santa.col] = santa.no; // 산타 이동
            
//            System.out.println("한 명씩 이동 후");
//            for(int[] a : map) System.out.println(Arrays.toString(a));
//            System.out.println(Arrays.toString(retire));
//            System.out.println();
        }
    }
    
    static boolean isInRange(int dr, int dc) {
        if(dr >= 0 && dr < n && dc >= 0 && dc < n) return true;
        return false;
    }
    
    static void bump(Santa[] santas, Santa santa, int dir, int rr, int cc, int stun, int push) {
    	map[santa.row][santa.col] = 0;
    	int row = rr;
        int col = cc;
        row += rows[dir] * push;
        col += cols[dir] * push;
//        System.out.println(row + " " + col);
        
        scores[santa.no] += push;
        
        if(!isInRange(row, col)) {
            retire[santa.no] = true;
            map[santa.row][santa.col] = 0;
            return;
        }
        
        updateSantaPos(santas, santa, row, col, dir);
        updateMap(santas);
        
        // 타겟 산타의 위치 및 상태 갱신
        map[row][col] = santa.no;
        santa.row = row;
        santa.col = col;
        santa.isStun = stun;
    }
    static void updateMap(Santa[] santas) {
        for(Santa sa : santas) {
            if(sa == null) continue;
            if(retire[sa.no]) continue;
            map[sa.row][sa.col] = sa.no;
        }
    }
    static void updateSantaPos(Santa[] santas, Santa target, int row, int col, int dir) {
        int nr = row;
        int nc = col;
        while(true) { // 밀려날 위치에 다른 산타가 있다면
            if(map[nr][nc] == 0) break;
            
            int no = map[nr][nc];
            Santa s = santas[no];
            
            s.row += rows[dir];
            s.col += cols[dir];
            
            nr = s.row;
            nc = s.col;
            if(!isInRange(nr, nc)) {
            	retire[s.no] = true;
            	break;
            }
        }
    }
}