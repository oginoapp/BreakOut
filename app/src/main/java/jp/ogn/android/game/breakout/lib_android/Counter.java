package jp.ogn.android.game.breakout.lib_android;

/**
 * 更新日時：20160520
 * 更新内容：待機と再開処理の追加
 *
 * 更新日時：20160514
 * 更新内容：停止メソッドの追加
 */
public abstract class Counter extends Thread{
	public static final int INFINITY = -1;// <= -1
	private int max;
	private int interval;
	private boolean countDown;
	private boolean err_flg;
	private boolean pause;
	private boolean loop;
	private boolean count_infinity;
	private int count;

	/* コンストラクタ */
	public Counter(){
		this(100, 1000, true);
	}

	public Counter(int max){
		this(max, 1000, true);
	}

	public Counter(int max, int interval){
		this(max, interval, true);
	}

	public Counter(int max, int interval, boolean countDown){
		this.max = max;
		this.interval = interval;
		this.countDown = countDown;
		this.count = (countDown ? max : 0);
		this.err_flg = false;
		this.pause = false;
		this.loop = true;
		this.count_infinity = (max == Counter.INFINITY ? true : false);
	}

	/* 間隔変更 */
	public void setInterval(int interval){
		this.interval = interval <= 0 ? 1 : interval;
	}

	/* 停止 */
	public void stopClock(){
		this.pause = false;
		this.loop = false;
	}

	/* 待機 */
	public void pauseClock(){
		this.pause = true;
	}

	/* notify再開 */
	public void resumeClock(){
		this.pause = false;
	}

	/* カウント処理 */
	@Override
	public void run(){
		onStart();
		clock: while(loop){
			try{
				while(pause);
				onCount(count);
				Thread.sleep(interval);
				if(!count_infinity){
					if(countDown){
						if(count <= 0){
							loop = false;
						}else{
							count--;
						}
					}else{
						if(count >= max){
							loop = false;
						}else{
							count++;
						}
					}
				}
			}catch(Exception ex){
				ex.printStackTrace();
				err_flg = true;
				loop = false;
				break clock;
			}
		}
		onStop(err_flg);
	}

	protected abstract void onStart();
	protected abstract void onCount(int count);
	protected abstract void onStop(boolean err_flg);
}
