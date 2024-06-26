import javax.swing.*;
import java.awt.*;

public class Enemy {
    // 이미지를 로드하여 저장하는 Image 객체
    Image image = new ImageIcon("src/images/enemy.png").getImage();

    // 적의 현재 위치
    int x, y;

    // 이미지의 폭과 높이
    int width = image.getWidth(null);
    int height = image.getHeight(null);

    // 적의 체력
    int hp = 10;

    // Enemy 클래스의 생성자
    public Enemy(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // 적을 이동시키는 메서드
    public void move() {
        this.x -= 7;  // x 좌표를 왼쪽으로 7만큼 이동
    }
}
class EnemyAttack {
    // 이미지를 로드하여 저장하는 Image 객체
    Image image = new ImageIcon("src/images/enemy_attack.png").getImage();

    // 적의 공격 현재 위치
    int x, y;

    // 적의 공격력
    int attack = 5;

    // EnemyAttack 클래스의 생성자
    public EnemyAttack(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // 적의 공격을 발사하는 메서드
    public void fire() {
        this.x -= 12;  // x 좌표를 왼쪽으로 12만큼 이동 (프레임마다 호출될 것으로 예상)
    }

    // 공격력을 반환하는 메서드
    public int getAttack() {
        return attack;
    }
}
