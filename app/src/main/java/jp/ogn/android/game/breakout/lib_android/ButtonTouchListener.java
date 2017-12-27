package jp.ogn.android.game.breakout.lib_android;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;

public abstract class ButtonTouchListener implements OnTouchListener{
	protected final Button btn;
	protected String defaultName;

	protected String getDefaultName(){
		return this.defaultName==null ? "" : this.defaultName;
	}

	public ButtonTouchListener(Button btn){
		super();
		this.btn=btn;
	}
	public ButtonTouchListener(Button btn,String defaultName){
		super();
		this.btn=btn;
		this.defaultName=defaultName;
		btn.setText(defaultName);
	}

	@Override
	public final boolean onTouch(View v,MotionEvent event){
		switch(event.getAction()){
		case MotionEvent.ACTION_DOWN:
			onDown(v,event);
			break;
		case MotionEvent.ACTION_UP:
			onUp(v,event);
			break;
		}
		return false;
	}

	protected abstract void onDown(View v,MotionEvent event);
	protected abstract void onUp(View v,MotionEvent event);

	protected final boolean inRange(float evX,float evY){
		return (evX>0 && evX<(btn.getRight()-btn.getLeft())
				&& evY>0 && evY<(btn.getBottom()-btn.getTop()));
	}
}
