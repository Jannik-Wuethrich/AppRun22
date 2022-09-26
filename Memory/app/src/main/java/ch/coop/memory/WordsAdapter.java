package ch.coop.memory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WordsAdapter extends
        RecyclerView.Adapter<WordsAdapter.ViewHolder> {

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        // public TextView nameTextView;
        public Button messageButton;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            //   nameTextView = (TextView) itemView.findViewById(R.id.message_button);
            messageButton = (Button) itemView.findViewById(R.id.message_button);
        }
    }

    private List<Word> words;

    public WordsAdapter(List<Word> wordsPar) {
        words = wordsPar;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View wordsView = inflater.inflate(R.layout.item_word, parent, false);
        ViewHolder viewHolder = new ViewHolder(wordsView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Word word = words.get(position);
        //  TextView textView = holder.nameTextView;
        Button button = holder.messageButton;
        button.setText(word.getWord());
    }

    @Override
    public int getItemCount() {
        return words.size();
    }
}