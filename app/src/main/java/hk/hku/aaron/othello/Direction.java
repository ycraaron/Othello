package hk.hku.aaron.othello;

/**
 * Created by Aaron on 2015/10/25.
 */
public class Direction {
    private int x;
    private int y;

    public Direction(int x, int y){
        this.x = x;
        this.y = y;
    }

    public int getX(){
        return this.x;
    }

    public int getY(){
        return this.y;
    }
}
