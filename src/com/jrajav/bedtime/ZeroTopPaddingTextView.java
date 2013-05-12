package com.jrajav.bedtime;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public final class ZeroTopPaddingTextView
extends TextView {

    private static final float NORMAL_FONT_PADDING_RATIO = 0.328f;
    private static final float BOLD_FONT_PADDING_RATIO = 0.208f;
    private static final float NORMAL_FONT_BOTTOM_PADDING_RATIO = 0.25f;
    private static final float BOLD_FONT_BOTTOM_PADDING_RATIO = 0.208f;

    private static final Typeface SAN_SERIF_BOLD = Typeface.create("san-serif", Typeface.BOLD);

    private int mPaddingRight = 0;

    public ZeroTopPaddingTextView(Context context) {
        this(context, null);
    }

    public ZeroTopPaddingTextView(Context context, AttributeSet attributes) {
        this(context, attributes, 0);
    }

    public ZeroTopPaddingTextView(Context context, AttributeSet attributes, int defaultStyle) {
        super(context, attributes, defaultStyle);
        setIncludeFontPadding(false);
        updatePadding();
    }

    public void updatePadding() {
        float paddingRatio = NORMAL_FONT_PADDING_RATIO;
        float bottomPaddingRatio = NORMAL_FONT_BOTTOM_PADDING_RATIO;
        if (getTypeface().equals(SAN_SERIF_BOLD)) {
            paddingRatio = BOLD_FONT_PADDING_RATIO;
            bottomPaddingRatio = BOLD_FONT_BOTTOM_PADDING_RATIO;
        }

        // There is no need to scale by display density at this point,
        // getTextSize() already returns the font height in px
        int leftPadding = 0;
        int topPadding = (int) (-paddingRatio * getTextSize());
        int rightPadding = mPaddingRight;
        int bottomPadding = (int) (-bottomPaddingRatio * getTextSize());
        setPadding(leftPadding, topPadding, rightPadding, bottomPadding);
    }


    public void setPaddingRight(int padding) {
        mPaddingRight = padding;
        updatePadding();
    }

}
