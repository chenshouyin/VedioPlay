package vedioplay.csy.com.videoplay;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Service;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import vedioplay.csy.com.videoplay.utils.DensityUtil;
import vedioplay.csy.com.videoplay.utils.TimeUtils;

/**
 * Created by chenshouyin on 2017/10/21.
 * 我的博客:http://blog.csdn.net/e_inch_photo
 * 我的Github:https://github.com/chenshouyin
 */


public class CustomPlayStyleActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, MediaPlayer.OnInfoListener, MediaPlayer.OnPreparedListener {

    private IVideoView mVideoView;
    private RelativeLayout rootVideoView;
    private String uri;
    private SeekBar mSeekBar;
    private TextView tvCurrentDuration, tvTotalDuration;
    private ImageView ivPlay, ivScreenSize, ivReplay, ivExit;
    private ObjectAnimator animator;
    private ImageView mProgeress;
    private View viewBottom;
    private boolean curentOrientationPortrait = true;
    private final static int MSG_UPDATA_PROGRESS = 1;
    private final static int MSG_CLOSE_BOTTOM = 2;

    private final static int MSG_UPDATA_DELAY = 1 * 500;
    private final static int MSG_CLOSE_BOTTOM_DELAY = 1 * 1000;
    private float judgeDistance = 20;
    private float oldX = 0;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //更新播放进度
            if (msg.what == MSG_UPDATA_PROGRESS) {
                // 获得当前播放时间和当前视频的长度
                long currentPosition = mVideoView.getCurrentPosition();
                long duration = mVideoView.getDuration();
                int time = (int) ((currentPosition * 100) / duration);
                // 设置进度条的主要进度，表示当前的播放时间
                mSeekBar.setProgress(time);
                tvCurrentDuration.setText(TimeUtils.getTimeShort(currentPosition));
                tvTotalDuration.setText(TimeUtils.getTimeShort(duration));
                //mHandler.sendEmptyMessageAtTime(MSG_UPDATA_PROGRESS, MSG_UPDATA_DELAY); //注意区别 循环发送崩溃
                mHandler.sendEmptyMessageDelayed(MSG_UPDATA_PROGRESS, MSG_UPDATA_DELAY);

            } else if (msg.what == MSG_CLOSE_BOTTOM) {
                //关闭底部控制栏
                animShow(false);
            }
            super.handleMessage(msg);
        }
    };


    //System.out.println("====mm====MSG_UPDATA_PROGRESS====");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_style);


        //uri = "android.resource://" + getPackageName() + "/" + R.raw.test2;
        //uri = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";
        uri = "android.resource://" + getPackageName() + "/" + R.raw.big_buck_bunny;

        initView();
        initPlay();

    }


    private void initView() {
        mVideoView = (IVideoView) findViewById(R.id.mVideoView);
        rootVideoView = (RelativeLayout) findViewById(R.id.rootVideoView);
        rootVideoView.setOnTouchListener(this);
        mSeekBar = (SeekBar) findViewById(R.id.mSeekBar);
        tvCurrentDuration = (TextView) findViewById(R.id.tvCurrentDuration);
        tvTotalDuration = (TextView) findViewById(R.id.tvTotalDuration);
        ivPlay = (ImageView) findViewById(R.id.ivPlay);
        ivScreenSize = (ImageView) findViewById(R.id.ivScreenSize);
        ivPlay.setOnClickListener(this);
        ivScreenSize.setOnClickListener(this);
        ivReplay = (ImageView) findViewById(R.id.ivReplay);
        ivReplay.setOnClickListener(this);
        ivExit = (ImageView) findViewById(R.id.ivExit);
        ivExit.setOnClickListener(this);
        mProgeress = (ImageView) findViewById(R.id.mProgeress);
        viewBottom = findViewById(R.id.viewBottom);

        animator = ObjectAnimator.ofFloat(mProgeress, "rotation", 0, 360);
        animator.setDuration(500);
        animator.setRepeatCount(-1);
        animator.start();

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mVideoView.seekTo(progress * mVideoView.getDuration() / 100);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mHandler.removeMessages(MSG_UPDATA_PROGRESS);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mHandler.sendEmptyMessageDelayed(MSG_UPDATA_PROGRESS, MSG_UPDATA_PROGRESS);
            }
        });
    }

    private void initPlay() {
        mVideoView.setVideoURI(Uri.parse(uri));
        //设置播放状态的监听
        mVideoView.setOnCompletionListener(this);
        mVideoView.setOnPreparedListener(this);
        mVideoView.setOnInfoListener(this);
        mVideoView.setOnErrorListener(this);
        //默认是没有这个回调的
        mVideoView.setmIMediaPlayState(new IVideoView.IMediaPlayState() {
            @Override
            public void start() {
                mHandler.removeMessages(MSG_UPDATA_PROGRESS);
                mHandler.sendEmptyMessageDelayed(MSG_UPDATA_PROGRESS, MSG_UPDATA_DELAY);
                System.out.println("====mm====setmIMediaPlayState==start==");
                ivPlay.setImageResource(R.drawable.img_pause);
            }

            @Override
            public void pause() {
                mHandler.removeMessages(MSG_UPDATA_PROGRESS);
                System.out.println("====mm====setmIMediaPlayState==pause==");
                ivPlay.setImageResource(R.drawable.img_play);
            }

        });

        mVideoView.start();
    }

    @Override
    public void onClick(View v) {
        //点击动画

        switch (v.getId()) {
            case R.id.ivPlay:
                if (mVideoView.isPlaying()) {
                    mVideoView.pause();
                } else {
                    mVideoView.start();
                }
                break;
            case R.id.ivScreenSize:
                animClick(v);
                if (curentOrientationPortrait) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//横屏
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
                }
                break;
            case R.id.ivReplay:

                ivReplay.setVisibility(View.GONE);
                mVideoView.start();
                break;
            case R.id.ivExit:

                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
                break;


            default:
                break;
        }
    }


    /**
     * 监听屏幕方向改变
     *
     * @param newConfig 配置文件配置了configChanges才会走此回调
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //配置文件中设置 android:configChanges="orientation|screenSize|keyboardHidden" 不然横竖屏切换的时候重新创建又重新播放
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mVideoView.setLayoutParams(params);
            rootVideoView.setLayoutParams(params);

            curentOrientationPortrait = false;
            ivExit.setVisibility(View.VISIBLE);
            ivScreenSize.setImageResource(R.drawable.img_full_screen);
            mHandler.sendEmptyMessageDelayed(MSG_CLOSE_BOTTOM, MSG_CLOSE_BOTTOM_DELAY);//延时1s,关闭底部控制栏
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);//显示顶部状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(CustomPlayStyleActivity.this, 300));
            mVideoView.setLayoutParams(params);
            rootVideoView.setLayoutParams(params);
            animShow(true);
            ivExit.setVisibility(View.GONE);
            ivScreenSize.setImageResource(R.drawable.img_not_full_screen);
            curentOrientationPortrait = true;
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//全屏不 显示顶部状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {//点击的是返回键
            relase();
        }
        return super.dispatchKeyEvent(event);
    }

    /**
     * 释放
     * 要在退出前手动调用,不能放在onDestroy中,不然可能引用得不到释放
     */
    private void relase() {
        mHandler.removeCallbacksAndMessages(null);
        if (animator.isRunning()) {
            animator.cancel();
        }
        mVideoView.suspend();//释放播放器
    }


    /**
     * 使用AudioManager控制音量
     *
     * @param value
     * @param context
     */
    private void setVoiceVolume(float value, Context context) {
        try {
            AudioManager audioManager =
                    (AudioManager) context.getSystemService(Service.AUDIO_SERVICE);
            int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);//最大15
            int flag = value > 0 ? -1 : 1;
            currentVolume += flag * 0.1 * maxVolume;
            currentVolume = Math.max(currentVolume,0);//保证最新为0
            // 对currentVolume进行限制
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
            System.out.println("====mm======maxVolume=="+maxVolume+"===currentVolume==="+currentVolume+"==="+audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onCompletion(MediaPlayer mp) {
        //播放完成
        ivReplay.setVisibility(View.VISIBLE);
    }

    //播放错误回调 一般比如视频无法播放等
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        switch (what) {

            case MediaPlayer.MEDIA_ERROR_IO:
                System.out.println("====mm====OnErrorListener===MEDIA_ERROR_IO=" + what);
                break;
            case MediaPlayer.MEDIA_ERROR_MALFORMED:
                System.out.println("====mm====OnErrorListener===MEDIA_ERROR_MALFORMED=" + what);
                break;
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                System.out.println("====mm====OnErrorListener===MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK=" + what);
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                System.out.println("====mm====OnErrorListener===MEDIA_ERROR_SERVER_DIED=" + what);
                break;
            case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
                System.out.println("====mm====OnErrorListener===MEDIA_ERROR_TIMED_OUT=" + what);
                break;
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                System.out.println("====mm====OnErrorListener===MEDIA_ERROR_UNKNOWN=" + what);
                break;
            case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
                System.out.println("====mm====OnErrorListener===MEDIA_ERROR_UNSUPPORTED=" + what);
                break;
            default:
                System.out.println("====mm====OnErrorListener====default==" + what);
                break;
        }
        return false;
    }

    //获取视频信息回调 包括缓冲中,缓冲结束 可用于加载中提示
    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        switch (what) {

            case MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
                System.out.println("====mm====OnInfoListener===MEDIA_INFO_BAD_INTERLEAVING=");
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                System.out.println("====mm====OnInfoListener===MEDIA_INFO_BUFFERING_END=" + what);
                mProgeress.setVisibility(View.GONE);
                System.out.println("====mmcccc======GONE==");
                if (animator.isRunning()) {
                    animator.cancel();
                }
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                System.out.println("====mm====OnInfoListener==MEDIA_INFO_BUFFERING_START==" + what);
                mProgeress.setVisibility(View.VISIBLE);
                if (!animator.isRunning()) {
                    animator.start();
                }
                System.out.println("====mmcccc======VISIBLE==");
                //缓冲中
                break;
            case MediaPlayer.MEDIA_INFO_METADATA_UPDATE:
                System.out.println("====mm====OnInfoListener===MEDIA_INFO_METADATA_UPDATE=" + what);
                break;
            case MediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
                System.out.println("====mm====OnInfoListener===MEDIA_INFO_NOT_SEEKABLE=" + what);
                break;
            case MediaPlayer.MEDIA_INFO_SUBTITLE_TIMED_OUT:
                System.out.println("====mm====OnInfoListener===MEDIA_INFO_SUBTITLE_TIMED_OUT=" + what);
                mProgeress.setVisibility(View.GONE);
                if (animator.isRunning()) {
                    animator.cancel();
                }
                break;
            case MediaPlayer.MEDIA_INFO_UNKNOWN:
                System.out.println("====mm====OnInfoListener==MEDIA_INFO_UNKNOWN==" + what);
                break;
            case MediaPlayer.MEDIA_INFO_UNSUPPORTED_SUBTITLE:
                System.out.println("====mm====OnInfoListener===MEDIA_INFO_UNSUPPORTED_SUBTITLE=" + what);
                break;
            case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                System.out.println("====mm====OnInfoListener===MEDIA_INFO_VIDEO_RENDERING_START=" + what);
                mProgeress.setVisibility(View.GONE);
                if (animator.isRunning()) {
                    animator.cancel();
                }
                break;
            case MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
                System.out.println("====mm====OnInfoListener===MEDIA_INFO_VIDEO_TRACK_LAGGING=" + what);
                break;
            default:
                System.out.println("====mm====OnInfoListener====default==" + what);
                break;
        }
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //缓冲进度回调,用于更新第二进度缓冲进度
        mp.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                // 设置进度条的次要进度，表示视频的缓冲进度
                mSeekBar.setSecondaryProgress(percent);
            }
        });
    }

    private void animClick(View view) {
        ObjectAnimator animationX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0f, 1f);
        ObjectAnimator animationY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0f, 1f);
        AnimatorSet set = new AnimatorSet();
        set.setDuration(500);
        set.playTogether(animationX, animationY);
        set.start();
    }

    private void animShow(final boolean show) {
        ObjectAnimator animationY;
        //animationY = ObjectAnimator.ofFloat(viewBottom, "scaleY", 0f, 1f);
        if (show) {
            //出现
            animationY = ObjectAnimator.ofFloat(viewBottom, "translationY", viewBottom.getHeight(), 0f);
        } else {
            //消失
            animationY = ObjectAnimator.ofFloat(viewBottom, "translationY", 0f, viewBottom.getHeight());
        }
        AnimatorSet set = new AnimatorSet();
        set.setDuration(500);
        set.playTogether(animationY);
        set.start();
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (show) {
                    viewBottom.setVisibility(View.VISIBLE);
                    ivExit.setVisibility(View.VISIBLE);
                } else {
                    viewBottom.setVisibility(View.GONE);
                    ivExit.setVisibility(View.GONE);
                }
            }


            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                oldX = event.getX();
                System.out.println("========onTouch=====ACTION_DOWN===false");
                if (curentOrientationPortrait) {
                    return true;
                }
                if (viewBottom.getVisibility() == View.GONE) {
                    animShow(true);
                    System.out.println("========onTouch=====ACTION_UP===true");
                } else {
                    animShow(false);
                    System.out.println("========onTouch=====ACTION_UP===false");
                }
                break;
            case MotionEvent.ACTION_UP:
                float dis = event.getX()-oldX ;
                WindowManager wm = this.getWindowManager();
                int width = wm.getDefaultDisplay().getWidth();
                if (dis >judgeDistance){
                    //向右
                    System.out.println("======向右==="+dis /width);
                }else if (dis <-judgeDistance){
                    //向左
                    System.out.println("======向左==="+dis /width);
                }
                setVoiceVolume(-dis/width*10,CustomPlayStyleActivity.this);
                break;
            case MotionEvent.ACTION_MOVE:
                System.out.println("========onTouch=====ACTION_MOVE===false");

            default:

                break;
        }
        //false 只进ACTION_DOWN 应该是继续向外传递了
        return true;
    }
}
