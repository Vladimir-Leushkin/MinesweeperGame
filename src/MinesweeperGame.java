import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MinesweeperGame extends JFrame {

    private JPanel panel;
    private JLabel label;
    private final int COLS = 9;
    private final int ROWS = 9;
    private final int BOMBS = 10;
    private final int IMAGE_SIZE = 50;
    private Cell[][] gameField = new Cell[COLS][ROWS];
    private static ArrayList<Cell> allCells;
    private int countMinesOnField;
    private static Random random = new Random();
    private static final String MINE = "\uD83D\uDCA3";
    private static final String FLAG = "\uD83D\uDEA9";
    private int countFlags = 0;
    private boolean isGameStopped;
    private int countClosedTiles = COLS * ROWS;
    private int score = 0;
    public static void main(String[] args) {

        new MinesweeperGame();
    }

    private MinesweeperGame(){
        createGame();
        setImages();
        initLabel();
        initPanel();
        initFrame();
    }

    private void initLabel(){
        label = new JLabel("Welcome!");
        add(label, BorderLayout.NORTH);
    }
    private void initPanel(){
        panel = new JPanel(){
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                for (Cell cell : allCells){
                    g.drawImage((Image) cell.box.image, cell.x * IMAGE_SIZE, cell.y * IMAGE_SIZE, this);
                }
            }
        };

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int x = e.getX() / IMAGE_SIZE;
                int y = e.getY() / IMAGE_SIZE;
                //Coord coord = new Coord(x, y);
                if (e.getButton() == MouseEvent.BUTTON1){
                    //game.pressLeftButton(coord);
                }
                if (e.getButton() == MouseEvent.BUTTON3){
                    //game.pressRightButton(coord);
                }
                if (e.getButton() == MouseEvent.BUTTON2){
                    //game.start();
                }
                //label.setText(getMessage());
                panel.repaint();
            }
        });
        panel.setPreferredSize(new Dimension(COLS * IMAGE_SIZE, ROWS * IMAGE_SIZE));
        add(panel);
    }

    private void initFrame(){
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Minesweeper");
        setResizable(false);
        setVisible(true);
        pack();
        setLocationRelativeTo(null);
        setIconImage(getImage("icon"));
    }

    private void setImages(){
        for(Box box: Box.values()){
            box.image = getImage(box.name().toLowerCase());
        }
    }

    private Image getImage (String name){
        String fileName = "img/" + name + ".png";
        ImageIcon icon = new ImageIcon(getClass().getResource(fileName));
        return icon.getImage();
    }

    private ArrayList<Cell> setAllCells(){
        allCells = new ArrayList<>();
        for (int y = 0; y < ROWS; y++) {
            for (int x = 0; x < COLS; x++) {
                allCells.add(gameField[y][x]);
            }
        }
        return allCells;
    }

    private void createGame() {
        boolean isMine = false;
        for (int y = 0; y < ROWS; y++) {
            for (int x = 0; x < COLS; x++) {
                if (countMinesOnField <= BOMBS){
                    isMine = random.nextBoolean();
                }
                if (isMine) {
                    countMinesOnField++;
                }
                gameField[y][x] = new Cell(x, y, isMine, Box.CLOSED);
            }
        }
        countMineNeighbors();
        countFlags = countMinesOnField;
        setAllCells();
    }

    private List<Cell> getNeighbors(Cell cell) {
        List<Cell> result = new ArrayList<>();
        for (int y = cell.y - 1; y <= cell.y + 1; y++) {
            for (int x = cell.x - 1; x <= cell.x + 1; x++) {
                if (y < 0 || y >= ROWS) {
                    continue;
                }
                if (x < 0 || x >= COLS) {
                    continue;
                }
                if (gameField[y][x] == cell) {
                    continue;
                }
                result.add(gameField[y][x]);
            }
        }
        return result;
    }

    private void countMineNeighbors(){
        for (int y = 0; y < ROWS; y++){
            for (int x = 0; x < COLS; x++){
                if(!gameField[y][x].isMine){
                    for(Cell o : getNeighbors(gameField[y][x])){
                        if(o.isMine){
                            gameField[y][x].countMineNeighbors++;
                        }
                    }
                }
            }
        }
    }
}
