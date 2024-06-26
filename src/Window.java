import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Timer;
import java.util.TimerTask;

public class Window extends JFrame {
    // 더블 버퍼링을 위한 이미지와 그래픽 객체
    private Image bufferImage;
    private Graphics screenGraphic;

    // 게임 화면에 사용될 이미지들
    private Image mainScreen = new ImageIcon("src/images/main_screen.png").getImage();
    private Image loadingScreen = new ImageIcon("src/images/loading_screen.png").getImage();
    private Image gameScreen = new ImageIcon("src/images/game_screen.png").getImage();

    private boolean isMainScreen, isLoadingScreen, isGameScreen;

    private Game game = new Game();  // 게임 로직을 처리하는 Game 클래스의 인스턴스

    private Audio backgroundMusic;  // 배경음악을 재생하는 Audio 클래스의 인스턴스

    public Window() {
        setTitle("Shooting Game");
        setUndecorated(true);
        setSize(Start.SCREEN_WIDTH, Start.SCREEN_HEIGHT);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setLayout(null);

        init();  // 초기화 메서드 호출
    }

    private void init() {
        isMainScreen = true;
        isLoadingScreen = false;
        isGameScreen = false;

        // 메뉴 화면 배경음악
        backgroundMusic = new Audio("src/audio/menuBGM.wav", true);
        backgroundMusic.start();

        addKeyListener(new KeyListener());  // 키 이벤트를 처리하는 KeyListener 추가
    }

    private void gameStart() {
        isMainScreen = false;
        isLoadingScreen = true;

        // 로딩 스크린 표시를 위한 타이머
        Timer loadingTimer = new Timer();
        TimerTask loadingTask = new TimerTask() {
            @Override
            public void run() {
                backgroundMusic.stop();
                isLoadingScreen = false;
                isGameScreen = true;
                game.start();  // 게임 시작
            }
        };
        loadingTimer.schedule(loadingTask, 800);  // 0.8초 후에 태스크 실행
    }

    // paint 메서드 오버라이드
    public void paint(Graphics g) {
        bufferImage = createImage(Start.SCREEN_WIDTH, Start.SCREEN_HEIGHT);
        screenGraphic = bufferImage.getGraphics();
        screenDraw(screenGraphic);
        g.drawImage(bufferImage, 0, 0, null);
    }

    // 화면을 그리는 메서드
    public void screenDraw(Graphics g) {
        if (isMainScreen) {
            g.drawImage(mainScreen, 0, 0, null);
        }
        if (isLoadingScreen) {
            g.drawImage(loadingScreen, 0, 0, null);
        }
        if (isGameScreen) {
            g.drawImage(gameScreen, 0, 0, null);
            game.gameDraw(g);
        }
        this.repaint();  // 다시 그리기 요청
    }

    // 키 이벤트 처리를 위한 내부 클래스 KeyListener
    class KeyListener extends KeyAdapter {
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP:
                    game.setUp(true);
                    break;
                case KeyEvent.VK_DOWN:
                    game.setDown(true);
                    break;
                case KeyEvent.VK_LEFT:
                    game.setLeft(true);
                    break;
                case KeyEvent.VK_RIGHT:
                    game.setRight(true);
                    break;
                case KeyEvent.VK_R:
                    if (game.isOver()) game.reset();
                    break;
                case KeyEvent.VK_SPACE:
                    game.setShooting(true);
                    break;
                case KeyEvent.VK_ENTER:
                    if (isMainScreen) gameStart();
                    break;
                case KeyEvent.VK_ESCAPE:
                    System.exit(0);
                    break;
            }
        }

        public void keyReleased(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP:
                    game.setUp(false);
                    break;
                case KeyEvent.VK_DOWN:
                    game.setDown(false);
                    break;
                case KeyEvent.VK_LEFT:
                    game.setLeft(false);
                    break;
                case KeyEvent.VK_RIGHT:
                    game.setRight(false);
                    break;
                case KeyEvent.VK_SPACE:
                    game.setShooting(false);
                    break;
            }
        }
    }
}
