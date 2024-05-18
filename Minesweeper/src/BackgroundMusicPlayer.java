import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class BackgroundMusicPlayer {
    private Clip clip;

    public void playBackgroundMusic(String filePath) {
        try {
            // 打开音频文件
            File audioFile = new File(filePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);

            // 获取音频格式
            AudioFormat format = audioStream.getFormat();

            // 创建数据行信息对象
            DataLine.Info info = new DataLine.Info(Clip.class, format);

            // 获取数据行对象
            clip = (Clip) AudioSystem.getLine(info);

            // 打开数据行并加载音频数据到内存中
            clip.open(audioStream);

            // 循环播放音乐
            clip.loop(Clip.LOOP_CONTINUOUSLY);

            // 开始播放
            clip.start();
        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
            e.printStackTrace();
        }
    }

    public void stopBackgroundMusic() {
        if (clip != null) {
            clip.stop();
            clip.close();
        }
    }
}
