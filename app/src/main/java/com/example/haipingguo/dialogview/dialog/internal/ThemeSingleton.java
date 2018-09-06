package com.example.haipingguo.dialogview.dialog.internal;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;

import com.example.haipingguo.dialogview.dialog.GravityEnum;

/**
 * Use of this is discouraged for now; for internal use only. See the Global Theming section of the
 * README.
 */
public class ThemeSingleton {

    private static ThemeSingleton singleton;
    @ColorInt
    public int titleColor = 0;
    @ColorInt
    public int contentColor = 0;
    public ColorStateList positiveColor = null;
    public ColorStateList neutralColor = null;
    public ColorStateList negativeColor = null;
    @ColorInt
    public int widgetColor = 0;
    @ColorInt
    public int itemColor = 0;
    public Drawable icon = null;
    public Drawable rightIcon = null;
    @ColorInt
    public int backgroundColor = 0;
    @ColorInt
    public int dividerColor = 0;
    public ColorStateList linkColor = null;
    @DrawableRes
    public int listSelector = 0;
    @DrawableRes
    public int btnSelectorStacked = 0;
    @DrawableRes
    public int btnSelectorPositive = 0;
    @DrawableRes
    public int btnSelectorNeutral = 0;
    @DrawableRes
    public int btnSelectorNegative = 0;
    public GravityEnum titleGravity = GravityEnum.CENTER;
    public GravityEnum contentGravity = GravityEnum.CENTER;
    public GravityEnum btnStackedGravity = GravityEnum.END;
    public GravityEnum itemsGravity = GravityEnum.START;
    public GravityEnum buttonsGravity = GravityEnum.START;

    public static ThemeSingleton get(boolean createIfNull) {
        if (singleton == null && createIfNull) {
            singleton = new ThemeSingleton();
        }
        return singleton;
    }

    public static ThemeSingleton get() {
        return get(true);
    }
}
