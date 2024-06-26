import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class Audio {
    private Clip clip;                 // 오디오 클립을 나타냅니다.
    private File audioFile;            // 오디오 파일을 위한 File 객체
    private AudioInputStream audioInputStream;  // 오디오 입력 스트림을 나타냅니다.
    private boolean isLoop;            // 오디오를 반복 재생할지 여부를 결정하는 플래그

    // Audio 클래스의 생성자
    public Audio(String pathName, boolean isLoop) {
        try {
            clip = AudioSystem.getClip();  // Clip 인스턴스를 얻습니다.
            audioFile = new File(pathName);  // 지정된 경로의 File 객체를 생성합니다.
            audioInputStream = AudioSystem.getAudioInputStream(audioFile);  // 오디오 파일로부터 AudioInputStream을 가져옵니다.
            clip.open(audioInputStream);  // 클립을 열고 오디오 파일을 로드합니다.
        } catch (LineUnavailableException e) {
            e.printStackTrace();  // LineUnavailableException을 처리합니다 (라인을 사용할 수 없는 경우 발생).
        } catch (IOException e) {
            e.printStackTrace();  // IOException을 처리합니다 (I/O 오류가 발생한 경우 발생).
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();  // UnsupportedAudioFileException을 처리합니다 (오디오 파일 형식이 지원되지 않는 경우 발생).
        }
    }

    // 오디오를 시작하는 메서드
    public void start() {
        clip.setFramePosition(0);  // 프레임 위치를 오디오 클립의 시작으로 설정합니다.
        clip.start();  // 오디오 클립을 재생합니다.

        // isLoop 플래그가 true이면 오디오를 계속해서 반복 재생합니다.
        if (isLoop) {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    // 오디오를 정지하는 메서드
    public void stop() {
        clip.stop();  // 오디오 클립을 정지합니다.
    }
}
