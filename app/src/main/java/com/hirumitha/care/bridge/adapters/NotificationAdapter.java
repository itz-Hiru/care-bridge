package com.hirumitha.care.bridge.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.widget.ArrayAdapter;

import com.hirumitha.care.bridge.R;
import com.hirumitha.care.bridge.models.Notification;

import java.util.List;

public class NotificationAdapter extends ArrayAdapter<Notification> {

    public NotificationAdapter(@NonNull Context context, List<Notification> notifications) {
        super(context, 0, notifications);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.notification_item, parent, false);
        }

        Notification notification = getItem(position);

        TextView titleTextView = convertView.findViewById(R.id.notification_title);
        TextView bodyTextView = convertView.findViewById(R.id.notification_body);

        if (notification != null) {
            titleTextView.setText(notification.getTitle() != null ? notification.getTitle() : "");
            bodyTextView.setText(notification.getBody() != null ? notification.getBody() : "");
        }

        return convertView;
    }
}
