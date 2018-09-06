package com.example.haipingguo.dialogview.dialog.internal;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ScrollView;

import com.example.haipingguo.dialogview.R;
import com.example.haipingguo.dialogview.dialog.GravityEnum;
import com.example.haipingguo.dialogview.dialog.StackingBehavior;
import com.example.haipingguo.dialogview.dialog.util.DividerEnum;
import com.example.haipingguo.dialogview.dialog.util.MUtils;

public class MRootLayout extends ViewGroup {

    private static final int INDEX_NEUTRAL = 0;
    private static final int INDEX_NEGATIVE = 1;
    private static final int INDEX_POSITIVE = 2;
    private final MButton[] buttons = new MButton[3];
    private int maxHeight;
    private View titleBar;
    private View content;
    private boolean drawTopDivider = false;
    private boolean drawBottomDivider = false;
    private StackingBehavior stackBehavior = StackingBehavior.ADAPTIVE;
    private boolean isStacked = false;
    private boolean reducePaddingNoTitleNoButtons;
    private boolean noTitleNoPadding = true;

    private int noTitlePaddingFull;
    private int minTitleHeight;
    private int buttonPaddingFull;
    private int buttonBarHeight;

    private GravityEnum buttonGravity = GravityEnum.START;

    /* Margin from dialog frame to first button */
    private int buttonHorizontalEdgeMargin, buttonMidEdgeMargin;

    private Paint dividerPaint;

    private ViewTreeObserver.OnScrollChangedListener topOnScrollChangedListener;
    private ViewTreeObserver.OnScrollChangedListener bottomOnScrollChangedListener;
    private int dividerWidth;

    private DividerEnum topDividerVisible = DividerEnum.AUTO;
    private DividerEnum bottomDividerVisible = DividerEnum.AUTO;

    public MRootLayout(Context context) {
        super(context);
        init(context, null, 0);
    }

    public MRootLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public MRootLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MRootLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr);
    }

    private static boolean isVisible(View v) {
        boolean visible = v != null && v.getVisibility() != View.GONE;
        if (visible && v instanceof MButton) {
            visible = ((MButton) v).getText().toString().trim().length() > 0;
        }
        return visible;
    }

    public static boolean canRecyclerViewScroll(RecyclerView view) {
        return view != null
                && view.getLayoutManager() != null
                && view.getLayoutManager().canScrollVertically();
    }

    private static boolean canScrollViewScroll(ScrollView sv) {
        if (sv.getChildCount() == 0) {
            return false;
        }
        final int childHeight = sv.getChildAt(0).getMeasuredHeight();
        return sv.getMeasuredHeight() - sv.getPaddingTop() - sv.getPaddingBottom() < childHeight;
    }

    private static boolean canWebViewScroll(WebView view) {
        //noinspection deprecation
        return view.getMeasuredHeight() < view.getContentHeight() * view.getScale();
    }

    private static boolean canAdapterViewScroll(AdapterView lv) {
    /* Force it to layout it's children */
        if (lv.getLastVisiblePosition() == -1) {
            return false;
        }

    /* We can scroll if the first or last item is not visible */
        boolean firstItemVisible = lv.getFirstVisiblePosition() == 0;
        boolean lastItemVisible = lv.getLastVisiblePosition() == lv.getCount() - 1;

        if (firstItemVisible && lastItemVisible && lv.getChildCount() > 0) {
      /* Or the first item's top is above or own top */
            if (lv.getChildAt(0).getTop() < lv.getPaddingTop()) {
                return true;
            }
      /* or the last item's bottom is beyond our own bottom */
            return lv.getChildAt(lv.getChildCount() - 1).getBottom()
                    > lv.getHeight() - lv.getPaddingBottom();
        }

        return true;
    }

    /**
     * Find the view touching the bottom of this ViewGroup. Non visible children are ignored, however
     * getChildDrawingOrder is not taking into account for simplicity and because it behaves
     * inconsistently across platform versions.
     *
     * @return View touching the bottom of this ViewGroup or null
     */
    @Nullable
    private static View getBottomView(ViewGroup viewGroup) {
        if (viewGroup == null || viewGroup.getChildCount() == 0) {
            return null;
        }
        View bottomView = null;
        for (int i = viewGroup.getChildCount() - 1; i >= 0; i--) {
            View child = viewGroup.getChildAt(i);
            if (child.getVisibility() == View.VISIBLE
                    && child.getBottom() == viewGroup.getMeasuredHeight()) {
                bottomView = child;
                break;
            }
        }
        return bottomView;
    }

    @Nullable
    private static View getTopView(ViewGroup viewGroup) {
        if (viewGroup == null || viewGroup.getChildCount() == 0) {
            return null;
        }
        View topView = null;
        for (int i = viewGroup.getChildCount() - 1; i >= 0; i--) {
            View child = viewGroup.getChildAt(i);
            if (child.getVisibility() == View.VISIBLE && child.getTop() == 0) {
                topView = child;
                break;
            }
        }
        return topView;
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        Resources r = context.getResources();

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MRootLayout, defStyleAttr, 0);
        reducePaddingNoTitleNoButtons =
                a.getBoolean(R.styleable.MRootLayout_md_reduce_padding_no_title_no_buttons, true);
        a.recycle();

        minTitleHeight = r.getDimensionPixelSize(R.dimen.md_title_min);
        noTitlePaddingFull = r.getDimensionPixelSize(R.dimen.md_notitle_vertical_padding);
        buttonPaddingFull = r.getDimensionPixelSize(R.dimen.md_button_frame_vertical_padding);

        buttonHorizontalEdgeMargin = r.getDimensionPixelSize(R.dimen.md_button_padding_frame_side);
        buttonMidEdgeMargin = r.getDimensionPixelSize(R.dimen.md_button_padding_frame_mid);
        buttonBarHeight = r.getDimensionPixelSize(R.dimen.md_button_height);

        dividerPaint = new Paint();
        dividerWidth = r.getDimensionPixelSize(R.dimen.md_divider_height);
        dividerPaint.setColor(MUtils.resolveColor(context, R.attr.md_divider_color));
        setWillNotDraw(false);
    }

    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    public void noTitleNoPadding() {
        noTitleNoPadding = true;
    }

    public void setTopDivederVisibility(DividerEnum state) {
        topDividerVisible = state;
    }

    public void setBottomDivederVisibility(DividerEnum state) {
        bottomDividerVisible = state;
    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();
        for (int i = 0; i < getChildCount(); i++) {
            View v = getChildAt(i);
            if (v.getId() == R.id.md_titleFrame) {
                titleBar = v;
            } else if (v.getId() == R.id.md_buttonDefaultNeutral) {
                buttons[INDEX_NEUTRAL] = (MButton) v;
            } else if (v.getId() == R.id.md_buttonDefaultNegative) {
                buttons[INDEX_NEGATIVE] = (MButton) v;
            } else if (v.getId() == R.id.md_buttonDefaultPositive) {
                buttons[INDEX_POSITIVE] = (MButton) v;
            } else {
                content = v;
            }
        }
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        if (height > maxHeight) {
            height = maxHeight;
        }

        boolean hasButtons = false;

        final boolean stacked;
        if (stackBehavior == StackingBehavior.ALWAYS) {
            stacked = true;
        } else if (stackBehavior == StackingBehavior.NEVER) {
            stacked = false;
        } else {
            int buttonsWidth = 0;
            for (MButton button : buttons) {
                if (button != null && isVisible(button)) {
                    button.setStacked(false, false);
                    measureChild(button, widthMeasureSpec, heightMeasureSpec);
                    buttonsWidth += button.getMeasuredWidth();
                    hasButtons = true;
                }
            }

            int buttonBarPadding =
                    getContext().getResources().getDimensionPixelSize(R.dimen.md_neutral_button_margin);
            final int buttonFrameWidth = width - 2 * buttonBarPadding;
            stacked = buttonsWidth > buttonFrameWidth;
        }

        int stackedHeight = 0;
        isStacked = stacked;
        if (stacked) {
            for (MButton button : buttons) {
                if (button != null && isVisible(button)) {
                    button.setStacked(true, false);
                    measureChild(button, widthMeasureSpec, heightMeasureSpec);
                    stackedHeight += button.getMeasuredHeight();
                    hasButtons = true;
                }
            }
        }

        int availableHeight = height;
        int fullPadding = 0;
        int minPadding = 0;
        if (hasButtons) {
            if (isStacked) {
                availableHeight -= (stackedHeight + buttonPaddingFull);
                fullPadding += buttonPaddingFull;
                minPadding += buttonPaddingFull;
            } else {
                availableHeight -= (buttonBarHeight + buttonPaddingFull);
                fullPadding += buttonPaddingFull;
            }
        } else {
//            fullPadding += buttonPaddingFull;
        }

        if (isVisible(titleBar)) {
            titleBar.measure(
                    MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.UNSPECIFIED);
            availableHeight -= titleBar.getMeasuredHeight();
        } else if (!noTitleNoPadding) {
            fullPadding += noTitlePaddingFull;
        } else if (hasButtons) {
            availableHeight -= minTitleHeight;
        }

        if (isVisible(content)) {
            content.measure(
                    MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(availableHeight - (hasButtons ? buttonPaddingFull : 0), MeasureSpec.AT_MOST));

            if (content.getMeasuredHeight() <= availableHeight - fullPadding) {
                if (!reducePaddingNoTitleNoButtons || hasButtons) {
                    availableHeight -= content.getMeasuredHeight() + fullPadding;
                } else {
                    availableHeight -= content.getMeasuredHeight() + minPadding;
                }
            } else {
                availableHeight = 0;
            }
        }

        setMeasuredDimension(width, height - availableHeight);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (content != null) {
            if (topDividerVisible == DividerEnum.GONE) {
                //nothing
            } else if (topDividerVisible == DividerEnum.VISIBLE || drawTopDivider) {
                int y = content.getTop();
                canvas.drawRect(0, y - dividerWidth, getMeasuredWidth(), y, dividerPaint);
            }
            if (bottomDividerVisible == DividerEnum.GONE) {
                //nothing
            } else if (bottomDividerVisible == DividerEnum.VISIBLE || drawBottomDivider) {
                int y = content.getBottom();
                canvas.drawRect(0, y, getMeasuredWidth(), y + dividerWidth, dividerPaint);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, final int l, int t, final int r, int b) {
        if (isVisible(titleBar)) {
            int height = titleBar.getMeasuredHeight();
            titleBar.layout(l, t, r, t + height);
            t += height;
        } else if (!noTitleNoPadding) {
            t += noTitlePaddingFull;
        } else if (isVisible(buttons[INDEX_POSITIVE]) || isVisible(buttons[INDEX_NEGATIVE]) || isVisible(buttons[INDEX_NEUTRAL])) {
            t += minTitleHeight;
        }

        if (isVisible(content)) {
            content.layout(l, t, r, t + content.getMeasuredHeight());
        }

        if (isStacked) {
            b -= buttonPaddingFull;
            for (MButton mButton : buttons) {
                if (isVisible(mButton)) {
                    mButton.layout(l, b - mButton.getMeasuredHeight(), r, b);
                    b -= mButton.getMeasuredHeight();
                }
            }
        } else {
            int barTop;
            int barBottom = b;
            barBottom -= buttonPaddingFull;
            barTop = barBottom - buttonBarHeight;
      /* START:
        Neutral   Negative  Positive

        CENTER:
        Negative  Neutral   Positive

        END:
        Positive  Negative  Neutral

        (With no Positive, Negative takes it's place except for CENTER)
      */
            int offset = buttonHorizontalEdgeMargin;
      /* Used with CENTER gravity */
            if (isVisible(buttons[INDEX_POSITIVE])) {
                int bl, br;
                if (buttonGravity == GravityEnum.END) {
                    bl = l + offset;
                    br = (r - l - buttonMidEdgeMargin) / 2;
                } else {
                    br = r - offset;
                    bl = (r - l + buttonMidEdgeMargin) / 2;
                }
                buttons[INDEX_POSITIVE].layout(bl, barTop, br, barBottom);
            }

            if (isVisible(buttons[INDEX_NEGATIVE])) {
                int bl, br;
                if (buttonGravity == GravityEnum.END) {
                    br = r - offset;
                    bl = (r - l + buttonMidEdgeMargin) / 2;
                } else {
                    bl = l + offset;
                    br = (r - l - buttonMidEdgeMargin) / 2;
                }
                buttons[INDEX_NEGATIVE].layout(bl, barTop, br, barBottom);
            }

            if (isVisible(buttons[INDEX_NEUTRAL])) {
                buttons[INDEX_POSITIVE].setVisibility(GONE);
                buttons[INDEX_NEGATIVE].setVisibility(GONE);

                int bl = l + offset, br = r - offset;
                buttons[INDEX_NEUTRAL].layout(bl, barTop, br, barBottom);
            }
        }

        setUpDividersVisibility(content, true, true);
    }

    public void setStackingBehavior(StackingBehavior behavior) {
        stackBehavior = behavior;
        invalidate();
    }

    public void setDividerColor(int color) {
        dividerPaint.setColor(color);
        invalidate();
    }

    public void setButtonGravity(GravityEnum gravity) {
        buttonGravity = gravity;
        invertGravityIfNecessary();
    }

    private void invertGravityIfNecessary() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return;
        }
        Configuration config = getResources().getConfiguration();
        if (config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
            switch (buttonGravity) {
                case START:
                    buttonGravity = GravityEnum.END;
                    break;
                case END:
                    buttonGravity = GravityEnum.START;
                    break;
            }
        }
    }

    public void setButtonStackedGravity(GravityEnum gravity) {
        for (MButton mButton : buttons) {
            if (mButton != null) {
                mButton.setStackedGravity(gravity);
            }
        }
    }

    private void setUpDividersVisibility(
            final View view, final boolean setForTop, final boolean setForBottom) {
        if (view == null) {
            return;
        }
        if (view instanceof ScrollView) {
            final ScrollView sv = (ScrollView) view;
            if (canScrollViewScroll(sv)) {
                addScrollListener(sv, setForTop, setForBottom);
            } else {
                if (setForTop) {
                    drawTopDivider = false;
                }
                if (setForBottom) {
                    drawBottomDivider = false;
                }
            }
        } else if (view instanceof AdapterView) {
            final AdapterView sv = (AdapterView) view;
            if (canAdapterViewScroll(sv)) {
                addScrollListener(sv, setForTop, setForBottom);
            } else {
                if (setForTop) {
                    drawTopDivider = false;
                }
                if (setForBottom) {
                    drawBottomDivider = false;
                }
            }
        } else if (view instanceof WebView) {
            view.getViewTreeObserver()
                    .addOnPreDrawListener(
                            new ViewTreeObserver.OnPreDrawListener() {
                                @Override
                                public boolean onPreDraw() {
                                    if (view.getMeasuredHeight() != 0) {
                                        if (!canWebViewScroll((WebView) view)) {
                                            if (setForTop) {
                                                drawTopDivider = false;
                                            }
                                            if (setForBottom) {
                                                drawBottomDivider = false;
                                            }
                                        } else {
                                            addScrollListener((ViewGroup) view, setForTop, setForBottom);
                                        }
                                        view.getViewTreeObserver().removeOnPreDrawListener(this);
                                    }
                                    return true;
                                }
                            });
        } else if (view instanceof RecyclerView) {
            boolean canScroll = canRecyclerViewScroll((RecyclerView) view);
            if (setForTop) {
                drawTopDivider = canScroll;
            }
            if (setForBottom) {
                drawBottomDivider = canScroll;
            }
            if (canScroll) {
                addScrollListener((ViewGroup) view, setForTop, setForBottom);
            }
        } else if (view instanceof ViewGroup) {
            View topView = getTopView((ViewGroup) view);
            setUpDividersVisibility(topView, setForTop, setForBottom);
            View bottomView = getBottomView((ViewGroup) view);
            if (bottomView != topView) {
                setUpDividersVisibility(bottomView, false, true);
            }
        }
    }

    private void addScrollListener(
            final ViewGroup vg, final boolean setForTop, final boolean setForBottom) {
        if ((!setForBottom && topOnScrollChangedListener == null
                || (setForBottom && bottomOnScrollChangedListener == null))) {
            if (vg instanceof RecyclerView) {
                RecyclerView.OnScrollListener scrollListener =
                        new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                super.onScrolled(recyclerView, dx, dy);
                                boolean hasButtons = false;
                                for (MButton button : buttons) {
                                    if (button != null && button.getVisibility() != View.GONE) {
                                        hasButtons = true;
                                        break;
                                    }
                                }
                                invalidateDividersForScrollingView(vg, setForTop, setForBottom, hasButtons);
                                invalidate();
                            }
                        };
                ((RecyclerView) vg).addOnScrollListener(scrollListener);
                scrollListener.onScrolled((RecyclerView) vg, 0, 0);
            } else {
                ViewTreeObserver.OnScrollChangedListener onScrollChangedListener =
                        new ViewTreeObserver.OnScrollChangedListener() {
                            @Override
                            public void onScrollChanged() {
                                boolean hasButtons = false;
                                for (MButton button : buttons) {
                                    if (button != null && button.getVisibility() != View.GONE) {
                                        hasButtons = true;
                                        break;
                                    }
                                }
                                if (vg instanceof WebView) {
                                    invalidateDividersForWebView((WebView) vg, setForTop, setForBottom, hasButtons);
                                } else {
                                    invalidateDividersForScrollingView(vg, setForTop, setForBottom, hasButtons);
                                }
                                invalidate();
                            }
                        };
                if (!setForBottom) {
                    topOnScrollChangedListener = onScrollChangedListener;
                    vg.getViewTreeObserver().addOnScrollChangedListener(topOnScrollChangedListener);
                } else {
                    bottomOnScrollChangedListener = onScrollChangedListener;
                    vg.getViewTreeObserver().addOnScrollChangedListener(bottomOnScrollChangedListener);
                }
                onScrollChangedListener.onScrollChanged();
            }
        }
    }

    private void invalidateDividersForScrollingView(
            ViewGroup view, final boolean setForTop, boolean setForBottom, boolean hasButtons) {
        if (setForTop && view.getChildCount() > 0) {
            drawTopDivider =
                    titleBar != null
                            && titleBar.getVisibility() != View.GONE
                            &&
                            //Not scrolled to the top.
                            view.getScrollY() + view.getPaddingTop() > view.getChildAt(0).getTop();
        }
        if (setForBottom && view.getChildCount() > 0) {
            drawBottomDivider =
                    hasButtons
                            && view.getScrollY() + view.getHeight() - view.getPaddingBottom()
                            < view.getChildAt(view.getChildCount() - 1).getBottom();
        }
    }

    private void invalidateDividersForWebView(
            WebView view, final boolean setForTop, boolean setForBottom, boolean hasButtons) {
        if (setForTop) {
            drawTopDivider =
                    titleBar != null
                            && titleBar.getVisibility() != View.GONE
                            &&
                            //Not scrolled to the top.
                            view.getScrollY() + view.getPaddingTop() > 0;
        }
        if (setForBottom) {
            //noinspection deprecation
            drawBottomDivider =
                    hasButtons
                            && view.getScrollY() + view.getMeasuredHeight() - view.getPaddingBottom()
                            < view.getContentHeight() * view.getScale();
        }
    }
}
