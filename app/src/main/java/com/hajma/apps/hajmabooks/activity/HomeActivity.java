package com.hajma.apps.hajmabooks.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.hajma.apps.hajmabooks.BookEphermenalKeyProvider;
import com.hajma.apps.hajmabooks.DataEvent;
import com.hajma.apps.hajmabooks.R;
import com.hajma.apps.hajmabooks.Variables;
import com.hajma.apps.hajmabooks.fragment.CartFragmentContainer;
import com.hajma.apps.hajmabooks.fragment.CategoryFragmentContainer;
import com.hajma.apps.hajmabooks.fragment.DetailedBookFragment;
import com.hajma.apps.hajmabooks.fragment.FragmentHome;
import com.hajma.apps.hajmabooks.fragment.FragmentSearch;
import com.hajma.apps.hajmabooks.fragment.HomeFragmentContainer;
import com.hajma.apps.hajmabooks.fragment.ProfileFragmentContainer;
import com.hajma.apps.hajmabooks.fragment.SearchFragmentContainer;

import com.hajma.apps.hajmabooks.util.LocaleHelper;
import com.stripe.android.CustomerSession;
import com.stripe.android.PaymentSessionConfig;
import com.stripe.android.model.PaymentMethod;
import com.stripe.android.model.ShippingInformation;
import com.stripe.android.view.PaymentMethodsActivityStarter;
import com.stripe.android.view.ShippingInfoWidget;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    final Fragment homeContainer = new HomeFragmentContainer();
    final Fragment cartContainer = new CartFragmentContainer();
    final Fragment profileContainer = new ProfileFragmentContainer();
    final Fragment categoryContainer = new CategoryFragmentContainer();
    final Fragment searchContainer = new SearchFragmentContainer();
    private int currentNavPosition;
    private int paidType;

    public int getPaidType() {
        return paidType;
    }

    public void setPaidType(int paidType) {
        this.paidType = paidType;
    }

    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = null;

    private String token;
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);
        setContentView(R.layout.activity_home);




            FirebaseMessaging.getInstance().subscribeToTopic("hajmabooks")
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            String msg = "Subcribed";

                            if (!task.isSuccessful()) {
                                msg = "Subscribe failed";
                            }
                        }

                    });

        sharedPreferences = getSharedPreferences("usercontrol", MODE_PRIVATE);
        token = sharedPreferences.getString("token", null);


        if(token != null) {
            //setup stripe customer session

            if(isInternetAvialible()) {
                CustomerSession.initCustomerSession(
                        this,
                        new BookEphermenalKeyProvider(token)
                );
            }

        }

        currentNavPosition = 0;
        active = homeContainer;

        //Bottom navigation view initialize
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        bottomNavigationView.setSelectedItemId(R.id.nav_bottom_home);

        //initialize fragments
        fm.beginTransaction().add(R.id.fragment_container, categoryContainer, "categoryctnr").hide(categoryContainer).commit();
        fm.beginTransaction().add(R.id.fragment_container, profileContainer, "profilectnr").hide(profileContainer).commit();
        fm.beginTransaction().add(R.id.fragment_container,cartContainer, "cartctnr").hide(cartContainer).commit();
        fm.beginTransaction().add(R.id.fragment_container,searchContainer, "searchctnr").hide(searchContainer).commit();
        fm.beginTransaction().add(R.id.fragment_container,homeContainer, "homectnr").commit();


        if(getIntent() != null) {
            Intent notifyIntent = getIntent();
            String extras = notifyIntent.getStringExtra("key");
            int bookId = notifyIntent.getIntExtra("bookId", 0);

            if(extras != null && extras.equals("dtFrag")) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        DetailedBookFragment dtBookFrg = new DetailedBookFragment(1, bookId);
                        loadFragment(dtBookFrg, "dtBookFrg");
                    }
                }, 1000);

            }
        }


    }

    private boolean isInternetAvialible() {

        return Variables.isNetworkConnected;

    }


    //Bottom navigation listener
    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.nav_bottom_home :
                    currentNavPosition = 0;
                    fm.beginTransaction().hide(active).show(homeContainer).commit();
                    active = homeContainer;
                    return true;

                case R.id.nav_bottom_cart :
                    currentNavPosition = 1;
                    fm.beginTransaction().hide(active).show(cartContainer).commit();
                    active = cartContainer;
                    return true;

                case R.id.nav_bottom_category :
                    currentNavPosition = 2;
                    viewCategoryFragment();
                    return true;

                case R.id.nav_bottom_search :
                    currentNavPosition = 3;
                    fm.beginTransaction().hide(active).show(searchContainer).commit();
                    active = searchContainer;
                    return true;

                case R.id.nav_bottom_profile :
                    currentNavPosition = 4;
                    fm.beginTransaction().hide(active).show(profileContainer).commit();
                    active = profileContainer;
                    return true;
            }
            return true;
        };
    };

    public void viewCategoryFragment() {
        fm.beginTransaction().hide(active).show(categoryContainer).commit();
        active = categoryContainer;
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    //On view fragment event
    @Subscribe
    public void onCallViewFragment(DataEvent.CallViewFragment event) {
        if(event.getResponse() == 1) {
            currentNavPosition = 2;
            viewCategoryFragment();
            bottomNavigationView.setSelectedItemId(R.id.nav_bottom_category);
        }
    }

    @Override
    public void onBackPressed() {
        fm.executePendingTransactions();
        Log.e("zxzx", "ozu"+currentNavPosition);


        String tag = currentNavPosition == 0 ? "homectnr" :
                currentNavPosition == 1 ? "cartctnr" :
                        currentNavPosition == 2 ? "categoryctnr" :
                                currentNavPosition == 3 ? "searchctnr" :
                                        "profilectnr";

        Fragment f = fm.findFragmentByTag(tag);

        if(f != null) {

            FragmentManager fragmentManager = f.getChildFragmentManager();

            List<Fragment> childFragments = fragmentManager.getFragments();

            if(childFragments.size() == 1) {
                Log.e("zxzx", "size == 1");
                super.onBackPressed();
            } else {
                fragmentManager.popBackStack();
                Log.e("zxzx", "popbackstack");

            }

        }else {
            super.onBackPressed();
        }
    }

    public void loadFragment(Fragment fragment, String frtag) {

        String tag = currentNavPosition == 0 ? "homectnr" :
                currentNavPosition == 1 ? "cartctnr" :
                        currentNavPosition == 2 ? "categoryctnr" :
                                currentNavPosition == 3 ? "searchctnr" :
                                        "profilectnr";

        Fragment f = fm.findFragmentByTag(tag);

        if(f != null) {

            List<Fragment> parentFragments = fm.getFragments();

            for (int i = 0; i < parentFragments.size(); i++) {
                Fragment frag = parentFragments.get(i);

                // check if tabs switched
                if (frag.isVisible() && frag.getTag() != null && !frag.getTag().equals(f.getTag())) {

                    FragmentManager fmchild = f.getChildFragmentManager();

                    List<Fragment> childFragments = fmchild.getFragments();

                    getSupportFragmentManager().beginTransaction()
                            .hide(frag)
                            .show(f)
                            .commitAllowingStateLoss();

                    return;
                }
            }

            // same tab (no switch)
            if (fragment instanceof ProfileFragmentContainer || fragment instanceof CartFragmentContainer) {

                getSupportFragmentManager().beginTransaction()
                        .remove(f)
                        .add(R.id.frg_ctnr, fragment, frtag)
                        .commitAllowingStateLoss();

                return;
            }

            FragmentManager fmchild2 = f.getChildFragmentManager();

            List<Fragment> childFragments = fmchild2.getFragments();

            for (int i = 0; i < childFragments.size(); i++) {
                Fragment frag = childFragments.get(i);
                if (frag.isVisible()) {

                    if (fragment instanceof HomeFragmentContainer ||
                            fragment instanceof SearchFragmentContainer) {

                        if (childFragments.size() > 1) {
                            FragmentTransaction ft = fm.beginTransaction();
                            for (int j = 1; j < childFragments.size(); j++) {
                                ft.remove(childFragments.get(j));
                            }
                            ft.show(childFragments.get(0));
                            ft.commitAllowingStateLoss();
                        } else {
                            if (frag instanceof FragmentHome) {
                                FragmentHome homeFragment = (FragmentHome) frag;
                                //homeFragment.scrollToTop();
                            } else if (frag instanceof FragmentSearch) {
                                FragmentSearch searchFragment = (FragmentSearch) frag;
                                //searchFragment.scrollToTop();
                            }
                        }

                    } else {

                        fmchild2.beginTransaction()
                                .hide(frag)
                                .add(R.id.frg_ctnr, fragment, frtag)
                                .addToBackStack(frtag)
                                .commitAllowingStateLoss();
                    }

                    break;
                }
            }

        } else {
            List<Fragment> parentFragments = getSupportFragmentManager().getFragments();

            if (!parentFragments.isEmpty()) {
                for (int i = 0; i < parentFragments.size(); i++) {
                    Fragment frag = parentFragments.get(i);
                    if (frag.isVisible()) {

                        getSupportFragmentManager().beginTransaction()
                                .hide(frag)
                                .add(R.id.frg_ctnr, fragment, frtag)
                                .commitAllowingStateLoss();

                        break;
                    }
                }
            } else {

                String tag2 = fragment.getClass().getSimpleName();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.frg_ctnr, fragment, tag2)
                        .commitAllowingStateLoss();
            }

        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        Log.e("baxaqda", "OnactivityResult");


        if (requestCode == PaymentMethodsActivityStarter.REQUEST_CODE) {
            final PaymentMethodsActivityStarter.Result result =
                    PaymentMethodsActivityStarter.Result.fromIntent(data);
            final PaymentMethod paymentMethod = result != null ?
                    result.paymentMethod : null;




            // use paymentMethod
            if(paymentMethod != null) {

                Intent intent = new Intent(this, CheckOutActivity.class);
                intent.putExtra("paymentMethodId", paymentMethod.id);

                startActivity(intent);
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if(intent != null) {
            String extras = intent.getStringExtra("key");
            int bookId = intent.getIntExtra("bookId", 0);

            if(extras != null && extras.equals("dtFrag")) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        DetailedBookFragment dtBookFrg = new DetailedBookFragment(1, bookId);
                        loadFragment(dtBookFrg, "dtBookFrg");
                    }
                }, 700);

            }
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }
}
