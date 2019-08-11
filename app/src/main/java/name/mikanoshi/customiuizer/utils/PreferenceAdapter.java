package name.mikanoshi.customiuizer.utils;

import java.util.ArrayList;
import java.util.Arrays;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SortableListView;
import android.widget.TextView;

import name.mikanoshi.customiuizer.R;

public class PreferenceAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private String key;
	private ArrayList<String> items = new ArrayList<String>();

	public PreferenceAdapter(Context context, String prefKey) {
		mInflater = LayoutInflater.from(context);
		key = prefKey;
		updateItems();
	}

	public void updateItems() {
		items.clear();
		String itemStr = Helpers.prefs.getString(key, "");
		if (itemStr == null || itemStr.isEmpty()) return;
		String[] itemArr = itemStr.trim().split("\\|");
		items.addAll(Arrays.asList(itemArr));
	}

	public int getCount() {
		return items.size();
	}

	public String getItem(int position) {
		return items.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("ClickableViewAccessibility")
	public View getView(final int position, View convertView, ViewGroup parent) {
		View row;
		if (convertView != null)
			row = convertView;
		else
			row = mInflater.inflate(R.layout.pref_item, parent, false);

		ImageView dragHandle = row.findViewById(R.id.drag_handle);
		ImageView itemIcon = row.findViewById(android.R.id.icon);
		TextView itemTitle = row.findViewById(android.R.id.title);
		TextView itemSummary = row.findViewById(android.R.id.summary);

		dragHandle.setVisibility(View.VISIBLE);
		dragHandle.setOnTouchListener(((SortableListView)parent).getListenerForStartingSort());
		itemIcon.setVisibility(View.VISIBLE);
		itemIcon.setColorFilter(Color.argb(200, 78, 78, 81));
		String uuid = getItem(position);
		Pair<String, String> name = Helpers.getActionNameLocal(row.getContext(), key + "_" + uuid);
		if (name == null) {
			itemTitle.setText(R.string.notselected);
			itemSummary.setVisibility(View.GONE);
		} else {
			itemTitle.setText(name.first);
			if (name.second == null || name.second.isEmpty()) {
				itemSummary.setVisibility(View.GONE);
			} else {
				itemSummary.setVisibility(View.VISIBLE);
				itemSummary.setText(name.second);
			}
		}

		String iconResName = Helpers.prefs.getString(key + "_" + uuid + "_icon", "");
		if (name == null || iconResName == null || iconResName.isEmpty() || iconResName.equals("miuizer"))
			itemIcon.setImageResource(R.drawable.keyguard_bottom_miuizer_shortcut_img);
		else try {
			PackageManager manager = row.getContext().getPackageManager();
			Resources resources = manager.getResourcesForApplication("com.android.systemui");
			int resId = resources.getIdentifier("keyguard_left_view_" + iconResName, "drawable", "com.android.systemui");
			Drawable drawable = resources.getDrawable(resId, row.getContext().getTheme());
			itemIcon.setImageDrawable(drawable);
		} catch (Throwable t) {
			t.printStackTrace();
		}

		row.setPadding(row.getPaddingLeft(), row.getPaddingTop(), 0, row.getPaddingBottom());
		return row;
	}

}