package com.smart;

import android.app.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
import android.view.animation.*;
import android.widget.*;
import me.anwarshahriar.calligrapher.*;
import android.view.inputmethod.*;
import android.text.*;
import com.google.firebase.database.*;
import android.content.*;
import android.graphics.*;
import com.google.firebase.auth.*;
import android.graphics.drawable.*;
import android.net.*;
import android.media.*;
import android.content.res.*;
import java.io.*;
import android.content.pm.*;
import android.animation.*;

public class MainActivity extends Activity implements OnClickListener
{
	TextView tvname,tvlimit,limvalue,tvacant,tvacant1,vacval,tstate;
    Button btni,btnd;
	ImageButton btnlog;
	ToggleButton btnam;
	EditText etlim;
	
	Typeface tp;
	Calligrapher call;
	Animation zoomin,zoomout,down,up,fade,sup,sdn;
	AnimationDrawable an;

	RelativeLayout topan,setpan;
	LinearLayout cardan,vcard,btmalt;
	
	private int limit=0;
	private int inside=0;
	private boolean press=false;
	private boolean aud=true;
	private MediaPlayer mp;

	private Medium mlimit;
	private AlertDialog dialog;
	
	FirebaseDatabase fdb;
	DatabaseReference db;
	FirebaseAuth auth;
	
	@Override
	public void onBackPressed(){
	    if(press){
			super.onBackPressed();
			return;
		}
		press=true;
		message("Press back once more");
		new Handler().postDelayed(new Runnable(){
			@Override
			public void run(){
				press=false;
			}
		},2000);
	}
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
		
		btnlog=findViewById(R.id.btn_logout);
		btnam=findViewById(R.id.btn_alarm);
		tvname=findViewById(R.id.tv_appname);
		tvlimit=findViewById(R.id.tv_limit);
		limvalue=findViewById(R.id.tv_lim_value);
		tvacant=findViewById(R.id.tv_vacant);
		tvacant1=findViewById(R.id.tv_vacant1);
		vacval=findViewById(R.id.tv_vacant_value);
		topan=findViewById(R.id.topLayout);
		btmalt=findViewById(R.id.bottom_alert);
		cardan=findViewById(R.id.card_content);
		vcard=findViewById(R.id.vacant_card);
		setpan=findViewById(R.id.set_content);
		btni=findViewById(R.id.btn_increment);
		btnd=findViewById(R.id.btn_decrement);
		etlim=findViewById(R.id.et_set);
		tstate=findViewById(R.id.txt_status);
		
		mlimit=new Medium();
		mp=new MediaPlayer().create(this,R.raw.alarm);
	
		auth=FirebaseAuth.getInstance();
		fdb=FirebaseDatabase.getInstance();
		db=fdb.getReference("owl");
		
		tp=Typeface.createFromAsset(getAssets(),"Lemonada-Bold.ttf");
		call=new Calligrapher(this);
		call.setFont(this,"Lemonada-Bold.ttf",true);
        zoomin=AnimationUtils.loadAnimation(this,R.anim.zoom_in);
		zoomout=AnimationUtils.loadAnimation(this,R.anim.zoom_out);
		down=AnimationUtils.loadAnimation(this,R.anim.move_down);
		up=AnimationUtils.loadAnimation(this,R.anim.move_up);
		fade=AnimationUtils.loadAnimation(this,R.anim.fade_in);
		sup=AnimationUtils.loadAnimation(this,R.anim.slide_up);
		sdn=AnimationUtils.loadAnimation(this,R.anim.slide_down);
		
		topan.setAnimation(down);
		cardan.setAnimation(zoomin);
		setpan.setAnimation(up);
		btmalt.setAnimation(up);
		
		etlim.setText(String.valueOf(limit));
		btni.setOnClickListener(this);
		btnd.setOnClickListener(this);
		btnlog.setOnClickListener(this);
        btnam.setOnClickListener(this);
		IntentFilter filter=new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(new NetworkReceiver(),filter);
		NetworkReceiver.Received(new Status(){

				@Override
				public void Received(String m)
				{
					if(m.equals("yes")){
						conStatus(true);
					}
					else{
						conStatus(false);
					}
				}
				
			
		});
		
		
		if(aud){
			btnam.setBackgroundResource(R.drawable.ic_alarm_enable);
		}
		else{
			btnam.setBackgroundResource(R.drawable.ic_alarm_disable);
		}
		db.addValueEventListener(new ValueEventListener(){

				@Override
				public void onDataChange(DataSnapshot p1)
				{
					limvalue.setText(String.valueOf(p1.getValue(Medium.class).getInside()));
					limit=p1.getValue(Medium.class).getLimit();
					inside=p1.getValue(Medium.class).getInside();
					etlim.setText(String.valueOf(limit));
					vacval.setText(String.valueOf(p1.getValue(Medium.class).getLimit()-p1.getValue(Medium.class).getInside()));
					if(inside<=limit){
						animCard();
						an.stop();
						vcard.setBackgroundResource(R.drawable.card_background);
						stopAlarm();
					}
					else{
						animCard();
						an.start();

						if(!mp.isPlaying()){
							startAlarm();
						}
					}
				}

				@Override
				public void onCancelled(DatabaseError p1)
				{
					message(p1.getMessage());
				}
				
			
		});
		
		etlim.setOnEditorActionListener(new EditText.OnEditorActionListener(){

				@Override
				public boolean onEditorAction(TextView p1, int p2, KeyEvent p3)
				{
				    if(p2==6){
						if(etlim.getText().toString().isEmpty()){
							message("Please enter the limit");
							etlim.setText(String.valueOf(limit));
						}
						else{
							db.child("limit").setValue(Integer.parseInt(etlim.getText().toString()));
						}
		
					}
					return false;
				}
			
		});
    }

	@Override
	public void onClick(View v){
		switch(v.getId()){
			case R.id.btn_decrement:
				if(limit>0){
					limit--;
				}
				db.child("limit").setValue(limit);
				break;
			case R.id.btn_increment:
				limit++;
				db.child("limit").setValue(limit);
				break;
			case R.id.btn_logout:
				logoutAlert();
				break;
			case R.id.logout_no:
				dialog.dismiss();
				break;
			case R.id.btn_alarm:
				checkAlarm();
				break;
			case R.id.logout_yes:
				dialog.dismiss();
				auth.signOut();
				stopAlarm();
				message("Logout successfull");
				Intent in=new Intent(this,SplashActivity.class);
				in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(in);
				break;
		}
	}
	private void conStatus(Boolean b){
		if(b){
			btmalt.setVisibility(View.VISIBLE);
		    btmalt.startAnimation(sup);
			tstate.setText("Internet connected!");
			btmalt.setBackgroundColor(Color.parseColor("#30800D"));
			new Handler().postDelayed(new Runnable(){
				@Override
				public void run(){
					btmalt.startAnimation(sdn);
					sdn.setAnimationListener(new Animation.AnimationListener(){

							@Override
							public void onAnimationStart(Animation p1){}
							@Override
							public void onAnimationEnd(Animation p1)
							{
								btmalt.clearAnimation();
								btmalt.setVisibility(View.GONE);
							}
							@Override
							public void onAnimationRepeat(Animation p1){}
					});
				}
			},6000);
		}
		else{
			btmalt.setVisibility(View.VISIBLE);
			btmalt.startAnimation(sup);
			btmalt.setBackgroundColor(Color.BLACK);
			tstate.setText("No internet connection!");
		}
		
	}
	private void checkAlarm(){
		if(!btnam.isChecked()){
			aud=true;
			if(inside>limit){
				startAlarm();
			}
			btnam.setBackgroundResource(R.drawable.ic_alarm_enable);
		}
		else{
			btnam.setBackgroundResource(R.drawable.ic_alarm_disable);
			aud=false;
			stopAlarm();
		}
	}
	private void startAlarm(){
		if(aud){
			if(!mp.isPlaying()){
				mp.start();
				mp.setLooping(true);
			}
		}
		else{
			if(inside>limit){
				stopAlarm();
			}
		}
	}
	private void stopAlarm(){
	    if(mp.isPlaying()){
			mp.pause();
		}
	}

	@Override
	protected void onResume()
	{
		if(inside<=limit){
			if(mp.isPlaying()){
				stopAlarm();
				aud=true;
			}
		  }
			else{
				startAlarm();
			}
		// registerReceiver(new NetworkReceiver(),filter);
		super.onResume();
	}

	@Override
	protected void onDestroy()
	{
		stopAlarm();
		aud=false;
		//unregisterReceiver(new NetworkReceiver());
		super.onDestroy();
	}

	private void logoutAlert(){
		AlertDialog.Builder ad=new AlertDialog.Builder(this);
		View v=getLayoutInflater().inflate(R.layout.logout_dialog,null);
		ad.setView(v);
		dialog=ad.create();
		TextView th=v.findViewById(R.id.log_head);
		TextView tt=v.findViewById(R.id.log_txt);
		Button btnno=v.findViewById(R.id.logout_no);
		Button btnyes=v.findViewById(R.id.logout_yes);
		th.setTypeface(tp);
		tt.setTypeface(tp);
		btnyes.setTypeface(tp);
		btnno.setTypeface(tp);
		btnno.setOnClickListener(this);
		btnyes.setOnClickListener(this);
	    if(dialog.getWindow()!=null){
			dialog.getWindow().getAttributes().windowAnimations=R.style.zoomAlert;
		}
		dialog.show();
	}
	private void animCard(){
		vcard.setBackgroundResource(R.drawable.transition);
		an=(AnimationDrawable)vcard.getBackground();
	}
	private void message(String m){
		Toast.makeText(this,m,Toast.LENGTH_SHORT).show();
	}
}
