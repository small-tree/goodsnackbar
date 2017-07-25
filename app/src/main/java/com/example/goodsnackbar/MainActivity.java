package com.example.goodsnackbar;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.example.goodsnackbar.mysnackbar.GoodSnackbar;

public class MainActivity extends Activity {

    private FrameLayout viewById;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewById = (FrameLayout) findViewById(R.id.activity_main);
    }


    public void onClick(View view) {
        final View inflate = View.inflate(this, R.layout.mysnackbar_layout, null);
        Button bt_action = (Button) inflate.findViewById(R.id.bt_action);
        final GoodSnackbar instance = GoodSnackbar.make(this.viewById).setMyView(inflate)
                .setDuration(2500);
        switch (((Button) view).getText().toString()) {
            case "top":
                instance.setWhereFrom(GoodSnackbar.From.TOP);
                break;
            case "bottom":
                instance.setWhereFrom(GoodSnackbar.From.BOTTOM);
                break;
        }
        bt_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                instance.close();
            }
        });
        instance.show();
    }

}
