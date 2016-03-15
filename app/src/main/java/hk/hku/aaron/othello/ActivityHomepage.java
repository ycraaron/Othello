package hk.hku.aaron.othello;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;


public class ActivityHomepage extends Activity implements View.OnClickListener{

    Button btn_game_start;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_homepage);
        btn_game_start = (Button)findViewById(R.id.btn_game_start);
        btn_game_start.setOnClickListener(this);
        btn_game_start.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        v.setBackgroundResource(R.drawable.img_btn_start_press);
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        v.setBackgroundResource(R.drawable.img_btn_start);
                    }
                return false;
            }
        });
    }

    @Override
    public void onClick(View view){
        Intent intent = new Intent(this,ActivityGamePlay.class);
        startActivity(intent);
    }
}
