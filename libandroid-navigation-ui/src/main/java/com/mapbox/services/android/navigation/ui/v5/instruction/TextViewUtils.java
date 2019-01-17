package com.mapbox.services.android.navigation.ui.v5.instruction;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.TextView;

class TextViewUtils {

  boolean textFits(TextView textView, String text) {
    Paint paint = new Paint(textView.getPaint());
    float width = paint.measureText(text);
    return width < textView.getWidth();
  }

  Drawable createDrawable(TextView textView, Bitmap bitmap) {
    Drawable drawable = new BitmapDrawable(textView.getContext().getResources(), bitmap);
    int bottom = textView.getLineHeight();
    int right = bottom * bitmap.getWidth() / bitmap.getHeight();
    drawable.setBounds(0, 0, right, bottom);

    return drawable;
  }

  void setImageSpan(TextView textView, View view, int start, int end) {
    Bitmap bitmap = createBitmapFromView(view);
    setImageSpan(textView, bitmap, start, end, true);
  }

  void setImageSpan(TextView textView, Bitmap bitmap, int start, int end, boolean shouldTruncate) {
    Drawable drawable = createDrawable(textView, bitmap);
    setImageSpan(textView, drawable, start, end, shouldTruncate);
  }

  void setImageSpan(TextView textView, Drawable drawable, int start, int end) {
    setImageSpan(textView, drawable, start, end, false);
  }

  void setImageSpan(TextView textView, Drawable drawable, int start, int end, boolean
    shouldTruncate) {
    Spannable instructionSpannable = new SpannableString(textView.getText());

    instructionSpannable.setSpan(new ImageSpan(drawable),
      start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

    // todo make sure this is necessary
    if (shouldTruncate) {
      CharSequence truncatedSequence = truncateImageSpan(instructionSpannable, textView);
      textView.setText(truncatedSequence);
    }
  }

  private Bitmap createBitmapFromView(View view) {
    int measureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
    view.measure(measureSpec, measureSpec);

    int measuredWidth = view.getMeasuredWidth();
    int measuredHeight = view.getMeasuredHeight();

    view.layout(0, 0, measuredWidth, measuredHeight);
    Bitmap bitmap = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888);
    bitmap.eraseColor(Color.TRANSPARENT);
    Canvas canvas = new Canvas(bitmap);
    view.draw(canvas);
    return bitmap;
  }

  // todo make sure this is necessary
  private static CharSequence truncateImageSpan(Spannable instructionSpannable, TextView textView) {
    int availableSpace = textView.getWidth() - textView.getPaddingRight() - textView.getPaddingLeft();
    if (availableSpace > 0) {
      return TextUtils.ellipsize(instructionSpannable, textView.getPaint(), availableSpace, TextUtils.TruncateAt.END);
    } else {
      return instructionSpannable;
    }
  }
}
