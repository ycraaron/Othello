package hk.hku.aaron.othello;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class ActivityGamePlay extends Activity implements View.OnClickListener{
    Context appContext;
    /**Chessboard**/
    ImageButton[][] btn_board;
    TableRow[] tbl_chessBoardRow;
    /**Operation button**/
    Button btn_restart;
    Button btn_undo;
    Button btn_redo;
    Button btn_hint;

    /**Foot**/
    TextView txt_cnt_black;
    TextView txt_turn;
    TextView txt_cnt_white;
    ImageView img_blackchess;
    ImageView img_turn;
    ImageView img_whitechess;
    RelativeLayout layout_game_play;

    /**Flags**/
    boolean started;
    boolean hintsOn;
    boolean turn;
    int cntHintsUpdate;

    /**Soundpool**/
    boolean soundOn = true;
    SoundPool sp;
    int soundMove;

    /**Search Direction**/
    Direction down = new Direction(1,0);
    Direction right = new Direction(0,1);
    Direction up = new Direction(-1,0);
    Direction left = new Direction(0,-1);
    Direction downRight    = new Direction(1,1);
    Direction upLeft    = new Direction(-1,-1);
    Direction downLeft = new Direction(1,-1);
    Direction upRight = new Direction(-1,1);

    CheckerState checkerState[][] = new CheckerState[8][8];

    /**Store the current chessboard state**/
    ArrayList<CheckerState> posBlack = new ArrayList<CheckerState>();
    ArrayList<CheckerState> posWhite = new ArrayList<CheckerState>();
    ArrayList<CheckerState> hints = new ArrayList<CheckerState>();
    HashMap<String,ArrayList<CheckerState>> map = new HashMap<String,ArrayList<CheckerState>>();

    /**For retract, stack to store the last step status**/
    Undo undo = new Undo();
    Redo redo = new Redo();
    @Override
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_game_play);
        getIntent();
        initialChessBoard();
        initialSound();
    }

    protected void initialSound(){
        sp = new SoundPool(10, AudioManager.STREAM_SYSTEM,5);
        soundMove = sp.load(this,R.raw.sound_move,1);
    }

    protected void initialChessBoard(){

        for(int i = 0; i < 8; i++)
        {
            for(int j = 0; j < 8; j++)
            {
                checkerState[i][j] = new CheckerState(i,j,Utils.NO_CHESS);
            }
        }
        this.checkerState[3][4].changeState(Utils.BLACK_CHESS);
        this.checkerState[4][3].changeState(Utils.BLACK_CHESS);
        this.checkerState[3][3].changeState(Utils.WHITE_CHESS);
        this.checkerState[4][4].changeState(Utils.WHITE_CHESS);
        this.posBlack.add(checkerState[3][4]);
        this.posBlack.add(checkerState[4][3]);
        this.posWhite.add(checkerState[3][3]);
        this.posWhite.add(checkerState[4][4]);
        turn = Utils.BLACK_TURN;

        //started = false;
        //hintsOn = false;

        /***** start of initialize chess board *****/
        this.btn_board = new ImageButton[8][8];
        this.tbl_chessBoardRow = new TableRow[8];
        int i = 0;
        while (i < 8)
        {
            this.tbl_chessBoardRow[i] = ((TableRow)findViewById(Utils.CHESS_BOARD_ROW_RESOURCES[i]));
            int j = 0;
            while (j < 8)
            {
                this.btn_board[i][j] = ((ImageButton)findViewById(Utils.CHESS_BOARD_RESOURCES[i][j]));
                //this.btn_board[i][j].setBackground(this.getResources().getDrawable(R.drawable.gradient_checker));
                //this.btn_board[i][j].getBackground().setAlpha(160);
                j++;
            }
            i++;
        }
        /***** end of initialize chess board *****/

        /***** start of layout chess board image button *****/
        this.appContext = getBaseContext();
        int k = this.appContext.getResources().getDisplayMetrics().widthPixels;
        int j = this.appContext.getResources().getDisplayMetrics().heightPixels;
        i = k / 28;
        int m = k - i * 2;
        k = m / 9; //calculate the width and height
        j = (j - m - convertDipToPixels(120.0F) * 2) / 2;
        m = convertPixelsToDip(i);
        int n = convertPixelsToDip(j);
        i = 0;
        while (i < 8)
        {
            j = 0;
            while (j < 8)
            {
                this.btn_board[i][j].getLayoutParams().width = k;
                this.btn_board[i][j].getLayoutParams().height = k;
                this.btn_board[i][j].setOnClickListener(this);
                j++;
            }
            i++;
        }
        TableLayout.LayoutParams tmpBundle2 = new TableLayout.LayoutParams(-1,-2);
        TableLayout.LayoutParams localLayoutParams1 = new TableLayout.LayoutParams(-1, -2);
        TableLayout.LayoutParams localLayoutParams2 = new TableLayout.LayoutParams(-1, -2);
        tmpBundle2.setMargins(m,n,m,0);
        localLayoutParams1.setMargins(m, 0, m, 0);
        localLayoutParams2.setMargins(m, 0, m, n);
        this.tbl_chessBoardRow[0].setLayoutParams(tmpBundle2);
        this.tbl_chessBoardRow[7].setLayoutParams(localLayoutParams2);
        i = 1;
        while (i < 7)
        {
            this.tbl_chessBoardRow[i].setLayoutParams(localLayoutParams1);
            i += 1;
        }
        /***** end of layout chess board image button *****/

        this.layout_game_play = (RelativeLayout)findViewById(R.id.layout_game_play_bg);
        this.btn_restart = ((Button)findViewById(R.id.btn_restart));
        this.btn_undo = ((Button)findViewById(R.id.btn_undo));
        this.btn_redo = ((Button)findViewById(R.id.btn_redo));
        this.btn_hint = ((Button)findViewById(R.id.btn_hint));
        this.txt_cnt_black = ((TextView)findViewById(R.id.txt_cnt_black));
        this.txt_cnt_white = ((TextView)findViewById(R.id.txt_cnt_white));
        this.txt_turn = ((TextView)findViewById(R.id.txt_turn));
        this.img_blackchess = ((ImageView)findViewById(R.id.img_blackchess));
        this.img_whitechess = ((ImageView)findViewById(R.id.img_whitechess));
        this.img_turn = ((ImageView)findViewById(R.id.img_turn));
        this.btn_restart.setOnClickListener(this);
        this.btn_undo.setOnClickListener(this);
        this.btn_hint.setOnClickListener(this);
        this.btn_redo.setOnClickListener(this);
        this.started = false;
        this.hintsOn = false;

        btn_restart.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    v.setBackgroundResource(R.drawable.img_btn_restart_press);
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    v.setBackgroundResource(R.drawable.img_btn_restart);
                }
                return false;
            }
        });

        btn_undo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    v.setBackgroundResource(R.drawable.img_btn_undo_press);
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    v.setBackgroundResource(R.drawable.img_btn_undo);
                }
                return false;
            }
        });

        btn_redo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    v.setBackgroundResource(R.drawable.img_btn_redo_press);
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    v.setBackgroundResource(R.drawable.img_btn_redo);
                }
                return false;
            }
        });

        btn_hint.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(!hintsOn){
                    if(event.getAction() == MotionEvent.ACTION_DOWN){
                        v.setBackgroundResource(R.drawable.img_btn_hints_on_press);
                    }else if(event.getAction() == MotionEvent.ACTION_UP){
                        v.setBackgroundResource(R.drawable.img_btn_hints_on);
                    }
                }
                else{
                    if(event.getAction() == MotionEvent.ACTION_DOWN){
                        v.setBackgroundResource(R.drawable.img_btn_hints_off_press);
                    }else if(event.getAction() == MotionEvent.ACTION_UP){
                        v.setBackgroundResource(R.drawable.img_btn_hints_off);
                    }
                }

                return false;
            }
        });

        resetGame();
    }

    protected int convertDipToPixels(float paramFloat)
    {
        if (this.appContext == null)
            this.appContext = getBaseContext();
        return (int)(paramFloat * this.appContext.getResources().getDisplayMetrics().density + 0.5F);
    }

    protected int convertPixelsToDip(float paramFloat)
    {
        if (this.appContext == null)
            this.appContext = getBaseContext();
        return (int)(paramFloat / this.appContext.getResources().getDisplayMetrics().density + 0.5F);
    }

    protected void resetGame(){
        this.btn_undo.setEnabled(false);
        this.btn_redo.setEnabled(false);
        this.btn_hint.setEnabled(true);
        if(hintsOn) {
            hideHints();
        }
        this.hintsOn = false;
        this.started = true;

        btn_undo.setBackgroundResource(R.drawable.img_btn_undo_unable);
        btn_redo.setBackgroundResource(R.drawable.img_btn_redo_unable);

        cntHintsUpdate = 0;

        for (int i = 0; i < this.posBlack.size(); i++) {
            posBlack.get(i).changeState(Utils.NO_CHESS);
            btn_board[posBlack.get(i).getX()][posBlack.get(i).getY()].setImageResource(R.drawable.transparent);
        }
        for (int i = 0; i < this.posWhite.size(); i++) {
            posWhite.get(i).changeState(Utils.NO_CHESS);
            btn_board[posWhite.get(i).getX()][posWhite.get(i).getY()].setImageResource(R.drawable.transparent);
        }
        this.posBlack.clear();
        this.posWhite.clear();
        undo.clear();
        this.checkerState[3][4].changeState(Utils.BLACK_CHESS);
        this.checkerState[4][3].changeState(Utils.BLACK_CHESS);
        this.checkerState[3][3].changeState(Utils.WHITE_CHESS);
        this.checkerState[4][4].changeState(Utils.WHITE_CHESS);
        this.posBlack.add(checkerState[3][4]);
        this.posBlack.add(checkerState[4][3]);
        this.posWhite.add(checkerState[3][3]);
        this.posWhite.add(checkerState[4][4]);
        btn_board[3][4].setImageResource(R.drawable.black_chess);
        btn_board[4][3].setImageResource(R.drawable.black_chess);
        btn_board[3][3].setImageResource(R.drawable.white_chess);
        btn_board[4][4].setImageResource(R.drawable.white_chess);
        this.turn = Utils.BLACK_TURN;

        updateHints();
    }

    protected void undo(){
        if(hintsOn)
            hideHints();
        //posBlack,posWhite,lastStep;
        this.btn_redo.setEnabled(true);
        this.btn_redo.setBackgroundResource(R.drawable.img_btn_redo);

        CheckerState chkHis = undo.getMove();

        if(chkHis.getState() == Utils.BLACK_CHESS) {
            //Log.v("msg","State B1  W2 :" + chkHis.getState());
            this.turn = Utils.BLACK_TURN;
            img_turn.setImageResource(R.drawable.black_chess);
        }
        else {
            //Log.v("msg","State B1  W2 :" + chkHis.getState());
            this.turn = Utils.WHITE_TURN;
            img_turn.setImageResource(R.drawable.white_chess);
        }


        redo.pushBlack(this.posBlack);
        redo.pushWhite(this.posWhite);
        redo.pushMove(chkHis);

        this.posBlack = undo.getBlack();
        this.posWhite = undo.getWhite();

        this.txt_cnt_black.setText("   " + posBlack.size());
        this.txt_cnt_white.setText("   " + posWhite.size());

        int x,y;

        this.checkerState[chkHis.getX()][chkHis.getY()].changeState(Utils.NO_CHESS);
        this.btn_board[chkHis.getX()][chkHis.getY()].setImageResource(R.drawable.transparent);

        for(int i = 0; i < posBlack.size(); i++){
            x = posBlack.get(i).getX();
            y = posBlack.get(i).getY();
            checkerState[x][y].changeState(Utils.BLACK_CHESS);
            btn_board[x][y].setImageResource(R.drawable.black_chess);
        }

        for(int j = 0; j < posWhite.size(); j++){
            x = posWhite.get(j).getX();
            y = posWhite.get(j).getY();
            checkerState[x][y].changeState(Utils.WHITE_CHESS);
            btn_board[x][y].setImageResource(R.drawable.white_chess);
        }

        updateHints();

        if(undo.isEmpty()) {
            btn_undo.setEnabled(false);
            btn_undo.setBackgroundResource(R.drawable.img_btn_undo_unable);
        }
        if(!redo.isEmpty()) {
            btn_redo.setEnabled(true);
            btn_redo.setBackgroundResource(R.drawable.img_btn_redo);
        }
    }

    protected void redo(){
        if(hintsOn)
            hideHints();
        CheckerState chk = redo.getMove();
        moveChess(chk.getX(),chk.getY(),1);
        if(redo.isEmpty()) {
            btn_redo.setEnabled(false);
            btn_redo.setBackgroundResource(R.drawable.img_btn_redo_unable);
        }
        if(!undo.isEmpty()) {
            btn_undo.setEnabled(true);
            btn_undo.setBackgroundResource(R.drawable.img_btn_undo);

        }

    }
    protected void moveChess(int x, int y, int flag){
        int i;

        /**Check checker available**/
        boolean isInHint = false;
        for(i = 0; i < hints.size(); i++){
            if((checkerState[x][y].getX() == hints.get(i).getX())&&(checkerState[x][y].getY() == hints.get(i).getY())&&(checkerState[x][y].getState() == hints.get(i).getState())){
                {
                    isInHint = true;
                    break;
                }
            }
        }

        if(isInHint){
            if(flag == -1) {
                if (!redo.isEmpty()) {
                    redo.clear();
                    btn_redo.setEnabled(false);
                    this.btn_redo.setBackgroundResource(R.drawable.img_btn_redo_unable);
                }
            }

            if(hintsOn == true)
                this.hideHints();

            if(this.started == true) {
                btn_undo.setEnabled(true);
                btn_undo.setBackgroundResource(R.drawable.img_btn_undo);
            }
            /**Record the chess state before move**/

            ArrayList<CheckerState> tmpBlack = new ArrayList<>(posBlack);
            ArrayList<CheckerState> tmpWhite = new ArrayList<>(posWhite);

            undo.pushBlack(tmpBlack);
            undo.pushWhite(tmpWhite);

            if(this.soundOn)
                sp.play(soundMove,1,1,0,0,1);

            if(this.turn == Utils.BLACK_TURN) {
                /**Move**/
                this.checkerState[x][y].changeState(Utils.BLACK_CHESS);
                undo.pushMove(new CheckerState(checkerState[x][y]));
                this.btn_board[x][y].setImageResource(R.drawable.black_chess);


//                Log.v("msg", "Before doReverse: ");

                /**Do reverse
                 * checkerState[x][y] = checker position in hints
                 * **/

                doReverse(checkerState[x][y]);
                txt_cnt_black.setText("   " + posBlack.size());
                txt_cnt_white.setText("   " + posWhite.size());

                /**Change turn**/
                this.turn = Utils.WHITE_TURN;
                this.img_turn.setImageResource(R.drawable.white_chess);

                /**Update hints**/
                updateHints();
            }
            else
            {

                this.checkerState[x][y].changeState(Utils.WHITE_CHESS);
                undo.pushMove(new CheckerState(checkerState[x][y]));
                this.btn_board[x][y].setImageResource(R.drawable.white_chess);
                /**Do reverse**/

//                Log.v("msg", "Before doReverse: ");

                doReverse(checkerState[x][y]);

                txt_cnt_white.setText("   " + posWhite.size());
                txt_cnt_black.setText("   " + posBlack.size());

                /**Change turn**/
                this.turn = Utils.BLACK_TURN;
                this.img_turn.setImageResource(R.drawable.black_chess);

                /**Update hints**/
                updateHints();
            }

        }
        else
        {

        }

        /**No Move Available**/
        if(hints.size() == 0 && this.started ==true){
            if(this.turn == Utils.BLACK_TURN){
                this.turn = Utils.WHITE_TURN;
                img_turn.setImageResource(R.drawable.white_chess);
            }
            else{
                this.turn = Utils.BLACK_TURN;
                img_turn.setImageResource(R.drawable.black_chess);
            }
            updateHints();
        }

        /**Game Ends**/

        if(posBlack.size() + posWhite.size() == 64 && this.started == true){
            if(posBlack.size() > posWhite.size()) {
                Toast toast = Toast.makeText(getApplicationContext(), "Black Win!", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
            else{
                Toast toast = Toast.makeText(getApplicationContext(), "White Win!", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
            this.btn_undo.setEnabled(false);
            this.btn_redo.setEnabled(false);
            this.started = false;
            btn_undo.setBackgroundResource(R.drawable.img_btn_undo_unable);
            btn_redo.setBackgroundResource(R.drawable.img_btn_redo_unable);
            btn_hint.setEnabled(false);
        }
    }

    protected boolean checkMoveDirection(CheckerState checker, Direction direction){
        /**Get checker state**/
        int x = checker.getX();
        int y = checker.getY();
        int state = checker.getState();
        int stateReversi;

        int cntReversiChess = 0; //Save reversible count

        ArrayList<CheckerState> listReversiChess = new ArrayList(); //Save reversible checker

        if(state == Utils.BLACK_CHESS)
            stateReversi = Utils.WHITE_CHESS;
        else
            stateReversi = Utils.BLACK_CHESS;

        while(true){
            /**Searching**/

            x = x + direction.getX();
            y = y + direction.getY();

            if(isInBound(x, y)){ /**Return point 1, FAIL for exceed bound**/
                if(checkerState[x][y].getState() == Utils.NO_CHESS) {     /** blank checker**/
                    if(cntReversiChess > 0){ /** New hint SUCCESS found**/
                        //CheckerState hintChecker = new CheckerState(x,y,NO_CHESS);
                        String keyForHint = checkerState[x][y].getStrPos();

                        boolean hintExist = false;

                        /**Check if the hint already exists**/
                        for(int i = 0; i < hints.size(); i++) {
                            if(hints.get(i).getStrPos().equals(keyForHint))
                                hintExist = true;
                        }
                        if(hintExist){                        /**Already exist the hint**/
                            for (int p = 0; p < listReversiChess.size(); p++)
                            {
                                map.get(keyForHint).add(listReversiChess.get(p));
                            }
                        }
                        else{
                            this.hints.add(checkerState[x][y]);    /**RECORD the available hint checker**/
                            ArrayList<CheckerState> chessReversiArr = new ArrayList();
                            for(int j = 0; j < listReversiChess.size(); j++)
                            {
                                chessReversiArr.add(listReversiChess.get(j));
                                map.put(keyForHint,chessReversiArr);
                            }

                        }
                        return true;
                    }
                    else
                        return false;   /** FAIL **/
                }
                else if(checkerState[x][y].getState() == state) {   /**FAIL for same chess**/
                    return false;
                }

                else if(checkerState[x][y].getState() == stateReversi) {   /**Record a reversiChess and continue searching**/
                    //Debug revise
                    //listReversiChess.add(new CheckerState(x,y,stateReversi));
                    listReversiChess.add(checkerState[x][y]);
                    cntReversiChess++;
//                    Log.v("msg",cntReversiChess + " cnt " + stateReversi + "(BLACK = 1, WHITE = 2) FOUND. POS: " + x + y);
                }
            }
            else
                return false;
        }
    }

    protected boolean isInBound(int x, int y){
        if( (x < 0) || ( x > 7 ) || ( y < 0 ) || ( y > 7) )
            return false;
        else
            return true;
    }

    protected  void updateHints(){
        cntHintsUpdate ++;
        this.map.clear();
        this.hints.clear();
        int i;
        if(this.turn == Utils.BLACK_TURN) {
            for(i = 0; i < posBlack.size(); i++){
                int x,y;
                x = posBlack.get(i).getX();
                y = posBlack.get(i).getY();
                checkMoveDirection(checkerState[x][y], this.up);
                checkMoveDirection(checkerState[x][y], this.down);
                checkMoveDirection(checkerState[x][y], this.left);
                checkMoveDirection(checkerState[x][y], this.right);
                checkMoveDirection(checkerState[x][y], this.upLeft);
                checkMoveDirection(checkerState[x][y],this.upRight);
                checkMoveDirection(checkerState[x][y],this.downLeft);
                checkMoveDirection(checkerState[x][y],this.downRight);
            }
        }
        else {
            for(i = 0; i < posWhite.size(); i++){
                int x,y;
                x = posWhite.get(i).getX();
                y = posWhite.get(i).getY();
                checkMoveDirection(checkerState[x][y], this.up);
                checkMoveDirection(checkerState[x][y], this.down);
                checkMoveDirection(checkerState[x][y], this.left);
                checkMoveDirection(checkerState[x][y], this.right);
                checkMoveDirection(checkerState[x][y], this.upLeft);
                checkMoveDirection(checkerState[x][y],this.upRight);
                checkMoveDirection(checkerState[x][y],this.downLeft);
                checkMoveDirection(checkerState[x][y],this.downRight);
            }
        }
    }

    protected void doReverse(CheckerState checker){
        /**Move**/
        if(this.turn == Utils.BLACK_TURN){
            posBlack.add(checkerState[checker.getX()][checker.getY()]);
        }
        else
        {
            posWhite.add(checkerState[checker.getX()][checker.getY()]);
        }

        String key = Integer.toString(checker.getX()) + "*" + Integer.toString(checker.getY());
        int i;
        int xReverse,yReverse;

        ArrayList<CheckerState> chessReverseArr = this.map.get(key);

        /**Start reverse**/
        for(i = 0; i < chessReverseArr.size(); i++){
            xReverse = chessReverseArr.get(i).getX();
            yReverse = chessReverseArr.get(i).getY();

            if(this.turn == Utils.BLACK_TURN){

                /**Put new reverse chess in the list**/
                posBlack.add(checkerState[xReverse][yReverse]);

                /**Remove old chess from the list**/

                for(int j = 0; j < posWhite.size(); j++) {
                    if((checkerState[xReverse][yReverse].getX() == posWhite.get(j).getX()) && (checkerState[xReverse][yReverse].getY() == posWhite.get(j).getY()) && (checkerState[xReverse][yReverse].getState() == posWhite.get(j).getState())){
                        {
                            posWhite.remove(j);
                            break;
                        }
                    }

                }
                /**Change image**/
                btn_board[xReverse][yReverse].setImageResource(R.drawable.black_chess);
                checkerState[xReverse][yReverse].changeState(Utils.BLACK_CHESS);

            }
            else {

                /**Put new reverse chess in the list**/

                posWhite.add(checkerState[xReverse][yReverse]);

                /**Remove old chess from the list**/
                for(int j = 0; j < posBlack.size(); j++) {
                    if((checkerState[xReverse][yReverse].getX() == posBlack.get(j).getX()) && (checkerState[xReverse][yReverse].getY() == posBlack.get(j).getY()) && (checkerState[xReverse][yReverse].getState() == posBlack.get(j).getState())){
                        {
                            posBlack.remove(j);
                            break;
                        }
                    }
                }

                /**Change image**/
                btn_board[xReverse][yReverse].setImageResource(R.drawable.white_chess);
                checkerState[xReverse][yReverse].changeState(Utils.WHITE_CHESS);
            }
        }
    }

    protected void showHints(){
        int i;
        for(i = 0; i < hints.size(); i++){
            if(this.turn == Utils.BLACK_TURN)
                btn_board[hints.get(i).getX()][hints.get(i).getY()].
                        setImageResource(R.drawable.black_chess_t);
            else
                btn_board[hints.get(i).getX()][hints.get(i).getY()].
                        setImageResource(R.drawable.white_chess_t);
        }
        hintsOn = true;
        btn_hint.setBackgroundResource(R.drawable.img_btn_hints_off);
    }

    protected void hideHints(){
        int i;
        for(i = 0; i < hints.size(); i++){
                btn_board[hints.get(i).getX()][hints.get(i).getY()].
                        setImageResource(R.drawable.transparent);
        }
        hintsOn = false;
        btn_hint.setBackgroundResource(R.drawable.img_btn_hints_on);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_restart:
                resetGame();
                break;
            case R.id.btn_undo:
                undo();
                break;
            case R.id.btn_redo:
                redo();
                break;
            case R.id.btn_hint:{
                if(!hintsOn)
                    showHints();
                else
                    hideHints();
                break;
            }
            case R.id.btn_board_0x0:
                moveChess(0,0,-1);
                break;
            case R.id.btn_board_0x1:
                moveChess(0,1,-1);
                break;
            case R.id.btn_board_0x2:
                moveChess(0,2,-1);
                break;
            case R.id.btn_board_0x3:
                moveChess(0,3,-1);
                break;
            case R.id.btn_board_0x4:
                moveChess(0,4,-1);
                break;
            case R.id.btn_board_0x5:
                moveChess(0,5,-1);
                break;
            case R.id.btn_board_0x6:
                moveChess(0,6,-1);
                break;
            case R.id.btn_board_0x7:
                moveChess(0,7,-1);
                break;
            case R.id.btn_board_1x0:
                moveChess(1,0,-1);
                break;
            case R.id.btn_board_1x1:
                moveChess(1,1,-1);
                break;
            case R.id.btn_board_1x2:
                moveChess(1,2,-1);
                break;
            case R.id.btn_board_1x3:
                moveChess(1,3,-1);
                break;
            case R.id.btn_board_1x4:
                moveChess(1,4,-1);
                break;
            case R.id.btn_board_1x5:
                moveChess(1,5,-1);
                break;
            case R.id.btn_board_1x6:
                moveChess(1,6,-1);
                break;
            case R.id.btn_board_1x7:
                moveChess(1,7,-1);
                break;
            case R.id.btn_board_2x0:
                moveChess(2,0,-1);
                break;
            case R.id.btn_board_2x1:
                moveChess(2,1,-1);
                break;
            case R.id.btn_board_2x2:
                moveChess(2,2,-1);
                break;
            case R.id.btn_board_2x3:
                moveChess(2,3,-1);
                break;
            case R.id.btn_board_2x4:
                moveChess(2,4,-1);
                break;
            case R.id.btn_board_2x5:
                moveChess(2,5,-1);
                break;
            case R.id.btn_board_2x6:
                moveChess(2,6,-1);
                break;
            case R.id.btn_board_2x7:
                moveChess(2,7,-1);
                break;
            case R.id.btn_board_3x0:
                moveChess(3,0,-1);
                break;
            case R.id.btn_board_3x1:
                moveChess(3,1,-1);
                break;
            case R.id.btn_board_3x2:
                moveChess(3,2,-1);
                break;
            case R.id.btn_board_3x3:
                moveChess(3,3,-1);
                break;
            case R.id.btn_board_3x4:
                moveChess(3,4,-1);
                break;
            case R.id.btn_board_3x5:
                moveChess(3,5,-1);
                break;
            case R.id.btn_board_3x6:
                moveChess(3,6,-1);
                break;
            case R.id.btn_board_3x7:
                moveChess(3,7,-1);
                break;
            case R.id.btn_board_4x0:
                moveChess(4,0,-1);
                break;
            case R.id.btn_board_4x1:
                moveChess(4,1,-1);
                break;
            case R.id.btn_board_4x2:
                moveChess(4,2,-1);
                break;
            case R.id.btn_board_4x3:
                moveChess(4,3,-1);
                break;
            case R.id.btn_board_4x4:
                moveChess(4,4,-1);
                break;
            case R.id.btn_board_4x5:
                moveChess(4,5,-1);
                break;
            case R.id.btn_board_4x6:
                moveChess(4,6,-1);
                break;
            case R.id.btn_board_4x7:
                moveChess(4,7,-1);
                break;
            case R.id.btn_board_5x0:
                moveChess(5,0,-1);
                break;
            case R.id.btn_board_5x1:
                moveChess(5,1,-1);
                break;
            case R.id.btn_board_5x2:
                moveChess(5,2,-1);
                break;
            case R.id.btn_board_5x3:
                moveChess(5,3,-1);
                break;
            case R.id.btn_board_5x4:
                moveChess(5,4,-1);
                break;
            case R.id.btn_board_5x5:
                moveChess(5,5,-1);
                break;
            case R.id.btn_board_5x6:
                moveChess(5,6,-1);
                break;
            case R.id.btn_board_5x7:
                moveChess(5,7,-1);
                break;
            case R.id.btn_board_6x0:
                moveChess(6,0,-1);
                break;
            case R.id.btn_board_6x1:
                moveChess(6,1,-1);
                break;
            case R.id.btn_board_6x2:
                moveChess(6,2,-1);
                break;
            case R.id.btn_board_6x3:
                moveChess(6,3,-1);
                break;
            case R.id.btn_board_6x4:
                moveChess(6,4,-1);
                break;
            case R.id.btn_board_6x5:
                moveChess(6,5,-1);
                break;
            case R.id.btn_board_6x6:
                moveChess(6,6,-1);
                break;
            case R.id.btn_board_6x7:
                moveChess(6,7,-1);
                break;
            case R.id.btn_board_7x0:
                moveChess(7,0,-1);
                break;
            case R.id.btn_board_7x1:
                moveChess(7,1,-1);
                break;
            case R.id.btn_board_7x2:
                moveChess(7,2,-1);
                break;
            case R.id.btn_board_7x3:
                moveChess(7,3,-1);
                break;
            case R.id.btn_board_7x4:
                moveChess(7,4,-1);
                break;
            case R.id.btn_board_7x5:
                moveChess(7,5,-1);
                break;
            case R.id.btn_board_7x6:
                moveChess(7,6,-1);
                break;
            case R.id.btn_board_7x7:
                moveChess(7,7,-1);
                break;
        }
    }
}
