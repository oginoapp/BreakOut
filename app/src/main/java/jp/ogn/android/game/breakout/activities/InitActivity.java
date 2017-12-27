package jp.ogn.android.game.breakout.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import jp.ogn.android.game.breakout.ENV;
import jp.ogn.android.game.breakout.R;
import jp.ogn.android.game.breakout.db.SQLiteMyHelper;
import jp.ogn.android.game.breakout.lib_android.LogView;
import jp.ogn.android.game.breakout.lib_android.Toaster;

public class InitActivity extends Activity {
	private LinearLayout screenWrapper = null;
	private LinearLayout setting1Wrapper = null;
	private LinearLayout scoreBoardWrapper = null;
	private TextView setting1Title = null;
	private TextView scoreBoardTitle = null;
	private Spinner blockQuantSelector = null;
	private Button startButton = null;

	private LogView scoreBoard = null;

	/* 最初に呼び出される */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_init);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//画面縦固定

		//環境変数初期化
		ENV.context = getApplicationContext();
		ENV.handler = new Handler();
		ENV.toaster = new Toaster(ENV.context);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		ENV.screenWidth = dm.widthPixels;
		ENV.screenHeight = dm.heightPixels;
		ENV.killCount = 0;
		ENV.gameflg = 0;

		//レイアウトを読み込み
		screenWrapper = (LinearLayout)findViewById(R.id.screen_wrapper);
		setting1Wrapper = (LinearLayout)findViewById(R.id.wrapper_setting1);
		scoreBoardWrapper = (LinearLayout)findViewById(R.id.wrapper_scoreboard);
		setting1Title = (TextView)findViewById(R.id.title_setting1);
		scoreBoardTitle = (TextView)findViewById(R.id.title_scoreboard);
		blockQuantSelector = (Spinner)findViewById(R.id.spinner_quant);
		startButton = (Button)findViewById(R.id.button_start);

		//レイアウト初期化
		scoreBoard = new LogView(50, true);

		//レイアウト構造設定
		scoreBoardWrapper.addView(scoreBoard);

		//デザイン設定 - サイズ
		int tableWidth = (int)(ENV.screenWidth * 0.9);
		int tableHeight = (int)(ENV.screenHeight * 0.85);
		int tableMarginX = (int)((ENV.screenWidth - tableWidth) / 1.9);
		int tableMarginY = tableMarginX;
		int btnHeight = ((int)(ENV.screenHeight / 10));
		int btnMarginY = ((int)(ENV.screenHeight / 20));
		float textSizeSmall = 15F;
		float textSizeNormal = 20F;
		float textSizeBig = 25F;

		MarginLayoutParams tableLP = (MarginLayoutParams)new LinearLayout.LayoutParams(tableWidth, tableHeight);
		MarginLayoutParams settingLP = (MarginLayoutParams)new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		MarginLayoutParams btnLP = (MarginLayoutParams)new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, btnHeight);
		MarginLayoutParams scoreLP = (MarginLayoutParams)new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		tableLP.setMargins(tableMarginX, tableMarginY, tableMarginX, tableMarginY);
		settingLP.setMargins(0, 0, 0, 0);
		btnLP.setMargins(10, btnMarginY, 10, btnMarginY);
		scoreLP.setMargins(0, 0, 0, 0);
		screenWrapper.setLayoutParams(tableLP);
		setting1Wrapper.setLayoutParams(settingLP);
		startButton.setLayoutParams(btnLP);
		scoreBoardWrapper.setLayoutParams(scoreLP);

		setting1Title.setTextSize(textSizeNormal);
		scoreBoardTitle.setTextSize(textSizeSmall);
		startButton.setTextSize(textSizeBig);
		scoreBoardTitle.setGravity(Gravity.CENTER);

		//デザイン設定 - 色
		scoreBoard.setDesign(LogView.COLOR_DEFAULT, Color.BLACK, textSizeNormal);

		//デザイン設定 - テキスト
		setting1Title.setText("ブロック数(横×縦)：");
		scoreBoardTitle.setText("スコアボード");
		startButton.setText("スタート");

		//コンテンツ設定 - ブロック数選択 - 縦
		ArrayAdapter<String> blockQuantAdapter = new ArrayAdapter<String>(this, R.layout.spinner);
		blockQuantAdapter.setDropDownViewResource(R.layout.spinner_dropdown);
		//blockQuantAdapter.add("2 × 2");
		//blockQuantAdapter.add("4 × 4");
		blockQuantAdapter.add("8 × 8");
		blockQuantAdapter.add("12 × 12");
		blockQuantAdapter.add("16 × 16");
		blockQuantAdapter.add("32 × 32（非推奨）");
		//blockQuantAdapter.add("64 × 64"（非推奨）);
		//blockQuantAdapter.add("128 × 128（非推奨）");
		blockQuantSelector.setAdapter(blockQuantAdapter);
		blockQuantSelector.setSelection(1);

		//コンテンツ設定 - 開始ボタン
		startButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View paramView) {
				startButtonClick();
			}
		});

		//コンテンツ設定 - スコアボード
		try{
			//SELECT
			String[] columns = SQLiteMyHelper.COLUMNS;
			SQLiteDatabase db=SQLiteMyHelper.getInstance(this).getReadableDatabase();
			Cursor cursor=db.query(SQLiteMyHelper.DB_TABLE,columns,null,null,null,null,columns[1],null);
			String[] record=new String[cursor.getCount()];
			cursor.moveToFirst();
			for(int i=0; i<cursor.getCount(); i++){
				record[i] = "score:" + cursor.getInt(1) + ",\t" + cursor.getString(2) + ",\t" + cursor.getString(3);
				scoreBoard.updateLog(record[i]);
				cursor.moveToNext();
			}
		}catch(Exception ex){}

		//テスト

	}

	/**
	 * 動作時期：スタートボタンクリック時に呼び出される
	 * 機能：設定した値を直接使える値に変換した後、セットして次のアクティビティにインテント
	 */
	private void startButtonClick(){
		String blockQuant = blockQuantSelector.getSelectedItem().toString().split(" ")[0];

		Intent intent = new Intent(InitActivity.this, MainActivity.class);
		intent.putExtra("blockQuant", blockQuant);
		startActivity(intent);
	}

}
