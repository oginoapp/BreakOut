package jp.ogn.android.game.breakout.physic;

import java.util.Random;

import jp.ogn.android.game.breakout.ENV;
import jp.ogn.android.game.breakout.R;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class PhysicallyDrawable extends LinearLayout{

	public static final float MOVEMENT_MAXVALUE = 16F;//1クロック当たりの最大移動量(設定)
	public static final float MOVEMENT_Y_POWER = 1.1F;//ブロック衝突時のY方向に対する移動の補正(設定)
	public static final int FORM_RECTANGLE = -1;//四角形
	public static final int FORM_ROUND = -2;//円形
	public static final int TYPE_BLOCK = -10;//ブロックタイプ
	public static final int TYPE_BALL = -11;//ボールタイプ
	public static final int TYPE_RACKET = -12;//ラケットタイプ

	//画像
	private ImageView view;

	//ステータス - 座標、サイズ
	public int width;
	public int height;
	public FloatPoint pos;//中心の座標xy(計算用)
	public FloatPoint direction;//移動予定の距離xy(計算用)
	private FloatPoint movement;//実際に移動予定の距離xy
	public float top;
	public float bottom;
	public float left;
	public float right;
	public float slantTop;
	public float slantBottom;
	public float slantLeft;
	public float slantRight;

	//ステータス - 状態変数 - セッターを使って操作できる
	private boolean manual;//自動では動かないかどうかフラグ
	private int type;//このオブジェクトのタイプ
	private int form;//半径と衝突計算用
	private float slantRadius;//斜めの半径

	/**
	 * コンストラクタ
	 * 画像と座標の初期化を行う
	 * 座標の初期値は全て0に設定される
	 */
	public PhysicallyDrawable(int resourceId, int width, int height){
		super(ENV.context);
		//Viewの生成とセット
		this.width = width;
		this.height = height;
		this.view = new ImageView(ENV.context);
		view.setBackgroundResource(resourceId);
		view.setLayoutParams(new LinearLayout.LayoutParams(width, height));
		this.addView(view);

		//座標の初期化
		pos = new FloatPoint();
		direction = new FloatPoint();
		movement = new FloatPoint();
		pos.x = 0;
		pos.y = 0;
		direction.x = 0;
		direction.y = 0;
		movement.x = 0;
		movement.y = 0;
		top = 0;
		bottom = 0;
		left = 0;
		right = 0;
		slantTop = 0;
		slantBottom = 0;
		slantLeft = 0;
		slantRight = 0;

		//状態変数の初期化
		manual = false;
		type = TYPE_BLOCK;
		setForm(FORM_ROUND);
	}

	/* セッター */
	public void setManual(boolean manual){
		this.manual = manual;
	}
	public void setType(int type){
		this.type = type;
	}
	public void setForm(int form){
		this.form = form;
		if(form == FORM_RECTANGLE){
			slantRadius = height / 2;
		}else
		if(form == FORM_ROUND){
			slantRadius = height / 2 / 1.41421F;
		}
	}

	/* ゲッター */
	public boolean manual(){
		return this.manual;
	}
	public int type(){
		return this.type;
	}
	public int form(){
		return this.form;
	}

	/* 慣性移動 */
	public void moveNext(){
		if(manual){return;}
		moveRelative(movement.x, movement.y);
	}

	/* 指定した座標分移動させる */
	public void moveRelative(float x, float y){
		moveAbsolute(x + left, y + top);
	}

	/**
	 * 絶対座標にセット
	 * 全ての移動メソッドはこのメソッドを最終的に呼び出す
	 */
	public void moveAbsolute(final float x, final float y){
		//移動
		ENV.handler.post(new Runnable(){
			@Override
			public void run(){
				setX(x);
				setY(y);
			}
		});

		//posなどを再計算
		pos.x = x + width/2;
		pos.y = y + height/2;
		top = y;
		bottom = y + height;
		left = x;
		right = x + width;
		slantTop = pos.y - slantRadius;
		slantBottom = pos.y + slantRadius;
		slantLeft = pos.x - slantRadius;
		slantRight = pos.x + slantRadius;
	}

	/**
	 * 衝突発生時に渡されたディレクション情報をセット
	 */
	public void setDirection(float directionX, float directionY){
		direction.x = directionX;
		direction.y = directionY;

		//移動値が移動値制限を超過しないように計算
		float maxDirection = Math.max(Math.abs(directionX),Math.abs(directionY));
		if(maxDirection > MOVEMENT_MAXVALUE){
			float per = maxDirection / MOVEMENT_MAXVALUE;
			movement.x = direction.x / per;
			movement.y = direction.y / per;
		}else{
			movement.x = direction.x;
			movement.y = direction.y;
		}
	}

	/**
	 * 消すか落とすかする
	 */
	public synchronized void kill(){
		ENV.handler.post(new Runnable(){
			@Override
			public void run(){
				Random rnd = new Random();
				int prob = (ENV.blockQuantX + ENV.blockQuantY) / 2;
				if(rnd.nextInt(prob) + 1 == prob){
					width = ENV.ballSize;
					height = ENV.ballSize;
					view.setBackgroundResource(R.drawable.ball_icon);
					view.setLayoutParams(new LinearLayout.LayoutParams(ENV.ballSize, ENV.ballSize));
					setManual(false);
					setType(TYPE_BALL);
					setForm(FORM_ROUND);
					setDirection(0, MOVEMENT_MAXVALUE/2);
				}else{
					setX(-1000);
					setY(-1000);
					moveAbsolute(-1000,-1000);
				}
			}
		});
		ENV.killCount++;
	}

}





