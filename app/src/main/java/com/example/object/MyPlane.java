package com.example.object;

import java.util.ArrayList;
import java.util.List;
import com.example.factory.GameObjectFactory;
import com.example.interfaces.IMyPlane;
import com.example.mybeatplane.R;
import com.example.view.MainView;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
/*��ҷɻ�����*/
public class MyPlane extends GameObject implements IMyPlane{
	private float middle_x;			 // �ɻ�����������
	private float middle_y;
	private long startTime;	 	 	 // ��ʼ��ʱ��
	private long endTime;	 	 	 // ������ʱ��
	private boolean isChangeBullet;  // ��Ǹ������ӵ�
	private Bitmap myplane;			 // �ɻ�����ʱ��ͼƬ
	private MainView mainView;
	private GameObjectFactory factory;
	public MyPlane(Resources resources) {
		super(resources);
		// TODO Auto-generated constructor stub
		initBitmap();
		this.speed = 8;
		isChangeBullet = false;
		factory = new GameObjectFactory();
		changeButtle();
	}
	public void setMainView(MainView mainView) {
		this.mainView = mainView;
	}
	// ������Ļ��Ⱥ͸߶�
	@Override
	public void setScreenWH(float screen_width, float screen_height) {
		super.setScreenWH(screen_width, screen_height);
		object_x = screen_width/2 - object_width/2;
		object_y = screen_height - object_height;
		middle_x = object_x + object_width/2;
		middle_y = object_y + object_height/2;
	}
	// ��ʼ��ͼƬ��Դ��
	@Override
	public void initBitmap() {
		// TODO Auto-generated method stub
		myplane = BitmapFactory.decodeResource(resources, R.drawable.sfish);
		object_width = myplane.getWidth() ; // ���ÿһ֡λͼ�Ŀ�
		object_height = myplane.getHeight(); 	// ���ÿһ֡λͼ�ĸ�
	}
	// ����Ļ�ͼ����
	@Override
	public void drawSelf(Canvas canvas) {
		// TODO Auto-generated method stub

			int x = (int) (currentFrame * object_width); // ��õ�ǰ֡�����λͼ��X����
			canvas.save();
			canvas.clipRect(object_x, object_y, object_x + object_width, object_y + object_height);
			canvas.drawBitmap(myplane, object_x - x, object_y, paint);
			canvas.restore();
	}
	// �ͷ���Դ�ķ���
	@Override
	public void release() {

		if(!myplane.isRecycled()){
			myplane.recycle();
		}

	}
	//�����ӵ�
	@Override
	public void shoot(Canvas canvas,List<EnemyPlane> planes) {

	}
	//��ʼ���ӵ�
	@Override
	public void initButtle() {

	}
	//�����ӵ�
	@Override
	public void changeButtle() {

	}
	//�ж��ӵ��Ƿ�ʱ
	public void isBulletOverTime(){
		if(isChangeBullet){
			endTime = System.currentTimeMillis();
			if(endTime - startTime > 15000){
				isChangeBullet = false;
				startTime = 0;
				endTime = 0;
				changeButtle();
			}
		}
	}
	//getter��setter����
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	@Override
	public boolean isChangeBullet() {
		return isChangeBullet;
	}
	@Override
	public void setChangeBullet(boolean isChangeBullet) {
		this.isChangeBullet = isChangeBullet;
	}
	@Override
	public float getMiddle_x() {
		return middle_x;
	}
	@Override
	public void setMiddle_x(float middle_x) {
		this.middle_x = middle_x;
		this.object_x = middle_x - object_width/2;
	}
	@Override
	public float getMiddle_y() {
		return middle_y;
	}
	@Override
	public void setMiddle_y(float middle_y) {
		this.middle_y = middle_y;
		this.object_y = middle_y - object_height/2;
	}
}
