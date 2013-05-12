package com.jrajav.bedtime;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public final class LabelView
extends TextView {

    public LabelView(Context context) {
        this(context, null);
    }

    public LabelView(Context context, AttributeSet attributes) {
        super(context, attributes);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Thin.ttf"));
    }

}
