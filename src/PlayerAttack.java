import java.awt.Image;
import javax.swing.ImageIcon;

public class PlayerAttack {
    // 이미지를 로드하여 저장하는 Image 객체
    Image image = new ImageIcon("src/images/player_attack.png").getImage();

    // 플레이어 공격 현재 위치
    int x, y;

    // 플레이어 공격력
    int attack = 5;

    // 공격의 방향을 나타내는 변수
    int direction;

    // PlayerAttack 클래스의 생성자
    public PlayerAttack(int x, int y, int direction) {
        this.x = x;
        this.y = y;
        this.direction = direction;
    }

    // 플레이어의 공격을 발사하는 메서드
    public void fire() {
        this.x += 10;  // x 좌표를 오른쪽으로 10만큼 이동

        // 방향에 따라 y 좌표를 조절
        if (direction == 1) {  // 위쪽으로 이동
            this.y -= 5;
        } else if (direction == 2) {  // 아래쪽으로 이동
            this.y += 5;
        }
    }
}
