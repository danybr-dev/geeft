package samurai.geeft.android.geeft.database;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.baasbox.android.BaasDocument;
import com.baasbox.android.BaasFile;
import com.baasbox.android.BaasLink;
import com.baasbox.android.BaasResult;
import com.baasbox.android.BaasUser;
import com.baasbox.android.Grant;
import com.baasbox.android.Role;
import com.baasbox.android.json.JsonObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import samurai.geeft.android.geeft.interfaces.TaskCallbackBoolean;
import samurai.geeft.android.geeft.models.Geeft;

/**
 * Created by danybr-dev on 31/01/16.
 */

public class BaaSUploadGeeft extends AsyncTask<Void,Void,Boolean> {

    private final String TAG = "BaaSUploadGeeft";
    Context mContext;
    Geeft mGeeft;
    String mOldGeeftId;
    TaskCallbackBoolean mCallback;

    /**
     * Constructor to create an object Geeft to send to Baasbox TODO: add the field 'expiration time'
     **/
    public BaaSUploadGeeft(Context context, Geeft geeft,
                           TaskCallbackBoolean callback) {
        mContext = context;
        mGeeft = geeft;
        mCallback = callback;
        Log.d(TAG, "Lanciato AsyncTask");
    }

    public BaaSUploadGeeft(Context context, Geeft geeft, String id,
                           TaskCallbackBoolean callback) {
        mContext = context;
        mGeeft = geeft;
        mCallback = callback;
        mOldGeeftId = id;
        Log.d(TAG, "Lanciato AsyncTask");
    }


    @Override
    protected Boolean doInBackground(Void... arg0) {
        if(BaasUser.current() !=null) {
            String userFbId = BaasUser.current().getScope(BaasUser.Scope.REGISTERED).getObject("_social")
                    .getObject("facebook")
                    .get("id").toString();
            BaasDocument doc = new BaasDocument("geeft");
            doc.put("title", mGeeft.getGeeftTitle());
            doc.put("description", mGeeft.getGeeftDescription());
            doc.put("location", mGeeft.getUserLocation());
            doc.put("close", false);
            doc.put("cap", mGeeft.getUserCap());
            doc.put("name", getFacebookName());
            doc.put("userFbId",userFbId);
            doc.put("profilePic", getProfilePicFacebook());
           doc.put("deadline", mGeeft.getDeadLine()); // is timestamp in long
            doc.put("category", mGeeft.getCategory().toLowerCase());
            // send the field fo allow communication and automatic selection; remember to manage them
            // in the BassReserveTask
            doc.put("automaticSelection", mGeeft.isAutomaticSelection());
            doc.put("allowCommunication", mGeeft.isAllowCommunication());
            BaasFile image = new BaasFile();
            BaasResult<BaasFile> resImage = image.uploadSync(mGeeft.getStreamImage());
            if (resImage.isSuccess()) {
                Log.d(TAG, "Image uploaded");
                BaasResult<Void> resGrant = image.grantAllSync(Grant.READ, Role.REGISTERED);
                if (resGrant.isSuccess()) {
                    Log.d(TAG, "Granted");
                    doc.put("image", getImageUrl(image));// Now imageUrl is cutted,append in
                    // another class your session token
                    //Retrieve the link at image and put in doc,then save the doc and return true
                    BaasResult<BaasDocument> resDoc = doc.saveSync();
                    if (resDoc.isSuccess()) {
                        mGeeft.setId(doc.getId());
                        Log.d(TAG, "Doc saved with success");
                        BaasResult<Void> resDocGrant = doc.grantAllSync(Grant.READ, Role.REGISTERED);
                        if (resDocGrant.isSuccess()) {
                            Log.d(TAG, "Doc granted with success");
                            if(mOldGeeftId != null) {
                                Log.d(TAG,"OLD GEEFT ID: "+mOldGeeftId);
                                BaasResult<BaasLink> resLink = BaasLink.createSync("geeft_story",mGeeft.getId()
                                        , mOldGeeftId);
                                return resLink.isSuccess();
                            }
                            return true;
                        } else {
                            Log.e(TAG, "Error with grant of doc");
                            return false;
                        }
                    } else {
                        Log.e(TAG, "Error with doc");
                        return false;
                    }
                } else {
                    Log.e(TAG, "Fatal error grant");
                    return false;
                }
            } else {
                Log.e(TAG, "Fatal error upload:" + resImage.error());
                return false;
            }
        }
        else{
            return false;
        }
    }

    private String getFacebookName(){// return display name of user's profile
        String FbName = BaasUser.current().getScope(BaasUser.Scope.PRIVATE).getString("name");
        Log.d(TAG, "FB_Name is: " + FbName);
        return FbName;
    }
    private String getProfilePicFacebook(){ // return link of user's profile picture
        JsonObject field = BaasUser.current().getScope(BaasUser.Scope.REGISTERED);
        String id = field.getObject("_social").getObject("facebook").getString("id");
        Log.d(TAG, "FB_id is: " + id);
        return "https://graph.facebook.com/" + id + "/picture?type=large";
    }

    private String getImageUrl(BaasFile image){
        String streamUri = image.getStreamUri().toString();
        String temp[] = streamUri.split("=");
        StringBuilder stbuild = new StringBuilder("");
        stbuild.append(temp[1]).append("=");
        return stbuild.toString();
    }

    @Override
    protected void onPostExecute(Boolean result) {
        mCallback.done(result);
    }
    public String getDeadlineTimestamp(int expTime){ // I know,there is a delay between creation and upload time of document,
        //so we have a not matching timestamp (deadline and REAL deadline
        // calculated like creation data + exptime in days)
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Calendar c = Calendar.getInstance();
        c.setTime(new Date()); // Now use today date.
        c.add(Calendar.DATE, expTime); // Adding "expTime" days
        //String deadline = sdf.format(c.getTime()); //return Date,not timestamp.
        String deadline = ""+ c.getTimeInMillis()/1000; //get timestamp
        Log.d(TAG, "deadline is:" + deadline); //DELETE THIS AFTER DEBUG
        return deadline;
    }
}


