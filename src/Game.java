import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Game extends Thread {
    // 게임 변수와 속성
    private int delay = 20; // 게임 루프의 딜레이
    private long pretime; // 이전 시간 기록
    private int cnt; // 게임 카운터
    private int score; // 플레이어의 점수

    // 플레이어 캐릭터 이미지
    private Image player = new ImageIcon("src/images/player.png").getImage();

    private int playerX, playerY; // 플레이어의 현재 위치
    private int playerWidth = player.getWidth(null); // 플레이어 이미지 너비
    private int playerHeight = player.getHeight(null); // 플레이어 이미지 높이
    private int playerSpeed = 10; // 플레이어 이동 속도
    private int playerHp = 30; // 플레이어 체력

    // 플레이어의 움직임과 발사를 제어하는 플래그
    private boolean up, down, left, right, shooting; // 키 입력 플래그
    private boolean isOver; // 게임 종료 여부 플래그

    // 게임 엔터티를 저장할 리스트
    private ArrayList<PlayerAttack> playerAttackList = new ArrayList<PlayerAttack>(); // 플레이어의 공격 리스트
    private ArrayList<Enemy> enemyList = new ArrayList<Enemy>(); // 적 리스트
    private ArrayList<EnemyAttack> enemyAttackList = new ArrayList<EnemyAttack>(); // 적의 공격 리스트

    // 게임 엔터티의 인스턴스
    private PlayerAttack playerAttack; // 플레이어의 공격 인스턴스
    private Enemy enemy; // 적 인스턴스
    private EnemyAttack enemyAttack; // 적의 공격 인스턴스

    // 배경 음악과 피격 사운드를 다루기 위한 오디오
    private Audio backgroundMusic; // 배경 음악
    private Audio hitSound; // 피격 사운드

    // 적과 충돌시 무적 상태 변수
    private boolean invincible = false; // 무적 상태 여부
    private long invincibleTime; // 무적 상태 지속 시간

    // 캐릭터가 공격받았는지 저장하는 변수
    private boolean isHit = false;
    // 반짝이는 효과를 주는 데 필요한 변수
    private int blinkCount = 0;

    // 아이템 리스트 추가
    private ArrayList<Item> itemList = new ArrayList<Item>(); // 아이템 리스트
    private Item item; // 아이템 인스턴스
    private boolean isTripleAttack = false; // 플레이어의 삼중 공격 여부

    // 아이템 획득 시간을 저장하는 변수
    private long itemAcquiredTime;

    @Override
    public void run() {
        // 배경 음악과 피격 사운드 초기화
        backgroundMusic = new Audio("src/audio/gameBGM.wav", true);
        hitSound = new Audio("src/audio/hitSound.wav", false);

        // 게임 상태를 초기화
        reset();

        while (true) {
            while (!isOver) {
                // 아이템 획득, 게임 로직 처리, 무적 상태 확인 등을 주기적으로 실행
                itemAcquireProcess();
                pretime = System.currentTimeMillis();
                if (System.currentTimeMillis() - pretime < delay) {
                    try {
                        Thread.sleep(delay - System.currentTimeMillis() + pretime);
                        // 게임 로직 처리
                        keyProcess();
                        playerAttackProcess();
                        enemyAppearProcess();
                        enemyMoveProcess();
                        enemyAttackProcess();
                        checkInvincible();
                        cnt++;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // 게임 상태 초기화
    public void reset() {
        // 게임 상태 초기화
        isOver = false;
        cnt = 0;
        score = 0;
        playerX = 10;
        playerY = (Start.SCREEN_HEIGHT - playerHeight) / 2;

        // 피를 가득 찬 상태로 초기화
        playerHp = 30;

        // 배경 음악 재생 시작
        backgroundMusic.start();

        // 게임 엔터티 리스트 초기화
        playerAttackList.clear();
        enemyList.clear();
        enemyAttackList.clear();
    }

    // 플레이어의 키 입력을 처리하여 움직임과 발사 제어
    private void keyProcess() {
        if (up && playerY - playerSpeed > 0) playerY -= playerSpeed;
        if (down && playerY + playerHeight + playerSpeed < Start.SCREEN_HEIGHT) playerY += playerSpeed;
        if (left && playerX - playerSpeed > 0) playerX -= playerSpeed;
        if (right && playerX + playerWidth + playerSpeed < Start.SCREEN_WIDTH) playerX += playerSpeed;
        if (shooting) {
            playerAttackProcess();
        }
    }

    // Game 클래스의 playerAttackProcess 메서드 내에서 playerAttackList를 처리하는 부분
    private void playerAttackProcess() {
        if (shooting && cnt % 15 == 0) {
            if (isTripleAttack) {
                createTripleAttack();  // 삼중 공격 생성 메서드 호출
            } else {
                createSingleAttack();  // 단일 공격 생성 메서드 호출
            }
        }

        movePlayerAttacks();  // 플레이어의 공격 이동 메서드 호출

        checkPlayerAttackCollision();  // 적과의 충돌 확인 메서드 호출
    }

// Game 클래스 내에 새로운 메서드들 추가

    // 단일 공격 생성 메서드
    private void createSingleAttack() {
        playerAttack = new PlayerAttack(playerX + 100, playerY + 25, 0);
        playerAttackList.add(playerAttack);
    }

    // 삼중 공격 생성 메서드
    private void createTripleAttack() {
        playerAttack = new PlayerAttack(playerX + 100, playerY + 25, 0);
        playerAttackList.add(playerAttack);

        playerAttack = new PlayerAttack(playerX + 100, playerY, 1);
        playerAttackList.add(playerAttack);

        playerAttack = new PlayerAttack(playerX + 100, playerY + 50, 2);
        playerAttackList.add(playerAttack);
    }

    // 플레이어의 공격 이동 메서드
    private void movePlayerAttacks() {
        for (int i = 0; i < playerAttackList.size(); i++) {
            playerAttack = playerAttackList.get(i);
            playerAttack.fire();
        }
    }

    // 플레이어의 공격과 적과의 충돌 확인 메서드
    private void checkPlayerAttackCollision() {
        for (int i = 0; i < playerAttackList.size(); i++) {
            playerAttack = playerAttackList.get(i);

            for (int j = 0; j < enemyList.size(); j++) {
                enemy = enemyList.get(j);

                if (playerAttack.x > enemy.x && playerAttack.x < enemy.x + enemy.width && playerAttack.y > enemy.y && playerAttack.y < enemy.y + enemy.height) {
                    handleEnemyCollision(enemy);  // 적과 충돌 처리 메서드 호출
                }
            }
        }
    }

    // 충돌 처리 메서드
    private void handleEnemyCollision(Enemy enemy) {
        enemy.hp -= playerAttack.attack;
        playerAttackList.remove(playerAttack);

        if (enemy.hp <= 0) {
            hitSound.start();
            enemyList.remove(enemy);
            score += 1000;

            if (Math.random() < 0.35) {
                item = new Item(enemy.x, enemy.y);
                itemList.add(item);
            }
        }
    }


    // 주기적으로 새로운 적 생성
    private void enemyAppearProcess() {
        if (cnt % 80 == 0) {
            // 오른쪽 끝에서 생성되며 세로 위치는 랜덤으로 결정
            enemy = new Enemy(1120, (int) (Math.random() * 621));
            enemyList.add(enemy);
        }
    }

    // 적의 이동 처리 및 플레이어와의 충돌 확인
    private void enemyMoveProcess() {
        for (int i = 0; i < enemyList.size(); i++) {
            enemy = enemyList.get(i);
            enemy.move();

            // 플레이어와 적의 충돌 확인
            if (!invincible && playerX < enemy.x + enemy.width && playerX + playerWidth > enemy.x && playerY < enemy.y + enemy.height && playerY + playerHeight > enemy.y) {
                // 피격 사운드 재생, 플레이어의 체력 감소 및 게임 종료 확인
                hitSound.start();
                playerHp -= enemyAttack.getAttack();
                if (playerHp <= 0) {
                    isOver = true;
                } else {
                    // 무적 상태 활성화 및 무적 상태 시작 시간 저장
                    invincible = true;
                    invincibleTime = System.currentTimeMillis();
                }
            }
        }
    }

    // 적의 공격 처리 및 플레이어와의 충돌 확인
    private void enemyAttackProcess() {
        if (cnt % 60 == 0) {
            // 적의 오른쪽에서 나가는 총알 생성
            enemyAttack = new EnemyAttack(enemy.x - 79, enemy.y + 35);
            enemyAttackList.add(enemyAttack);
        }

        for (int i = 0; i < enemyAttackList.size(); i++) {
            enemyAttack = enemyAttackList.get(i);
            enemyAttack.fire();

            if (enemyAttack.x > playerX && enemyAttack.x < playerX + playerWidth && enemyAttack.y > playerY && enemyAttack.y < playerY + playerHeight) {
                // 피격 사운드 재생, 플레이어의 체력 감소 및 게임 종료 확인
                hitSound.start();
                playerHp -= enemyAttack.attack;
                enemyAttackList.remove(enemyAttack);

                if (playerHp <= 0) isOver = true;
                // 공격 받았으므로 isHit를 true로 설정하고 blinkCount를 초기화합니다.
                isHit = true;
                blinkCount = 0;
            }
        }
    }

    // 무적 상태 확인 및 처리
    private void checkInvincible() {
        // 무적 상태에서 2초가 경과하면 무적 상태 해제
        if (invincible && System.currentTimeMillis() - invincibleTime >= 2000) {
            invincible = false;
        }
    }

    // 게임 화면에 게임 엘리먼트를 그립니다.
    public void gameDraw(Graphics g) {
        playerDraw(g);
        enemyDraw(g);
        infoDraw(g);
        itemDraw(g);
    }

    // 게임 정보를 그립니다 (예: 점수 및 게임 오버 메시지)
    private void infoDraw(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        g.drawString("SCORE : " + score, 40, 80);

        if (isOver) {
            g.setColor(Color.red);
            g.setFont(new Font("Arial", Font.BOLD, 160));
            String gameOverText = "GAME OVER";
            int textWidth = g.getFontMetrics().stringWidth(gameOverText);
            int x = (Start.SCREEN_WIDTH - textWidth) / 2;
            g.drawString(gameOverText, x, 320);

            g.setColor(Color.yellow);
            g.setFont(new Font("Arial", Font.BOLD, 80));
            String restartText = "PRESS R TO RESTART";
            textWidth = g.getFontMetrics().stringWidth(restartText);
            x = (Start.SCREEN_WIDTH - textWidth) / 2;
            g.drawString(restartText, x, 500);
        }
    }

    // 화면에 아이템을 그립니다
    private void itemDraw(Graphics g) {
        for (int i = 0; i < itemList.size(); i++) {
            item = itemList.get(i);
            g.drawImage(item.image, item.x, item.y, null);
        }
    }

    // 플레이어 캐릭터와 그들의 체력 바를 그립니다
    private void playerDraw(Graphics g) {
// isHit이 true이고 blinkCount가 짝수일 때만 캐릭터를 그립니다.
        if (!isHit || blinkCount % 2 == 0) {
            g.drawImage(player, playerX, playerY, null);
        }
        g.setColor(Color.GREEN);
        g.fillRect(playerX - 1, playerY - 40, playerHp * 3, 20);

        for (int i = 0; i < playerAttackList.size(); i++) {
            playerAttack = playerAttackList.get(i);
            g.drawImage(playerAttack.image, playerAttack.x, playerAttack.y, null);
        }
        // isHit이 true일 때 blinkCount를 증가시키고, blinkCount가 일정 값 이상이면 isHit을 false로 설정합니다.
        if (isHit) {
            blinkCount++;
            if (blinkCount > 5) {
                isHit = false;
            }
        }
    }

    // 적 캐릭터, 그들의 체력 바 및 적 공격을 그리는 메서드
    public void enemyDraw(Graphics g) {
        for (int i = 0; i < enemyList.size(); i++) {
            enemy = enemyList.get(i);

            // 적 캐릭터 그리기
            g.drawImage(enemy.image, enemy.x, enemy.y, null);

            // 적 체력 바 그리기 (빨간색으로 표시)
            g.setColor(Color.red);
            g.fillRect(enemy.x + 1, enemy.y - 40, enemy.hp * 8, 20);
        }

        // 적 공격 그리기
        for (int i = 0; i < enemyAttackList.size(); i++) {
            enemyAttack = enemyAttackList.get(i);
            g.drawImage(enemyAttack.image, enemyAttack.x, enemyAttack.y, null);
        }
    }

    // 아이템 클래스 정의
    public class Item {
        Image image = new ImageIcon("src/images/item.png").getImage();
        int x, y;
        int width = image.getWidth(null);
        int height = image.getHeight(null);

        // 아이템 생성 시 초기 위치 설정
        public Item(int x, int y) {
            this.x = x;
            this.y = y;
        }

        // 아이템의 움직임을 처리하는 메서드
        public void move() {
            this.x -= 5; // 아이템이 왼쪽으로 이동하는 속도
        }
    }

    // 아이템 획득 처리 로직
    private void itemAcquireProcess() {
        for (int i = 0; i < itemList.size(); i++) {
            item = itemList.get(i);
            item.move();

            // 아이템과 플레이어의 충돌 검사
            if (playerX < item.x + item.width && playerX + playerWidth > item.x && playerY < item.y + item.height && playerY + playerHeight > item.y) {
                itemList.remove(item);

                // 아이템 획득 시 처리 로직
                isTripleAttack = true;
                // 아이템 획득 시간 저장
                itemAcquiredTime = System.currentTimeMillis();
            }
        }
        // 아이템 획득 후 4초가 지났을 때 아이템 효과 해제
        if (isTripleAttack && System.currentTimeMillis() - itemAcquiredTime >= 4000) {
            isTripleAttack = false;
        }
    }

    // 게임 종료 상태 확인
    public boolean isOver() {
        return isOver;
    }

    // 플레이어의 움직임 관련 플래그 설정
    public void setUp(boolean up) {
        this.up = up;
    }

    public void setDown(boolean down) {
        this.down = down;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public void setRight(boolean right) {
        this.right = right;
    }

    // 플레이어의 공격 관련 플래그 설정
    public void setShooting(boolean shooting) {
        this.shooting = shooting;
    }
}
