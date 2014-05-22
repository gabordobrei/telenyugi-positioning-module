
package hu.bme.emt.telenyugi;

import hu.bme.emt.telenyugi.positioning.R;

import java.text.MessageFormat;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.TextAppearanceSpan;

public final class UIUtils {

    private static Typeface sRobotoBold = null;
    private static Typeface sRobotoRegular = null;
    private static Typeface sRobotoCondensed = null;

    private UIUtils() {
    }

    public static Typeface getRobotoBold(Context context) {
        if (sRobotoBold == null) {
            sRobotoBold = Typeface.createFromAsset(context.getAssets(), "RobotoBold.ttf");
        }
        return sRobotoBold;
    }

    public static Typeface getRobotoRegular(Context context) {
        if (sRobotoRegular == null) {
            sRobotoRegular = Typeface.createFromAsset(context.getAssets(), "RobotoRegular.ttf");
        }
        return sRobotoRegular;
    }

    public static Typeface getRobotoCondensed(Context context) {
        if (sRobotoCondensed == null) {
            sRobotoCondensed = Typeface.createFromAsset(context.getAssets(), "RobotoCondensed.ttf");
        }
        return sRobotoCondensed;
    }

    public static CharSequence setKmTitle(String text, int start, int offset, Context context) {
        SpannableStringBuilder builder = new SpannableStringBuilder(text.toUpperCase());
        builder.setSpan(new TextAppearanceSpan("sans-serif-condensed", 0, 32, null, null), 0,
                builder.length(), 0);
        builder.setSpan(
                new CustomTypefaceSpan("sans-serif-condensed", getRobotoCondensed(context)),
                0,
                builder.length(),
                0);
        builder.setSpan(
                new CustomTypefaceSpan("sans-serif-bold", getRobotoBold(context)),
                start,
                start + offset,
                0);
        final int color = context.getResources().getColor(R.color.green_main);
        builder.setSpan(new ForegroundColorSpan(color), start,
                start + offset,
                0);
        return builder;
    }

    public static CharSequence setKmValue(String km, Context context) {
        String text = MessageFormat.format("{0}km", km);
        SpannableStringBuilder builder = new SpannableStringBuilder(text.toUpperCase());
        builder.setSpan(new RelativeSizeSpan(0.3f), builder.length() - 2, builder.length(),
                0);
        builder.setSpan(
                new CustomTypefaceSpan("sans-serif-bold", getRobotoBold(context)),
                0,
                builder.length(),
                0);
        SpannableStringBuilder builder2 = new SpannableStringBuilder("\nMORE TO GO");
        builder2.setSpan(
                new CustomTypefaceSpan("sans-serif-bold", getRobotoBold(context)),
                0,
                builder2.length(),
                0);
        builder2.setSpan(new RelativeSizeSpan(0.3f), 0, builder2.length(),
                0);
        final int color = context.getResources().getColor(R.color.grey_main);
        builder2.setSpan(new ForegroundColorSpan(color), 0, builder2.length(),
                0);
        return TextUtils.concat(builder, builder2);
    }
}
