package codepathproject.nothinganswered.TinderExperiment;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.util.Log;

import java.io.IOException;

/**
 * Created by jnagaraj on 3/13/16.
 */
public class TinderVideo {

    private AssetFileDescriptor fileName;
    public String stringFileName;

    public TinderVideo(String path, Context context) {

        //stringFileName = "android.resource://" + context.getPackageName() + "/" + path;
        stringFileName = path;
        Log.i("VIDEO", stringFileName);

        try {
            this.fileName = context.getAssets().openFd(path);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public AssetFileDescriptor getItemDescripter(){
        if(fileName != null) {
            return fileName;

        }else{
            Log.i("PRINT", "Null");
            return null;
        }
    }

}
