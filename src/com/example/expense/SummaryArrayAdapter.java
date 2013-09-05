package com.example.expense;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.common.helpers.FormatHelper;
import com.example.expense.models.SummaryListItem;

public class SummaryArrayAdapter extends ArrayAdapter<SummaryListItem> {

	private final static int LAYOUT_RESOURCE = R.layout.list_item_summary;
	
	private final Activity context;
	private final List<SummaryListItem> list;

	static class ViewHolder {
		public TextView label;
		public TextView amount;
	}

	public SummaryArrayAdapter(Activity context, List<SummaryListItem> list) {
		super(context, LAYOUT_RESOURCE, list);
		this.context = context;
		this.list = list;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		
		if (rowView == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			rowView = inflater.inflate(LAYOUT_RESOURCE, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.label = (TextView) rowView.findViewById(R.id.label);
			viewHolder.amount = (TextView) rowView.findViewById(R.id.amount);
			rowView.setTag(viewHolder);
		}

		ViewHolder holder = (ViewHolder) rowView.getTag();
		SummaryListItem listItem = list.get(position);
		holder.label.setText(listItem.getLabel());
		holder.amount.setText(FormatHelper.formatMoney(listItem.getAmount()));

		return rowView;
	}
	
}
