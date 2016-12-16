package group65.chess;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.view.View;
import android.content.Intent;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ListView listView;
    private String[] activities;
    public ArrayList<Recorder> rec = new ArrayList<Recorder>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // action bar
        Toolbar myToolbar = (Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(false);

        // list view (activities)
        listView = (ListView)findViewById(R.id.activities_list);
        activities = getResources().getStringArray(R.array.activities_array);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.activity, activities);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                switch(position) {
                    case 0: startGame(); break;
                    case 1: showRecordedGames(); break;
                    default: break;
                }
            }
        });
    }

    // launches play game screen
    private void startGame() {
        Intent intent = new Intent(this, PlayGame.class);
        intent.putExtra("recorder", rec);
        startActivity(intent);

    }

    // launches recorded games screen
    private void showRecordedGames() {
        Intent intent = new Intent(this, GamesList.class);
        startActivity(intent);
    }

}