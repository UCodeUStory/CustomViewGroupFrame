package com.example.qiyue.myviewgroup;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by qiyue on 2016/9/27 0027.
 * 学习自定义ViewGroup 经典自定义LinearLayout，目前只想做竖直的
 *
 * 1.确定自定定义布局的样式，比如linearLayout 就是横向和竖向摆放
 * 2.初始化额外的配置参数例如自定义属性，配置额外的需要的View
 * 3.根据1的样式，先测量子View,再根据子View测量自身的宽高  (一个优秀的ViewGroup 需要支持margin,这里padding 不影响,因为高度包括padding)
 * 4.根据1的央视，开始摆放子控件，一般先获取数量然后遍历摆放，
 *   这里有一个重点就是需要对子View margin和padding 的支持 （一个优秀的ViewGroup 需要支持margin,这里padding 不影响,因为高度包括padding）
 *
 *   (自定义ViewGroup需要对Margin支持，自定义VIew需要对padding支持)
 *
 *   存在bug 对marginright 不知道怎么支持，match_parent时还好说，具体数值时就不好说
 */
public class CustomViewGroupFrame extends ViewGroup{
    public CustomViewGroupFrame(Context context) {
        super(context);
        init(context);
    }


    public CustomViewGroupFrame(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomViewGroupFrame(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        measureChildren(widthMeasureSpec,heightMeasureSpec);
        /**
         * 这里处理支持padding margin
         */
        setCurrentMeasuredDimension(widthMeasureSpec,heightMeasureSpec);

    }



    /**
     * 对子View的摆放设置，并且放子View支持margin和padding
     * @param changed
     * @param l
     * @param t
     * @param r
     * @param b
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int bottom = 0;
        int top = 0;
        int count = getChildCount();

        Log.e("qiyue", "childCount="+count + "");
        for (int i = 0;i<count;i++){
            View child = getChildAt(i);
            MarginLayoutParams params = (MarginLayoutParams) child.getLayoutParams();
            int l1 = params.leftMargin;
            int t2 = params.topMargin;
            int r3 = l1 + child.getMeasuredWidth();
            int b4 = t2 + child.getMeasuredHeight();
            top = bottom + t2;
            bottom = bottom + t2 + child.getMeasuredHeight();
//            Log.i("qiyue","l1="+l1+"t2="+t2+"r3="+r3+"b4="+b4);
//            Log.i("qiyue","top-before"+top);
//            Log.i("qiyue","top-after"+top);
            child.layout(l1,top,r3,bottom);
            /**
             * 支持marginbottom 要layout后加
             */
            bottom = bottom + params.bottomMargin;

        }


    }


    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(),attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(p);
    }

    /**
     * 初始化控件
     * @param context
     */
    private void init(Context context) {
    }

    /**
     * 根据 xml里设置的 wrap_content 和 match_parent 和 具体值 ，设置自身的真实宽度
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    private void setCurrentMeasuredDimension(int widthMeasureSpec, int heightMeasureSpec) {
        MarginLayoutParams params = null;
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        Log.i("qiyue","widthMode="+widthMode+"widthSize="+widthSize);
        Log.i("qiyue","heightMode="+heightMode+"heightSize="+heightSize);
        //开始处理wrap_content,如果一个子元素都没有，就设置为0
        //开始处理wrap_content,如果一个子元素都没有，就设置为0
        if (getChildCount() == 0) {
            setMeasuredDimension(0,0);
        } else if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {
            //ViewGroup，宽，高都是wrap_content，根据我们的需求，宽度是子控件的宽度，高度则是所有子控件的总和
            View childOne = getChildAt(0);
            params = (MarginLayoutParams) childOne.getLayoutParams();
            int childWidth = childOne.getMeasuredWidth();
            int childHeight = childOne.getMeasuredHeight();
            setMeasuredDimension(childWidth + params.leftMargin + params.rightMargin,
                    childHeight * getChildCount() + params.topMargin + params.bottomMargin);

        } else if (widthMode == MeasureSpec.AT_MOST) {
            //ViewGroup的宽度为wrap_content,则高度不需要管，宽度还是自控件的宽度
            View childOne = getChildAt(0);
            params = (MarginLayoutParams) childOne.getLayoutParams();
            int childWidth = childOne.getMeasuredWidth();
            setMeasuredDimension(childWidth + params.leftMargin + params.rightMargin,heightSize);
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //ViewGroup的高度为wrap_content,则宽度不需要管，高度为子View的高度和
            View childOne = getChildAt(0);
            params = (MarginLayoutParams) childOne.getLayoutParams();
            int childHeight = childOne.getMeasuredHeight();
            setMeasuredDimension(widthSize, childHeight * getChildCount() + params.topMargin + params.bottomMargin);
        }
    }
}
