import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/*
扫雷游戏开始界面
* */

public class MinesweeperStartScreen extends JFrame {
    SaveScore sav = new SaveScore();
    public MinesweeperStartScreen() {
        setTitle("扫雷游戏-电子2202-郭鑫");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 400);
        setLocationRelativeTo(null); // 居中显示

        JPanel panel = new JPanel(new GridLayout(4, 1));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add some padding

        JLabel label = new JLabel("欢迎来到扫雷游戏！最好成绩："+sav.loadBestScore());
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setFont(new Font("微软雅黑", Font.BOLD, 20));
        panel.add(label);//标签添加到页面中央

        JButton button10x10 = new JButton("简单关卡 10x10 10");
        button10x10.addActionListener(e -> startGame(10, 10));
        panel.add(button10x10);

        JButton button15x15 = new JButton("普通关卡 15x15 25");
        button15x15.addActionListener(e -> startGame(15, 25));
        panel.add(button15x15);

        JButton button20x20 = new JButton("困难关卡 20x20 50");
        button20x20.addActionListener(e -> startGame(20, 50));
        panel.add(button20x20);


        add(panel);
        setVisible(true);
    }


    private void startGame(int size,int mines) {
        setVisible(false); // 隐藏开始界面
        new Minesweeper(size,mines);
    }

    public static void main(String[] args) {
        new MinesweeperStartScreen();
    }
}
