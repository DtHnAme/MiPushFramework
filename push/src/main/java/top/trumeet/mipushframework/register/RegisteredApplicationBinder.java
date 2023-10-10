package top.trumeet.mipushframework.register;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.NonNull;

import com.xiaomi.xmsf.R;

import top.trumeet.common.utils.Utils;
import top.trumeet.mipush.provider.register.RegisteredApplication;
import top.trumeet.mipushframework.permissions.ManagePermissionsActivity;
import top.trumeet.mipushframework.utils.BaseAppsBinder;
import top.trumeet.mipushframework.utils.ParseUtils;

/**
 * Created by Trumeet on 2017/8/26.
 * @author Trumeet
 */

public class RegisteredApplicationBinder extends BaseAppsBinder<RegisteredApplication> {
    RegisteredApplicationBinder() {
        super();
    }

    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder holder
            , @NonNull final RegisteredApplication item) {
        Context context = holder.itemView.getContext();
        fillData(item.getPackageName(), true,
                holder);
        int ErrorColor = context.getColor(R.color.text_color_error);
        holder.text2.setText(null);
        if (!item.existServices) {
            holder.text2.setText(R.string.mipush_services_not_found);
            holder.text2.setTextColor(ErrorColor);
        }

        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.textColorPrimary, typedValue, true);
        TypedArray array = context.obtainStyledAttributes(typedValue.data, new int[]{android.R.attr.textColorPrimary});

        holder.summary.setText(R.string.no_pushed);
        holder.summary.setTextColor(array.getColor(0, -1));

        if (item.lastReceiveTime.getTime() != 0) {
            holder.summary.setText(String.format("%s%s",
                    context.getString(R.string.last_receive),
                    ParseUtils.getFriendlyDateString(item.lastReceiveTime, Utils.getUTC(), context)));
        }
        switch (item.getRegisteredType()) {
            case 1: {
                holder.status.setText(null);
                break;
            }
            case 2: {
                holder.summary.setText(R.string.app_registered_error);
                holder.summary.setTextColor(ErrorColor);
                break;
            }
            case 0: {
                holder.summary.setText(R.string.status_app_not_registered);
                holder.summary.setTextColor(ErrorColor);
                break;
            }
        }
        holder.itemView.setOnClickListener(view -> context
                .startActivity(new Intent(context,
                        ManagePermissionsActivity.class)
                .putExtra(ManagePermissionsActivity.EXTRA_PACKAGE_NAME,
                        item.getPackageName())
                .putExtra(ManagePermissionsActivity.EXTRA_IGNORE_NOT_REGISTERED, true)));

        if (holder.status.getText().equals(""))
            holder.status.setVisibility(View.GONE);
        if (holder.text2.getText().equals(""))
            holder.text2.setVisibility(View.GONE);
    }
}
