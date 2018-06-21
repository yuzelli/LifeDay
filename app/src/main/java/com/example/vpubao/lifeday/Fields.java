package com.example.vpubao.lifeday;

import android.net.Uri;
import android.provider.BaseColumns;

public final class Fields {
	//����� AUTHORITY Ҫ����Ψһ�����Һ�Manifest����provider��ǩ��AUTHORITY����һ��
	//������URI����Ȩ������
    public static final String AUTHORITY = "andy.ham.diarycontentprovider";
//���췽��
    private Fields() {}
    public static final class DiaryColumns implements BaseColumns {
        // �ڲ���̬�࣬������������˼һ���������б��ֶε�����
        private DiaryColumns() {}
        //��ʽ������contentURI
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/diaries");

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.google.diary";

        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.google.diary";

        public static final String DEFAULT_SORT_ORDER = "created DESC";

        public static final String TITLE = "title";

        public static final String BODY = "body";

        public static final String CREATED = "created";   
    }
}

