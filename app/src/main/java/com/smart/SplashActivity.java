package com.smart;

import android.app.*;
import android.os.*;
import android.widget.*;
import android.view.animation.*;
import android.graphics.*;
import android.content.*;
import android.view.*;
import com.google.firebase.auth.*;
import com.google.android.gms.tasks.*;
import javax.crypto.*;

public class SplashActivity extends Activity
{
	FirebaseAuth auth;
	FirebaseUser user;

	ImageView img;
	TextView tv,tg;
	EditText etmail,etpass;
	Button btnLog;
	LinearLayout authPan;

    Animation up,down,top,zoom;
	Typeface tp,changa;

	private boolean press=false;
	private AlertDialog dialog;

	@Override
	public void onBackPressed()
	{
        if (press)
		{
			super.onBackPressed();
			return;
		}
		press = true;
		message("Press back once more");
		new Handler().postDelayed(new Runnable(){
				@Override
				public void run()
				{
					press = false;
				}
			}, 2000);
	}

	@Override 
	protected void onCreate(Bundle bundle)
	{
		super.onCreate(bundle);
		setContentView(R.layout.splash_activity);

		img = findViewById(R.id.splash_activityImageView);
		tv = findViewById(R.id.splash_activityTextView);
		authPan = findViewById(R.id.auth_pan);
		etmail = findViewById(R.id.txt_mail);
		etpass = findViewById(R.id.txt_pass);
		tg = findViewById(R.id.tv_log);
		btnLog = findViewById(R.id.btn_log);

		auth = FirebaseAuth.getInstance();
		user = auth.getInstance().getCurrentUser();

		tp = Typeface.createFromAsset(getAssets(), "Lemonada-Bold.ttf");
		changa = Typeface.createFromAsset(getAssets(), "Changa-Medium.ttf");
		up = AnimationUtils.loadAnimation(this, R.anim.move_up);
		down = AnimationUtils.loadAnimation(this, R.anim.move_down);
		top = AnimationUtils.loadAnimation(this, R.anim.move_top);
		zoom = AnimationUtils.loadAnimation(this, R.anim.zoom_in);
		tv.setAnimation(up);
		tv.setTypeface(tp);
		etmail.setTypeface(changa);
		etpass.setTypeface(changa);
		tg.setTypeface(changa);
		btnLog.setTypeface(changa);
		img.setAnimation(down);
		if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
		   etmail.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.mail_icon),null,null,null);
		   etpass.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.pass_icon),null,null,null);
		}

		new Handler().postDelayed(new Runnable(){
				@Override
				public void run()
				{
					if (user != null)
					{
						Intent in=new Intent(SplashActivity.this, MainActivity.class);
						in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(in);
					}
					else
					{
						tv.clearAnimation();
						img.clearAnimation();
						tv.setAnimation(top);
						img.setAnimation(top);
						authPan.setVisibility(LinearLayout.VISIBLE);
						authPan.setAnimation(zoom);
					}

				}
			}, 2000);
		btnLog.setOnClickListener(new Button.OnClickListener(){
				@Override
				public void onClick(View v)
				{
					if (!etmail.getText().toString().contains("@") ||
						!etmail.getText().toString().contains("."))
					{
						etmail.setError("Check mail");
					}
					else if (etpass.getText().toString().trim().isEmpty())
					{
						etpass.setError("Check password");
					}
					else
					{
						checkDetail();
					}
				}
			});
		etpass.setOnEditorActionListener(new EditText.OnEditorActionListener(){

				@Override
				public boolean onEditorAction(TextView p1, int p2, KeyEvent p3)
				{
					if(p2==6){
						checkDetail();
					}
					return false;
				}
				
				
			});
	}
	private void checkDetail(){
		loadingAlert();
		auth.signInWithEmailAndPassword(etmail.getText().toString(), etpass.getText().toString()).addOnCompleteListener(SplashActivity.this, new OnCompleteListener<AuthResult>(){

				@Override
				public void onComplete(Task<AuthResult> tsk)
				{
					if(tsk.isSuccessful()){
						Intent in=new Intent(SplashActivity.this, MainActivity.class);
						in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(in);
						dialog.dismiss();
					}
					else{
						dialog.dismiss();
						message(tsk.getException().getMessage());
					}
				}
			});
	}
	private void loadingAlert(){
		AlertDialog.Builder ad=new AlertDialog.Builder(this);
		View v=getLayoutInflater().inflate(R.layout.loading_dialog,null);
		ad.setCancelable(false);
		ad.setView(v);
		TextView th=v.findViewById(R.id.loading_head);
		TextView tt=v.findViewById(R.id.loading_txt);
		th.setTypeface(tp);
		tt.setTypeface(tp);
		dialog=ad.create();
		if(dialog.getWindow()!=null){
			dialog.getWindow().getAttributes().windowAnimations=R.style.zoomAlert;
		}
		dialog.show();
	}
	private void message(String m)
	{
		Toast.makeText(this, m, Toast.LENGTH_SHORT).show();
	}
}
