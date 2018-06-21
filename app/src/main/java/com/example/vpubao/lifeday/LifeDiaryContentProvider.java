package com.example.vpubao.lifeday;

import java.util.Calendar;
import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
public class LifeDiaryContentProvider extends ContentProvider{
	//����һ���ռǱ��б���
	private static final String DATABASE_NAME = "database";
	private static final int DATABASE_VERSION = 3;
	private static final String DIARY_TABLE_NAME = "diary";
	private static final int DIARIES = 1;
	private static final int DIARY_ID = 2;
	//�������UriMatcher������ƥ��URI�����ͣ��ǵ�һ������������ȫ����������
	private static final UriMatcher sUriMatcher;
	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(Fields.AUTHORITY, "diaries", DIARIES);
		sUriMatcher.addURI(Fields.AUTHORITY, "diaries/#", DIARY_ID);

	}
	/*��������DatabaseHelper��DatabaseHelper�Ǽ̳�SQLiteOpenHelper��
	 * SQLiteOpenHelper��һ�������࣬��3������ onCreate��onUpdate��onOpen
	*/
	private DatabaseHelper mOpenHelper;
	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			Log.i("jinyan", "DATABASE_VERSION=" + DATABASE_VERSION);
		}
		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.i("jinyan", "onCreate(SQLiteDatabase db)");
			String sql = "CREATE TABLE " + DIARY_TABLE_NAME + " ("
					+ Fields.DiaryColumns._ID + " INTEGER PRIMARY KEY,"
					+Fields. DiaryColumns.TITLE + " TEXT," + Fields.DiaryColumns.BODY
					+ " TEXT," + Fields.DiaryColumns.CREATED + " TEXT" + ");";
			//
			sql ="CREATE TABLE " + DIARY_TABLE_NAME + " ("
			+ Fields.DiaryColumns._ID + " INTEGER PRIMARY KEY,"
			+ Fields.DiaryColumns.TITLE + " varchar(255)," + Fields.DiaryColumns.BODY
			+ " TEXT," +Fields. DiaryColumns.CREATED + " TEXT" + ");";
			//
			Log.i("jinyan", "sql="+sql);
			db.execSQL(sql); 		
		}
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.i("jinyan",
					" onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)="
							+ newVersion);
			db.execSQL("DROP TABLE IF EXISTS diary");
			onCreate(db);
			
		}
		
	}
	//�����ģ�getFormateCreatedDate��һ����ȡ��ǰʱ��ĺ���
	public static String getFormateCreatedDate() {
		Calendar calendar = Calendar.getInstance();
		String created = calendar.get(Calendar.YEAR) + "��"
				+ calendar.get(Calendar.MONTH) + "��"
				+ calendar.get(Calendar.DAY_OF_MONTH) + "��"
				+ calendar.get(Calendar.HOUR_OF_DAY) + "ʱ"
				+ calendar.get(Calendar.MINUTE) + "��";
		return created;
	}

	
	@Override
	public boolean onCreate() {
		mOpenHelper = new DatabaseHelper(getContext());
		return true;
	}
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		//SQLiteQueryBuilder��һ������sql��ѯ���ĸ�����
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		//UriMatcher.match(uri)���ݷ���ֵ�����ж���β�ѯ����ʱ
		//���������ȫ�����ݻ���ĳ��id������
		switch (sUriMatcher.match(uri)) {
		//���������DIARIESֻ��ִ��setTables(DIARY_TABLE_NAME)
		case DIARIES:
			qb.setTables(DIARY_TABLE_NAME);
			break;
			//�������ֵ��DIARY_ID������Ҫ����where����
		case DIARY_ID:
			qb.setTables(DIARY_TABLE_NAME);
			qb.appendWhere(Fields.DiaryColumns._ID + "="
					+ uri.getPathSegments().get(1));
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		String orderBy;
		if (TextUtils.isEmpty(sortOrder)) {
			orderBy = Fields.DiaryColumns.DEFAULT_SORT_ORDER;
		} else {
			orderBy = sortOrder;
		}
		//�ؼ��Ĳ�ѯ��䣬query��������Ϊ��
		//���ݿ�ʵ�����ַ������顢where����
		//�Լ��ַ�����������ÿһ�����δ�����������������ʺ�
		//null��ʾsql���groupby
		//�ڶ���null��ʾsql���having���֡����һ������������
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		Cursor c = qb.query(db, projection, selection, selectionArgs
				, null,null, orderBy);
		return c;
	}

	//����һ����Uri�ƶ����ݵ�MIME����
	//���ķ���ֵ�������vnd.android.cursor.item��ͷ����ôUri�ǵ�������
	//�������vnd.android.cursor.dir��ͷ�ģ�˵��Uriָ����ȫ������
	public String getType(Uri uri) {
		switch (sUriMatcher.match(uri)) {
		case DIARIES:
			return Fields.DiaryColumns.CONTENT_TYPE;

		case DIARY_ID:
			return Fields.DiaryColumns.CONTENT_ITEM_TYPE;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		//�ж�uri��������uri����DIARIES���͵ģ���ô���uri����һ���Ƿ���uri
		if (sUriMatcher.match(uri) != DIARIES) {
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		ContentValues values;
		if (initialValues != null) {
			values = new ContentValues(initialValues);
		} else {
			values = new ContentValues();
		}

		if (values.containsKey(Fields.DiaryColumns.CREATED) == false) {
			values.put(Fields.DiaryColumns.CREATED, getFormateCreatedDate());
		}

		if (values.containsKey(Fields.DiaryColumns.TITLE) == false) {
			Resources r = Resources.getSystem();
			values.put(Fields.DiaryColumns.TITLE, r
					.getString(android.R.string.untitled));
		}

		if (values.containsKey(Fields.DiaryColumns.BODY) == false) {
			values.put(Fields.DiaryColumns.BODY, "");
		}
		//�õ�һ��SQLiteDatabase��ʵ��
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		//�������һ����¼�����ݿ⵱��
		long rowId = db.insert
		(DIARY_TABLE_NAME, Fields.DiaryColumns.BODY, values);
		if (rowId > 0) {
			Uri diaryUri = ContentUris.withAppendedId(
					Fields.DiaryColumns.CONTENT_URI, rowId);
			//ע�⣺insert�������ص���һ��uri��������һ����¼id
			return diaryUri;
		}

		throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		//getWritableDatabase���������õ�һ��string��list
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		//�õ�rowId��ֵ�����get��0����ֵ��Ϊdiaries
		String rowId = uri.getPathSegments().get(1);
		//��׼��SQLiteɾ����������һ�����������ݱ������
		//�ڶ����൱��SQL����е�where���֡�
		return db.delete(DIARY_TABLE_NAME, Fields.DiaryColumns._ID +
				"=" + rowId, null);
	}

	@Override
	public int update(Uri uri, ContentValues values, String where,
			String[] whereArgs) {
		//getWritableDatabase���������õ�SQLiteDatabase��ʵ��
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		//�õ�rowId��ֵ
		String rowId = uri.getPathSegments().get(1);
		//����ڵ���update���ִ�и��²���
		return db.update(DIARY_TABLE_NAME, values, Fields.DiaryColumns._ID +
				"="+ rowId, null);
	}

}
