package jp.ogn.android.game.breakout.lib_android;

import jp.ogn.android.game.breakout.ENV;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * 更新日時：20160515
 * 更新内容：背景色、テキスト色、テキストサイズを変更できるメソッド追加。
 *           定数にfinal修飾子を追加。スクロールできないバグを修正。
 */
public class LogView extends ScrollView{
	public static final int COLOR_DEFAULT = 0;
	public static final float SIZE_DEFAULT = -1F;

	private final LinearLayout jp;
	private final TextView[] labels;
	private final int logSize;
	private final boolean topInsert;

	public LogView(){
		this(50, false);
	}

	/* コンストラクタ */
	public LogView(int logSize, boolean topInsert){
		super(ENV.context);

		//構造の設定
		jp = new LinearLayout(ENV.context);
		jp.setOrientation(LinearLayout.VERTICAL);
		addView(jp);

		//レイアウトの設定
		MarginLayoutParams myLP = (MarginLayoutParams)new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		myLP.setMargins(0, 0, 0, 0);
		setLayoutParams(myLP);

		//ラベルの生成
		labels = new TextView[logSize];
		for(int i=0; i<logSize; i++){
			labels[i] = new TextView(ENV.context);
			jp.addView(labels[i]);
		}

		this.logSize = logSize;
		this.topInsert = topInsert;
	}

	/* ログのデザイン変更 */
	final public void setDesign(int backgroundColor, int textColor, float textSize){
		for(int i=0; i<logSize; i++){
			if(backgroundColor != COLOR_DEFAULT){
				labels[i].setBackgroundColor(backgroundColor);
			}
			if(textColor != COLOR_DEFAULT){
				labels[i].setTextColor(textColor);
			}
			if(textSize != SIZE_DEFAULT){
				labels[i].setTextSize(textSize);
			}
		}
	}

	/* ログ追加メソッド */
	final public void updateLog(final Object log){
		ENV.handler.post(new Runnable(){
			@Override
			public void run(){
				if(topInsert){
					//最上行にログ追加
					for(int i=labels.length-2; i>=0; i--){
						labels[i+1].setText(labels[i].getText());
					}
					labels[0].setText(String.valueOf(log));
				}else{
					//最下行にログ追加
					for(int i=0; i<labels.length-1; i++){
						labels[i].setText(labels[i+1].getText());
					}
					labels[labels.length-1].setText(String.valueOf(log));
					//一番下にスクロール
					scrollTo(0, getBottom());
					fullScroll(ScrollView.FOCUS_DOWN);
				}
			}
		});
	}
}
