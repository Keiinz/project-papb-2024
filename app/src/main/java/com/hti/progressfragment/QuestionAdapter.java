package com.hti.progressfragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder> {

    private List<Question> questionList;
    private OnDeleteClickListener deleteClickListener;

    public QuestionAdapter(List<Question> questionList) {
        this.questionList = questionList;
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_question, parent, false);
        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
        Question question = questionList.get(position);
        holder.questionText.setText(question.getQuestion());
        holder.option1.setText(question.getOption1());
        holder.option2.setText(question.getOption2());
        holder.option3.setText(question.getOption3());
        holder.option4.setText(question.getOption4());

        // Tombol Delete
        holder.deleteButton.setOnClickListener(v -> {
            if (deleteClickListener != null) {
                deleteClickListener.onDeleteClick(question);
            }
        });
    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }

    // Perbarui data
    public void updateData(List<Question> newQuestions) {
        this.questionList = newQuestions;
        notifyDataSetChanged();
    }

    public void setDeleteClickListener(OnDeleteClickListener listener) {
        this.deleteClickListener = listener;
    }

    // Interface untuk Delete
    public interface OnDeleteClickListener {
        void onDeleteClick(Question question);
    }

    static class QuestionViewHolder extends RecyclerView.ViewHolder {

        TextView questionText, option1, option2, option3, option4;
        Button deleteButton;

        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            questionText = itemView.findViewById(R.id.textQuestion);
            option1 = itemView.findViewById(R.id.textOption1);
            option2 = itemView.findViewById(R.id.textOption2);
            option3 = itemView.findViewById(R.id.textOption3);
            option4 = itemView.findViewById(R.id.textOption4);
            deleteButton = itemView.findViewById(R.id.buttonDelete);
        }
    }
}
