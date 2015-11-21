package tjjj.com.hackuci2015;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class SoundboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soundboard);

        View boardLayout = findViewById(R.id.board_layout);

        /*
        //TODO - Loop over all word clips found and create buttons for each of them.

        for(... : ...) {
            Button button = new Button(this);
            button.setText(..get word);
            //TODO - Set top margin 10dp
            boardLayout.addView(button);
        }
        */
    }

}
