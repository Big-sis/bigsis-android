package fr.bigsis.android.helpers;

import android.graphics.Color;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

public class MySpannableHelper extends ClickableSpan {

    private boolean isUnderline = true;

    public MySpannableHelper(boolean isUnderline) {
        this.isUnderline = isUnderline;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setUnderlineText(isUnderline);
        ds.setColor(Color.parseColor("#33AB62"));
    }

    @Override
    public void onClick(View widget) {

    }
}