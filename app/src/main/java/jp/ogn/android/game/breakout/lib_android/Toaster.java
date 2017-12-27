package jp.ogn.android.game.breakout.lib_android;

import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 更新日時：20160514
 * 更新内容：makeViewメソッドのバグ修正
 */
public class Toaster{
	final public static int HEIGHT_DEFAULT=-1;

	final private Handler handler;
	final private Context context;

	public Toaster(Context context){
		handler=new Handler();
		this.context=context;
	}

	/**
	 * @see 説明：引数に入れた文字列をトースト出力する
	 */
	public synchronized void put(Object mess){
		put(mess,Toast.LENGTH_SHORT);
	}
	public synchronized void put(final Object mess,final int length){
		handler.post(new Runnable(){
			@Override
			public void run(){
				Toast.makeText(context,String.valueOf(mess),length).show();
			}
		});
	}

	/**
	 * @see 説明：引数に入れたViewをトースト出力する
	 */
	public synchronized void putView(View v){
		putView(v,Toast.LENGTH_SHORT,HEIGHT_DEFAULT);
	}
	public synchronized void putView(View v,int length){
		putView(v,length,HEIGHT_DEFAULT);
	}
	public synchronized void putView(final View view,final int length,final float startY){
		handler.post(new Runnable(){
			@Override
			public void run(){
				Toast toast=new Toast(context);
				toast.setView(view);
				toast.setDuration(length);
				if(startY!=HEIGHT_DEFAULT){
					toast.setGravity(Gravity.TOP,0,(int)startY);
				}
				toast.show();
			}
		});
	}

	/**
	 * @see makeView(text,textSize,textColor)
	 * @see makeView(backgroundResourceID)
	 */
	public synchronized View makeView(Object text,float textSize,int textColor){
		TextView txtv=new TextView(context);
		txtv.setText(String.valueOf(text));
		txtv.setTextSize(textSize);
		txtv.setTextColor(textColor);
		return txtv;
	}
	public synchronized View makeView(int backgroundResourceID){
		ImageView imgv=new ImageView(context);
		imgv.setImageResource(backgroundResourceID);
		return imgv;
	}
}
