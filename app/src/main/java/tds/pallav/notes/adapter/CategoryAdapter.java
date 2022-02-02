package tds.pallav.notes.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;

import tds.pallav.notes.R;
import tds.pallav.notes.adapter.template.ModelAdapter;
import tds.pallav.notes.model.Category;
import tds.pallav.notes.widget.CategoryViewHolder;

public class CategoryAdapter extends ModelAdapter<Category, CategoryViewHolder> {
	public CategoryAdapter(ArrayList<Category> items, ArrayList<Category> selected, ClickListener<Category> listener) {
		super(items, selected, listener);
	}

	@Override
	public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new CategoryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false));
	}
}
