package com.example.view;

import java.util.ArrayList;
import java.util.List;

import com.example.constant.ConstantUtil;
import com.example.factory.GameObjectFactory;
import com.example.mybeatplane.R;
import com.example.object.BigPlane;
import com.example.object.BossPlane;
import com.example.object.BulletGoods;
import com.example.object.EnemyPlane;
import com.example.object.GameObject;
import com.example.object.MiddlePlane;
import com.example.object.MissileGoods;
import com.example.object.MyPlane;
import com.example.object.SmallPlane;
import com.example.sounds.GameSoundPool;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Message;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
/*��Ϸ���е�������*/
public class MainView extends BaseView{
	private int missileCount; 		// ����������
	private int middlePlaneScore;	// ���͵л��Ļ���
	private int bigPlaneScore;		// ���͵л��Ļ���
	private int bossPlaneScore;		// boss�͵л��Ļ���
	private int missileScore;		// �����Ļ���
	private int sumScore;			// ��Ϸ�ܵ÷�
	private int speedTime;			// ��Ϸ�ٶȵı���
	private float bg_y;				// ͼƬ������
	private float bg_y2;
	private float play_bt_w;
	private float play_bt_h;
	private float missile_bt_y;
	private boolean isPlay;			// �����Ϸ����״̬
	private boolean isTouchPlane;	// �ж�����Ƿ�����Ļ
	private Bitmap background; 		// ����ͼƬ
	private Bitmap background2; 	// ����ͼƬ
	private Bitmap playButton; 		// ��ʼ/��ͣ��Ϸ�İ�ťͼƬ
	private Bitmap missile_bt;		// ������ťͼ��
	private MyPlane myPlane;		// ��ҵķɻ�
	private BossPlane bossPlane;	// boss�ɻ�
	private List<EnemyPlane> enemyPlanes;
	private MissileGoods missileGoods;
	private BulletGoods bulletGoods;
	private GameObjectFactory factory;
	public MainView(Context context,GameSoundPool sounds) {
		super(context,sounds);
		// TODO Auto-generated constructor stub
		isPlay = true;
		speedTime = 1;
		factory = new GameObjectFactory();						  //������
		enemyPlanes = new ArrayList<EnemyPlane>();
		myPlane = (MyPlane) factory.createMyPlane(getResources());//������ҵķɻ�
		myPlane.setMainView(this);
		for(int i = 0;i < SmallPlane.sumCount;i++){
			//����С�͵л�
			SmallPlane smallPlane = (SmallPlane) factory.createSmallPlane(getResources());
			enemyPlanes.add(smallPlane);
		}
		for(int i = 0;i < MiddlePlane.sumCount;i++){
			//�������͵л�
			MiddlePlane middlePlane = (MiddlePlane) factory.createMiddlePlane(getResources());
			enemyPlanes.add(middlePlane);
		}
		for(int i = 0;i < BigPlane.sumCount;i++){
			//�������͵л�
			BigPlane bigPlane = (BigPlane) factory.createBigPlane(getResources());
			enemyPlanes.add(bigPlane);
		}
		//����BOSS�л�
		bossPlane = (BossPlane)factory.createBossPlane(getResources());
		bossPlane.setMyPlane(myPlane);
		enemyPlanes.add(bossPlane);
		//����������Ʒ
		missileGoods = (MissileGoods) factory.createMissileGoods(getResources());
		//�����ӵ���Ʒ
		bulletGoods = (BulletGoods) factory.createBulletGoods(getResources());
		thread = new Thread(this);
	}
	// ��ͼ�ı�ķ���
	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		super.surfaceChanged(arg0, arg1, arg2, arg3);
	}
	// ��ͼ�����ķ���
	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		super.surfaceCreated(arg0);
		initBitmap(); // ��ʼ��ͼƬ��Դ
		for(GameObject obj:enemyPlanes){
			obj.setScreenWH(screen_width,screen_height);
		}
		missileGoods.setScreenWH(screen_width, screen_height);
		bulletGoods.setScreenWH(screen_width, screen_height);
		myPlane.setScreenWH(screen_width,screen_height);
		myPlane.setAlive(true);
		if(thread.isAlive()){
			thread.start();
		}
		else{
			thread = new Thread(this);
			thread.start();
		}
	}
	// ��ͼ���ٵķ���
	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		super.surfaceDestroyed(arg0);
		release();
	}
	// ��Ӧ�����¼��ķ���
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_UP){
			isTouchPlane = false;
			return true;
		}
		else if(event.getAction() == MotionEvent.ACTION_DOWN){
			float x = event.getX();
			float y = event.getY();
			if(x > 10 && x < 10 + play_bt_w && y > 10 && y < 10 + play_bt_h){
				if(isPlay){
					isPlay = false;
				}
				else{
					isPlay = true;
					synchronized(thread){
						thread.notify();
					}
				}
				return true;
			}
			//�ж���ҷɻ��Ƿ񱻰���
			else if(x > myPlane.getObject_x() && x < myPlane.getObject_x() + myPlane.getObject_width()
					&& y > myPlane.getObject_y() && y < myPlane.getObject_y() + myPlane.getObject_height()){
				if(isPlay){
					isTouchPlane = true;
				}
				return true;
			}
			//�жϵ�����ť�Ƿ񱻰���
			else if(x > 10 && x < 10 + missile_bt.getWidth()
					&& y > missile_bt_y && y < missile_bt_y + missile_bt.getHeight()){
				if(missileCount > 0){
					missileCount--;
					sounds.playSound(5, 0);
					for(EnemyPlane pobj:enemyPlanes){
						if(pobj.isCanCollide()){
							pobj.attacked(100);		   // �л������˺�
							if(pobj.isExplosion()){
								addGameScore(pobj.getScore());// ��÷���
							}
						}
					}
				}
				return true;
			}
		}
		//��Ӧ��ָ����Ļ�ƶ����¼�
		else if(event.getAction() == MotionEvent.ACTION_MOVE && event.getPointerCount() == 1){
			//�жϴ������Ƿ�Ϊ��ҵķɻ�
			if(isTouchPlane){
				float x = event.getX();
				float y = event.getY();
				if(x > myPlane.getMiddle_x() + 20){
					if(myPlane.getMiddle_x() + myPlane.getSpeed() <= screen_width){
						myPlane.setMiddle_x(myPlane.getMiddle_x() + myPlane.getSpeed());
					}
				}
				else if(x < myPlane.getMiddle_x() - 20){
					if(myPlane.getMiddle_x() - myPlane.getSpeed() >= 0){
						myPlane.setMiddle_x(myPlane.getMiddle_x() - myPlane.getSpeed());
					}
				}
				if(y > myPlane.getMiddle_y() + 20){
					if(myPlane.getMiddle_y() + myPlane.getSpeed() <= screen_height){
						myPlane.setMiddle_y(myPlane.getMiddle_y() + myPlane.getSpeed());
					}
				}
				else if(y < myPlane.getMiddle_y() - 20){
					if(myPlane.getMiddle_y() - myPlane.getSpeed() >= 0){
						myPlane.setMiddle_y(myPlane.getMiddle_y() - myPlane.getSpeed());
					}
				}
				return true;
			}
		}
		return false;
	}
	// ��ʼ��ͼƬ��Դ����
	@Override
	public void initBitmap() {
		// TODO Auto-generated method stub
		playButton = BitmapFactory.decodeResource(getResources(),R.drawable.play);
		background = BitmapFactory.decodeResource(getResources(), R.drawable.background);
		background2 = BitmapFactory.decodeResource(getResources(), R.drawable.background);
		missile_bt = BitmapFactory.decodeResource(getResources(), R.drawable.missile_bt);
		scalex = screen_width / background.getWidth();
		scaley = screen_height / background.getHeight();
		play_bt_w = playButton.getWidth();
		play_bt_h = playButton.getHeight()/2;
		bg_y = 0;
		bg_y2 = bg_y - screen_height;
		missile_bt_y = screen_height - 10 - missile_bt.getHeight();
	}
	//��ʼ����Ϸ����
	public void initObject(){
		for(EnemyPlane obj:enemyPlanes){
			//��ʼ��С�͵л�
			if(obj instanceof SmallPlane){
				if(!obj.isAlive()){
					obj.initial(speedTime,0,0);
					break;
				}
			}
			//��ʼ�����͵л�
			else if(obj instanceof MiddlePlane){
				if(middlePlaneScore > 10000){
					if(!obj.isAlive()){
						obj.initial(speedTime,0,0);
						break;
					}
				}
			}
			//��ʼ�����͵л�
			else if(obj instanceof BigPlane){
				if(bigPlaneScore >= 25000){
					if(!obj.isAlive()){
						obj.initial(speedTime,0,0);
						break;
					}
				}
			}
			//��ʼ��BOSS�л�
			else{
				if(bossPlaneScore >= 80000){
					if(!obj.isAlive()){
						obj.initial(0,0,0);
						break;
					}
				}
			}
		}
		//��ʼ��������Ʒ
		if(missileScore >= 30000){
			if(!missileGoods.isAlive()){
				missileGoods.initial(0,0,0);
				missileScore = 0;
			}
		}

		//��ʼ��BOSS�ɻ����ӵ�
		if(bossPlane.isAlive())
			bossPlane.initButtle();
	}
	// �ͷ�ͼƬ��Դ�ķ���
	@Override
	public void release() {
		// TODO Auto-generated method stub
		for(GameObject obj:enemyPlanes){
			obj.release();
		}
		myPlane.release();
		bulletGoods.release();
		missileGoods.release();
		if(!playButton.isRecycled()){
			playButton.recycle();
		}
		if(!background.isRecycled()){
			background.recycle();
		}
		if(!background2.isRecycled()){
			background2.recycle();
		}
		if(!missile_bt.isRecycled()){
			missile_bt.recycle();
		}
	}
	// ��ͼ����
	@Override
	public void drawSelf() {
		// TODO Auto-generated method stub
		try {
			canvas = sfh.lockCanvas();
			canvas.drawColor(Color.BLACK); // ���Ʊ���ɫ
			canvas.save();
			// ���㱳��ͼƬ����Ļ�ı���
			canvas.scale(scalex, scaley, 0, 0);
			canvas.drawBitmap(background, 0, bg_y, paint);   // ���Ʊ���ͼ
			canvas.drawBitmap(background2, 0, bg_y2, paint); // ���Ʊ���ͼ
			canvas.restore();
			//���ư�ť
			canvas.save();
			canvas.clipRect(10, 10, 10 + play_bt_w,10 + play_bt_h);
			if(isPlay){
				canvas.drawBitmap(playButton, 10, 10, paint);
			}
			else{
				canvas.drawBitmap(playButton, 10, 10 - play_bt_h, paint);
			}
			canvas.restore();
			//���Ƶ�����ť
			if(missileCount > 0){
				paint.setTextSize(40);
				paint.setColor(Color.BLACK);
				canvas.drawBitmap(missile_bt, 10,missile_bt_y, paint);
				canvas.drawText("X "+String.valueOf(missileCount), 20 + missile_bt.getWidth(), screen_height - 25, paint);//��������
			}
			//���Ƶ�����Ʒ
			if(missileGoods.isAlive()){
				if(missileGoods.isCollide(myPlane)){
					missileGoods.setAlive(false);
					missileCount++;
					sounds.playSound(6, 0);
				}
				else
					missileGoods.drawSelf(canvas);
			}
			//�����ӵ���Ʒ
			if(bulletGoods.isAlive()){
				if(bulletGoods.isCollide(myPlane)){
					bulletGoods.setAlive(false);
					if(!myPlane.isChangeBullet()){
						myPlane.setChangeBullet(true);
						myPlane.changeButtle();
						myPlane.setStartTime(System.currentTimeMillis());
					}
					else{
						myPlane.setStartTime(System.currentTimeMillis());
					}
					sounds.playSound(6, 0);
				}
				else
					bulletGoods.drawSelf(canvas);
			}
			//���Ƶл�
			for(EnemyPlane obj:enemyPlanes){
				if(obj.isAlive()){
					obj.drawSelf(canvas);
					//���л��Ƿ�����ҵķɻ���ײ
					if(obj.isCanCollide() && myPlane.isAlive()){
						if(obj.isCollide(myPlane)){
							if(obj.getObject_width()> myPlane.getObject_width())
								myPlane.setAlive(false);
							else {
								addGameScore(obj.getScore());
								obj.setAlive(false);
							}
						}
					}
				}
			}
			if(!myPlane.isAlive()){
				threadFlag = false;
				sounds.playSound(4, 0);			//�ɻ�ը�ٵ���Ч
			}
			myPlane.drawSelf(canvas);	//������ҵķɻ�
			myPlane.shoot(canvas,enemyPlanes);
			sounds.playSound(1, 0);	  //�ӵ���Ч
			//���Ƶ�����ť
			if(missileCount > 0){
				paint.setTextSize(40);
				paint.setColor(Color.BLACK);
				canvas.drawBitmap(missile_bt, 10,missile_bt_y, paint);
				canvas.drawText("X "+String.valueOf(missileCount), 20 + missile_bt.getWidth(), screen_height - 25, paint);//��������
			}
			//���ƻ�������
			paint.setTextSize(30);
			paint.setColor(Color.rgb(235, 161, 1));
			canvas.drawText("����:"+String.valueOf(sumScore), 30 + play_bt_w, 40, paint);		//��������
			canvas.drawText("�ȼ� X "+String.valueOf(speedTime), screen_width - 150, 40, paint); //��������
		} catch (Exception err) {
			err.printStackTrace();
		} finally {
			if (canvas != null)
				sfh.unlockCanvasAndPost(canvas);
		}
	}
	// �����ƶ����߼�����
	public void viewLogic(){
		if(bg_y > bg_y2){
			bg_y += 10;
			bg_y2 = bg_y - background.getHeight();
		}
		else{
			bg_y2 += 10;
			bg_y = bg_y2 - background.getHeight();
		}
		if(bg_y >= background.getHeight()){
			bg_y = bg_y2 - background.getHeight();
		}
		else if(bg_y2 >= background.getHeight()){
			bg_y2 = bg_y - background.getHeight();
		}
	}
	// ������Ϸ�����ķ���
	public void addGameScore(int score){
		middlePlaneScore += score;	// ���͵л��Ļ���
		bigPlaneScore += score;		// ���͵л��Ļ���
		bossPlaneScore += score;	// boss�͵л��Ļ���
		missileScore += score;		// �����Ļ���
		sumScore += score;			// ��Ϸ�ܵ÷�
	}
	// ������Ч
	public void playSound(int key){
		sounds.playSound(key, 0);
	}
	// �߳����еķ���
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (threadFlag) {
			long startTime = System.currentTimeMillis();
			initObject();
			drawSelf();
			viewLogic();		//�����ƶ����߼�
			long endTime = System.currentTimeMillis();
			if(!isPlay){
				synchronized (thread) {
				    try {
				    	thread.wait();
				    } catch (InterruptedException e) {
				        e.printStackTrace();
				    }
				}
			}
			try {
				if (endTime - startTime < 100)
					Thread.sleep(100 - (endTime - startTime));
			} catch (InterruptedException err) {
				err.printStackTrace();
			}
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Message message = new Message();
		message.what = 	ConstantUtil.TO_END_VIEW;
		message.arg1 = Integer.valueOf(sumScore);
		mainActivity.getHandler().sendMessage(message);
	}
}
