package codepathproject.nothinganswered.models;

import android.graphics.drawable.Drawable;

/**
 * Created by rmukhedkar on 3/6/16.
 */
public class GaffeCardModel {

    private String  gaffeCardQuestion;
    private String gaffeCardVideoThumbnail;
    private Drawable gaffeCardProfilePictureUrl;
    private String gaffeCardProfileName;


    private OnCardDismissedListener mOnCardDismissedListener = null;

    private OnClickListener mOnClickListener = null;

    public interface OnCardDismissedListener {
        void onLike();
        void onDislike();
    }

    public interface OnClickListener {
        void OnClickListener();
    }


   public GaffeCardModel (String gaffeCardQuestion, String gaffeCardVideoThumbnail, Drawable gaffeCardProfilePictureUrl,
                          String gaffeCardProfileName)
   {
       this.gaffeCardQuestion = gaffeCardQuestion;
       this.gaffeCardVideoThumbnail = gaffeCardVideoThumbnail;
       this.gaffeCardProfilePictureUrl = gaffeCardProfilePictureUrl;
       this.gaffeCardProfileName = gaffeCardProfileName;
   }

    public String getGaffeCardQuestion() {
        return gaffeCardQuestion;
    }

    public void setGaffeCardQuestion(String gaffeCardQuestion) {
        this.gaffeCardQuestion = gaffeCardQuestion;
    }

    public String getGaffeCardVideoThumbnail() {
        return gaffeCardVideoThumbnail;
    }

    public void setGaffeCardVideoThumbnail(String gaffeCardVideoThumbnail) {
        this.gaffeCardVideoThumbnail = gaffeCardVideoThumbnail;
    }

    public Drawable getGaffeCardProfilePictureUrl() {
        return gaffeCardProfilePictureUrl;
    }

    public void setGaffeCardProfilePictureUrl(Drawable gaffeCardProfilePictureUrl) {
        this.gaffeCardProfilePictureUrl = gaffeCardProfilePictureUrl;
    }

    public String getGaffeCardProfileName() {
        return gaffeCardProfileName;
    }

    public void setGaffeCardProfileName(String gaffeCardProfileName) {
        this.gaffeCardProfileName = gaffeCardProfileName;
    }

    public OnCardDismissedListener getmOnCardDismissedListener() {
        return mOnCardDismissedListener;
    }

    public void setmOnCardDismissedListener(OnCardDismissedListener mOnCardDismissedListener) {
        this.mOnCardDismissedListener = mOnCardDismissedListener;
    }

    public OnClickListener getmOnClickListener() {
        return mOnClickListener;
    }

    public void setmOnClickListener(OnClickListener mOnClickListener) {
        this.mOnClickListener = mOnClickListener;
    }

    public void setOnCardDismissedListener( OnCardDismissedListener listener ) {
        this.mOnCardDismissedListener = listener;
    }

    public OnCardDismissedListener getOnCardDismissedListener() {
        return this.mOnCardDismissedListener;
    }


    public void setOnClickListener( OnClickListener listener ) {
        this.mOnClickListener = listener;
    }

    public OnClickListener getOnClickListener() {
        return this.mOnClickListener;
    }

}
