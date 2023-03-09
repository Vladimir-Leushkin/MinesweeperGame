public class Cell {

    public int x;
    public int y;
    public boolean isMine;
    public boolean isOpen = false;
    public boolean isFlag = false;
    public int countMineNeighbors;
    public Box box;

    Cell(int x, int y, boolean isMine, Box box){
        this.x = x;
        this.y = y;
        this.isMine = isMine;
        this.box = box;
    }
}
