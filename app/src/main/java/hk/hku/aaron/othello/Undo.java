package hk.hku.aaron.othello;

import java.util.ArrayList;
import java.util.Stack;

/**
 * Created by Aaron on 2015/10/27.
 */
public class Undo {
    private Stack<ArrayList<CheckerState>> stackHisBlack;
    private Stack<ArrayList<CheckerState>> stackHisWhite;
    private Stack<CheckerState> stackHisMove;

    public Undo(){
        stackHisBlack = new Stack<>();
        stackHisWhite = new Stack<>();
        stackHisMove = new Stack<>();
    }

    public ArrayList<CheckerState> getBlack(){
        return stackHisBlack.pop();
    }

    public ArrayList<CheckerState> getWhite(){
        return stackHisWhite.pop();
    }

    public CheckerState getMove(){
        return stackHisMove.pop();
    }

    public boolean isEmpty(){
        if(this.stackHisMove.size() == 0)
            return true;
        else
            return false;
    }

    public void pushBlack(ArrayList<CheckerState> arr){
        this.stackHisBlack.push(arr);
    }

    public void pushWhite(ArrayList<CheckerState> arr){
        this.stackHisWhite.push(arr);
    }

    public void pushMove(CheckerState chk){
        this.stackHisMove.push(chk);
    }

    public void clear(){
        this.stackHisMove.clear();
        this.stackHisWhite.clear();
        this.stackHisBlack.clear();
    }
}
