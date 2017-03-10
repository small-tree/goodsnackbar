package com.example.goodsnackbar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.example.goodsnackbar.mysnackbar.GoodSnackbar;

public class MainActivity extends AppCompatActivity {

    private FrameLayout viewById;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewById = (FrameLayout) findViewById(R.id.activity_main);
    }


    public void onClick(View view){
        GoodSnackbar make = GoodSnackbar.make(viewById, "i am goodsnackbar", 2000);
        switch (((Button) view).getText().toString()) {
            case "left":
                make.setMessage("i am left");
                make.setWhereFrom(GoodSnackbar.From.LEFT);
                break;
            case "top":
                make.setMessage("i am top");
                make.setWhereFrom(GoodSnackbar.From.TOP);
                break;
            case "right":
                make.setMessage("i am right");
                make.setWhereFrom(GoodSnackbar.From.RIGHT);
                break;
            case "bottom":
                make.setMessage("i am bottom");
                make.setWhereFrom(GoodSnackbar.From.BOTTOM);
                break;
        }

        make.show();
    }

}
