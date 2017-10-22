package vedioplay.csy.com.videoplay;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by chenshouyin on 2017/10/21.
 * 我的博客:http://blog.csdn.net/e_inch_photo
 * 我的Github:https://github.com/chenshouyin
 */


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button systemPlayStyle,customPlayStyle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        systemPlayStyle = (Button) findViewById(R.id.systemPlayStyle);
        customPlayStyle = (Button) findViewById(R.id.customPlayStyle);
        systemPlayStyle.setOnClickListener(this);
        customPlayStyle.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        if (v.getId() == systemPlayStyle.getId()){
            Intent intent = new Intent(MainActivity.this,SystemPlayStyleActivity.class);
            startActivity(intent);
        }else if (v.getId() == customPlayStyle.getId()){
            Intent intent = new Intent(MainActivity.this,CustomPlayStyleActivity.class);
            startActivity(intent);
        }
    }
}
