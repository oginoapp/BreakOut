package jp.ogn.android.game.breakout.physic;

import java.util.ArrayList;

import jp.ogn.android.game.breakout.ENV;
import jp.ogn.android.game.breakout.R;
import android.widget.FrameLayout;

public class PhysicallyField {
	private FrameLayout background;

	private ArrayList<PhysicallyDrawable> drawables = new ArrayList<PhysicallyDrawable>();

	/**
	 * コンストラクタ
	 * 初期化時に指定された数のオブジェクトを生成して配置
	 */
	public PhysicallyField(FrameLayout background, int xQuant, int yQuant, float areaOfUsePerc){
		this.background = background;

		//配置する要素の生成
		int blockWidth = (int)(ENV.screenWidth * 0.95 / xQuant);
		int blockHeight = (int)(ENV.screenHeight * areaOfUsePerc / yQuant);
		for(int i=0; i<xQuant * yQuant; i++){
			PhysicallyDrawable drawable = new PhysicallyDrawable(R.drawable.block_icon, blockWidth, blockHeight);
			drawables.add(drawable);
			this.background.addView(drawable);
			drawable.setManual(true);//非推奨
			drawable.setType(PhysicallyDrawable.TYPE_BLOCK);
			drawable.moveAbsolute((ENV.screenWidth-blockWidth*xQuant)/2+blockWidth*(i%xQuant), blockHeight/3+blockHeight*(i/yQuant));
		}

	}

	/**
	 * 物理演算されるオブジェクトを追加する
	 */
	public void addDrawable(PhysicallyDrawable drawable){
		drawables.add(drawable);
		this.background.addView(drawable);
	}

	/**
	 * 物理演算メソッド
	 * 全てのオブジェクトを計算メソッドに投げる
	 */
	public void physicalSimulation(){
		for(int i=0; i<drawables.size(); i++){
			PhysicallyDrawable obj1 = drawables.get(i);

			if(obj1.type() == PhysicallyDrawable.TYPE_BALL){//自動で動くブロックの場合

				//移動処理
				obj1.moveNext();

				//物理演算
				for(int j=0; j<drawables.size(); j++){
					if(i != j){

						PhysicallyDrawable obj2 = drawables.get(j);
						if(checkCollision(obj1,obj2).flg){//ブロックとの衝突が発生した場合

							float directionX = 0;
							float directionY = 0;

							if(obj2.form() == PhysicallyDrawable.FORM_ROUND){//球体
								directionX = (obj1.direction.x + obj2.direction.x) / 2;
								directionY = (obj1.direction.y + obj2.direction.y) / 2;
								directionX = (directionX + (obj1.pos.x - obj2.pos.x)) / 2;
								directionY = (directionY + (obj1.pos.y - obj2.pos.y)) / 2;
							}else
							if(obj2.form() == PhysicallyDrawable.FORM_RECTANGLE && obj2.type() != PhysicallyDrawable.TYPE_RACKET){//四角形
								if(obj1.pos.x >= obj2.left && obj1.pos.x <= obj2.right){//縦からの衝突
									if(obj1.pos.y > obj2.pos.y){
										directionY = Math.abs(obj1.direction.y);
									}else{
										directionY = -Math.abs(obj1.direction.y);
									}
								}else
								if(obj1.pos.y >= obj2.top && obj1.pos.y <= obj2.bottom){//横からの衝突
									if(obj1.pos.x < obj2.pos.x){
										directionX = Math.abs(obj1.direction.x);
									}else{
										directionX = -Math.abs(obj1.direction.x);
									}
								}else{
									directionX = (obj1.direction.x + obj2.direction.x) / 2 + (obj1.pos.x - obj2.pos.x);
									directionY = (obj2.direction.y + obj2.direction.y) / 2 + (obj1.pos.y - obj2.pos.y);
								}
							}else{//その他
								directionX = obj1.direction.x;
								directionY = obj2.direction.y;
								directionX += (obj1.pos.x - obj2.pos.x);
								directionY += (obj1.pos.y - obj2.pos.y);
								directionX += obj2.direction.x;
								directionY += obj2.direction.y;
							}

							//衝突発生時にdirectionを渡す
							obj1.setDirection(directionX, directionY * PhysicallyDrawable.MOVEMENT_Y_POWER);

							//衝突発生時の相手側を操作
							if(obj2.type() == PhysicallyDrawable.TYPE_BLOCK){
								obj2.kill();
							}

						}

					}
				}

				//壁との衝突判定
				if(obj1.top < 1){
					obj1.setDirection(obj1.direction.x, Math.abs(obj1.direction.y));
				}else
				if(obj1.left < 1){
					obj1.setDirection(Math.abs(obj1.direction.x), obj1.direction.y);
				}else
				if(obj1.right >= ENV.screenWidth){
					obj1.setDirection(-Math.abs(obj1.direction.x), obj1.direction.y);
				}else
				if(obj1.bottom >= ENV.screenHeight){
					ENV.gameflg = 3;//ゲームオーバー
				}

			}

		}
	}

	/**
	 * 衝突計算メソッド
	 */
	private FloatPoint checkCollision(PhysicallyDrawable obj1, PhysicallyDrawable obj2){
		FloatPoint collisionPoint = new FloatPoint();

		//衝突ではない条件をチェックし、すべて不一致であれば衝突発生
		if(obj1.top >= obj2.bottom){//上が相手の下より低い
		}else
		if(obj1.bottom <= obj2.top){//下が相手の上より高い
		}else
		if(obj1.left >= obj2.right){//左が相手の右より右
		}else
		if(obj1.right <= obj2.left){//右が相手の左より左
		}else
		if(obj2.form() == PhysicallyDrawable.FORM_ROUND){//相手が円形である
			if(obj1.slantLeft >= obj2.slantRight && obj1.slantTop >= obj2.slantBottom){//左上が相手の右下よりも右下
			}else
			if(obj1.slantRight <= obj2.slantLeft && obj1.slantTop >= obj2.slantBottom){//右上が相手の左下よりも左下
			}else
			if(obj1.slantLeft >= obj2.slantRight && obj1.slantBottom <= obj2.slantTop){//左下が相手の右上よりも右上
			}else
			if(obj1.slantRight <= obj2.slantLeft && obj1.slantBottom <= obj2.slantTop){//右下が相手の左上よりも左上
			}else{
				collisionPoint.flg = true;
			}
		}else{
			collisionPoint.flg = true;
		}

		return collisionPoint;
	}

}
