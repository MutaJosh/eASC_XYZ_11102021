package com.betterise.maladiecorona.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.betterise.maladiecorona.R;
import com.betterise.maladiecorona.model.Item_session;

import java.util.ArrayList;
public class ExampleAdapter extends RecyclerView.Adapter<ExampleAdapter.ExampleViewHolder> {
    private ArrayList<Item_session> mExampleList;
    private OnNoteListener mOnNoteListener;

    public static class ExampleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tv_Patientdata,tv_patientphone_number,tv_patientNID,tv_patientnames;
        OnNoteListener onNoteListener;
        public ExampleViewHolder(View itemView ,OnNoteListener onNoteListener) {
            super(itemView);
            tv_patientnames = itemView.findViewById(R.id.tv_patientnames);
            tv_patientphone_number=itemView.findViewById(R.id.tv_patientphone_number);
            tv_patientNID=itemView.findViewById(R.id.tv_patientNID);
            tv_Patientdata=itemView.findViewById(R.id.tv_Patientdata);

          this.onNoteListener=onNoteListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onNoteListener.onNoteClick(getAdapterPosition());

        }
    }
    public ExampleAdapter(ArrayList<Item_session> exampleList,OnNoteListener onNoteListener) {
        this.mExampleList = exampleList;
        this.mOnNoteListener=onNoteListener;
    }
    @Override
    public ExampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_test_sessions, parent, false);
        //ExampleViewHolder evh = new ExampleViewHolder(v);
        return new ExampleViewHolder(v,mOnNoteListener);
    }
    @Override
    public void onBindViewHolder(ExampleViewHolder holder, int position) {
        Item_session currentItem = mExampleList.get(position);
        holder.tv_patientnames.setText(currentItem.getFirstname()+" "+currentItem.getLastname());
        holder.tv_patientNID.setText(currentItem.getNational_ID());
        holder.tv_patientphone_number.setText(currentItem.getPatienttelephone());
        holder.tv_Patientdata.setText(currentItem.getDob()+currentItem.getPatientgender()+currentItem.getOccupation()
        +currentItem.getResidence()+currentItem.getNationality()+currentItem.getProvince()+currentItem.getDistrict()+currentItem.getSector()
        +currentItem.getCell()+currentItem.getVillage()+currentItem.getIndex()+currentItem.getCategory()+currentItem.getASCOV_diagnostic()+currentItem.getRdt_result());




    }
    @Override
    public int getItemCount() {
        return mExampleList.size();
    }

public  interface  OnNoteListener{
        void onNoteClick(int position);
}


}