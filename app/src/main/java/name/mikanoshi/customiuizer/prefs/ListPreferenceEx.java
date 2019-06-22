package name.mikanoshi.customiuizer.prefs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import name.mikanoshi.customiuizer.R;

public class ListPreferenceEx extends ListPreference {

	private boolean dynamic;
	private boolean unsupported = false;
	private boolean valueAsSummary;
	private CharSequence sValue;
	private Resources res = getContext().getResources();
	private int primary = res.getColor(R.color.preference_primary_text, getContext().getTheme());
	private int secondary = res.getColor(R.color.preference_secondary_text, getContext().getTheme());

	public ListPreferenceEx(Context context, AttributeSet attrs) {
		super(context, attrs);
		final TypedArray xmlAttrs = context.obtainStyledAttributes(attrs, R.styleable.ListPreferenceEx);
		dynamic = xmlAttrs.getBoolean(R.styleable.ListPreferenceEx_dynamic, false);
		valueAsSummary = xmlAttrs.getBoolean(R.styleable.ListPreferenceEx_valueAsSummary, false);
		xmlAttrs.recycle();
	}

	@Override
	public void setValue(String value) {
		super.setValue(value);
		sValue = getEntries()[findIndexOfValue(value)];
	}

	public void setUnsupported(boolean value) {
		unsupported = value;
		setEnabled(!value);
	}

	@Override
	@SuppressLint("SetTextI18n")
	public View getView(View view, ViewGroup parent) {
		View finalView = super.getView(view, parent);
		TextView title = finalView.findViewById(android.R.id.title);
		TextView summary = finalView.findViewById(android.R.id.summary);
		TextView valSummary = finalView.findViewById(android.R.id.hint);

		summary.setVisibility(valueAsSummary || getSummary() == null || getSummary().equals("") ? View.GONE : View.VISIBLE);
		valSummary.setVisibility(valueAsSummary ? View.VISIBLE : View.GONE);
		valSummary.setText(valueAsSummary ? sValue : "");
		title.setTextColor(isEnabled() ? primary : secondary);
		title.setText(getTitle() + (unsupported ? " ⨯" : (dynamic ? " ⟲" : "")));

		return finalView;
	}

	@Override
	protected View onCreateView(ViewGroup parent) {
		ViewGroup view = (ViewGroup)super.onCreateView(parent);

		TextView title = view.findViewById(android.R.id.title);
		title.setMaxLines(3);
		title.setTextColor(primary);

		TextView summary = view.findViewById(android.R.id.summary);
		summary.setTextColor(secondary);

		TextView valSummary = new TextView(getContext());
		valSummary.setTextSize(TypedValue.COMPLEX_UNIT_PX, summary.getTextSize());
		valSummary.setTextColor(summary.getCurrentTextColor());
		valSummary.setPadding(summary.getPaddingLeft(), summary.getPaddingTop(), res.getDimensionPixelSize(R.dimen.preference_summary_padding_right), summary.getPaddingBottom());
		valSummary.setId(android.R.id.hint);
		view.addView(valSummary, 2);

		return view;
	}
}
