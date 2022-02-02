package tds.pallav.notes.fragment;

import android.graphics.Color;
import android.view.View;
import android.widget.EditText;

import androidx.core.content.ContextCompat;

import jp.wasabeef.richeditor.RichEditor;
import tds.pallav.notes.R;
import tds.pallav.notes.fragment.template.NoteFragment;
import tds.pallav.notes.model.DatabaseModel;

public class SimpleNoteFragment extends NoteFragment {
	private RichEditor body;

	EditText editText;

	public SimpleNoteFragment() {
	}

	@Override
	public int getLayout() {
		return R.layout.fragment_simple_note;

	}

	@Override
	public void saveNote(final SaveListener listener) {

		super.saveNote(listener);
		note.body = body.getHtml();

		new Thread() {
			@Override
			public void run() {
				long id = note.save();
				if (note.id == DatabaseModel.NEW_MODEL_ID) {
					note.id = id;
				}
				listener.onSave();
				interrupt();
			}
		}.start();
	}



	@Override
	public void init(View view) {
		body = (RichEditor) view.findViewById(R.id.editor);
		body.setPlaceholder("Click here to add Note Description");
		body.setEditorBackgroundColor(ContextCompat.getColor(getContext(), R.color.bg));

		view.findViewById(R.id.action_bold).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				body.setBold();
			}
		});

		view.findViewById(R.id.action_italic).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				body.setItalic();
			}
		});

		view.findViewById(R.id.action_underline).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				body.setUnderline();
			}
		});

		view.findViewById(R.id.action_undo).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				body.undo();
			}
		});

		view.findViewById(R.id.action_redo).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				body.redo();
			}
		});
		view.findViewById(R.id.action_subscript).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				body.setSubscript();
			}
		});

		view.findViewById(R.id.action_superscript).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				body.setSuperscript();
			}
		});

		view.findViewById(R.id.action_strikethrough).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				body.setStrikeThrough();
			}
		});

		view.findViewById(R.id.action_underline).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				body.setUnderline();
			}
		});

		view.findViewById(R.id.action_heading1).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				body.setHeading(1);
			}
		});

		view.findViewById(R.id.action_heading2).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				body.setHeading(2);
			}
		});

		view.findViewById(R.id.action_heading3).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				body.setHeading(3);
			}
		});

		view.findViewById(R.id.action_heading4).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				body.setHeading(4);
			}
		});

		view.findViewById(R.id.action_heading5).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				body.setHeading(5);
			}
		});

		view.findViewById(R.id.action_heading6).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				body.setHeading(6);
			}
		});

		view.findViewById(R.id.action_txt_color).setOnClickListener(new View.OnClickListener() {
			private boolean isChanged;

			@Override
			public void onClick(View v) {
				body.setTextColor(isChanged ? Color.BLACK : Color.RED);
				isChanged = !isChanged;
			}
		});

		view.findViewById(R.id.action_bg_color).setOnClickListener(new View.OnClickListener() {
			private boolean isChanged;

			@Override
			public void onClick(View v) {
				body.setTextBackgroundColor(isChanged ? Color.TRANSPARENT : Color.YELLOW);
				isChanged = !isChanged;
			}
		});

		view.findViewById(R.id.action_indent).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				body.setIndent();
			}
		});

		view.findViewById(R.id.action_outdent).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				body.setOutdent();
			}
		});

		view.findViewById(R.id.action_align_left).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				body.setAlignLeft();
			}
		});

		view.findViewById(R.id.action_align_center).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				body.setAlignCenter();
			}
		});

		view.findViewById(R.id.action_align_right).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				body.setAlignRight();
			}
		});

		view.findViewById(R.id.action_blockquote).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				body.setBlockquote();
			}
		});

		view.findViewById(R.id.action_insert_bullets).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				body.setBullets();
			}
		});

		view.findViewById(R.id.action_insert_numbers).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				body.setNumbers();
			}
		});




		body.setHtml(note.body);
	}
}
