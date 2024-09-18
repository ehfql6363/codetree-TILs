import java.io.*;
import java.util.*;

public class Main {
    static class Knight {
        int r, c;
        int h, w;
        int k;
        boolean inBoard;
        boolean isDamaged;
        int damage;

        public Knight(int r, int c, int h, int w, int k) {
            this.r = r;
            this.c = c;
            this.h = h;
            this.w = w;
            this.k = k;

            inBoard = true;
            isDamaged = false;
            damage = 0;
        }
    }

    static int[][] directions = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};

    static int L, N, Q;
    static int[][] board;
    static int[][] knightBoard;
    static Knight[] knights;

    static void process(int i, int d) {
        if (!checkMovable(i, d)) {
            return;
        }

        moveKnight(i, d);
        knights[i].isDamaged = false;
        checkDamage();
    }

    static void moveKnight(int i, int d) {
        for (int r = knights[i].r; r < knights[i].r + knights[i].h; r++) {
            for (int c = knights[i].c; c < knights[i].c + knights[i].w; c++) {
                int nr = r + directions[d][0];
                int nc = c + directions[d][1];

                int prevKnight = knightBoard[nr][nc];

                if (prevKnight == 0 || prevKnight == (i + 1)) {
                    continue;
                }

                // 이동하려는 위치에 다른 기사가 있다면 그 기사도 함께 연쇄적으로 한 칸 밀려난다.
                moveKnight(prevKnight - 1, d);
            }
        }

        switch (d) {
            case 0:
                moveUp(i, d);
                break;
            case 1:
                moveRight(i, d);
                break;
            case 2:
                moveDown(i, d);
                break;
            case 3:
                moveLeft(i, d);
                break;
        }

        knights[i].isDamaged = true;
        knights[i].r += directions[d][0];
        knights[i].c += directions[d][1];
    }

    static boolean checkMovable(int i, int d) {
        for (int r = knights[i].r; r < knights[i].r + knights[i].h; r++) {
            for (int c = knights[i].c; c < knights[i].c + knights[i].w; c++) {
                int nr = r + directions[d][0];
                int nc = c + directions[d][1];

                // 기사가 이동하려는 방향에 벽이 있다면 기사는 움직일 수 없다.
                if (isWall(nr, nc)) {
                    return false;
                }

                int prevKnight = knightBoard[nr][nc];

                if (prevKnight == 0 || prevKnight == (i + 1)) {
                    continue;
                }

                // 이동하려는 위치에 다른 기사가 있다면 그 기사도 함께 연쇄적으로 한 칸 밀려난다.
                if (!checkMovable(prevKnight - 1, d)) {
                    return false;
                }
            }
        }

        return true;
    }

    static boolean isWall(int r, int c) {
        return r < 0 || c < 0 || r >= L || c >= L || board[r][c] == 2;
    }

    static void moveUp(int i, int d) {
        for (int r = knights[i].r; r < knights[i].r + knights[i].h; r++) {
            for (int c = knights[i].c; c < knights[i].c + knights[i].w; c++) {
                int nr =  r + directions[d][0];
                int nc =  c + directions[d][1];

                knightBoard[r][c] = 0;
                knightBoard[nr][nc] = (i + 1);
            }
        }
    }

    static void moveDown(int i, int d) {
        for (int r = knights[i].r + knights[i].h - 1; r >= knights[i].r; r--) {
            for (int c = knights[i].c; c < knights[i].c + knights[i].w; c++) {
                int nr = r + directions[d][0];
                int nc = c + directions[d][1];

                knightBoard[r][c] = 0;
                knightBoard[nr][nc] = (i + 1);
            }
        }
    }

    static void moveRight(int i, int d) {
        for (int c = knights[i].c + knights[i].w - 1; c >= knights[i].c; c--) {
            for (int r = knights[i].r; r < knights[i].r + knights[i].h; r++) {
                int nr = r + directions[d][0];
                int nc = c + directions[d][1];

                knightBoard[r][c] = 0;
                knightBoard[nr][nc] = (i + 1);
            }
        }
    }

    static void moveLeft(int i, int d) {
        for (int c = knights[i].c; c < knights[i].c + knights[i].w; c++) {
            for (int r = knights[i].r; r < knights[i].r + knights[i].h; r++) {
                int nr = r + directions[d][0];
                int nc = c + directions[d][1];

                knightBoard[r][c] = 0;
                knightBoard[nr][nc] = (i + 1);
            }
        }
    }

    static void checkDamage() {
        for (int i = 0; i < N; i++) {
            if (!knights[i].isDamaged) {
                continue;
            }

            int damage = countTrap(i);

            knights[i].damage += damage;
            knights[i].k -= damage;

            // 체력이 0이 되면 체스판에서 사라진다.
            if (knights[i].k <= 0) {
                deleteKnight(i);
            }
        }
    }

    static void deleteKnight(int i) {
        knights[i].inBoard = false;

        for (int r = knights[i].r; r < knights[i].r + knights[i].h; r++) {
            for (int c = knights[i].c; c < knights[i].c + knights[i].w; c++) {
                knightBoard[r][c] = 0;
            }
        }
    }

    static int countTrap(int i) {
        int count = 0;

        // 해당 기사가 이동한 위치에서 w x h 직사각형 내 존재하는 함정 개수 만큼 피해 발생
        for (int r = knights[i].r; r < knights[i].r + knights[i].h; r++) {
            for (int c = knights[i].c; c < knights[i].c + knights[i].w; c++) {
                if (isWall(r, c)) {
                    continue;
                }

                if (board[r][c] == 1) {
                    count++;
                }
            }
        }

        return count;
    }

    static void initIsDamaged() {
        for (int i = 0; i < N; i++) {
            knights[i].isDamaged = false;
        }
    }

    static int totalDamage() {
        int totalDamage = 0;

        for (Knight knight : knights) {
            if (knight.inBoard) {
                totalDamage += knight.damage;
            }
        }

        return totalDamage;
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        L = Integer.parseInt(st.nextToken());
        board = new int[L][L];
        knightBoard = new int[L][L];

        N = Integer.parseInt(st.nextToken());
        knights = new Knight[N];

        Q = Integer.parseInt(st.nextToken());

        for (int i = 0; i < L; i++) {
            st = new StringTokenizer(br.readLine());

            for (int j = 0; j < L; j++) {
                board[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        for (int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());

            int r = Integer.parseInt(st.nextToken()) - 1;
            int c = Integer.parseInt(st.nextToken()) - 1;
            int h = Integer.parseInt(st.nextToken());
            int w = Integer.parseInt(st.nextToken());
            int k = Integer.parseInt(st.nextToken());

            knights[i] = new Knight(r, c, h, w, k);

            for (int j = 0; j < h; j++) {
                for (int l = 0; l < w; l++) {
                    knightBoard[r + j][c + l] = (i + 1);
                }
            }
        }

        for (int q = 0; q < Q; q++) {
            st = new StringTokenizer(br.readLine());

            int i = Integer.parseInt(st.nextToken()) - 1;
            int d = Integer.parseInt(st.nextToken());

            // 체스판에서 사라진 기사에게 명령을 내리면 아무 반응이 없다.
            if (!knights[i].inBoard) {
                continue;
            }

            process(i, d);
            initIsDamaged();
        }

        System.out.println(totalDamage());
        br.close();
    }
}