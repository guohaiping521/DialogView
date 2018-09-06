package com.example.haipingguo.dialogview.dialog;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.haipingguo.dialogview.R;
import com.example.haipingguo.dialogview.dialog.internal.MAdapter;
import com.example.haipingguo.dialogview.dialog.internal.MButton;
import com.example.haipingguo.dialogview.dialog.internal.MRootLayout;
import com.example.haipingguo.dialogview.dialog.internal.MTintHelper;
import com.example.haipingguo.dialogview.dialog.util.MUtils;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Used by MDialog while initializing the dialog. Offloads some of the code to make the main
 * class cleaner and easier to read/maintain.
 *
 * @author Aidan Follestad (afollestad)
 */
class DialogInit {

    @LayoutRes
    static int getInflateLayout(MDialog.Builder builder) {
        if (builder.customView != null) {
            return R.layout.dialog_custom;
        } else if (builder.items != null || builder.adapter != null) {
            if (builder.checkBoxPrompt != null) {
                return R.layout.dialog_list_check;
            }
            return R.layout.dialog_list;
        } else if (builder.progress > -2) {
            return R.layout.dialog_progress;
        } else if (builder.indeterminateProgress) {
            if (builder.indeterminateIsHorizontalProgress) {
                return R.layout.dialog_progress_indeterminate_horizontal;
            }
            return R.layout.dialog_progress_indeterminate;
        } else if (builder.inputCallback != null) {
            if (builder.checkBoxPrompt != null) {
                return R.layout.dialog_input_check;
            }
            return R.layout.dialog_input;
        } else if (builder.checkBoxPrompt != null) {
            return R.layout.dialog_basic_check;
        } else {
            return R.layout.dialog_basic;
        }
    }

    @SuppressWarnings("ConstantConditions")
    @UiThread
    public static void init(final MDialog dialog) {
        final MDialog.Builder builder = dialog.builder;

        // Set cancelable flag and dialog background color
        dialog.setCancelable(builder.cancelable);
        dialog.setCanceledOnTouchOutside(builder.canceledOnTouchOutside);
        if (builder.backgroundColor == 0) {
            builder.backgroundColor =
                    MUtils.resolveColor(dialog.getContext(), R.attr.md_background_color, Color.WHITE);
        }
        if (builder.backgroundColor != 0) {
            GradientDrawable drawable = new GradientDrawable();
            drawable.setCornerRadius(
                    dialog.getContext().getResources().getDimension(R.dimen.md_bg_corner_radius));
            drawable.setColor(builder.backgroundColor);
            dialog.getWindow().setBackgroundDrawable(drawable);
        }

        // Retrieve color theme attributes
        if (!builder.positiveColorSet) {
            builder.positiveColor =
                    MUtils.resolveActionTextColorStateList(
                            dialog.getContext(), R.attr.md_positive_color, builder.positiveColor);
        }
        if (!builder.neutralColorSet) {
            builder.neutralColor =
                    MUtils.resolveActionTextColorStateList(
                            dialog.getContext(), R.attr.md_neutral_color, builder.neutralColor);
        }
        if (!builder.negativeColorSet) {
            builder.negativeColor =
                    MUtils.resolveActionTextColorStateList(
                            dialog.getContext(), R.attr.md_negative_color, builder.negativeColor);
        }
        if (!builder.widgetColorSet) {
            builder.widgetColor =
                    MUtils.resolveColor(dialog.getContext(), R.attr.md_widget_color, builder.widgetColor);
        }

        // Retrieve default title/content colors
        if (!builder.titleColorSet) {
            final int titleColorFallback =
                    MUtils.resolveColor(dialog.getContext(), android.R.attr.textColorPrimary);
            builder.titleColor =
                    MUtils.resolveColor(dialog.getContext(), R.attr.md_title_color, titleColorFallback);
        }
        if (!builder.contentColorSet) {
            final int contentColorFallback =
                    MUtils.resolveColor(dialog.getContext(), android.R.attr.textColorSecondary);
            builder.contentColor =
                    MUtils.resolveColor(dialog.getContext(), R.attr.md_content_color, contentColorFallback);
        }
        if (!builder.itemColorSet) {
            builder.itemColor =
                    MUtils.resolveColor(dialog.getContext(), R.attr.md_item_color, builder.titleColor);
        }

        if (builder.list_margin == -1) {
            builder.list_margin = MUtils.resolveDimension(dialog.getContext(), R.attr.md_list_margin);
        }

        // Retrieve references to views
        dialog.title = (TextView) dialog.view.findViewById(R.id.md_title);
        dialog.icon = (ImageView) dialog.view.findViewById(R.id.md_icon);
        dialog.rightIcon = (ImageView) dialog.view.findViewById(R.id.md_icon_right);
        dialog.rightIcon.setTag(DialogAction.RIGHTICON);
        dialog.rightIcon.setOnClickListener(dialog);
        dialog.titleFrame = dialog.view.findViewById(R.id.md_titleFrame);
        dialog.content = (TextView) dialog.view.findViewById(R.id.md_content);
        dialog.recyclerView = (RecyclerView) dialog.view.findViewById(R.id.md_contentRecyclerView);
        dialog.checkBoxPrompt = (CheckBox) dialog.view.findViewById(R.id.md_promptCheckbox);

        // Button views initially used by checkIfStackingNeeded()
        dialog.positiveButton = (MButton) dialog.view.findViewById(R.id.md_buttonDefaultPositive);
        dialog.neutralButton = (MButton) dialog.view.findViewById(R.id.md_buttonDefaultNeutral);
        dialog.negativeButton = (MButton) dialog.view.findViewById(R.id.md_buttonDefaultNegative);

        // Don't allow the submit button to not be shown for input dialogs
        if (builder.inputCallback != null && builder.positiveText == null) {
            builder.positiveText = dialog.getContext().getText(android.R.string.ok);
        }

        // Set up the initial visibility of action buttons based on whether or not text was set
        dialog.positiveButton.setVisibility(builder.positiveText != null ? View.VISIBLE : View.GONE);
        dialog.neutralButton.setVisibility(builder.neutralText != null ? View.VISIBLE : View.GONE);
        dialog.negativeButton.setVisibility(builder.negativeText != null ? View.VISIBLE : View.GONE);

        // Set up the focus of action buttons
        dialog.positiveButton.setFocusable(true);
        dialog.neutralButton.setFocusable(true);
        dialog.negativeButton.setFocusable(true);
        if (builder.positiveFocus) {
            dialog.positiveButton.requestFocus();
        }
        if (builder.neutralFocus) {
            dialog.neutralButton.requestFocus();
        }
        if (builder.negativeFocus) {
            dialog.negativeButton.requestFocus();
        }

        // Setup icon
        if (builder.icon != null) {
            dialog.icon.setVisibility(View.VISIBLE);
            dialog.icon.setImageDrawable(builder.icon);
        } else {
            Drawable d = MUtils.resolveDrawable(dialog.getContext(), R.attr.md_icon);
            if (d != null) {
                dialog.icon.setVisibility(View.VISIBLE);
                dialog.icon.setImageDrawable(d);
            } else {
                dialog.icon.setVisibility(View.GONE);
            }
        }

        if (builder.rightIcon != null) {
            dialog.rightIcon.setVisibility(View.VISIBLE);
            dialog.rightIcon.setImageDrawable(builder.rightIcon);
        } else {
            Drawable d = MUtils.resolveDrawable(dialog.getContext(), R.attr.md_rightIcon);
            if (d != null) {
                dialog.rightIcon.setVisibility(View.VISIBLE);
                dialog.rightIcon.setImageDrawable(d);
            } else {
                dialog.rightIcon.setVisibility(View.GONE);
            }
        }

        // Setup icon size limiting
        int maxIconSize = builder.maxIconSize;
        if (maxIconSize == -1) {
            maxIconSize = MUtils.resolveDimension(dialog.getContext(), R.attr.md_icon_max_size);
        }
        if (builder.limitIconToDefaultSize
                || MUtils.resolveBoolean(dialog.getContext(), R.attr.md_icon_limit_icon_to_default_size)) {
            maxIconSize = dialog.getContext().getResources().getDimensionPixelSize(R.dimen.md_icon_max_size);
        }
        if (maxIconSize > -1) {
            dialog.icon.setAdjustViewBounds(true);
            dialog.icon.setMaxHeight(maxIconSize);
            dialog.icon.setMaxWidth(maxIconSize);
            dialog.icon.requestLayout();

            dialog.rightIcon.setAdjustViewBounds(true);
            dialog.rightIcon.setMaxHeight(maxIconSize);
            dialog.rightIcon.setMaxWidth(maxIconSize);
            dialog.rightIcon.requestLayout();
        }

        // Setup divider color in case content scrolls
        if (!builder.dividerColorSet) {
            final int dividerFallback = MUtils.resolveColor(dialog.getContext(), R.attr.md_divider);
            builder.dividerColor =
                    MUtils.resolveColor(dialog.getContext(), R.attr.md_divider_color, dividerFallback);
        }
        dialog.view.setDividerColor(builder.dividerColor);

        // Setup title and title frame
        if (dialog.title != null) {
            dialog.setTypeface(dialog.title, builder.mediumFont);
            dialog.title.setTextColor(builder.titleColor);
            dialog.title.setGravity(builder.titleGravity.getGravityInt());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                //noinspection ResourceType
                dialog.title.setTextAlignment(builder.titleGravity.getTextAlignment());
            }

            if (builder.title == null) {
                dialog.titleFrame.setVisibility(View.GONE);
            } else {
                dialog.title.setText(builder.title);
                dialog.titleFrame.setVisibility(View.VISIBLE);
            }
        }

        // Setup content
        if (dialog.content != null) {
            dialog.content.setMovementMethod(new LinkMovementMethod());
            dialog.setTypeface(dialog.content, builder.regularFont);
            dialog.content.setLineSpacing(0f, builder.contentLineSpacingMultiplier);
            if (builder.linkColor == null) {
                dialog.content.setLinkTextColor(
                        MUtils.resolveColor(dialog.getContext(), android.R.attr.textColorPrimary));
            } else {
                dialog.content.setLinkTextColor(builder.linkColor);
            }
            if (TextUtils.isEmpty(builder.title)) {
                //没有 title  的时候，content 使用 title 的颜色和大小
                dialog.content.setTextColor(builder.titleColor);
                dialog.content.setTextSize(TypedValue.COMPLEX_UNIT_PX, builder.getContext().getResources().getDimensionPixelSize(R.dimen.md_title_textsize));
            } else {
                dialog.content.setTextColor(builder.contentColor);
                dialog.content.setTextSize(TypedValue.COMPLEX_UNIT_PX, builder.getContext().getResources().getDimensionPixelSize(R.dimen.md_content_textsize));
            }

            if (builder.contentGravity == GravityEnum.AUTO) {
                dialog.content.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        dialog.content.getViewTreeObserver().removeOnPreDrawListener(this);
                        if (dialog.content.getLineCount() <= 2) {
                            dialog.content.setGravity(Gravity.CENTER);
                        } else
                            dialog.content.setGravity(Gravity.START);
                        return true;
                    }
                });
            } else {
                dialog.content.setGravity(builder.contentGravity.getGravityInt());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    //noinspection ResourceType
                    dialog.content.setTextAlignment(builder.contentGravity.getTextAlignment());
                }
            }
            if (builder.content != null) {
                dialog.content.setText(builder.content);
                dialog.content.setVisibility(View.VISIBLE);
            } else {
                dialog.content.setVisibility(View.GONE);
            }
        }

        // Setup prompt checkbox
        if (dialog.checkBoxPrompt != null) {
            dialog.checkBoxPrompt.setText(builder.checkBoxPrompt);
            dialog.checkBoxPrompt.setChecked(builder.checkBoxPromptInitiallyChecked);
            dialog.checkBoxPrompt.setOnCheckedChangeListener(builder.checkBoxPromptListener);
            dialog.setTypeface(dialog.checkBoxPrompt, builder.regularFont);
            dialog.checkBoxPrompt.setTextColor(builder.contentColor);
            MTintHelper.setTint(dialog.checkBoxPrompt, builder.widgetColor);
        }

        // Setup action buttons
        dialog.view.setButtonGravity(builder.buttonsGravity);
        dialog.view.setButtonStackedGravity(builder.btnStackedGravity);
        dialog.view.setStackingBehavior(builder.stackingBehavior);

        MButton positiveTextView = dialog.positiveButton;
        positiveTextView.setText(builder.positiveText);
        positiveTextView.setTextColor(builder.positiveColor);
        dialog.positiveButton.setStackedSelector(dialog.getButtonSelector(DialogAction.POSITIVE, true));
        dialog.positiveButton.setDefaultSelector(
                dialog.getButtonSelector(DialogAction.POSITIVE, false));
        dialog.positiveButton.setTag(DialogAction.POSITIVE);
        dialog.positiveButton.setOnClickListener(dialog);
        dialog.positiveButton.setVisibility(View.VISIBLE);

        MButton negativeTextView = dialog.negativeButton;
        negativeTextView.setText(builder.negativeText);
        negativeTextView.setTextColor(builder.negativeColor);
        dialog.negativeButton.setStackedSelector(dialog.getButtonSelector(DialogAction.NEGATIVE, true));
        dialog.negativeButton.setDefaultSelector(
                dialog.getButtonSelector(DialogAction.NEGATIVE, false));
        dialog.negativeButton.setTag(DialogAction.NEGATIVE);
        dialog.negativeButton.setOnClickListener(dialog);
        dialog.negativeButton.setVisibility(View.VISIBLE);

        MButton neutralTextView = dialog.neutralButton;
        neutralTextView.setText(builder.neutralText);
        neutralTextView.setTextColor(builder.neutralColor);
        dialog.neutralButton.setStackedSelector(dialog.getButtonSelector(DialogAction.NEUTRAL, true));
        dialog.neutralButton.setDefaultSelector(dialog.getButtonSelector(DialogAction.NEUTRAL, false));
        dialog.neutralButton.setTag(DialogAction.NEUTRAL);
        dialog.neutralButton.setOnClickListener(dialog);
        dialog.neutralButton.setVisibility(View.VISIBLE);

        // Setup list dialog stuff
        if (builder.listCallbackMultiChoice != null) {
            dialog.selectedIndicesList = new ArrayList<>();
        }
        if (dialog.recyclerView != null) {
            if (builder.adapter == null) {
                if (builder.list_margin > 0) {
                    ViewGroup.LayoutParams layoutParams = dialog.recyclerView.getLayoutParams();
                    ViewGroup.MarginLayoutParams marginParams;
                    if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
                        marginParams = (ViewGroup.MarginLayoutParams) layoutParams;
                    } else {
                        marginParams = new ViewGroup.MarginLayoutParams(layoutParams);
                    }
                    marginParams.setMargins(0, builder.list_margin, 0, builder.list_margin);
                    dialog.recyclerView.setLayoutParams(layoutParams);
                }

                if (builder.listCallbackSingleChoice != null) {
                    dialog.listType = MDialog.ListType.SINGLE;
                } else if (builder.listCallbackMultiChoice != null) {
                    dialog.listType = MDialog.ListType.MULTI;
                    if (builder.selectedIndices != null) {
                        dialog.selectedIndicesList = new ArrayList<>(Arrays.asList(builder.selectedIndices));
                        builder.selectedIndices = null;
                    }
                } else {
                    dialog.listType = MDialog.ListType.REGULAR;
                }
                builder.adapter =
                        new DefaultRvAdapter(dialog, MDialog.ListType.getLayoutForType(dialog.listType));
            } else if (builder.adapter instanceof MAdapter) {
                // Notify simple list adapter of the dialog it belongs to
                ((MAdapter) builder.adapter).setDialog(dialog);
            }
        }

        // Setup progress dialog stuff if needed
        setupProgressDialog(dialog);

        // Setup input dialog stuff if needed
        setupInputDialog(dialog);

        dialog.view.setTopDivederVisibility(builder.topDividerVisible);
        dialog.view.setBottomDivederVisibility(builder.bottomDividerVisible);
        // Setup custom views
        if (builder.customView != null) {
            ((MRootLayout) dialog.view.findViewById(R.id.md_root)).noTitleNoPadding();
            FrameLayout frame = (FrameLayout) dialog.view.findViewById(R.id.md_customViewFrame);
            dialog.customViewFrame = frame;
            View innerView = builder.customView;
            if (innerView.getParent() != null) {
                ((ViewGroup) innerView.getParent()).removeView(innerView);
            }
            if (builder.wrapCustomViewInScroll) {
                final Resources r = dialog.getContext().getResources();
                final int framePadding = r.getDimensionPixelSize(R.dimen.md_dialog_frame_margin);
                final ScrollView sv = new ScrollView(dialog.getContext());
                int paddingTop = r.getDimensionPixelSize(R.dimen.md_content_padding_top);
                int paddingBottom = r.getDimensionPixelSize(R.dimen.md_content_padding_bottom);
                sv.setClipToPadding(false);
                if (innerView instanceof EditText) {
                    // Setting padding to an EditText causes visual errors, set it to the parent instead
                    sv.setPadding(framePadding, paddingTop, framePadding, paddingBottom);
                } else {
                    // Setting padding to scroll view pushes the scroll bars out, don't do it if not necessary (like above)
                    sv.setPadding(0, paddingTop, 0, paddingBottom);
                    innerView.setPadding(framePadding, 0, framePadding, 0);
                }
                sv.addView(
                        innerView,
                        new ScrollView.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                innerView = sv;
            }
            frame.addView(
                    innerView,
                    new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        // Setup user listeners
        if (builder.showListener != null) {
            dialog.setOnShowListener(builder.showListener);
        }
        if (builder.cancelListener != null) {
            dialog.setOnCancelListener(builder.cancelListener);
        }
        if (builder.dismissListener != null) {
            dialog.setOnDismissListener(builder.dismissListener);
        }
        if (builder.keyListener != null) {
            dialog.setOnKeyListener(builder.keyListener);
        }

        // Setup internal show listener
        dialog.setOnShowListenerInternal();

        // Other internal initialization
        dialog.invalidateList();
        dialog.setViewInternal(dialog.view);
        dialog.checkIfListInitScroll();

        // Min height and max width calculations
        WindowManager wm = dialog.getWindow().getWindowManager();
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
//        final int windowWidth = size.x;
        final int windowHeight = size.y - MUtils.getStatusBarHeight(dialog.getContext());

        final int windowVerticalPadding =
                dialog.getContext().getResources().getDimensionPixelSize(R.dimen.md_dialog_vertical_margin);
        if (builder.heightRestriction)
            dialog.view.setMaxHeight(windowHeight - windowVerticalPadding * 2);
        else
            dialog.view.setMaxHeight(windowHeight);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
////        lp.width = Math.min(maxWidth, calculatedWidth);
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);
    }

    private static void fixCanvasScalingWhenHardwareAccelerated(ProgressBar pb) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            // Canvas scaling when hardware accelerated results in artifacts on older API levels, so
            // we need to use software rendering
            if (pb.isHardwareAccelerated() && pb.getLayerType() != View.LAYER_TYPE_SOFTWARE) {
                pb.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            }
        }
    }

    private static void setupProgressDialog(final MDialog dialog) {
        final MDialog.Builder builder = dialog.builder;
        if (builder.indeterminateProgress || builder.progress > -2) {
            dialog.progressBar = (ProgressBar) dialog.view.findViewById(android.R.id.progress);
            if (dialog.progressBar == null) {
                return;
            }

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                MTintHelper.setTint(dialog.progressBar, builder.widgetColor);
            }

            if (!builder.indeterminateProgress || builder.indeterminateIsHorizontalProgress) {
                dialog.progressBar.setIndeterminate(
                        builder.indeterminateProgress && builder.indeterminateIsHorizontalProgress);
                dialog.progressBar.setProgress(0);
                dialog.progressBar.setMax(builder.progressMax);
                dialog.progressLabel = (TextView) dialog.view.findViewById(R.id.md_label);
                if (dialog.progressLabel != null) {
                    dialog.progressLabel.setTextColor(builder.contentColor);
                    dialog.setTypeface(dialog.progressLabel, builder.mediumFont);
                    dialog.progressLabel.setText(builder.progressPercentFormat.format(0));
                }
                dialog.progressMinMax = (TextView) dialog.view.findViewById(R.id.md_minMax);
                if (dialog.progressMinMax != null) {
                    dialog.progressMinMax.setTextColor(builder.contentColor);
                    dialog.setTypeface(dialog.progressMinMax, builder.regularFont);

                    if (builder.showMinMax) {
                        dialog.progressMinMax.setVisibility(View.VISIBLE);
                        dialog.progressMinMax.setText(
                                String.format(builder.progressNumberFormat, 0, builder.progressMax));
                        ViewGroup.MarginLayoutParams lp =
                                (ViewGroup.MarginLayoutParams) dialog.progressBar.getLayoutParams();
                        lp.leftMargin = 0;
                        lp.rightMargin = 0;
                    } else {
                        dialog.progressMinMax.setVisibility(View.GONE);
                    }
                } else {
                    builder.showMinMax = false;
                }
            }
        }

        if (dialog.progressBar != null) {
            fixCanvasScalingWhenHardwareAccelerated(dialog.progressBar);
        }
    }

    private static void setupInputDialog(final MDialog dialog) {
        final MDialog.Builder builder = dialog.builder;
        dialog.input = (EditText) dialog.view.findViewById(android.R.id.input);
        if (dialog.input == null) {
            return;
        }
        dialog.setTypeface(dialog.input, builder.regularFont);
        if (builder.inputPrefill != null) {
            dialog.input.setText(builder.inputPrefill);
        }
        dialog.setInternalInputCallback();
        dialog.input.setHint(builder.inputHint);
        dialog.input.setSingleLine();
        dialog.input.setTextColor(builder.contentColor);
        dialog.input.setHintTextColor(MUtils.adjustAlpha(builder.contentColor, 0.3f));
        MTintHelper.setTint(dialog.input, dialog.builder.widgetColor);

        if (builder.inputType != -1) {
            dialog.input.setInputType(builder.inputType);
            if (builder.inputType != InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    && (builder.inputType & InputType.TYPE_TEXT_VARIATION_PASSWORD)
                    == InputType.TYPE_TEXT_VARIATION_PASSWORD) {
                // If the flags contain TYPE_TEXT_VARIATION_PASSWORD, apply the password transformation method automatically
                dialog.input.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        }

        dialog.inputMinMax = (TextView) dialog.view.findViewById(R.id.md_minMax);
        if (builder.inputMinLength > 0 || builder.inputMaxLength > -1) {
            dialog.invalidateInputMinMaxIndicator(
                    dialog.input.getText().toString().length(), !builder.inputAllowEmpty);
        } else {
            dialog.inputMinMax.setVisibility(View.GONE);
            dialog.inputMinMax = null;
        }
    }
}
