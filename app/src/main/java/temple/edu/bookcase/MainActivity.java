package temple.edu.bookcase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;


    public class MainActivity extends AppCompatActivity implements BookListFragment.OnBookSelectedInterface {
        BookDetailsFragment bookDetailsFragment;
        Fragment fl1;
        Fragment fl2;
        ArrayList<Book> books;
        Button searchBtn;
        EditText searchInput;
        String searchQuery = "";
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
                        Book newBook = new Book(
                                bookObject.getInt("book_id"),
                                bookObject.getString("title"),
                                bookObject.getString("author"),
                                bookObject.getString("duration"),
                                bookObject.getInt("published"),
                                bookObject.getString("cover_url"));
                        // Add newBook to ArrayList<Book>
                        books.add(newBook);
                    }

                    fl1 = getSupportFragmentManager().findFragmentById(R.id.fl_1); // get reference to fragment currently in container_1
                    fl2 = getSupportFragmentManager().findFragmentById(R.id.fl_2); // get reference to fragment currently in container_1
                    singlePane = (findViewById(R.id.fl_2) == null);

                    Fragment fl1 = getSupportFragmentManager().findFragmentById(R.id.fl_1);

                    if (fl1 == null && singlePane) {
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fl_1, ViewPagerFragment.newInstance(books))
                                .commit();
                    } else if (fl1 == null) {
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fl_1, BookListFragment.newInstance(books))
                                .commit();
                    } else if (fl1 instanceof ViewPagerFragment && !searchQuery.equals("")) {
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fl_1, ViewPagerFragment.newInstance(books))
                                .commit();
                    } else if (fl1 instanceof BookListFragment && !searchQuery.equals("")) {
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fl_1, BookListFragment.newInstance(books))
                                .commit();
                    } else if (searchQuery.equals("") && ((fl1 instanceof ViewPagerFragment))) {
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fl_1, ViewPagerFragment.newInstance(books))
                                .commit();
                    } else if (searchQuery.equals("") && ((fl1 instanceof BookListFragment))) {
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

            searchInput = findViewById(R.id.searchInput);
            searchBtn = findViewById(R.id.searchBtn);

            fl1 = getSupportFragmentManager().findFragmentById(R.id.fl_1);
            fl2 = getSupportFragmentManager().findFragmentById(R.id.fl_2);
            singlePane = (findViewById(R.id.fl_2) == null);

            if (fl1 == null && fl2 == null) {
                fetchBooks(null);
            }

            if (fl1 instanceof BookListFragment && singlePane) {
                if (fl1 != null && ((BookListFragment) fl1).getBooks() != null) {
                    books = ((BookListFragment) fl1).getBooks();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fl_1, ViewPagerFragment.newInstance(books))
                            .commit();
                }
            } else if (fl1 instanceof ViewPagerFragment && !singlePane) {
                if (fl1 != null && ((ViewPagerFragment) fl1).getBooks() != null) {
                    books = ((ViewPagerFragment) fl1).getBooks();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fl_1, BookListFragment.newInstance(books))
                            .commit();
                }
            } else if (fl1 instanceof BookListFragment) {
                if (fl1 != null && ((BookListFragment) fl1).getBooks() != null) {
                    books = ((BookListFragment) fl1).getBooks();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fl_1, BookListFragment.newInstance(books))
                            .commit();
                }
            }

            searchBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    searchQuery = searchInput.getText().toString();
                    fl2 = getSupportFragmentManager().findFragmentById(R.id.fl_2); // get reference to fragment currently in container_1=
                    if (fl2 != null) {
                        getSupportFragmentManager()
                                .beginTransaction()
                                .remove(fl2)
                                .commit();
                    }
                    // Do query for new books
                    fetchBooks(searchQuery);
                }
            });
        }

        /* Fetches books */
        public void fetchBooks(final String searchString) {
            if (searchString == null || searchString.length() == 0) { // if searchQuery is null or a user has deleted all entered text and hit search again, fetch all books
                // Fetch books via API and add them all or some if query provided to ArrayList<Book> books
                new Thread() {
                    @Override
                    public void run() {
                        URL url = null;
                        try {
                            url = new URL("https://kamorris.com/lab/audlib/booksearch.php");
                            Log.d("No search query entered. URL is: ", url.toString());
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
            } else {
                // Fetch books via API and add them all or some if query provided to ArrayList<Book> books
                new Thread() {
                    @Override
                    public void run() {
                        URL url = null;
                        try {
                            url = new URL("https://kamorris.com/lab/audlib/booksearch.php?search=" + searchString);
                            Log.d("Search URL is: ", url.toString());
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
                        .replace(R.id.fl_2, bookDetailsFragment)
                        .commit();
            }
        }
    }
