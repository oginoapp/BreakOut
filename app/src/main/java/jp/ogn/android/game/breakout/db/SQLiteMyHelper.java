package jp.ogn.android.game.breakout.db;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteMyHelper extends SQLiteOpenHelper {

	private static SQLiteMyHelper instance=null;
	private static final String DB_NAME="score_data.db";
	private static final int DB_VERSION=3;
	public static final String DB_TABLE="t_score";
	public static final String[] COLUMNS = new String[]{"id","score","date","end_type"};

	public SQLiteMyHelper(Context context) {
		super(context,DB_NAME,null,DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db){
		String sql_create = "create table if not exists [DB_TABLE]([COLUMNS0] integer primary key autoincrement,[COLUMNS1] integer,[COLUMNS2] text,[COLUMNS3] text);";
		String sql_insert = "insert into [DB_TABLE]([COLUMNS1],[COLUMNS2],[COLUMNS3]) values(123,'TEST2','TEST3');";

		sql_create = sql_create.replace("[DB_TABLE]", DB_TABLE);
		sql_create = sql_create.replace("[COLUMNS0]", COLUMNS[0]);
		sql_create = sql_create.replace("[COLUMNS1]", COLUMNS[1]);
		sql_create = sql_create.replace("[COLUMNS2]", COLUMNS[2]);
		sql_create = sql_create.replace("[COLUMNS3]", COLUMNS[3]);
		sql_insert = sql_insert.replace("[DB_TABLE]", DB_TABLE);
		sql_insert = sql_insert.replace("[COLUMNS0]", COLUMNS[0]);
		sql_insert = sql_insert.replace("[COLUMNS1]", COLUMNS[1]);
		sql_insert = sql_insert.replace("[COLUMNS2]", COLUMNS[2]);
		sql_insert = sql_insert.replace("[COLUMNS3]", COLUMNS[3]);

		db.execSQL(sql_create);
		//db.execSQL(sql_insert);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exists "+DB_TABLE);
		onCreate(db);
	}
	public static SQLiteMyHelper getInstance(Context context){
		if(instance==null){
			instance=new SQLiteMyHelper(context);
		}
		return instance;
	}
}
