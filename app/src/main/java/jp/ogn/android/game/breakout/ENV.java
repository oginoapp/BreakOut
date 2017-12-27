package jp.ogn.android.game.breakout;

import android.content.Context;
import android.os.Handler;
import android.provider.Browser;
import jp.ogn.android.game.breakout.lib_android.Toaster;


public class ENV {
	//環境変数
	public static Context context = null;
	public static Handler handler = null;
	public static Toaster toaster = null;
	public static float screenWidth = 0.0F;
	public static float screenHeight = 0.0F;
	public static int ballSize = 0;
	public static int bottomLineY = 0;
	public static int gameflg = 0;//0=初期状態,1=進行,2=ゲームクリア,3=ゲームオーバー,4=終了
	public static int blockQuantX = 0;//ブロックの列数
	public static int blockQuantY = 0;//ブロックの行数
	public static volatile int killCount = 0;//ブロックを壊した回数

	/* テスト */
	public static void test(){
		Browser.getAllVisitedUrls(null);
		Browser.sendString(null, null);
	}
}
