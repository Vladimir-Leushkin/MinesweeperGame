import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static javax.swing.JOptionPane.showMessageDialog;

public class MinesweeperGame extends JFrame {

    private JPanel panel;
    private JLabel label;
    private final int COLS = 9;
    private final int ROWS = 9;
    private final int IMAGE_SIZE = 50;
    private final Cell[][] gameField = new Cell[COLS][ROWS];
    private static ArrayList<Cell> allCells;
    private int countMinesOnField;
    private static final Random random = new Random();
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
                if (e.getButton() == MouseEvent.BUTTON1){
                    onMouseLeftClick(x, y);
                }
                if (e.getButton() == MouseEvent.BUTTON3){
                    onMouseRightClick(x, y);
                }
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

    private ArrayList<Cell> getAllCells(){
        allCells = new ArrayList<>();
        for (int y = 0; y < ROWS; y++) {
            allCells.addAll(Arrays.asList(gameField[y]).subList(0, COLS));
        }
        return allCells;
    }

    private void createGame() {
        for (int y = 0; y < ROWS; y++) {
            for (int x = 0; x < COLS; x++) {
                gameField[y][x] = new Cell(x, y, false, Box.CLOSED);
            }
        }
        setBomb();
        countMineNeighbors();
        countFlags = countMinesOnField;
        getAllCells();
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

    private void openTile(int x, int y){
        if(gameField[y][x].isOpen || gameField[y][x].isFlag || isGameStopped){
            return;
        }
        gameField[y][x].isOpen = true;
        countClosedTiles--;
        if(!gameField[y][x].isMine && gameField[y][x].countMineNeighbors == 0){
            gameField[y][x].box = Box.ZERO;
            score += 5;
            label.setText(getMessage("Think twice! Score:" + score));
            for(Cell o : getNeighbors(gameField[y][x])){
                if(!o.isOpen){
                    openTile(o.x, o.y);
                }
            }
        }else if(gameField[y][x].isMine){
            gameField[y][x].box = Box.BOMBED;
            gameOver();
        }else{
            gameField[y][x].box = Box.values()[gameField[y][x].countMineNeighbors];
            score += 5;
            label.setText(getMessage("Think twice! Score:" + score));
        }
        if(countClosedTiles == countMinesOnField && !gameField[y][x].isMine){
            win();
        }
    }

    private void markTile(int x, int y){
        if(isGameStopped){
            return;
        }else if(gameField[y][x].isOpen){
            return;
        }else if(!gameField[y][x].isFlag && countFlags == 0){
            return;
        }else if(!gameField[y][x].isFlag){
            gameField[y][x].isFlag = true;
            gameField[y][x].box = Box.FLAGED;
            countFlags--;
        }else if(gameField[y][x].isFlag){
            gameField[y][x].isFlag = false;
            gameField[y][x].box = Box.CLOSED;
            countFlags++;
        }
    }

    public void onMouseLeftClick(int x, int y){
        if(isGameStopped){
            restart();
            return;
        }
        openTile(x, y);

    }

    public void onMouseRightClick(int x, int y){

        markTile(x, y);
    }

    private void gameOver(){
        isGameStopped = true;
        showMines();
        label.setText(getMessage("You lose!  Score:" + score));
    }

    private void win(){
        isGameStopped = true;
        label.setText(getMessage("You win!!!  Score:" + score));
    }


    private void restart(){
        isGameStopped = false;
        countClosedTiles = COLS * ROWS;
        score = 0;
        countMinesOnField = 0;
        label.setText(getMessage("Let's play again"));
        createGame();
    }

    private void setBomb(){
        int BOMBS = 10;
        while (countMinesOnField < BOMBS){
            Cell cell = getRandomCell();
            if (gameField[cell.y][cell.x].isMine){
                continue;
            }
            gameField[cell.y][cell.x] = cell;
            countMinesOnField ++;
        }
    }

    private Cell getRandomCell(){
        return new Cell(random.nextInt(ROWS), random.nextInt(COLS), true, Box.CLOSED);
    }

    private String getMessage(String s) {
        return s;
    }

    private void showMines(){
        for (Cell cell : getAllCells()){
            if (cell.isMine){
                if (!cell.isFlag){
                    if (Box.BOMBED != cell.box){
                        gameField[cell.y][cell.x].box = Box.BOMB;
                    }
                }
            }
        }
    }
}
