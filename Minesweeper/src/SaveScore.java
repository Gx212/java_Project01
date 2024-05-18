import java.io.*;
public class SaveScore {
    private int bestScor;
    public SaveScore(){}

    //保存成绩
    public void savescore(int score) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/Score/score.txt"))) {
            writer.write(String.valueOf(score));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //返回成绩
    public int loadBestScore() {
        try (BufferedReader reader = new BufferedReader(new FileReader("src/Score/score.txt"))) {
            String line = reader.readLine();
            if (line != null) {
                bestScor = Integer.parseInt(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bestScor;
    }

    public void saveBestScore(int value){
        int bestScore = loadBestScore();
        if (value > bestScore ) {
            savescore(value);
        }
    }
}
