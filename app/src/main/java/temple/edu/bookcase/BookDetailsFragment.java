package temple.edu.bookcase;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class BookDetailsFragment extends Fragment {
    TextView textView;
    public static final String BOOK_KEY = "book";
    Book book;


    public BookDetailsFragment() {
        // Required empty public constructor
    }


    public static BookDetailsFragment newInstance(Book book) {
        BookDetailsFragment bookDetailsFragment = new BookDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable(BOOK_KEY, book);
        bookDetailsFragment.setArguments(args);
        return bookDetailsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            book = args.getParcelable(BOOK_KEY);
        }
    }
/*
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        textView = (TextView) inflater.inflate(R.layout.fragment_book_details, container, false);
        if (bookTitle != null) {
            displayBook(bookTitle);
        }
        return textView;
    }
    */


    // Public method for parent Activity to "talk" to BookDetailsFragment
    public void displayBook(String title) {
        textView.setGravity(Gravity.CENTER);
        textView.setText(title);
        textView.setTextSize(30);
    }
}