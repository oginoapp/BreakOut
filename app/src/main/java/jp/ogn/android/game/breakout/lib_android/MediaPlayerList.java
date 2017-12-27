package jp.ogn.android.game.breakout.lib_android;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.media.MediaPlayer;

public class MediaPlayerList extends HashMap<String,MediaPlayer>{
	private static Context context;
	private static MediaPlayerList mpList=new MediaPlayerList();

	private MediaPlayerList(){super();}//コンストラクタによるインスタンス化を不可にするため

	public static MediaPlayerList getInstance(){
		return mpList;
	}
	public static void setContext(Context context){
		if(context==null){return;}
		MediaPlayerList.context=context;
	}

	//以下インスタンス
	private List<String> keyList=Collections.synchronizedList(new ArrayList<String>());
	public void play(int resourceID){
		play("bgmName",resourceID,false);
	}
	public void play(String bgmName,int resourceID){
		play(bgmName,resourceID,false);
	}
	public void play(String bgmName,int resourceID,boolean loop){
		if(containsKey(bgmName)){
			stop(bgmName);
		}
		MediaPlayer player=MediaPlayer.create(context,resourceID);
		player.setLooping(loop);
		player.start();
		put(bgmName,player);
		keyList.add(bgmName);
	}
	public void stop(String bgmName){
		get(bgmName).stop();
		remove(bgmName);
		keyList.remove(bgmName);
	}
	public void stopAll(){
		for(int i=keyList.size()-1;i>=0;i--){
			if(containsKey(keyList.get(i))){
				stop(keyList.get(i));
			}
		}
		/*
		for(String bgmName:keyList){
			get(bgmName).stop();
		}
		keyList.clear();
		this.clear();
		*/
	}
}
