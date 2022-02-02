package tds.pallav.notes.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;

import tds.pallav.notes.R;
import tds.pallav.notes.adapter.template.ModelAdapter;
import tds.pallav.notes.model.Note;
import tds.pallav.notes.widget.NoteViewHolder;

public class NoteAdapter extends ModelAdapter<Note, NoteViewHolder> {
	public NoteAdapter(ArrayList<Note> items, ArrayList<Note> selected, ClickListener<Note> listener) {
		super(items, selected, listener);
	}

	@Override
	public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new NoteViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false));
	}
}