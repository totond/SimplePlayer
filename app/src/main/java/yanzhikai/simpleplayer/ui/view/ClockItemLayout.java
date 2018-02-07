package yanzhikai.simpleplayer.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import yanzhikai.simpleplayer.R;

/**
 * author : yany
 * e-mail : yanzhikai_yjk@qq.com
 * time   : 2018/02/02
 * desc   :
 */

public class ClockItemLayout extends RelativeLayout {
    private Context mContext;
    private TextView tv_item_name, tv_content;
    private ImageView iv_enter;
    private Switch sw;

    public ClockItemLayout(Context context) {
        super(context);
        init(context);
    }

    public ClockItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        initAttrs(context, attrs);
    }

    public ClockItemLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        initAttrs(context, attrs);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        mContext = context;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ClockItemLayout);
        setItemName(typedArray.getString(R.styleable.ClockItemLayout_itemName));
        setSwitchShow(typedArray.getBoolean(R.styleable.ClockItemLayout_showSwitch, false));
        typedArray.recycle();
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.clock_item, this);
        tv_item_name = findViewById(R.id.tv_item_name);
        tv_content = findViewById(R.id.tv_content);
        iv_enter = findViewById(R.id.iv_enter);
        sw = findViewById(R.id.sw);
    }

    public void setSwitchShow(Boolean show) {
        if (show) {
            sw.setVisibility(VISIBLE);
            iv_enter.setVisibility(INVISIBLE);
        } else {
            sw.setVisibility(INVISIBLE);
            iv_enter.setVisibility(VISIBLE);
        }
    }

    public void setSwitchClickable(boolean clickable){
        sw.setClickable(clickable);
    }

    public Switch getSwitch(){
        return sw;
    }

    public void setSwitch(boolean open) {
        sw.setChecked(open);
    }

    public void setContentText(String text) {
        tv_content.setText(text);
    }

    public void setItemName(String name) {
        tv_item_name.setText(name);
    }

    public void setItemEnable(boolean enable) {
        setEnabled(enable);
        if (enable) {
            tv_item_name.setTextColor(mContext.getResources().getColor(R.color.colorBlack));
            tv_content.setTextColor(mContext.getResources().getColor(R.color.colorBlack));
            iv_enter.setImageResource(R.mipmap.right);
        } else {
            tv_item_name.setTextColor(mContext.getResources().getColor(R.color.colorLightGrey));
            tv_content.setTextColor(mContext.getResources().getColor(R.color.colorLightGrey));
            iv_enter.setImageResource(R.mipmap.right_disable);
        }
    }
}
