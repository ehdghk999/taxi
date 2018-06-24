package "패키지 네임";

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class EmailCertifiDialog extends Dialog{
    private EditText edt_email_cert_number;
    private TextView txt_email_cert_timelimit;
    private Button btn_email_cert_check;
    private String certifi_number;
    private CountDownTimer countDownTimer;
    private int result_number;
    final int MILLISINFUTURE = 180 * 1000; //총 시간 (300초 = 5분)
    final int COUNT_DOWN_INTERVAL = 1000;
    public void setCertifi_number(String certifi_number) {
        this.certifi_number = certifi_number;
    }
    public int getResult_number() {
        return result_number;
    }

    public EmailCertifiDialog(@NonNull final Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.activity_email_certifi_dialog);

        edt_email_cert_number = findViewById(R.id.edt_email_cert_number);
        txt_email_cert_timelimit = findViewById(R.id.txt_email_cert_timelimit);
        btn_email_cert_check = findViewById(R.id.btn_email_cert_check);

        countDownTime();
        btn_email_cert_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edt_email_cert_number.getText().toString().length() == 0){
                    Toast.makeText(v.getContext(), "인증 번호를 입력해주십시오.", Toast.LENGTH_LONG).show();
                    edt_email_cert_number.requestFocus();
                    return;
                }
                if(!edt_email_cert_number.getText().toString().equals(certifi_number)){
                    Toast.makeText(v.getContext(),"인증 번호가 다릅니다.", Toast.LENGTH_LONG).show();
                    result_number = 0;
                    dismiss();
                    return;
                }else{
                    Toast.makeText(v.getContext(), "인증이 완료 되었습니다.", Toast.LENGTH_LONG).show();
                    result_number = 1;
                    dismiss();
                    return;
                }
            }
        });



    }

    public void countDownTime(){
        countDownTimer = new CountDownTimer(MILLISINFUTURE, COUNT_DOWN_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                long emailAuthCount = millisUntilFinished / 1000;
                if ((emailAuthCount - ((emailAuthCount / 60) * 60)) >= 10) {
                    txt_email_cert_timelimit.setText(
                            (emailAuthCount / 60) + " : " + (emailAuthCount - ((emailAuthCount / 60) * 60)));
                } else {
                    txt_email_cert_timelimit.setText(
                            (emailAuthCount / 60) + " : 0" + (emailAuthCount - ((emailAuthCount / 60) * 60)));
                }
            }
            @Override
            public void onFinish() {
                dismiss();
            }
        }.start();
    }
}
