import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;
import java.util.Random;

/*
扫雷游戏界面
* */

public class Minesweeper extends JFrame {//继承JFram类，创建窗口
    public int SIZE;//定义方块数量sizexsize
    public int MINES;//定义地雷数量
    private JButton[][] buttons = new JButton[SIZE][SIZE];// 创建了一个大小为 SIZE × SIZE 的二维数组 buttons，用来存储游戏板上每个格子对应的按钮（或者说GUI组件）。
    private boolean[][] mines = new boolean[SIZE][SIZE];//用来表示游戏板上每个格子是否含有地雷。true 表示有地雷，false 表示没有地雷。
    private int[][] counts = new int[SIZE][SIZE];//用来存储每个非地雷格子周围的地雷数量。
    private boolean[][] revealed = new boolean[SIZE][SIZE];//用来表示每个格子是否已经被揭开。true 表示已经揭开，false 表示未揭开。
    private boolean[][] flagged = new boolean[SIZE][SIZE];

    private JLabel countTitleLabel;//创建为成员变量，用于动态更新flag_count

    private JLabel TimerCount;
    private int timeCount = 0;
    private Timer timer;

    private final ImageIcon scaledIcon;

    private ImageIcon scaledboomImage;
    private boolean flagMode = false;//插旗
    private boolean revealMode = true;//翻块
    private int flag_count;
    private BackgroundMusicPlayer musicPlayer = new BackgroundMusicPlayer();
    SaveScore save = new SaveScore();



    //初始化窗口
    public Minesweeper(int size, int mines) {

        this.SIZE = size;
        this.MINES = mines;
        this.buttons = new JButton[SIZE][SIZE];
        this.mines = new boolean[SIZE][SIZE];
        this.counts = new int[SIZE][SIZE];
        this.revealed = new boolean[SIZE][SIZE];
        this.flagged = new boolean[SIZE][SIZE];



        setTitle("扫雷游戏-电子2202-郭鑫");
        if(size >= 20){setSize(1000, 1000);}
        else{setSize(800, 850);}

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);//窗口居中显示

        setLayout(new BorderLayout());
        JPanel gamePanel = new JPanel(new GridLayout(SIZE, SIZE));
        add(gamePanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new BorderLayout());
        add(controlPanel, BorderLayout.NORTH);

        /*
        功能面板布局
        * */
        // 1、创建Box来存放标题和标签
        Box horizontalBox = Box.createHorizontalBox();
        // 创建并添加状态标题
        JLabel statusTitleLabel = new JLabel("状态:翻块");
        horizontalBox.add(statusTitleLabel);

        // 添加一个水平的间隔
        horizontalBox.add(Box.createHorizontalStrut(40));

        // 创建并添加数量标题
        countTitleLabel = new JLabel("数量:"+ flag_count);
        horizontalBox.add(countTitleLabel);

        horizontalBox.add(Box.createHorizontalStrut(40));

        JLabel TitleLabel = new JLabel("炸弹数："+mines);
        horizontalBox.add(TitleLabel);

        horizontalBox.add(Box.createHorizontalStrut(40));

        TimerCount = new JLabel("时间:0");
        horizontalBox.add(TimerCount);
        // 将Box添加到控制面板，并设置靠左边显示
        controlPanel.add(horizontalBox, BorderLayout.WEST);


        // 2、创建Box来存放标题和标签
        // 创建一个水平Box来存放按钮并添加间隔
        Box buttonBox = Box.createHorizontalBox();
        buttonBox.add(Box.createHorizontalGlue()); // 添加一个水平的间隔，使按钮靠右显示

        // 创建并添加“插旗”按钮
        JButton flagButton = new JButton("插旗");
        flagButton.addActionListener(e-> {flagMode = true; revealMode = false;statusTitleLabel.setText("状态：插旗");});
        buttonBox.add(flagButton);

        buttonBox.add(Box.createHorizontalStrut(15));

        // 创建并添加“取消插旗”按钮
        JButton unflagButton = new JButton("取旗");
        unflagButton.addActionListener(e-> {flagMode = false; revealMode = false;statusTitleLabel.setText("状态：取旗");});
        buttonBox.add(unflagButton);

        buttonBox.add(Box.createHorizontalStrut(15));

        // 创建并添加“翻块”按钮
        JButton revealButton = new JButton("翻块");
        revealButton.addActionListener(e->{revealMode = true;statusTitleLabel.setText("状态：翻块");});
        buttonBox.add(revealButton);

        buttonBox.add(Box.createHorizontalStrut(15));

        //创建返回按钮
        JButton returnButton = new JButton("返回");
        returnButton.addActionListener(e->{ setVisible(false);musicPlayer.stopBackgroundMusic();new MinesweeperStartScreen();});
        buttonBox.add(returnButton);

        controlPanel.add(buttonBox, BorderLayout.EAST);
        //初始化游戏并显示
        initGame(gamePanel);//gamelpanel中显示按钮
        setVisible(true);

        //gamelpanel显示后才能设置flag_icon大小
        Dimension panelSize = gamePanel.getSize();
        ImageIcon flagIcon = new ImageIcon("src/image/flag.png");
        Image scaledImage = flagIcon.getImage().getScaledInstance((panelSize.width)/SIZE, (panelSize.height)/SIZE, Image.SCALE_SMOOTH);
        scaledIcon = new ImageIcon(scaledImage);

        ImageIcon boomIcon = new ImageIcon("src/image/boom.png");
        Image scaledImage_2 = boomIcon.getImage().getScaledInstance((panelSize.width)/SIZE, (panelSize.height)/SIZE, Image.SCALE_SMOOTH);
        scaledboomImage = new ImageIcon(scaledImage_2);


        musicPlayer.playBackgroundMusic("src/music/gaming.wav");//音乐播放
        TimeCountStart();//时间计时
    }

    private void initGame(JPanel gamePanel) {
        Random random = new Random();

        // 放置地雷
        int placedMines = 0;
        while (placedMines < MINES) {
            int x = random.nextInt(SIZE);
            int y = random.nextInt(SIZE);
            if (!mines[x][y]) {
                mines[x][y] = true;
                placedMines++;
            }
        }

        // 计算每个地方的地雷数量
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                if (mines[x][y]) continue;
                counts[x][y] = countMinesAround(x, y);
            }
        }

        // 设置按钮
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                JButton button = new JButton();
                button.addActionListener(new ButtonListener(x, y));//为按钮添加监视器，监视按键按下的动作
                buttons[x][y] = button;//将创建的按钮存储到buttons数组中，方便访问
                gamePanel.add(button);//按钮添加到gamepanel中
            }
        }
    }

    //查找周围地雷数量
    private int countMinesAround(int x, int y) {
        int count = 0;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                int nx = x + dx;
                int ny = y + dy;
                if (nx >= 0 && nx < SIZE && ny >= 0 && ny < SIZE && mines[nx][ny]) {
                    count++;
                }
            }
        }
        return count;
    }

    //揭开板子
    private void reveal(int x, int y) {
        if (x < 0 || x >= SIZE || y < 0 || y >= SIZE || revealed[x][y] || (flagged[x][y])) return;//边界检查，查看是否超出游戏面板范围.插旗后不能翻块
        revealed[x][y] = true;//解开盖子
        buttons[x][y].setEnabled(false);//禁用按钮

        //判断格子内容
        if (mines[x][y]) {//是地雷
            buttons[x][y].setIcon(scaledboomImage);
            musicPlayer.stopBackgroundMusic();
            musicPlayer.playBackgroundMusic("src/music/gameover.wav");
            TimeCountStop();//停止计时
            //使用延时器，执行完上面代码后再执行后面代码

            Timer timer = new Timer(1500, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    musicPlayer.stopBackgroundMusic();
                    gameOver(false);
                }
            });
            timer.setRepeats(false);
            timer.start();

        } else if (counts[x][y] > 0) {
            buttons[x][y].setText(String.valueOf(counts[x][y]));//不是地雷展示存储的地雷数量
        } else {//如果地雷数量是0，递归方法揭开周围8个相邻的格子
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    reveal(x + dx, y + dy);
                }
            }
        }
    }

    //放置旗帜
    private void flag(int x, int y) {
        if (!flagged[x][y]) {
            flagged[x][y] = !flagged[x][y];
            buttons[x][y].setIcon(scaledIcon);
            flag_count++;
            updateFlagCount(flag_count);
        }

    }

    //取消旗帜
    private void cancel_flag(int x,int y){
        if(flagged[x][y]){
            flagged[x][y] = !flagged[x][y];//如果插旗了，取消插旗
            buttons[x][y].setIcon(null);//取消图片
            flag_count--;
            updateFlagCount(flag_count);
        }
    }

    //游戏胜负响应
    private void gameOver(boolean won) {
        String message = won ? "恭喜你找出所有炸弹!" : "很遗憾，你失败了";
        JOptionPane.showMessageDialog(this, message);
        setVisible(false);
        new MinesweeperStartScreen();
//        System.exit(0);//退出程序
    }

    //动态更新数量
    private void updateFlagCount(int flag_count){
        countTitleLabel.setText("数量:"+flag_count);
    }

    public void updateTimerCount(int value){
        TimerCount.setText("时间："+value+"s");
    }

    //计时器
    private void TimeCountStart(){
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timeCount++;
                updateTimerCount(timeCount);
            }
        });

        timer.start();
    }

    // 游戏结束时停止计时器
    private void TimeCountStop() {
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }
    }

    //按钮响应部分
    private class ButtonListener implements ActionListener {//实现ActionListener接口，点击每个方块按钮时的响应
        private int x, y;

        public ButtonListener(int x, int y) {//监听按下的按钮坐标
            this.x = x;
            this.y = y;
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            if(revealMode){reveal(x,y);}//根据功能进行操作
            else{
                if(flagMode){flag(x,y);}
                else{cancel_flag(x,y);}
            }
            if (checkWin()) {
                TimeCountStop();
                save.saveBestScore(timeCount);
                gameOver(true);
            }
        }

        //检查游戏是否胜利
        private boolean checkWin() {
            for (int x = 0; x < SIZE; x++) {
                for (int y = 0; y < SIZE; y++) {
                    if (!mines[x][y] && !revealed[x][y]) {
                        return false;
                    }
                }
            }
            return true;
        }
    }

}
