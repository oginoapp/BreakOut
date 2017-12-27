package jp.ogn.android.game.breakout.activities;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

import jp.ogn.android.game.breakout.ENV;
import jp.ogn.android.game.breakout.R;
import jp.ogn.android.game.breakout.db.SQLiteMyHelper;
import jp.ogn.android.game.breakout.lib_android.Counter;
import jp.ogn.android.game.breakout.lib_android.LogView;
import jp.ogn.android.game.breakout.physic.PhysicallyDrawable;
import jp.ogn.android.game.breakout.physic.PhysicallyField;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;
public class MainActivity extends Activity {
	//ログ
	private boolean show_log = false;
	public static LogView log = null;

	//ゲーム進行用クロッカー
	private Counter clock;
	private boolean started;

	//初期設定用パラメータ
	private int blockQuantX;
	private int blockQuantY;
	private int barWidth;
	private int barHeight;
	private int barDefaultPosX;
	private int barDefaultPosY;
	private int ballDefaultPosX;
	private int ballDefaultPosY;

	//レイアウト
	private FrameLayout breakoutScreen;
	private PhysicallyField field;
	private PhysicallyDrawable bar;
	private PhysicallyDrawable ball;

	//制御変数
	private boolean onPause = false;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(started && keyCode==KeyEvent.KEYCODE_BACK){
			clock.pauseClock();
			new AlertDialog.Builder(MainActivity.this)
			.setMessage("　最初の画面に戻りますか？")
			.setCancelable(false)
			.setNegativeButton("はい",new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog,int which){
					gameEnd(4);
				}
			})
			.setPositiveButton("いいえ",new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog,int which){
					clock.resumeClock();
				}
			})
			.show();
			return true;
		}
		return false;
	}

	/* 画面タップ時に呼び出される */
	@Override
	public boolean onTouchEvent(MotionEvent event){
		if(started){
			ENV.gameflg = 1;
			bar.moveAbsolute(event.getX() - barWidth / 2, barDefaultPosY);
		}
		return false;
	}

	/* ポーズ時の処理 */
	@Override
	protected void onPause(){
		super.onPause();
		onPause = true;
	}
	@Override
	protected void onRestart(){
		super.onRestart();
		onPause = false;
	};

	/* 最初に呼び出される */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//画面縦固定

		//設定値を受け取って代入、設定値初期化
		Intent intent = getIntent();
		blockQuantX = Integer.parseInt(intent.getStringExtra("blockQuant"));
		blockQuantY = Integer.parseInt(intent.getStringExtra("blockQuant"));
		ENV.blockQuantX = blockQuantX;
		ENV.blockQuantY = blockQuantY;
		started = false;

		//レイアウトを読み込み
		breakoutScreen = (FrameLayout)findViewById(R.id.screen_breakout);

		//レイアウト構造設定
		if(show_log){//ログを表示する場合
			log = new LogView(50, true);
			breakoutScreen.addView(log);
		}

		//デザイン設定 - サイズ、位置
		ENV.bottomLineY = (int)(ENV.screenHeight * 0.9);
		barWidth = (int)(ENV.screenWidth / 1.618 / 1.618 / 1.618);
		barHeight = (int)(barWidth / 1.618 / 1.618 / 1.618);
		barDefaultPosX = (int)(ENV.screenWidth / 2 - barWidth / 2);
		barDefaultPosY = (int)(ENV.bottomLineY - barHeight * 1.618 * 1.618);
		ENV.ballSize = barHeight;
		ballDefaultPosX = (int)(ENV.screenWidth / 2 - ENV.ballSize / 2);
		ballDefaultPosY = barDefaultPosY - ENV.ballSize;

		LayoutParams breakoutLP = (LayoutParams)new LinearLayout.LayoutParams((int)ENV.screenWidth, (int)ENV.screenHeight);
		breakoutScreen.setLayoutParams(breakoutLP);

		//デザイン設定 - 色
		//breakoutScreen.setBackgroundColor(randomColor(64,192));

		//デザイン設定 - テキスト


		//コンテンツ初期化
		field = new PhysicallyField(breakoutScreen, blockQuantX, blockQuantY, 0.55F);
		bar = new PhysicallyDrawable(R.drawable.bar_long, barWidth, barHeight);
		ball = new PhysicallyDrawable(R.drawable.ball_icon, ENV.ballSize, ENV.ballSize);
		field.addDrawable(bar);
		field.addDrawable(ball);
		bar.setType(PhysicallyDrawable.TYPE_RACKET);
		bar.moveAbsolute(barDefaultPosX, barDefaultPosY);
		bar.setManual(true);//非推奨
		bar.setForm(PhysicallyDrawable.FORM_RECTANGLE);
		ball.moveAbsolute(ballDefaultPosX, ballDefaultPosY);
		ball.setDirection(0, -10);
		ball.setType(PhysicallyDrawable.TYPE_BALL);

		if(show_log){//ログを表示する場合
			log.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					return onTouchEvent(event);
				}
			});
		}

		clock = new Counter(Counter.INFINITY,50) {
			@Override
			protected void onStart() {
			}

			@Override
			protected void onCount(int count) {
				if(ENV.gameflg == 0){
					return;
				}else
				if(ENV.gameflg == 1){
					sequence();//進行
				}else
				if(ENV.gameflg == 2){
					gameEnd(2);//ゲームクリア
				}else
				if(ENV.gameflg == 3){
					gameEnd(3);//ゲームオーバー
				}else
				if(ENV.gameflg == 4){
					gameEnd(4);//終了
				}
			}

			@Override
			protected void onStop(boolean err_flg) {
			}
		};

		//ゲーム開始
		clock.start();
		toastPut("　　　　START　　　　\n画面をタップしてください");
		started = true;

		//テスト
	}

	/* ゲームの進行 */
	private synchronized void sequence(){
		field.physicalSimulation();
		if(ENV.killCount >= ENV.blockQuantX * ENV.blockQuantY){
			ENV.gameflg = 2;
		}
	}

	/* ゲームオーバー */
	private void gameEnd(int pattern){
		try{
			started = false;
			clock.stopClock();

			while(onPause){
				Thread.sleep(100);
			}

			//INSERT
			SQLiteDatabase db=SQLiteMyHelper.getInstance(ENV.context).getReadableDatabase();
			ContentValues values=new ContentValues();
			Calendar c = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss", Locale.getDefault());
			values.put(SQLiteMyHelper.COLUMNS[1],ENV.killCount);
			values.put(SQLiteMyHelper.COLUMNS[2],sdf.format(c.getTime()));
			if(pattern == 2){
				toastPut("GAME CLEAR", Toast.LENGTH_LONG);
				values.put(SQLiteMyHelper.COLUMNS[3],"GAME CLEAR!!");
			}
			if(pattern == 3){
				toastPut("GAME OVER", Toast.LENGTH_LONG);
				values.put(SQLiteMyHelper.COLUMNS[3],"GAME OVER");
			}
			if(pattern == 4){
				toastPut("EXIT GAME", Toast.LENGTH_LONG);
				values.put(SQLiteMyHelper.COLUMNS[3],"EXIT GAME");
			}
			db.insert(SQLiteMyHelper.DB_TABLE,null,values);

			Thread.sleep(2500);
		}catch(Exception e){
			ENV.toaster.put(e.getStackTrace());
		}finally{
			Intent intent=new Intent(MainActivity.this,InitActivity.class);
			startActivity(intent);
			finish();
		}
	}

	/* ランダムカラーのトースト出力 */
	private void toastPut(Object message){
		toastPut(message, Toast.LENGTH_SHORT);
	}
	private void toastPut(Object message, int length){
		float txtSize = 40F;
		float startY = ENV.screenHeight / 4F;
		View v = ENV.toaster.makeView(String.valueOf(message), txtSize, randomColor(64,192));
		ENV.toaster.putView(v, length, startY);
	}

	/* ランダムな色を取得 */
	private int randomColor(int min, int max){
		Random random = new Random();
		int r = random.nextInt(max-min) + min;
		int g = random.nextInt(max-min) + min;
		int b = random.nextInt(max-min) + min;
		return Color.rgb(r,g,b);
	}

}
