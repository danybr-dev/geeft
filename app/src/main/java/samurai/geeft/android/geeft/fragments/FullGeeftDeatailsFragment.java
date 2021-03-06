package samurai.geeft.android.geeft.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baasbox.android.BaasBox;
import com.baasbox.android.BaasHandler;
import com.baasbox.android.BaasLink;
import com.baasbox.android.BaasQuery;
import com.baasbox.android.BaasResult;
import com.baasbox.android.BaasUser;
import com.baasbox.android.RequestOptions;
import com.baasbox.android.Rest;
import com.baasbox.android.json.JsonObject;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.nvanbenschoten.motion.ParallaxImageView;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.activities.AddGeeftActivity;
import samurai.geeft.android.geeft.activities.AddStoryActivity;
import samurai.geeft.android.geeft.activities.DonatedActivity;
import samurai.geeft.android.geeft.activities.FullScreenImageActivity;
import samurai.geeft.android.geeft.activities.LoginActivity;
import samurai.geeft.android.geeft.activities.MainActivity;
import samurai.geeft.android.geeft.activities.ReceivedActivity;
import samurai.geeft.android.geeft.activities.ReservedActivity;
import samurai.geeft.android.geeft.database.BaaSDeleteGeeftTask;
import samurai.geeft.android.geeft.database.BaaSGeeftHistoryArrayTask;
import samurai.geeft.android.geeft.database.BaaSSignalisationTask;
import samurai.geeft.android.geeft.interfaces.TaskCallBackBooleanInt;
import samurai.geeft.android.geeft.interfaces.TaskCallbackBooleanToken;
import samurai.geeft.android.geeft.interfaces.TaskCallbackDeletion;
import samurai.geeft.android.geeft.models.Geeft;
import samurai.geeft.android.geeft.models.User;
import samurai.geeft.android.geeft.utilities.StatedFragment;
import samurai.geeft.android.geeft.utilities.TagsValue;

/**
 * Created by ugookeadu on 20/02/16.
 */
public class FullGeeftDeatailsFragment extends StatedFragment implements TaskCallBackBooleanInt
        , TaskCallbackBooleanToken,TaskCallbackDeletion, OnMapReadyCallback {

    private static final String KEY_CONTEXT = "key_context" ;
    private final String TAG = getClass().getSimpleName();
    public static final String GEEFT_KEY = "geeft_key";
    private Geeft mGeeft;
    private Toolbar mToolbar;
    private LinearLayout mGeefterProfileCard;
    private TextView mGeeftTimeAgo;
    private TextView mGeeftDeadline;
    private TextView mGeeftLocation;
    private TextView mGeeftChooseType;
    private TextView mGeeftComunicationAllowed;
    private ImageView mGeeftImageView;
    private ImageView mGeefterProfilePicImageView;
    private TextView mGeefterNameTextView;
    private RatingBar mGeefterRank;
    private TextView mGeeftTitleTextView;
    private TextView mGeeftDescriptionTextView;


    private LinearLayout mDonatedButtonField;
    private View mStoryView;
    private View mAddStoryView;

    private LinearLayout mReceivedButtonField;
    private View mDeleteView;
    private View mModifyView;
    private TextView mAssignView;
    private View mPrenoteView;
    private View mAssignedToView;
    private View mCardView;

    private View mDonateReceivedGeeftView;
    private List<Geeft> mGeeftList = new ArrayList<>();

    private ProgressDialog mProgressDialog;
    private TextView mProfileDialogUsername;
    private TextView mProfileDialogUserLocation;
    private ImageView mProfileDialogUserImage;
    private TextView mProfileDialogUserRank;
    private TextView mShowGeefterProfileTextView;

    private TextView mProfileDialogUserGiven;
    private TextView mProfileDialogUserReceived;
    private ImageButton mProfileDialogFbButton;
    private ParallaxImageView mProfileDialogBackground;
    private LayoutInflater inflater;
    private TextView mGeeftTitleInCard;
    private TextView mGeeftSize;
    private TextView mGeeftAssignedTo;

    private LatLng SYDNEY;
    List<Address> addresses;
    private static final double DEFAULT_RADIUS = 2000;
    private GoogleMap mMap;
    private int mStrokeColor;
    private int mFillColor;
    private List<DraggableCircle> mCircles = new ArrayList<DraggableCircle>(1);
    private SupportMapFragment mapFragment;
    private TextView mAssignTextView;
    private TextView mGeeftReservationNumber;
    private boolean mNotShowAssignButton;


    public static FullGeeftDeatailsFragment newInstance(Geeft geeft, String className) {
        FullGeeftDeatailsFragment fragment = new FullGeeftDeatailsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(GEEFT_KEY, geeft);
        bundle.putString(KEY_CONTEXT, className);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getChildFragmentManager().beginTransaction().remove(mapFragment);

        if (savedInstanceState==null) {
            mGeeft = (Geeft) getArguments().getSerializable(GEEFT_KEY);
        }
        inflater = LayoutInflater.from(getContext()); //prova
    }


    private static View rootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null)
                parent.removeView(rootView);
        }
        try {
            rootView = inflater.inflate(R.layout.fragment_geeft_deatails, container, false);
        } catch (InflateException e) {
        /* map is already there, just return view as it is */
        }
        initUI(rootView);
        if (savedInstanceState==null)
            initSupportActionBar(rootView);
        return rootView;
    }

    @Override
    protected void onSaveState(Bundle outState) {
        super.onSaveState(outState);
        // Save items for later restoring them on rotation
        outState.putSerializable(GEEFT_KEY, mGeeft);
    }

    @Override
    protected void onRestoreState(Bundle savedInstanceState) {
        super.onRestoreState(savedInstanceState);
        Log.d("OnRestore", savedInstanceState + "");
        if (savedInstanceState != null) {
            mGeeft = (Geeft)savedInstanceState.getSerializable(GEEFT_KEY);
            View rootView = getView();
            if (rootView!=null){
                initUI(rootView);
                initSupportActionBar(rootView);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.geeft_detail_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_segnalation:
                segnalateGeeft();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void segnalateGeeft() {
        final android.support.v7.app.AlertDialog.Builder alertDialog =
                new android.support.v7.app.AlertDialog.Builder(getContext(),
                        R.style.AppCompatAlertDialogStyle); //Read Update

        alertDialog.setPositiveButton(R.string.segnalate_dialog_positive_answer, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new BaaSSignalisationTask(getContext(), mGeeft.getId(),FullGeeftDeatailsFragment.this).execute();
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.setMessage(R.string.segnalate_dialog_message);
        android.support.v7.app.AlertDialog dialog = alertDialog.create();
        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
        dialog.show();
    }

    private void initUI(View rootView) {
        mapFragment = (SupportMapFragment)getChildFragmentManager()
                        .findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = new SupportMapFragment();
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.add(R.id.map, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);
        manageMapScroll(rootView);
        mGeeftImageView = (ImageView)rootView.findViewById(R.id.collapsing_toolbar_image);
        mGeefterProfilePicImageView = (ImageView)rootView.findViewById(R.id.geefter_profile_image);
        mGeefterProfileCard = (LinearLayout)rootView.findViewById(R.id.geeft_item_profile_card);
        mAssignedToView = (LinearLayout) rootView.findViewById(R.id.geeft_assigned_to_view);
        mGeeftTitleInCard = (TextView) rootView.findViewById(R.id.geeft_title);
        mGeeftReservationNumber = (TextView) rootView.findViewById(R.id.geeft_reservation_number);
        mGeeftTimeAgo = (TextView) rootView.findViewById(R.id.geeft_time_ago);
        mGeeftDeadline = (TextView) rootView.findViewById(R.id.geeft_deadline);
        mGeeftLocation = (TextView) rootView.findViewById(R.id.geeft_location);
        mGeeftChooseType = (TextView) rootView.findViewById(R.id.geeft_choose_type);
        mGeeftComunicationAllowed = (TextView) rootView.findViewById(R.id.geeft_comunication_allowed);
        mGeefterNameTextView = (TextView)rootView.findViewById(R.id.geefter_name);
        mGeefterRank = (RatingBar)rootView.findViewById(R.id.ratingBarSmall);
        mShowGeefterProfileTextView = (TextView) rootView.findViewById(R.id.show_profile);
        mGeeftTitleTextView = (TextView)rootView.findViewById(R.id.geeft_title_textview);
        mGeeftDescriptionTextView = (TextView)rootView
                .findViewById(R.id.geeft_description_textview);

        mDonatedButtonField = (LinearLayout)rootView.findViewById(R.id.fragment_details_donated_buttons_layout);
        mReceivedButtonField = (LinearLayout)rootView.findViewById(R.id.fragment_details_received_buttons_layout);
        mStoryView = rootView.findViewById(R.id.item_geeft_story);
        mPrenoteView = rootView.findViewById(R.id.prenote_geeft);
        mModifyView = rootView.findViewById(R.id.item_modify_geeft);
        mDeleteView = rootView.findViewById(R.id.item_delete_geeft);
        mAssignView = (TextView) rootView.findViewById(R.id.item_assign_geeft);
        mGeeftSize = (TextView) rootView.findViewById(R.id.geeft_size) ;
        mGeeftAssignedTo = (TextView) rootView.findViewById(R.id.geeft_assigned_to);
        mAddStoryView = rootView.findViewById(R.id.item_add_geeft_story);
        mDonateReceivedGeeftView = rootView.findViewById(R.id.item_donate_received_geeft);

        if(mGeeft!=null) {
            Picasso.with(getContext()).load(mGeeft.getGeeftImage())
                    .fit().centerInside().into(mGeeftImageView);
            /*Picasso.with(getContext()).load(Uri.parse(mGeeft.getUserProfilePic()))
                    .fit().centerInside().placeholder(R.drawable.ic_account_circle_black_24dp)
                    .into(mGeefterProfilePicImageView);*/  //Included in asynchrone call,setUserInformations()

            mGeefterNameTextView.setText(mGeeft.getUsername());
            //mGeeftTitleTextView.setText(mGeeft.getGeeftTitle());
            mGeeftDescriptionTextView.setText(mGeeft.getGeeftDescription());
            setGeeftDetails();
            setUserInformations();

            mGeeftImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<Geeft> geeftList = new ArrayList<>();
                    geeftList.add(mGeeft);
                    startImageGallery(geeftList);
                }
            });

            mGeefterProfileCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //initGeefterDialog(mGeeft);
                    mNotShowAssignButton = true;
                    showGeefterProfile();
                }
            });
            mStoryView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mGeeftList.size() == 1) {
                        new AlertDialog.Builder(getContext()).setTitle(R.string.ooops)
                                .setMessage(R.string.no_story_alert_dialog_message).show();
                    } else if (mGeeftList.size() > 1) {
                        startImageGallery(mGeeftList);
                    } else {
                        mProgressDialog = new ProgressDialog(getContext());
                        mProgressDialog.show();
                        mProgressDialog.setMessage("Attendere...");
                        new BaaSGeeftHistoryArrayTask(getContext(), mGeeftList,
                                mGeeft.getId(), "geeft", FullGeeftDeatailsFragment.this).execute();
                    }
                }
            });

            if(mGeeft.isAllowCommunication()){
                mShowGeefterProfileTextView.setText("Contatta utente");
            }
            else{
                mShowGeefterProfileTextView.setText("Visualizza profilo utente");
            }
            /*if(mGeeft.isAutomaticSelection()){ //if is automatic selection,geefter can't assign
                //mAssignView.setVisibility(View.GONE);
            }*/



            if(mGeeft.isAssigned()){ //If Geeft is assigned,is not possible to modify or delete it
                mDonatedButtonField.setVisibility(View.GONE);
//                mModifyView.setVisibility(View.GONE);
//                mDeleteView.setVisibility(View.GONE);
            }

            if(!getArguments().getSerializable(KEY_CONTEXT)
                    .equals(DonatedActivity.class.getSimpleName())){//TODO: Check this,and put it up
                mDonatedButtonField.setVisibility(View.GONE);
//                mModifyView.setVisibility(View.GONE);
//                mDeleteView.setVisibility(View.GONE);
            }

            if(!getArguments().getSerializable(KEY_CONTEXT)
                    .equals(ReceivedActivity.class.getSimpleName())){//TODO: Check this,and put it up
                mReceivedButtonField.setVisibility(View.GONE);
                if(mGeeft.isAutomaticSelection()){ //if is automatic selection,geefter can't assign
                    mAssignView.setText("Lista\nprenotati");
                }
            }

            mPrenoteView.setVisibility(View.GONE);
            if(getArguments().getSerializable(KEY_CONTEXT)
                    .equals(ReservedActivity.class.getSimpleName())) {
                //showPrenoteViewIfGeeftReserved(); it is useless in this context
                mPrenoteView.setVisibility(View.VISIBLE);
                mPrenoteView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteReservation();
                    }
                });
            }

            if(getArguments().getSerializable(KEY_CONTEXT)
                    .equals(DonatedActivity.class.getSimpleName())){
                if(mGeeft.isAssigned()) {
                    mAssignedToView.setVisibility(View.VISIBLE);
                    //TODO:FILL AssignedTo TextView
                    //mGeeftAssignedTo.setText(getAssignedNameUser);

                }
            }
            mModifyView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final android.support.v7.app.AlertDialog.Builder builder =
                            new android.support.v7.app.AlertDialog.Builder(getContext(),
                                    R.style.AppCompatAlertDialogStyle); //Read Update
                    builder.setTitle("Avviso");
                    builder.setMessage("Vuoi veramente modificare il geeft?");
                    builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startAddGeeftActivity(mGeeft);
                            getActivity().finish();
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();


                }
            });
            mDeleteView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(getContext(),
                            R.style.AppCompatAlertDialogStyle)
                            .setTitle("Attenzione")
                            .setMessage("Sei sicuro di voler cancellare l'annuncio?")
                            .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteGeeft();
                                }
                            })
                            .setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                }
            });



            mAssignView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment fragment = AssignUserListFragment.newInstance(mGeeft);
                    FragmentTransaction transaction = getActivity()
                            .getSupportFragmentManager().beginTransaction();
                    transaction.addToBackStack(null);
                    transaction.replace(R.id.fragment_container, fragment);
                    transaction.commit();

                }
            });

            mDonateReceivedGeeftView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startAddGeeftActivity(mGeeft);
                }
            });

            mAddStoryView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startAddStoryActivity();
                }
            });
        }
    }

    private void showGeefterProfile() {
        showProgressDialog();
        BaasUser.fetch(mGeeft.getBaasboxUsername(), new BaasHandler<BaasUser>() {
            @Override
            public void handle(BaasResult<BaasUser> baasResult) {
                if(baasResult.isSuccess()){
                    User user = fillUser(baasResult.value());
                    if(mProgressDialog != null)
                        mProgressDialog.dismiss();
                    boolean isCurrentUser = false;
                    if(BaasUser.current()!=null){
                       isCurrentUser = user.getID().equals(BaasUser.current().getName());
                    }
                    boolean  allowComunication;
                    if(BaasUser.current()!= null && BaasUser.current().getName().equals(user.getID())){
                        allowComunication=false;
                    }else {
                        allowComunication = mGeeft.isAllowCommunication();
                    }
                    startUserProfileFragment(user,isCurrentUser,allowComunication,mNotShowAssignButton); //@Nullable User user,
                    // showProfile, allowComunication)
                }if (baasResult.isFailed()){
                    showAlertDialog();
                }
            }
        });

    }

    private User fillUser(BaasUser baasUser) {

        User user = new User(baasUser.getName());
        JsonObject registeredFields = baasUser.getScope(BaasUser.Scope.REGISTERED);

        String username = registeredFields.getString("username");
        String description = registeredFields.getString("user_description");
        String docId = registeredFields.getString("doc_id");
        String email = registeredFields.getString("email");
        double userRank = registeredFields.get("feedback");
        user.setProfilePic(baasUser.getScope(BaasUser.Scope.REGISTERED).getString("profilePic"));
        if(registeredFields.getObject("_social").getObject("facebook") == null)
            user.setFbID("");
        else
            user.setFbID(registeredFields.getObject("_social").getObject("facebook").getString("id"));
        user.setUsername(username);
        user.setDescription(description);
        user.setDocId(docId);
        user.setRank(userRank);
        user.setEmail(email == null ? "" : email);

        return user;
    }

    private void startUserProfileFragment(User user, boolean isCurrentUser,boolean allowComunication, boolean notShowAssignButton) {
        FragmentTransaction transaction = getActivity()
                .getSupportFragmentManager().beginTransaction();
        transaction.addToBackStack(null);
        FragmentManager fm = getActivity().getSupportFragmentManager();
        Fragment fragment = UserProfileFragment.newInstance(user,isCurrentUser,allowComunication,
                notShowAssignButton,false); //false is for reassign
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    private void showAlertDialog() {
        if (mProgressDialog != null)
            mProgressDialog.dismiss();
        if(getContext()!=null) {
            new AlertDialog.Builder(getContext(),
                    R.style.AppCompatAlertDialogStyle)
                    .setTitle("Riprovare")
                    .setMessage("Connessione lenta.")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //startMainActivity();
                        }
                    }).show();
        }
    }


    private void showPrenoteViewIfGeeftReserved() { //It is for GeeftItemAdapter,but not implemented yet
        String docId = BaasUser.current().getScope(BaasUser.Scope.PRIVATE).getString("doc_id");
         //retrieve doc_is attached at user
        //find all links with the doc_id (User id <--> doc id )
        BaasQuery.Criteria query = BaasQuery.builder().where("in.id like '" + docId + "'" +
        "and out.id = '" + mGeeft.getId() + "'").criteria();
        BaasLink.fetchAll(TagsValue.LINK_NAME_RESERVE, query, RequestOptions.DEFAULT,
                new BaasHandler<List<BaasLink>>() {
                    @Override
                    public void handle(BaasResult<List<BaasLink>> baasResult) {
                        if (baasResult.isSuccess()) {
                            List<BaasLink> links = baasResult.value();
                            Log.d(TAG, "Size of links reserved (expected 1) are: " + links.size());
                            if (links.size() > 0)
                                mPrenoteView.setVisibility(View.VISIBLE);
                        } else {
                            Log.e("LOG", "Error", baasResult.error());
                        }
                    }
                });
    }

    private void deleteReservation() {
        //TODO: Create Was_Reserved link
        mProgressDialog = new ProgressDialog(getContext());
        try {
//                    mProgress.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mProgressDialog.show();
        } catch (WindowManager.BadTokenException e) {
        }
        mProgressDialog.setCancelable(false);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Operazione in corso...");

        String docId = BaasUser.current().getScope(BaasUser.Scope.PRIVATE).getString("doc_id");
        //retrieve doc_is attached at user
        //find all links with the doc_id (User id <--> doc id )
        BaasQuery.Criteria query = BaasQuery.builder().where("in.id like '" + docId + "'" +
                "and out.id = '" + mGeeft.getId() + "'").criteria();
        BaasLink.fetchAll(TagsValue.LINK_NAME_RESERVE, query, RequestOptions.DEFAULT,
                new BaasHandler<List<BaasLink>>() {
                    @Override
                    public void handle(BaasResult<List<BaasLink>> baasResult) {
                        if (baasResult.isSuccess()) {
                            List<BaasLink> links = baasResult.value();
                            int links_size = links.size();
                            Log.d(TAG, "Size of links reserved (expected 1) are: " + links_size);
                            if (links_size > 0) {
                                for (BaasLink link : links) {
                                    BaasLink.withId(link.getId()).delete(RequestOptions.DEFAULT
                                            , new BaasHandler<Void>() {
                                        @Override
                                        public void handle(BaasResult<Void> baasResult) {
                                            if (baasResult.isSuccess()) {
                                                if (mProgressDialog != null)
                                                    mProgressDialog.dismiss();
                                                showSuccessDialog();
                                                mPrenoteView.setVisibility(View.GONE);
                                            } else {
                                                if (mProgressDialog != null)
                                                    mProgressDialog.dismiss();
                                                Toast.makeText(getContext(),
                                                        "E' accaduto un errore,riprovare più tardi",
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                }
                            } else {
                                Toast.makeText(getContext(),
                                        "E' accaduto un errore,riprovare più tardi",
                                        Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Log.e("LOG", "Error", baasResult.error());
                        }
                    }
                });
    }

    private void showSuccessDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Successo")
                .setMessage("Operazione effettuata con successo.")
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        startMainActivity();
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        startMainActivity();
                    }
                })
                .show();
    }

    private void setUserInformations() {
        BaasUser.fetch(mGeeft.getBaasboxUsername(), new BaasHandler<BaasUser>() {
            @Override
            public void handle(BaasResult<BaasUser> res) {
                if (res.isSuccess()) {
                    BaasUser user = res.value();
                    Log.d("LOG", "The user: " + user);
                    float rankToset;
                    double l = user.getScope(BaasUser.Scope.REGISTERED).get("feedback");
                    Object o = l;
                    double rank = (double)l;
                    rankToset = getRoundedRank(rank);

                    String profilePic = user.getScope(BaasUser.Scope.REGISTERED).getString("profilePic");

                    mGeefterRank.setRating(rankToset);
                    Picasso.with(getContext()).load(Uri.parse(profilePic))
                            .fit().centerInside().placeholder(R.drawable.ic_account_circle_black_24dp)
                            .into(mGeefterProfilePicImageView);

                    //mGeefterNameTextView.setText(username);

                } else {
                    Log.e("LOG", "Error", res.error());
                }
            }
        });
    }

    private void getUserFeedback(String username){
        BaasUser.fetch(username, new BaasHandler<BaasUser>() {
            @Override
            public void handle(BaasResult<BaasUser> res) {
                if (res.isSuccess()) {
                    BaasUser user = res.value();
                    Log.d("LOG", "The user: " + user);
                    double rank = user.getScope(BaasUser.Scope.REGISTERED).get("feedback");
                    float rankToset = getRoundedRank(rank);
                    mGeefterRank.setRating(rankToset);
                } else {
                    Log.e("LOG", "Error", res.error());
                }
            }
        });
    }

    private void setUserInformationDialog(String username){
        BaasUser.fetch(username, new BaasHandler<BaasUser>() {
            @Override
            public void handle(BaasResult<BaasUser> res) {
                if (res.isSuccess()) {
                    BaasUser user = res.value();
                    Log.d("LOG", "The user: " + user);
                    double rank = user.getScope(BaasUser.Scope.REGISTERED).get("feedback");
                    long given = user.getScope(BaasUser.Scope.REGISTERED).get("n_given");
                    long received = user.getScope(BaasUser.Scope.REGISTERED).get("n_received");

                    mProfileDialogUserRank.setText("" + new DecimalFormat("#.##").format(rank) + "/5.0");
                    mProfileDialogUserGiven.setText("" + given);
                    mProfileDialogUserReceived.setText("" + received);

                } else {
                    Log.e("LOG", "Error", res.error());
                }
            }
        });
    }
    private float getRoundedRank(double rank) {

        float iPart;
        double fPart;

        iPart = (long) rank;
        fPart = rank - iPart;
        if(fPart > 0.49){
            return ++iPart;
        }
        else{
            return iPart;
        }
    }

    private void startAddGeeftActivity(Geeft geeft){
        Intent intent = AddGeeftActivity.newIntent(getContext(),geeft, true);
        startActivity(intent);
        getActivity().finish();
    }

    private void startAddStoryActivity(){ //TODO: FILL STORY ACTIVITY WITH GEEFT
        Intent intent = new Intent(getContext(),AddStoryActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    private void deleteGeeft(){
        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mProgressDialog.setMessage("Eliminazione in corso...");
        mProgressDialog.show();

        new BaaSDeleteGeeftTask(getContext(),mGeeft,FullGeeftDeatailsFragment.this).execute();

    }

    private void startLoginActivity() {
        getContext().startActivity(new Intent(getContext(), LoginActivity.class));
        getActivity().finish();
    }

    private void startMainActivity() {
        getContext().startActivity(new Intent(getContext(), MainActivity.class));
        getActivity().finish();
    }


    private void startImageGallery(List<Geeft> geeftList) {
        Intent intent =
                FullScreenImageActivity.newIntent(getContext(), geeftList,0);
        startActivity(intent);
    }

    private void initSupportActionBar(View rootView) {
        mToolbar = (Toolbar)rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
        android.support.v7.app.ActionBar actionBar = ((AppCompatActivity)getActivity())
                .getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(mGeeft.getGeeftTitle());
    }


    private void initGeefterDialog(final Geeft geeft){
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(getContext()); //Read Update
        View dialogLayout = inflater.inflate(R.layout.profile_dialog, null);
        alertDialog.setView(dialogLayout);
        //On click, the user visualize can visualize some infos about the geefter
        android.app.AlertDialog dialog = alertDialog.create();

        //profile dialog fields-----------------------
        mProfileDialogUsername = (TextView) dialogLayout.findViewById(R.id.dialog_geefter_name);
        mProfileDialogUserLocation = (TextView) dialogLayout.findViewById(R.id.dialog_geefter_location);
        mProfileDialogUserImage = (ImageView) dialogLayout.findViewById(R.id.dialog_geefter_profile_image);

        mProfileDialogUserRank = (TextView) dialogLayout.findViewById(R.id.dialog_ranking_score);
        mProfileDialogUserGiven = (TextView) dialogLayout.findViewById(R.id.dialog_given_geeft);
        mProfileDialogUserReceived = (TextView) dialogLayout.findViewById(R.id.dialog_received_geeft);
        mProfileDialogFbButton = (ImageButton) dialogLayout.findViewById(R.id.dialog_geefter_facebook_button);

        //--------------------------------------------
        mProfileDialogUsername
                .setText(geeft
                        .getUsername());
        mProfileDialogBackground = (ParallaxImageView) dialogLayout.findViewById
                (R.id.dialog_geefter_background);
        //--------------------------------------------
        mProfileDialogUsername
                .setText(geeft
                        .getUsername());
        mProfileDialogUserLocation.setText(geeft.getUserLocation());
        Picasso.with(getContext()).load(Uri.parse(geeft.getUserProfilePic())).fit()
                .centerInside()
                .into(mProfileDialogUserImage);

        //Show Facebook profile of geefter------------------------
        if(geeft.isAllowCommunication()){
            mProfileDialogFbButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent facebookIntent = getOpenFacebookProfileIntent(getContext(),geeft.getUserFbId());
                    getContext().startActivity(facebookIntent);
                }
            });
        }
        else{
            mProfileDialogFbButton.setVisibility(View.GONE);
        }

        //Parallax background -------------------------------------
        mProfileDialogBackground.setTiltSensitivity(5);
        mProfileDialogBackground.registerSensorManager();
        mProfileDialogBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uriUrl = Uri.parse(TagsValue.WEBSITE_URL);
                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                getContext().startActivity(launchBrowser);
            }
        });
        //new BaaSGetGeefterInformation(getContext(),FullGeeftDeatailsFragment.this).execute();

        //-------------------------
        setUserInformationDialog(mGeeft.getBaasboxUsername());

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations = R.style.profile_info_dialog_animation;
        dialog.show();  //<-- See This!

    }

    private void setGeeftDetails() {
        // Converting timestamp into x ago format
        //CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(mGeeft.getCreationTime(),
        //        System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
        mGeeftTitleInCard.setText(mGeeft.getGeeftTitle());
        setNumberOfReservationText(); //this set text in mGeeftReservationNumber
        mGeeftTimeAgo.setText(convertTimestamp(mGeeft.getCreationTime()/1000));
        mGeeftDeadline.setText(convertTimestamp(mGeeft.getDeadLine()));

        mGeeftLocation.setText(mGeeft.getUserLocation() +"," + mGeeft.getUserCap());

        if(mGeeft.isAutomaticSelection()){
            mGeeftChooseType.setText("Automatica");
        }
        else{
            mGeeftChooseType.setText("Manuale");
        }
        if(mGeeft.isAllowCommunication()){
            mGeeftComunicationAllowed.setText("Si");
        }
        else{
            mGeeftComunicationAllowed.setText("No");
        }
        if (mGeeft.isDimensionRead()){
            mGeeftSize.setText(mGeeft.getGeeftHeight()
                    +" x "+mGeeft.getGeeftWidth()+" x "+mGeeft.getGeeftDepth());
        }
    }

    private void setNumberOfReservationText() {
        BaasQuery.Criteria query = BaasQuery.builder().where("out.id like '" + mGeeft.getId() + "'" )
                .criteria();
        BaasLink.fetchAll(TagsValue.LINK_NAME_RESERVE, query, RequestOptions.DEFAULT, new BaasHandler<List<BaasLink>>() {
            @Override
            public void handle(BaasResult<List<BaasLink>> resReservationLinks) {
                if(resReservationLinks.isSuccess()){
                    List<BaasLink> reservationLinks = resReservationLinks.value();
                    mGeeftReservationNumber.setText(reservationLinks.size()+"");
                }
            }
        });

    }

    private String convertTimestamp(long timestamp) {
        String date = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(timestamp*1000);
        return date;
    }

    @Override
    public void done(boolean result, int token) {
        if(mProgressDialog!=null){
            mProgressDialog.dismiss();
        }
        if(result) {
            if (mGeeftList.size()<2){
                new AlertDialog.Builder(getContext()).setTitle(R.string.ooops)
                        .setMessage(R.string.no_story_alert_dialog_message).show();
            }
            else {
                startImageGallery(mGeeftList);
            }
        }else {
            new AlertDialog.Builder(getContext())
                    .setTitle("Errore")
                    .setMessage("Operazione non possibile. Riprovare più tardi.").show();
        }
    }
    @Override
    public void doneDeletion(boolean result,int token) {
        if(mProgressDialog!=null){
            mProgressDialog.dismiss();
        }
        if(result) {
            new AlertDialog.Builder(getContext(), R.style.AppCompatAlertDialogStyle)
                    .setTitle("Successo")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startMainActivity();
                            getActivity().finish();
                        }
                    })
                    .setMessage("Il Geeft è stato eliminato con successo.").show();
        }else {
            new AlertDialog.Builder(getContext(), R.style.AppCompatAlertDialogStyle)
                    .setTitle("Errore")
                    .setMessage("Operazione non possibile. Riprovare più tardi.").show();
        }
    }

    public void done(boolean result,int action,String docId){ //This is for signalisation button!
        // action_i with i={1,2,3}
        if(result) {
            switch (action) {
                case 1:
                    if (docId==null){
                        Toast.makeText(getContext(),
                                "C'è stato un errore nella segnalazione", Toast.LENGTH_LONG).show();
                    }else {
                        //I'm registered user
                        sendEmail(docId);
                    }
                    break;
                case 2: //document is already deleted by BaaSSignalisationTask, I'm a moderator
                    Toast.makeText(getContext(),"Documento eliminato con successo",Toast.LENGTH_LONG).show();
                    sendPushToSgravone();
                    startMainActivity();
                    break;
                default:
                    Toast.makeText(getContext(),"C'è stato un errore nella segnalazione",Toast.LENGTH_LONG).show();
                    break;
            }
        }
        else{
            Toast.makeText(getContext(), "C'è stato un errore nella segnalazione", Toast.LENGTH_LONG).show();
        }
    }

    private void sendPushToSgravone() {
        String receiverUsername = mGeeft.getBaasboxUsername();
        String message = "Il tuo annuncio " + mGeeft.getGeeftTitle() + " e' stato cancellato a " +
                "seguito di varie segnalazioni";
        sendPush(receiverUsername,message);
    }

    private void sendEmail(String docId){
        BaasUser currentUser = BaasUser.current();
        //final Intent emailIntent = new Intent(android.content.Intent.ACTION_SENDTO);
        //ACTION_SENDTO is filtered,but my list is empty
        final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("plain/text");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { "geeft.app@gmail.com" });
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Segnalazione oggetto: "
                + docId);
        //Name is added in e-mail for debugging,TODO: delete
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "User: " + currentUser.getName() +
                " \n" + "E' presente un Geeft non conforme al regolamento. " + "\n"
                + "ID: " + docId);
        getContext().startActivity(Intent.createChooser(emailIntent, "Invia mail..."));
    }

    public Intent getOpenFacebookProfileIntent(Context context,String userFacebookId) { // THIS
        // create a intent to user's facebook profile
        try {
            int versionCode = context.getPackageManager().getPackageInfo("com.facebook.katana", 0).versionCode;
            Log.d(TAG,"UserFacebookId is: " + userFacebookId);
            if(versionCode >= 3002850) {
                Uri uri = Uri.parse("fb://facewebmodal/f?href=https://www.facebook.com/" + userFacebookId);
                return  new Intent(Intent.ACTION_VIEW, uri);
            }
            else {
                Uri uri = Uri.parse("fb://page/" + userFacebookId);
                return  new Intent(Intent.ACTION_VIEW, uri);

            }
        } catch (Exception e) {
            Log.d(TAG,"profileDialogFbButton i'm in catch!!");
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/" + userFacebookId));
        }
    }
    private void showProgressDialog() {
        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setCancelable(false);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Attendere");
        mProgressDialog.show();
    }
    private void sendPush(final String receiverUsername,String message){

        BaasBox.rest().async(Rest.Method.GET, "plugin/push.send?receiverName=" + receiverUsername
                + "&message=" + message.replace(" ","%20"),
                new BaasHandler<JsonObject>() {
            @Override
            public void handle(BaasResult<JsonObject> baasResult) {
                if(baasResult.isSuccess()){
                    Log.d(TAG,"Push notification sended to: " + receiverUsername);
                }
                else{
                    Log.e(TAG,"Error while sending push notification:" + baasResult.error());
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        if(isNetworkConnected()) {
            // Override the default content description on the view, for accessibility mode.
            map.setContentDescription(getString(R.string.map_circle_description));
            mFillColor = Color.parseColor("#704879C3");
            mStrokeColor = Color.parseColor("#FF4879C3");
            addresses = getLocationFromAddress(mGeeft.getUserLocation()+" "+mGeeft.getUserCap());
            // Move the map so that it is centered on the initial circle
            if (addresses != null && mMap!=null) {
                SYDNEY = new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
                mMap.addCircle(new CircleOptions()
                        .center(SYDNEY)
                        .radius(DEFAULT_RADIUS)
                        .strokeColor(mStrokeColor)
                        .fillColor(mFillColor));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SYDNEY, 11.0f));
            }
        }else{
            mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
        }
    }

    private void manageMapScroll(View rootView){
        final NestedScrollView mainScrollView = (NestedScrollView) rootView.findViewById(R.id.scroll);
        ImageView transparentImageView = (ImageView) rootView.findViewById(R.id.transparent_image);

        transparentImageView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        mainScrollView.requestDisallowInterceptTouchEvent(true);
                        // Disable touch on transparent view
                        return false;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        mainScrollView.requestDisallowInterceptTouchEvent(false);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        mainScrollView.requestDisallowInterceptTouchEvent(true);
                        return false;

                    default:
                        return true;
                }
            }
        });
    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm =
                (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }


    public List<Address> getLocationFromAddress(String strAddress){

        Geocoder coder = new Geocoder(getContext());
        List<Address> address;

        try {
            address = coder.getFromLocationName(strAddress,5);
            if (address==null || address.size() == 0) {
                return null;
            }
            Address location=address.get(0);
            location.getLatitude();
            location.getLongitude();

            return address;
        } catch (IOException e) {
            Log.d(TAG,e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private class DraggableCircle {

        //private final Marker centerMarker;

        //private final Marker radiusMarker;

        private final Circle circle;

        private double radius;

        public DraggableCircle(LatLng center, double radius) {
            this.radius = radius;
            circle = mMap.addCircle(new CircleOptions()
                    .center(center)
                    .radius(radius)
                    .strokeColor(mStrokeColor)
                    .fillColor(mFillColor));
        }
    }
}