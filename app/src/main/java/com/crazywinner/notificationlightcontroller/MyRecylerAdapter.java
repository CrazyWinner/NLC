package com.crazywinner.notificationlightcontroller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;


import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;

/**
 * mertcan tarafindan 17.01.2017 tarihinde olusturuldu.
 */

class MyRecylerAdapter extends RecyclerView.Adapter<MyRecylerAdapter.ViewHolder> {
    SqlController SQL;
    Context myContext;
    MyRecylerAdapter(Context context, ArrayList<ColorPackage> packagess){
        myContext=context;
        packages=packagess;
        SQL=new SqlController(context);
    }
    ArrayList<ColorPackage> packages;
static class ViewHolder extends RecyclerView.ViewHolder {
    //a simple viewholder
CardView myCardView;
    ViewHolder(CardView card){
        super(card);
        myCardView=card;
    }

}
    void setPackages(ArrayList<ColorPackage> paa){
        //set our data
        packages=paa;
        notifyDataSetChanged();
    }
    public void addPackage(ColorPackage p){
        //insert a package
        packages.add(p);
        notifyItemInserted(packages.size());
    }
    public void removePackage(ColorPackage p){
        //remove a package
        int x = packages.indexOf(p);
        packages.remove(p);

        notifyItemRemoved(x);
    }
    public void refreshColor(ColorPackage x){
        for(int i =0;i<packages.size();i++){
            if(packages.get(i).packageName.equals(x.packageName)){
                packages.set(i,x);
                notifyDataSetChanged();
                break;
            }

        }

    }
    @Override
    public MyRecylerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        CardView v = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recylersatir, parent, false);

        return new ViewHolder(v);
    }
    private String getAppLabel(String x){
        //get application's label from packagename
        ApplicationInfo i= null;
        try {
            i = myContext.getPackageManager().getApplicationInfo(x,0);
        } catch (PackageManager.NameNotFoundException e) {
            return x;
        }
        return myContext.getPackageManager().getApplicationLabel(i).toString();

    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        // - set some view's data
        ColorPackage myPack=packages.get(position);
        TextView tw=(TextView) holder.myCardView.findViewById(R.id.textViewAd);
        tw.setText(getAppLabel(myPack.packageName));
        //set tag it's important because you will get packagename from tag
        holder.myCardView.setTag(myPack);
        Button edit=(Button) holder.myCardView.findViewById(R.id.buttonEdit);
        ((TextView)holder.myCardView.findViewById(R.id.textView4)).setText(myPack.duration+"ms");
        ((ImageView)holder.myCardView.findViewById(R.id.imageView4)).setColorFilter(Color.rgb(((myPack.color>>16)&0xFF),((myPack.color>>8)&0xFF),(myPack.color)&0xFF));
        final ImageView img=(ImageView)holder.myCardView.findViewById(R.id.imageView);
        //set an listener for edit button if clicked create a editcoloractivity with editmode=true
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(myContext,EditColorActivity.class);
                ColorPackage pack=(ColorPackage) holder.myCardView.getTag();
                i.putExtra("RGB",pack.color);
                i.putExtra("duration",pack.duration);
                i.putExtra("package",pack.packageName);
                i.putExtra("edit",true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                    ((MainActivity)myContext).startActivityForResult(i,1, ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) myContext,holder.myCardView,"cardTrans").toBundle());
            }else{((MainActivity)myContext).startActivityForResult(i,1);}}
        });
        Button delete=(Button)holder.myCardView.findViewById(R.id.buttonDelete);
        //set a listener for delete button if clicked delete the package but store it.
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               final ColorPackage myyPackage=(ColorPackage) holder.myCardView.getTag();
               removePackage(myyPackage);
               SQL.deleteColor(((ColorPackage) holder.myCardView.getTag()).packageName);
                Snackbar.make(((MainActivity)myContext).findViewById(R.id.main_content),"Notification deleted",Snackbar.LENGTH_LONG)
                       //If user clicks undo from snackbar add it again
                        .setAction("Undo", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ((MainActivity)myContext).addPackage(myyPackage);
                            }
                        }).show();
            }
        });
        boolean yap=true;
        Drawable d = null;
        try {
            d = myContext.getPackageManager().getApplicationIcon(myPack.packageName);
        } catch (PackageManager.NameNotFoundException e) {
            yap=false;
            e.printStackTrace();
        }
        if(yap){

            img.setImageDrawable(d);


        }


    }

    @Override
    public int getItemCount() {
        return packages.size();
    }
}
