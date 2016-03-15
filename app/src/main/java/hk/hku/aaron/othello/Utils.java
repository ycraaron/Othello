package hk.hku.aaron.othello;

/**
 * Created by Aaron on 2015/11/4.
 */
public class Utils {

    public static final int NO_CHESS = 0;
    public static final int BLACK_CHESS = 1;
    public static final int WHITE_CHESS = 2;
    public static final int SOUND_MOVE = 0;

    public static final boolean BLACK_TURN = true;
    public static final boolean WHITE_TURN = false;

    public static final int[][] CHESS_BOARD_RESOURCES =
            {
                    { R.id.btn_board_0x0, R.id.btn_board_0x1, R.id.btn_board_0x2, R.id.btn_board_0x3, R.id.btn_board_0x4, R.id.btn_board_0x5, R.id.btn_board_0x6, R.id.btn_board_0x7 },
                    { R.id.btn_board_1x0, R.id.btn_board_1x1, R.id.btn_board_1x2, R.id.btn_board_1x3, R.id.btn_board_1x4, R.id.btn_board_1x5, R.id.btn_board_1x6, R.id.btn_board_1x7 },
                    { R.id.btn_board_2x0, R.id.btn_board_2x1, R.id.btn_board_2x2, R.id.btn_board_2x3, R.id.btn_board_2x4, R.id.btn_board_2x5, R.id.btn_board_2x6, R.id.btn_board_2x7 },
                    { R.id.btn_board_3x0, R.id.btn_board_3x1, R.id.btn_board_3x2, R.id.btn_board_3x3, R.id.btn_board_3x4, R.id.btn_board_3x5, R.id.btn_board_3x6, R.id.btn_board_3x7 },
                    { R.id.btn_board_4x0, R.id.btn_board_4x1, R.id.btn_board_4x2, R.id.btn_board_4x3, R.id.btn_board_4x4, R.id.btn_board_4x5, R.id.btn_board_4x6, R.id.btn_board_4x7 },
                    { R.id.btn_board_5x0, R.id.btn_board_5x1, R.id.btn_board_5x2, R.id.btn_board_5x3, R.id.btn_board_5x4, R.id.btn_board_5x5, R.id.btn_board_5x6, R.id.btn_board_5x7 },
                    { R.id.btn_board_6x0, R.id.btn_board_6x1, R.id.btn_board_6x2, R.id.btn_board_6x3, R.id.btn_board_6x4, R.id.btn_board_6x5, R.id.btn_board_6x6, R.id.btn_board_6x7 },
                    { R.id.btn_board_7x0, R.id.btn_board_7x1, R.id.btn_board_7x2, R.id.btn_board_7x3, R.id.btn_board_7x4, R.id.btn_board_7x5, R.id.btn_board_7x6, R.id.btn_board_7x7 }
            };
    public static final int[] CHESS_BOARD_ROW_RESOURCES =
            {
                    R.id.chessboard_r0, R.id.chessboard_r1, R.id.chessboard_r2, R.id.chessboard_r3,
                    R.id.chessboard_r4, R.id.chessboard_r5, R.id.chessboard_r6, R.id.chessboard_r7
            };

}
