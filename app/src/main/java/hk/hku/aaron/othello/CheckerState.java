package hk.hku.aaron.othello;


/**
 * Created by Aaron on 2015/10/25.
 */
public class CheckerState extends Object{
    private int x;
    private int y;
    private int state;

    public CheckerState(int x, int y, int state)
    {
        this.x = x;
        this.y = y;
        this.state = state;
    }

    public CheckerState(CheckerState checker)
    {
        this.x = checker.getX();
        this.y = checker.getY();
        this.state = checker.getState();
    }

    public int getX(){

        return this.x;
    }

    public int getY(){

        return this.y;
    }

    public String getStrPos(){
        String strPos = Integer.toString(this.x) + "*" + Integer.toString(this.y);
        return strPos;
    }

    public int getState(){
        return state;
    }

    public boolean changeState(int state){
        this.state = state;
        return true;
    }
}

