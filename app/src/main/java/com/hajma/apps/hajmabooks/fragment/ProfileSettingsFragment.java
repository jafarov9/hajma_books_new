package com.hajma.apps.hajmabooks.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.hajma.apps.hajmabooks.C;
import com.hajma.apps.hajmabooks.DataEvent;
import com.hajma.apps.hajmabooks.R;
import com.hajma.apps.hajmabooks.activity.HomeActivity;
import com.hajma.apps.hajmabooks.activity.LanguageActivity;
import com.hajma.apps.hajmabooks.api.MySingleton;
import com.hajma.apps.hajmabooks.api.retrofit.ApiUtils;
import com.hajma.apps.hajmabooks.api.retrofit.UserDAOInterface;
import com.hajma.apps.hajmabooks.model.LanguageApiModel;
import com.hajma.apps.hajmabooks.model.UserApiModel;
import com.hajma.apps.hajmabooks.util.ConvertUriToFile;
import com.hajma.apps.hajmabooks.util.LocaleHelper;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class ProfileSettingsFragment extends Fragment {

    private ImageButton imageButtonBackFromProfileSettings;
    private ImageView imgSettingsProfilePicture;
    private TextView txtChangeProfilePicture;
    private TextView txtUsernameSettings;
    private Button btnSaveProfileDetails;
    private Button btnLogOut;
    private Button btnChangePassword;
    private EditText etNameProfileSettings;
    private TextView txtEmailProfileSettings;
    private EditText etSettingsPhoneNumber;
    private UserApiModel user;
    private UserDAOInterface userDIF;
    private ArrayList<LanguageApiModel> languageList;
    private static int IMAGE_PICK_CODE = 1000;
    private static int PERMISSON_CODE = 1001;
    private String filePath = "";
    private File f;
    private String token;
    private SharedPreferences sharedPreferences;
    private int langID;
    private TextView txtSelectLanguage;
    private String language;

    public ProfileSettingsFragment(UserApiModel user) {
        this.user = user;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_setttings, container, false);

        language = LocaleHelper.getPersistedData(getActivity(), "az");

        if(language.equals("az")) {
            langID = C.LANGUAGE_AZ;
        } else if(language.equals("en")) {
            langID = C.LANGUAGE_EN;
        }else if(language.equals("ru")) {
            langID = C.LANGUAGE_RU;
        }else {
            langID = C.LANGUAGE_AZ;
        }


        userDIF = ApiUtils.getUserDAOInterface();

        sharedPreferences = getActivity().getSharedPreferences("usercontrol", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("token", null);

        //initialize variables
        txtSelectLanguage = view.findViewById(R.id.txtSelectLanguage);
        btnLogOut = view.findViewById(R.id.btnLogOut);
        btnSaveProfileDetails = view.findViewById(R.id.btnSaveProfileDetails);
        imageButtonBackFromProfileSettings = view.findViewById(R.id.imageButtonBackFromProfileSettings);
        imgSettingsProfilePicture = view.findViewById(R.id.imgSettingsProfilePicture);
        txtChangeProfilePicture  = view.findViewById(R.id.txtChangeProfilePicture);
        txtEmailProfileSettings = view.findViewById(R.id.txtEmailProfileSettings);
        txtUsernameSettings = view.findViewById(R.id.txtUsernameSettings);
        etNameProfileSettings = view.findViewById(R.id.etNameProfileSettings);
        etSettingsPhoneNumber = view.findViewById(R.id.etSettingsPhoneNumber);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentChangePassword frgChangePassword = new FragmentChangePassword();

                ((HomeActivity)getActivity()).loadFragment(frgChangePassword, "frgChangePassword");
            }
        });

        etSettingsPhoneNumber.setEnabled(false);

        txtSelectLanguage.setText(language.toUpperCase());

        txtSelectLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadLanguages();
            }
        });

        imageButtonBackFromProfileSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        txtChangeProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //check runtime permisson
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if(getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_DENIED) {
                        //permisson not granted, request it.
                        String permissons[] = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        //show popup for runtime permission
                        requestPermissions(permissons, PERMISSON_CODE);
                    }else {
                        //permisson already granted
                        pickImageFromGallery();
                    }
                } else {
                    //system os is less than marsmallow

                }


            }
        });


        loadUserDetails();

        btnSaveProfileDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();

            }
        });

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
            }
        });

        return view;
    }

    private void uploadImage() {

        if(!filePath.isEmpty()) {
            File f = new File(filePath);
            RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), f);
            MultipartBody.Part part = MultipartBody.Part.createFormData("image", f.getName(), requestBody);

            userDIF.postChangeProfilePicture(part, "Bearer "+ token).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                    if(response.isSuccessful()) {
                        EventBus.getDefault().post(new DataEvent.CallProfileDetailsUpdate(1));
                        getActivity().onBackPressed();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });

        }else {
            return;
        }

    }

    private void pickImageFromGallery() {
        //intent to pick image
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(resultCode == getActivity().RESULT_OK && requestCode == IMAGE_PICK_CODE) {

            //set image to imageView
            Log.e("zzzz", "Burdayam");

            Log.e("zzzz", data.getData().toString());
            imgSettingsProfilePicture.setImageURI(data.getData());

            f = ConvertUriToFile.convertImageUriToFile(data.getData(),getActivity());
            filePath = f.getAbsolutePath();

        }

    }

    //load user details
    private void loadUserDetails() {

        txtUsernameSettings.setText(user.getUsername());
        txtEmailProfileSettings.setText(user.getEmail());
        etNameProfileSettings.setText(user.getName());
        etSettingsPhoneNumber.setText(user.getMobile());
        etNameProfileSettings.setText(user.getName());

        if(!user.getProfile().equals("")) {
            Picasso.get()
                    .load(user.getProfile()
                            .replace("http:", "https:"))
                    .into(imgSettingsProfilePicture);
        }


    }

    //load languages
    private void loadLanguages() {

        LanguageDialogFragment dialogFragment = new LanguageDialogFragment();
        dialogFragment.show(getFragmentManager(), "language");

    }

    //logout method
    private void logOut() {
        /*userDIF.postLogout("Bearer "+ token).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {

                Log.e("rubu", "Onresponse");

                if(response.isSuccessful()) {

                    Log.e("rubu", "Success");


                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove("token");
                    editor.remove("logined");
                    editor.apply();
                    editor.commit();
                    restarttoLogin();
                }else {
                    try {
                        String err = response.errorBody().string();
                        Log.e("rubu", err);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });*/

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("token");
        editor.remove("logined");
        editor.apply();
        editor.commit();

        FirebaseMessaging.getInstance().unsubscribeFromTopic("hajmabooks");

        restarttoLogin();
    }

    private void restarttoLogin() {
        Log.e("zczc", "restart home");
        Intent intent = new Intent(getActivity(), LanguageActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == PERMISSON_CODE) {

            if(grantResults.length > 0 && grantResults[0] ==
            PackageManager.PERMISSION_GRANTED) {
                //permission was granted
                pickImageFromGallery();
            } else {
                //permission was denied
                Toast.makeText(getActivity(), "Permission denied..", Toast.LENGTH_SHORT).show();
            }

        }

    }
}
