    package temple.edu.bookcase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;


    public class MainActivity extends AppCompatActivity implements BookListFragment.OnBookSelectedInterface {
        BookDetailsFragment bookDetailsFragment;
        ArrayList<Book> books;
        boolean singlePane;

        Handler booksHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                // Response to process from worker thread, in this case a list of books to parse
                try {
                    JSONArray booksArray = new JSONArray(msg.obj.toString());
                    books = new ArrayList<>();
                    for (int i = 0; i < booksArray.length(); i++) {
                        // Get book at index
                        JSONObject bookObject = booksArray.getJSONObject(i);
                        // Create Book using JSON data
                        Book newBook = new Book(bookObject.getInt("book_id"), bookObject.getString("title"), bookObject.getString("author"),
                                                bookObject.getString("time"), bookObject.getInt("published"), bookObject.getString("cover_url"));
                        // Add newBook to ArrayList<Book>
                        books.add(newBook);
                        Log.d("Adding book: ", newBook.toString());
                    }

                    singlePane = (findViewById(R.id.fl_2) == null);

                    Fragment container1Fragment = getSupportFragmentManager().findFragmentById(R.id.fl_1);

                    if (container1Fragment == null && singlePane) { // if container_1 has no Fragment already attached to it and we're in singlePane
                        // Attach ViewPagerFragment
                        getSupportFragmentManager()
                                .beginTransaction()
                                .addToBackStack(null)
                                .add(R.id.fl_1, ViewPagerFragment.newInstance(books))
                                .commit();
                    } else if (container1Fragment instanceof BookListFragment && singlePane) { // if container1Fragment is a BookListFragment, meaning we're coming back to singlePane from landscape mode
                        // Attach ViewPagerFragment
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fl_1, ViewPagerFragment.newInstance(books))
                                .commit();
                    } else { // it's not singlePane or its null
                        // Attach BookListFragment
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fl_1, BookListFragment.newInstance(books))
                                .commit();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return true;
            }
        });

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            Resources res = getResources();
            //books.addAll(Arrays.asList(res.getStringArray(R.array.books)));
            new Thread() {
                @Override
                public void run() {
                    URL url = null;
                    try {
                        // Using NBA API
                        url = new URL("https://kamorris.com/lab/audlib/booksearch.php");
                        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                        StringBuilder builder = new StringBuilder(); // StringBuilder, keep adding on bits of a string
                        String response;
                        while ((response = reader.readLine()) != null) {
                            builder.append(response);
                        }
                        // Need to use Handler
                        Message msg = Message.obtain();
                        msg.obj = builder.toString(); // gives you string created from StringBuilder object
                        booksHandler.sendMessage(msg);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }



        @Override
        public void bookSelected(int position) {
            Book book = books.get(position);

            bookDetailsFragment = new BookDetailsFragment();
            Bundle detailsBundle = new Bundle();
            detailsBundle.putParcelable(BookDetailsFragment.BOOK_KEY, book);
            bookDetailsFragment.setArguments(detailsBundle);

            if (!singlePane) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.fl_2, bookDetailsFragment)
                        .commit();
            }
        }
    }
