package com.mvvm.demo.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mvvm.demo.R;
import com.mvvm.demo.db.category.CategoryEntity;
import com.mvvm.demo.db.category.CategoryViewModel;
import com.mvvm.demo.interfaces.CalltoParent;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChildCategoryFragment extends Fragment {

    final String TAG = getClass().getName();
    @BindView(R.id.ccf_rv)
    RecyclerView rv;
    CategoryViewModel categoryViewModel;
    List<CategoryEntity> list;
    ChildCategoryAdapter categoryAdapter;
    int id = 0;
    boolean proceed = false;
    CalltoParent calltoParent;

    @OnClick(R.id.ccf_btnback)
    public void onBack(){
        getActivity().getSupportFragmentManager().popBackStack();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
       calltoParent = (CategoryFragment)this.getTargetFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.childcategoryfragment,container,false);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        id = getArguments().getInt("id");
        Log.d(TAG,"id is "+id);
        list = new ArrayList<>();
        rv.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        categoryAdapter=new ChildCategoryAdapter();
        rv.setAdapter(categoryAdapter);
        categoryViewModel = ViewModelProviders.of(this).get(CategoryViewModel.class);
        new AsyncDbCall().execute();

    }

    class  AsyncDbCall extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            categoryViewModel.getChildList(id).observe(ChildCategoryFragment.this, new Observer<List<CategoryEntity>>() {
                @Override
                public void onChanged(@Nullable List<CategoryEntity> categoryEntities) {
                    if (categoryEntities != null && categoryEntities.size() != 0) {
                        list.clear();
                        list.addAll(categoryEntities);
                        Log.d(TAG,"found");
                        proceed = true;
                    }else{
                        Log.d(TAG,"not found");
                        proceed = false;
                    }
                    onpostexecute();
                }
            });
            return null;
        }

        @UiThread
        private void onpostexecute(){
            try {
                if (proceed) {
                    categoryAdapter.notifyDataSetChanged();
                } else {
                    Log.d(TAG, "child not present now");
//                    ProductFragment f = new ProductFragment();
//                    getActivity().getSupportFragmentManager().beginTransaction().add(R.id.am_mainfl,f).addToBackStack(null).commit();
                    getActivity().getSupportFragmentManager().popBackStack();
                    calltoParent.onpreviouscallback(id);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    class ChildCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            //if(viewType==1){
            view= LayoutInflater.from(parent.getContext()).inflate(R.layout.categoryrow,parent,false);
            return new RegularRow(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            CategoryEntity cdb = list.get(position);
            RegularRow
                    row = ((RegularRow)holder);
            row.tv.setText(cdb.getName());
        }

        @Override
        public int getItemCount() {
            if(list!=null && list.size()!=0){
                return list.size();
            }else{
                return 0;
            }
        }

        class RegularRow extends RecyclerView.ViewHolder implements View.OnClickListener{

            @BindView(R.id.cr_tv)
            TextView tv;


            public RegularRow(View itemView) {
                super(itemView);
                ButterKnife.bind(this,itemView);
                itemView.setOnClickListener(this);

            }

            @Override
            public void onClick(View view) {
                id = list.get(getAdapterPosition()).getId();
                ChildCategoryFragment childCategoryFragment = new ChildCategoryFragment();
                Bundle b = new Bundle();
                b.putInt("id",id);
                childCategoryFragment.setArguments(b);
                childCategoryFragment.setTargetFragment(ChildCategoryFragment.this.getTargetFragment(),100);
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.am_nvfl,childCategoryFragment).addToBackStack(null).commit();
//                new AsyncDbCall().execute();
            }
        }

    }
}
