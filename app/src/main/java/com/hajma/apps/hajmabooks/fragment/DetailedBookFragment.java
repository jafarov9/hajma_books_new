package com.hajma.apps.hajmabooks.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.folioreader.FolioReader;
import com.folioreader.model.locators.ReadLocator;
import com.folioreader.util.ReadLocatorListener;
import com.hajma.apps.hajmabooks.C;
import com.hajma.apps.hajmabooks.DataEvent;
import com.hajma.apps.hajmabooks.R;
import com.hajma.apps.hajmabooks.activity.AudioPlayerActivity;
import com.hajma.apps.hajmabooks.activity.HomeActivity;
import com.hajma.apps.hajmabooks.adapter.NewBooksAdapter;
import com.hajma.apps.hajmabooks.api.retrofit.ApiUtils;
import com.hajma.apps.hajmabooks.api.retrofit.UserDAOInterface;
import com.hajma.apps.hajmabooks.data.AppProvider;
import com.hajma.apps.hajmabooks.model.AuthorApiModel;
import com.hajma.apps.hajmabooks.model.BookApiModel;
import com.hajma.apps.hajmabooks.model.BookFileModel;
import com.hajma.apps.hajmabooks.model.CategoryApiModel;
import com.hajma.apps.hajmabooks.model.DetailedBookApiModel;
import com.hajma.apps.hajmabooks.model.SoundApiModel;
import com.hajma.apps.hajmabooks.util.LocaleHelper;
import com.hajma.apps.hajmabooks.util.SimpleErrorDialog;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailedBookFragment extends Fragment implements View.OnClickListener {

    private static final int PERMISSION_STORAGE_CODE = 1000;
    private ImageButton imageButtonBack;
    private ImageView imgDetailedBook;
    private TextView txtDetailedBookName;
    private TextView txtDetailedBookAuthor;
    private Button btnDetailedAddToCard;
    private Button btnDetailedBuy;
    private ImageButton imageButtonDetailedGift;
    private ImageButton imageButtonDetailedMore;
    private ProgressBar pbLoadingProcess;
    private RecyclerView rvMoreBooksThan;
    private ArrayList<BookApiModel> moreBooksList;
    private NewBooksAdapter moreBooksAdapter;

    private TextView txtDetailedPublisherDescription;
    private TextView txtDetailedGenre, txtDetailedReleased,
            txtDetailedBookLength, txtDetailedBookSeller,
            txtDetailedBookLanguage;

    private UserDAOInterface userDIF;
    private String token;
    private int langID;
    private int bookID;
    private int type;
    private ArrayList<CategoryApiModel> bookCategories = new ArrayList<>();
    private ArrayList<AuthorApiModel> bookAuthors = new ArrayList<>();
    private ArrayList<SoundApiModel> soundList = new ArrayList<>();
    private DetailedBookApiModel dtBook;
    private boolean isPayed;
    private boolean isSound;
    private static Uri CONTENT_URI = AppProvider.CONTENT_URI_BOOKS;
    private String fullPathBook;

    public DetailedBookFragment(int langID, int bookID) {
        this.langID = langID;
        this.bookID = bookID;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_detailed_book, container, false);

        String language = LocaleHelper.getPersistedData(getActivity(), "az");

        if(language.equals("az")) {
            langID = C.LANGUAGE_AZ;
        } else if(language.equals("en")) {
            langID = C.LANGUAGE_EN;
        }else if(language.equals("ru")) {
            langID = C.LANGUAGE_RU;
        }else {
            langID = C.LANGUAGE_AZ;
        }


        token = getActivity()
                .getSharedPreferences("usercontrol", Context.MODE_PRIVATE)
                .getString("token", null);


        userDIF = ApiUtils.getUserDAOInterface();

        //initialize variables
        pbLoadingProcess = view.findViewById(R.id.pbLoadingProcess);
        pbLoadingProcess.setIndeterminate(true);


        imageButtonBack = view.findViewById(R.id.imageButtonBack);
        btnDetailedAddToCard = view.findViewById(R.id.btnDetailedAddToCart);
        btnDetailedAddToCard.setOnClickListener(this);
        btnDetailedBuy = view.findViewById(R.id.btnDetailedBookBuy);
        btnDetailedBuy.setOnClickListener(this);



        imageButtonDetailedGift = view.findViewById(R.id.imageButtonDetailedGift);
        imageButtonDetailedMore = view.findViewById(R.id.imageButtonDetailedMore);
        imageButtonDetailedMore.setOnClickListener(this);
        imageButtonDetailedGift.setOnClickListener(this);

        rvMoreBooksThan = view.findViewById(R.id.rvDetailedMoreBooksThan);
        setRecyclerViewBestSellerBooks();


        imgDetailedBook = view.findViewById(R.id.imgDetailedBookImage);
        txtDetailedBookName = view.findViewById(R.id.txtDetailedBookName);
        txtDetailedBookAuthor = view.findViewById(R.id.txtDetailedBookAuthor);
        txtDetailedGenre = view.findViewById(R.id.txtDetailedGenre);
        txtDetailedReleased = view.findViewById(R.id.txtDetailedReleased);
        txtDetailedBookSeller = view.findViewById(R.id.txtDetailedBookSeller);
        txtDetailedPublisherDescription = view.findViewById(R.id.txtDetailedPublisherDescription);
        txtDetailedBookLength = view.findViewById(R.id.txtDetailedBookLength);
        txtDetailedBookLanguage = view.findViewById(R.id.txtDetailedBookLanguage);




        //set click listener to back button
        imageButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });


        loadDetailedBook(langID, bookID);

        return view;
    }

    private void loadDetailedBook(int langID, int bookID) {

        RequestBody bodyLangId = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(langID));
        RequestBody bodyBookId = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(bookID));


        userDIF.postDetailedBook(bodyLangId, bodyBookId, "Bearer "+token).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if(response.isSuccessful()) {



                    try {
                        String s = response.body().string();
                        Log.e("birde", s);

                        JSONObject jsonObject = new JSONObject(s).getJSONObject("success");

                        dtBook = new DetailedBookApiModel();
                        dtBook.setId(jsonObject.getInt("id"));
                        dtBook.setImage(jsonObject.getString("image"));
                        dtBook.setName(jsonObject.getString("name"));
                        dtBook.setContent(jsonObject.getString("content"));
                        dtBook.setEpub(jsonObject.getString("epub"));
                        dtBook.setPage_count(jsonObject.getInt("page_count"));
                        dtBook.setHas_sound(jsonObject.getBoolean("has_sound"));
                        dtBook.setPrice(jsonObject.getString("price"));
                        dtBook.setSeller(jsonObject.getString("seller"));
                        dtBook.setYear(jsonObject.getInt("year"));
                        dtBook.setBook_language(jsonObject.getString("book_language"));

                        Log.e("ahaa", dtBook.getEpub());

                        //get book authors
                        JSONArray authors = jsonObject.getJSONArray("authors");
                        for(int i = 0;i < authors.length();i++) {

                            JSONObject a = authors.getJSONObject(i);
                            AuthorApiModel author = new AuthorApiModel();
                            author.setId(a.getInt("id"));
                            author.setName(a.getString("name"));

                            bookAuthors.add(author);
                        }

                        //get book categories
                        JSONArray categories = jsonObject.getJSONArray("categries");
                        for(int i = 0;i < categories.length();i++) {

                            JSONObject c = categories.getJSONObject(i);
                            CategoryApiModel category = new CategoryApiModel();
                            category.setId(c.getInt("id"));
                            category.setName(c.getString("name"));

                            bookCategories.add(category);
                        }

                        JSONObject relatedbookOj = jsonObject.getJSONObject("related_books");
                        JSONArray jsonArrayRelatedBooks = relatedbookOj.getJSONArray("books");

                        //add related books list to books
                        for(int i = 0;i < jsonArrayRelatedBooks.length();i++) {

                            JSONObject j = jsonArrayRelatedBooks.getJSONObject(i);
                            BookApiModel book = new BookApiModel();
                            book.setId(j.getInt("id"));
                            book.setCover(j.getString("cover"));
                            book.setName(j.getString("name"));
                            book.setSound_count(j.getInt("sound_count"));
                            book.setPageCount(j.getInt("page_count"));
                            book.setPrice(j.getString("price"));
                            book.setYear(j.getInt("year"));
                            moreBooksList.add(book);
                        }

                        JSONArray sounds = jsonObject.getJSONArray("sounds");

                        if(sounds.length() > 1) {
                            for(int i = 0;i < sounds.length();i++) {

                                JSONObject j = sounds.getJSONObject(i);

                                SoundApiModel sound = new SoundApiModel();

                                sound.setId(j.getInt("id"));
                                sound.setTitle(j.getString("title"));
                                sound.setSound(j.getString("sound"));

                                soundList.add(sound);
                            }
                        }

                        moreBooksAdapter.notifyDataSetChanged();
                        loadViews();

                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }


                }else {
                    try {
                        Log.e("birde", response.errorBody().string());
                        Log.e("birde", token);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

    }

    //set recyclerview best seller books
    private void setRecyclerViewBestSellerBooks() {
        moreBooksList = new ArrayList<>();
        moreBooksAdapter = new NewBooksAdapter(getActivity(), moreBooksList);
        RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(getActivity());
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rvMoreBooksThan.setLayoutManager(layoutManager2);
        rvMoreBooksThan.setAdapter(moreBooksAdapter);
    }

    private void loadViews() {

        Picasso.get()
                .load(dtBook.getImage()
                        .replace("http:", "https:"))
                .into(imgDetailedBook);

        txtDetailedBookName.setText(dtBook.getName());
        StringBuilder str = new StringBuilder();
        for(int i = 0;i < bookAuthors.size(); i++) {
            str.append(bookAuthors.get(i).getName() + "\n");
        }

        StringBuilder str2 = new StringBuilder();
        for(int i = 0;i < bookCategories.size(); i++) {
            str2.append(bookCategories.get(i).getName() + "\n");
        }

        txtDetailedGenre.setText(str2.toString());
        txtDetailedBookAuthor.setText(txtDetailedBookAuthor.getText() + str.toString());
        txtDetailedBookLanguage.setText(dtBook.getBook_language());
        txtDetailedBookSeller.setText(dtBook.getSeller());
        txtDetailedBookLength.setText(""+dtBook.getPage_count());
        txtDetailedPublisherDescription.setText(dtBook.getContent());
        txtDetailedReleased.setText(""+dtBook.getYear());

        if(!dtBook.getEpub().equals("") || dtBook.getPrice().equals("0.00")) {
            isPayed = true;

            if(dtBook.isHas_sound()) {
                isSound = true;
                btnDetailedBuy.setText(getResources().getString(R.string._listen));
                btnDetailedBuy.setBackgroundResource(R.drawable.btn_free_background);
            }else if(bookisExist() != null) {

                btnDetailedBuy.setText(getResources().getString(R.string._read));
                btnDetailedBuy.setBackgroundResource(R.drawable.btn_read_background);

            }else {

                btnDetailedBuy.setText(getResources().getString(R.string._download));
                btnDetailedBuy.setBackgroundResource(R.drawable.btn_free_background);

            }

        }else {
            btnDetailedBuy.setText(btnDetailedBuy.getText() + dtBook.getPrice());
        }

        txtDetailedBookLanguage.setText(dtBook.getBook_language());


        pbLoadingProcess.setIndeterminate(false);
        pbLoadingProcess.setVisibility(View.GONE);
        btnDetailedBuy.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {


        if(v.getId() == R.id.btnDetailedBookBuy) {

            if(isPayed) {

                if (isSound) {
                    Intent intent = new Intent(getContext(), AudioPlayerActivity.class);
                    intent.putExtra("cover", dtBook.getImage());
                    intent.putExtra("name", dtBook.getName());
                    intent.putParcelableArrayListExtra("sounds", soundList);
                    startActivity(intent);
                } else {

                    BookFileModel currentBook = bookisExist();

                    if (currentBook == null) {
                        checkPermissionDownloading();
                    } else {
                        openBook(currentBook.getPath(), currentBook.getLocation());
                    }
                }

            }else {

                float price = Float.valueOf(dtBook.getPrice());

                PaymentFragment paymentFragment = new PaymentFragment(price, bookID, null, C.PAID_TYPE_SINGLE, 0);
                ((HomeActivity) getActivity()).loadFragment(paymentFragment, "singlePayment");
            }
        }

        if(v.getId() == R.id.btnDetailedAddToCart) {
            addToCard();
        }

        if(v.getId() == R.id.imageButtonDetailedGift) {

            FragmentGiftPersons frgGiftPersons = new FragmentGiftPersons(dtBook);
            ((HomeActivity)getActivity()).loadFragment(frgGiftPersons, "frgGiftPersons");
        }

        if(v.getId() == R.id.imageButtonDetailedMore) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT,dtBook.getName());
            String extraText = "www.hajmabooks.com/bookdetails/" + String.valueOf(bookID);
            shareIntent.putExtra(Intent.EXTRA_TEXT, extraText);
            shareIntent.setType("text/plain");
            startActivity(shareIntent);
        }

    }

    private void addToCard() {

        RequestBody bookIDBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(dtBook.getId()));

        userDIF.postAddToCard(bookIDBody, "Bearer "+token).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()) {
                    String message = getResources().getString(R.string._added_to_cart);
                    openDialog(message);
                    EventBus.getDefault().post(new DataEvent.CallCartUpdate(1));
                }else {
                    try {
                        String errorBody = response.errorBody().string();
                        JSONObject error = new JSONObject(errorBody).getJSONObject("error");
                        openDialog(error.getString("message"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                String err = getResources().getString(R.string.check_your_internet_connection);
                openDialog(err);
            }
        });

    }

    private void openDialog(String message) {
        SimpleErrorDialog errorDialog = new SimpleErrorDialog(message, C.ALERT_TYPE_NOT);
        errorDialog.show(getFragmentManager(), "dlg");
    }


    private void openBook(String path, String location) {

        FolioReader folioReader = FolioReader.get();

        folioReader.setReadLocatorListener(new ReadLocatorListener() {
            @Override
            public void saveReadLocator(ReadLocator readLocator) {
                //You can save this last read position in your local or remote db
                updateLastLocationCurrentBook(readLocator.toJson());
            }
        });


        if(!location.equals("")) {
            ReadLocator readLocator = ReadLocator.fromJson(location);
            folioReader.setReadLocator(readLocator);
        }

        folioReader.openBook(path);

    }

    private void updateLastLocationCurrentBook(String location) {

        ContentValues values = new ContentValues();
        values.put("locator", location);

        getActivity()
                .getContentResolver()
                .update(CONTENT_URI, values, "bookId = ?", new String[] {String.valueOf(bookID)});


    }

    private void checkPermissionDownloading() {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if(getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED) {

                //permission denied, request it
                String permissions[] = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

                //show pop runtime permission
                requestPermissions(permissions, PERMISSION_STORAGE_CODE);
            }else {
                //permission already granted
                downloadBook();
            }

        }else {
            //system os is less than marsmollow
            downloadBook();
        }


    }

    private void downloadBook() {

        btnDetailedBuy.setVisibility(View.INVISIBLE);
        pbLoadingProcess.setVisibility(View.VISIBLE);
        pbLoadingProcess.setIndeterminate(true);





        String epub = dtBook.getEpub();

        //create download request
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(epub));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);

        request.setTitle("Download");
        request.setDescription(dtBook.getName());
        request.allowScanningByMediaScanner();

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);

        String basePath = getActivity().getExternalFilesDir(null).getPath();
        String bookName = "book"+ dtBook.getId() + ".epub";

        fullPathBook = basePath + "/"+ bookName;

        request.setDestinationInExternalFilesDir(getContext(), null,bookName);

        DownloadManager manager = (DownloadManager)getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);



    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(onComplete);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case PERMISSION_STORAGE_CODE : {

                if(grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                    //permission granted from popup, start download
                    downloadBook();
                }else {
                    //permission denied from popup, show error message
                    Toast.makeText(getContext(), "Permission denied!!!!", Toast.LENGTH_LONG).show();
                }

            }
        }
    }

    private BroadcastReceiver onComplete=new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {

            insertBookToDatabase();
            addSingleBookToMyBooks(bookID);

            pbLoadingProcess.setVisibility(View.GONE);
            pbLoadingProcess.setIndeterminate(false);

            btnDetailedBuy.setBackgroundResource(R.drawable.btn_read_background);
            btnDetailedBuy.setText(getResources().getString(R.string._read));

            btnDetailedBuy.setVisibility(View.VISIBLE);



        }
    };

    //insert downloaded book path database
    private void insertBookToDatabase() {

        ContentValues values = new ContentValues();
        values.put("bookId", bookID);
        values.put("path", fullPathBook);
        values.put("locator", "");

        getActivity()
                .getContentResolver()
                .insert(CONTENT_URI, values);


    }

    //Book is exist control
    private BookFileModel bookisExist() {

        String bookPath;
        String location;
        Cursor c = null;

        String[] projection = {"path", "locator"};
        String selection = "bookId = ?";
        String selectionArgs[] = {String.valueOf(bookID)};
        c = getActivity()
                .getContentResolver()
                .query(CONTENT_URI, projection, selection, selectionArgs, null, null);

        if(c != null && c.getCount() >  0) {

            c.moveToNext();
            bookPath = c.getString(c.getColumnIndex("path"));
            location = c.getString(c.getColumnIndex("locator"));
            return new BookFileModel(bookPath, location);

        } else {
            return null;
        }
    }

    private void addSingleBookToMyBooks(int bookID) {
        RequestBody bookIdBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(bookID));
        RequestBody toUserIdBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(0));


        userDIF.addSingleBookToMyBooksOrGift(bookIdBody, toUserIdBody, "Bearer "+token).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String message = getResources().getString(R.string._added_to_my_books);
                openDialogSimple(message, C.ALERT_TYPE_PAYMENT_NOTIFY);
                EventBus.getDefault().postSticky(new DataEvent.CallProfileDetailsUpdate(1));
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                String err = getResources().getString(R.string.check_your_internet_connection);
                openDialogSimple(err, C.ALERT_TYPE_LOGIN_ERROR);
            }
        });
    }

    private void openDialogSimple(String message, int type) {
        SimpleErrorDialog errorDialog = new SimpleErrorDialog(message, type);
        assert getFragmentManager() != null;
        errorDialog.show(getFragmentManager(), "dlg");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        FolioReader.clear();
    }
}
