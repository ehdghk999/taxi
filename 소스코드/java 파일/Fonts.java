package "��Ű�� ����";

import android.app.Application;

import com.tsengvn.typekit.Typekit;

public class Fonts extends Application {
    @Override
    public void onCreate(){
        super.onCreate();
        Typekit.getInstance()
                .addNormal(Typekit.createFromAsset(this, "bndohyeon_ttf.ttf"));
    }

}
