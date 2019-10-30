    package temple.edu.bookcase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.content.res.Resources;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.Arrays;


    public class MainActivity extends AppCompatActivity implements BookListFragment.OnBookSelectedInterface {
        BookDetailsFragment bookDetailsFragment;
        ArrayList<String> books = new ArrayList<>();
        boolean singlePane;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            Resources res = getResources();
            books.addAll(Arrays.asList(res.getStringArray(R.array.books)));

            // Checking whether its landscape or portrait
            singlePane = (findViewById(R.id.fl_2) == null);

            Fragment container1Fragment = getSupportFragmentManager().findFragmentById(R.id.fl_1);

            if (container1Fragment == null && singlePane) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .addToBackStack(null)
                        .add(R.id.fl_1, new ViewPagerFragment())
                        .commit();
            } else if (container1Fragment instanceof BookListFragment && singlePane) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fl_1, new ViewPagerFragment())
                        .commit();
            } else {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fl_1, BookListFragment.newInstance(books))
                        .commit();
            }
        }

        @Override
        public void bookSelected(int position) {
            String bookTitle = books.get(position);

            bookDetailsFragment = new BookDetailsFragment();
            Bundle detailsBundle = new Bundle();
            detailsBundle.putString(BookDetailsFragment.BOOK_TITLE_ARGS, bookTitle);
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
