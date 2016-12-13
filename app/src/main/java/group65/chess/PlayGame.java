package group65.chess;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

public class PlayGame extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // action bar (up navigation)
        Toolbar myToolbar = (Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(false);

        // grid view (chessboard and pieces)
        GridView gridview = (GridView)findViewById(R.id.gridview);
        gridview.setAdapter(new ImageAdapter(this));

        // displays position of chess piece in grid view
        gridview.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(PlayGame.this, "" + position,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    // inflate menu
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.game_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    // override callback for event handling
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_undo:
                // undo
                return true;
            case R.id.action_ai:
                // ai
                return true;
            case R.id.action_draw:
                // draw
                return true;
            case R.id.action_resign:
                // resign
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}