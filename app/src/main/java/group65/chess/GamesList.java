package group65.chess;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class GamesList extends AppCompatActivity{
    private ListView listView;
    private String[] gameNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);

        // action bar (up navigation)
        Toolbar myToolbar = (Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        // list view (recorded games)
        listView = (ListView)findViewById(R.id.records_list);
        gameNames = getResources().getStringArray(R.array.records_array);
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this,
                        R.layout.activity,
                        gameNames);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                runGame(position);
            }
        });
    }

    // launches game playback screen
    private void runGame(int pos) {
        Intent intent = new Intent(this, Playback.class);
        startActivity(intent);
    }

    @Override
    // inflate menu
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.records_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    // override callback for event handling
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_byName:
                // sort by name
                return true;
            case R.id.action_byDate:
                // sort by date
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}