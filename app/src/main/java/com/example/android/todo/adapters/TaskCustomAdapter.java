package com.example.android.todo.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.todo.R;
import com.example.android.todo.data.TaskContract;
import com.example.android.todo.data.TaskDbHelper;

public class TaskCustomAdapter extends RecyclerView.Adapter<TaskCustomAdapter.TaskViewHolder>  {

private  static final String LOG_TAG=TaskCustomAdapter.class.getSimpleName();
    private Cursor mCursor;
    private Context mContext;
   private TaskDbHelper helper;
    private  int valGlobal;






  OnItemClickListener mItemClickListener;

    public interface OnItemClickListener {

        void OnItemClick(View view, int position);

    }


    public TaskCustomAdapter(Context mContext) {
        this.mContext = mContext;
        helper=new TaskDbHelper(mContext);



    }

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.task_item_layout, parent, false);

        return new TaskViewHolder(view);


    }

    @Override
    public void onBindViewHolder(final  TaskViewHolder holder, final int position) {


        mCursor.moveToPosition(position);
        int idIndex = mCursor.getColumnIndex(TaskContract.TaskEntry._ID);
        int descriptionIndex = mCursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_DESCRIPTION);

        String description = mCursor.getString(descriptionIndex);
        int id1=mCursor.getInt(idIndex);

        holder.taskDescriptionView.setText(description);
        holder.itemView.setTag(id1);


        Cursor mcursor = getData(position + 1);
        if (mcursor != null && mcursor.getCount()!=0 && mcursor.getCount()!=-1){
            mcursor.moveToFirst();
        valGlobal = mcursor.getInt(mcursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_ISCOMPLETED));
        if (valGlobal == 1)
            holder.mCompleteTaskCheck.setChecked(true);
        else
            holder.mCompleteTaskCheck.setChecked(false);

    }
        holder.mCompleteTaskCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.v("Data Position ",""+position);

                Cursor mcursorT = getData(position + 1);
                if(mcursorT!=null && mcursorT.getCount()!=0) {
                    mcursorT.moveToFirst();

                    valGlobal = mcursorT.getInt(mcursorT.getColumnIndex(TaskContract.TaskEntry.COLUMN_ISCOMPLETED));
                }
                if( valGlobal==0){


                    Cursor mcursor=getData(position+1);
                    if(mcursor!=null && mcursor.getCount()!=0) {
                        mcursor.moveToFirst();
                        valGlobal = mcursor.getInt(mcursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_ISCOMPLETED));
                    }
                    Log.v("Before value global",""+  valGlobal+ "position "+position);
                    boolean isupdated=updateContact(position+1,1);
                    if(isupdated){
                        Cursor mcursor1=getData(position+1);
                        if(mcursor1!=null && mcursor1.getCount()!=0) {
                            mcursor1.moveToFirst();
                            valGlobal = mcursor1.getInt(mcursor1.getColumnIndex(TaskContract.TaskEntry.COLUMN_ISCOMPLETED));
                        }
                        Log.v("After value global "," "+  valGlobal+ " position "+position);



                        }

                    Log.v("Printing updated "," "+ valGlobal);



                }else {

                    Cursor mcursor=getData(position+1);
                    if(mcursor!=null && mcursor.getCount()!=0) {
                        mcursor.moveToFirst();
                        valGlobal = mcursor.getInt(mcursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_ISCOMPLETED));
                    }
                    Log.v("Else Before value "," "+  valGlobal+ " position "+position);


                    boolean isupdated=updateContact(position+1,0);
                    if(isupdated){
                        Cursor mcursor1=getData(position+1);
                        if(mcursor1!=null && mcursor1.getCount()!=0) {
                            mcursor1.moveToFirst();
                            valGlobal = mcursor1.getInt(mcursor1.getColumnIndex(TaskContract.TaskEntry.COLUMN_ISCOMPLETED));
                        }
                        Log.v("Else After value global","  "+  valGlobal+ " position  "+position);



                    }


                }
            }
        });




               //   Log.v(LOG_TAG,""+description+"  "+ischeckTask+" "+checkedtaskIndex);


    }



        public Cursor swapCursor(Cursor c) {

        if (mCursor == c) {
            return null;
        }
        Cursor temp = mCursor;
        this.mCursor = c;

        if (c != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }


    class TaskViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{


        TextView taskDescriptionView;
        CheckBox mCompleteTaskCheck;




        public TaskViewHolder(View itemView) {
            super(itemView);

            taskDescriptionView = (TextView) itemView.findViewById(R.id.taskDescription);
            mCompleteTaskCheck=itemView.findViewById(R.id.task_checkbox);
            itemView.setClickable(true);
            itemView.setOnClickListener(this);




        }


        @Override
        public void onClick(View view) {

            if (mItemClickListener != null) {
                mItemClickListener.OnItemClick(view, getAdapterPosition());
            }

        }
    }


    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }


    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }


//    public boolean insertContact (String name, String phone, String email, String street,String place) {
//        SQLiteDatabase db = helper.getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//        contentValues.put("name", name);
//        contentValues.put("phone", phone);
//        contentValues.put("email", email);
//        contentValues.put("street", street);
//        contentValues.put("place", place);
//        db.insert("contacts", null, contentValues);
//        return true;
//    }
    public Cursor getData(int id) {

        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from tasks where _id="+id+"", null );
        return res;
    }
    public boolean updateContact (Integer id,int val) {

        SQLiteDatabase db =helper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("completed", val);
        db.update("tasks", contentValues, "_id = ? ", new String[]{Integer.toString(id)});
        return true;
    }
}

