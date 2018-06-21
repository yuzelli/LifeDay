package com.example.vpubao.lifeday;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.example.vpubao.green.DaoMaster;
import com.example.vpubao.green.DaoSession;
import com.example.vpubao.green.Note;
import com.example.vpubao.green.NoteDao;

//�̳�����listView
public class LifeDiary extends ListActivity {
	// �������
	private SharedPreferences textpassword = null;
	private String password = null;
	private boolean isSet = false;
	private EditText checkpass = null;
	// ����Ƿ��һ�ν���Ӧ��
	boolean isFirstIn = false;
	// ����һ���¼�¼
	public static final int MENU_ITEM_INSERT = Menu.FIRST;
	// �༭����
	public static final int MENU_ITEM_EDIT = Menu.FIRST + 1;
	// private static final String[] PROJECTION =
	// new String[] { DiaryColumns._ID,
	// DiaryColumns.TITLE, DiaryColumns.CREATED };
	//
	// GreenDAOʹ�õı���
	private SQLiteDatabase db;

	private DaoMaster daoMaster;
	private DaoSession daoSession;
	private NoteDao noteDao;

	private Cursor cursor;
	private Button btn_add;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.diary_list);
		btn_add = (Button) this.findViewById(R.id.btn_add);
		btn_add.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent0 = new Intent(LifeDiary.this, DiaryEditor.class);
				intent0.setAction(DiaryEditor.INSERT_DIARY_ACTION);
				intent0.setData(getIntent().getData());
				startActivity(intent0);
				LifeDiary.this.finish();
				cursor.requery();
			}
		});
		// ��ʼ��GreenDAO
		InitDAO();

		// ��ʼ��List
		InitList();
		this.getListView().setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                    int arg2, long arg3) {
                // TODO Auto-generated method stub
            	Note note = noteDao.loadByRowId(arg3);
            	Intent share_intent = new Intent(); 
            	share_intent.setAction(Intent.ACTION_SEND);
            	//设置分享行为 
            	share_intent.setType("text/plain");
            	//设置分享内容的类型 
            	share_intent.putExtra(Intent.EXTRA_SUBJECT, note.getTitle());
            	//添加分享内容标题 
            	share_intent.putExtra(Intent.EXTRA_TEXT, note.getBody());
            	//添加分享内容 //创建分享的Dialog
            	 share_intent = Intent.createChooser(share_intent, "分享"); 
            	 LifeDiary.this.startActivity(share_intent);


                return true;
            }
       });

	}
	
	

	/*
	 * ��ʼ��DAO
	 */
	private void InitDAO() {
		DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "notes-db",
				null);
		db = helper.getWritableDatabase();
		daoMaster = new DaoMaster(db);
		daoSession = daoMaster.newSession();
		noteDao = daoSession.getNoteDao();

	}

	/*
	 * ��ʼ��List
	 */
	private void InitList() {
		String textColumn = NoteDao.Properties.Title.columnName;
		String dateColunm = NoteDao.Properties.Date.columnName;
		String orderBy = dateColunm + " COLLATE LOCALIZED DESC";
		cursor = db.query(noteDao.getTablename(), noteDao.getAllColumns(),
				null, null, null, null, orderBy);
		String[] from = { textColumn, dateColunm };
		int[] to = { R.id.text1, R.id.created };

		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
				R.layout.diary_row, cursor, from, to);
		setListAdapter(adapter);
	}

	// ���ѡ��˵�
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(Menu.NONE, MENU_ITEM_INSERT, 0, R.string.menu_insert);
		return true;
	}

	// ���ѡ��˵���ѡ���¼�
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// ����һ������
		case MENU_ITEM_INSERT:
			Intent intent0 = new Intent(this, DiaryEditor.class);
			intent0.setAction(DiaryEditor.INSERT_DIARY_ACTION);
			intent0.setData(getIntent().getData());
			startActivity(intent0);
			LifeDiary.this.finish();
			cursor.requery();
			return true;

		}
		return super.onOptionsItemSelected(item);
	}

	protected void onListItemClick(ListView l, View v, int position, long id) {
		// Uri uri = ContentUris.withAppendedId(getIntent().getData(), id);
		String mid = Long.toString(id);
		Log.d("id", mid);
		// Intent intent = new Intent();
		// startActivity(new Intent(DiaryEditor.EDIT_DIARY_ACTION, uri));
		Intent intent = new Intent(this, DiaryEditor.class);
		intent.setAction(DiaryEditor.EDIT_DIARY_ACTION);
		Bundle bundle = new Bundle();
		bundle.putLong("id", id);
		intent.putExtras(bundle);
		Log.d("id", mid);
		startActivity(intent);
		LifeDiary.this.finish();
		cursor.requery();
	}
	


	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		cursor.requery();
		super.onActivityResult(requestCode, resultCode, intent);
		// renderListView();
	}
	// @SuppressWarnings("deprecation")
	// private void renderListView() {
	// Cursor cursor = managedQuery(getIntent().getData(), PROJECTION,
	// null,null, DiaryColumns.DEFAULT_SORT_ORDER);
	//
	// SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
	// R.layout.diary_row, cursor, new String[] { DiaryColumns.TITLE,
	// DiaryColumns.CREATED }, new int[] { R.id.text1,R.id.created });
	// setListAdapter(adapter);
	// }
}
