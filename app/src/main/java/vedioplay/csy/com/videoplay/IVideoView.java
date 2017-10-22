package vedioplay.csy.com.videoplay;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

/**
 * Created by chenshouyin on 2017/10/22.
 * 我的博客:http://blog.csdn.net/e_inch_photo
 * 我的Github:https://github.com/chenshouyin
 */

public class IVideoView extends VideoView {
    public void setmIMediaPlayState(IMediaPlayState mIMediaPlayState) {
        this.mIMediaPlayState = mIMediaPlayState;
    }

    private IMediaPlayState mIMediaPlayState;
    public IVideoView(Context context) {
        super(context);
    }

    public IVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //此处设置的默认值可随意,因为getDefaultSize中的size是有值的
        int width = getDefaultSize(0,widthMeasureSpec);
        int height = getDefaultSize(0,heightMeasureSpec);
        setMeasuredDimension(width,height);
        System.out.println("======onMeasure===width==="+width);
        System.out.println("======onMeasure===height==="+height);
    }

    @Override
    public void start() {
        super.start();
        if(mIMediaPlayState!=null){
            mIMediaPlayState.start();
        }
        System.out.println("======start======");

    }

    @Override
    public void pause() {
        super.pause();
        if(mIMediaPlayState!=null){
            mIMediaPlayState.pause();
        }
        System.out.println("======pause======");

    }

    public interface IMediaPlayState{
        void start();//默认是没有start和pause回调的
        void pause();

    }

}
