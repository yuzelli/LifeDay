package com.example.vpubao.lifeday;

import java.text.DateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.vpubao.green.DaoMaster;
import com.example.vpubao.green.DaoSession;
import com.example.vpubao.green.Note;
import com.example.vpubao.green.NoteDao;


public class DiaryEditor extends Activity {

	private static final String TAG = "Diary";
	public static final String EDIT_DIARY_ACTION = "andy.ham.DiaryEditor.EDIT_DIARY";
	public static final String INSERT_DIARY_ACTION = "andy.ham.DiaryEditor.action.INSERT_DIARY";
	public static final int MENU_ITEM_DELETE = Menu.FIRST + 1;

	/**
	 * ��ѯcursorʱ�򣬸���Ȥ����Щ������
	 */
	// private static final String[] PROJECTION
	// = new String[] { DiaryColumns._ID, // 0
	// DiaryColumns.TITLE, DiaryColumns.BODY, // 1
	// };

	// GreenDAOʹ�õı���
	private SQLiteDatabase db;

	private DaoMaster daoMaster;
	private DaoSession daoSession;
	private NoteDao noteDao;
	private Note note;
	private Cursor cursor;

	private static final int STATE_EDIT = 0;
	private static final int STATE_INSERT = 1;
	private String dUriString;
	private int mState;
	private Uri mUri;
	private Cursor mCursor;
	private EditText mTitleText;
	private EditText mBodyText;
	private Button confirmButton;
	private long mid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(android.R.style.Theme_Black);
		final Intent intent = getIntent();
		final String action = intent.getAction();
		setContentView(R.layout.diary_edit);

		mTitleText = (EditText) findViewById(R.id.title);
		mBodyText = (EditText) findViewById(R.id.body);
		confirmButton = (Button) findViewById(R.id.confirm);

		initDAO();
		Log.d("action", action);

		if (EDIT_DIARY_ACTION.equals(action)) {// �༭�ռ�
		// mState = STATE_EDIT;
		// mUri = intent.getData();
		// dUriString = mUri.toString();
		// mCursor = managedQuery(mUri, PROJECTION, null, null, null);
		// mCursor.moveToFirst();
		// String title = mCursor.getString(1);
		// mTitleText.setTextKeepState(title);
		// String body = mCursor.getString(2);
		// mBodyText.setTextKeepState(body);
			Bundle bundle = new Bundle();
			bundle = this.getIntent().getExtras();
			mid = bundle.getLong("id");
			// note = noteDao.load(mid);
			note = noteDao.loadByRowId(mid);

			mTitleText.setTextKeepState(note.getTitle());
			mBodyText.setTextKeepState(note.getBody());

			// setResult(RESULT_OK, (new Intent()).setAction(mUri.toString()));
			setTitle("�༭�ռ�");
		} else if (INSERT_DIARY_ACTION.equals(action)) {// �½��ռ�
			mState = STATE_INSERT;
			setTitle("�½��ռ�");
		} else {
			Log.e(TAG, "no such action error");
			finish();
			return;
		}
		confirmButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if (mState == STATE_INSERT) {
					insertDiary();
				} else {
					updateDiary();
				}
				Intent intent = new Intent(DiaryEditor.this, LifeDiary.class);
				startActivity(intent);
				DiaryEditor.this.finish();
			}
		});
	}

	private void initDAO() {
		DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "notes-db",
				null);
		db = helper.getWritableDatabase();
		daoMaster = new DaoMaster(db);
		daoSession = daoMaster.newSession();
		noteDao = daoSession.getNoteDao();
	}

	private void insertDiary() {
		String title = mTitleText.getText().toString();
		String body = mBodyText.getText().toString();
		final DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,
				DateFormat.MEDIUM);
		String date = df.format(new Date());

		Note note = new Note(null, title, body, date);
		noteDao.insert(note);

		Log.d("DaoExample", "Inserted new note, ID: " + note.getId());
		// ContentValues values = new ContentValues();
		// values.put(Fields.DiaryColumns.CREATED, LifeDiaryContentProvider
		// .getFormateCreatedDate());
		// values.put(Fields.DiaryColumns.TITLE, title);
		// values.put(Fields.DiaryColumns.BODY, body);
		// getContentResolver().
		// insert(Fields.DiaryColumns.CONTENT_URI, values);

	}

	private void updateDiary() {
		String title = mTitleText.getText().toString();
		String body = mBodyText.getText().toString();
		final DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,
				DateFormat.MEDIUM);
		String date = df.format(new Date());

		Note note = new Note(null, title, body, date);
		noteDao.insertOrReplace(note);
		// ContentValues values = new ContentValues();
		// values.put(Fields.DiaryColumns.CREATED, LifeDiaryContentProvider
		// .getFormateCreatedDate());
		// values.put(Fields.DiaryColumns.TITLE, title);
		// values.put(Fields.DiaryColumns.BODY, body);
		// getContentResolver().
		// update(mUri, values,null, null);
	}

	// ��Ӳ˵���ѡ��
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(Menu.NONE, MENU_ITEM_DELETE, 0, R.string.menu_delete);
		return true;
	}

	// ���ѡ��˵���ѡ���¼�
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// ɾ����ǰ����
		case MENU_ITEM_DELETE:
			// getContentResolver().delete(dUri, null, null);
			noteDao.deleteByKey(mid);

			Toast.makeText(DiaryEditor.this, R.string.diary_delete_success,
					Toast.LENGTH_LONG).show();
			Intent intent = new Intent(DiaryEditor.this, LifeDiary.class);
			startActivity(intent);
			DiaryEditor.this.finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
