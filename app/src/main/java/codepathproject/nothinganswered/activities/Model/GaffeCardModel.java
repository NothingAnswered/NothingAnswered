package codepathproject.nothinganswered.activities.Model;

/**
 * Created by rmukhedkar on 3/6/16.
 */
public class GaffeCardModel {

    private String  gaffeCardQuestion;
    private String  gaffeCardVideoUrl;
    private String gaffeCardProfilePictureUrl;
    private String gaffeCardDescription;


    private OnCardDismissedListener mOnCardDismissedListener = null;

    private OnClickListener mOnClickListener = null;

    public interface OnCardDismissedListener {
        void onLike();
        void onDislike();
    }

    public interface OnClickListener {
        void OnClickListener();
    }


   public GaffeCardModel (String gaffeCardQuestion, String gaffeCardVideoUrl, String gaffeCardProfilePictureUrl,
                          String gaffeCardDescription)
   {
       this.gaffeCardDescription = gaffeCardDescription;
       this.gaffeCardVideoUrl = gaffeCardVideoUrl;
       this.gaffeCardProfilePictureUrl = gaffeCardProfilePictureUrl;
       this.gaffeCardDescription = gaffeCardDescription;
   }

    public String getGaffeCardQuestion() {

        return gaffeCardQuestion;
    }

    public void setGaffeCardQuestion(String gaffeCardQuestion) {
        this.gaffeCardQuestion = gaffeCardQuestion;
    }

    public String getGaffeCardVideo() {
        return gaffeCardVideoUrl;
    }

    public void setGaffeCardVideo(String gaffeCardVideoUrl) {
        this.gaffeCardVideoUrl = gaffeCardVideoUrl;
    }

    public String getGaffeCardProfilePictureUrl() {
        return gaffeCardProfilePictureUrl;
    }

    public void setGaffeCardProfilePictureUrl(String gaffeCardProfilePictureUrl) {
        this.gaffeCardProfilePictureUrl = gaffeCardProfilePictureUrl;
    }

    public String getGaffeCardDescription() {
        return gaffeCardDescription;
    }

    public void setGaffeCardDescription(String gaffeCardDescription) {
        this.gaffeCardDescription = gaffeCardDescription;
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
