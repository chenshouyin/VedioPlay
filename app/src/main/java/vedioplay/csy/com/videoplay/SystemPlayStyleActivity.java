package vedioplay.csy.com.videoplay;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

/**
 * Created by chenshouyin on 2017/10/21.
 * 我的博客:http://blog.csdn.net/e_inch_photo
 * 我的Github:https://github.com/chenshouyin
 */


public class SystemPlayStyleActivity extends AppCompatActivity implements View.OnClickListener{

    private VideoView mVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_style);



        String uri = "android.resource://" + getPackageName() + "/" + R.raw.big_buck_bunny;
        //uri = "https://github.com/chenshouyin/DevNote/blob/master/source_for_commen/video_file.mp4";
        //uri = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";
        mVideoView = (VideoView) findViewById(R.id.mVideoView);
        //mVideoView.setVideoPath(uri);
        mVideoView.setVideoURI(Uri.parse(uri));
        mVideoView.setMediaController(new MediaController(this));//设置播放控制器
        mVideoView.start();



    }

    @Override
    public void onClick(View v) {

    }
}
