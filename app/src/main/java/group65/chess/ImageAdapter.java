package group65.chess;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TableLayout;


public class ImageAdapter extends BaseAdapter {
    private Context mContext;

    public ImageAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        return mThumbIds.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if(convertView == null) { // if it's not recycled, initialized some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.MATCH_PARENT));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(0, 0, 0, 0);

            // set appropriate chessboard color
            if((position/8) %2 == 0) {
                if(position%2 == 0)
                    imageView.setBackgroundColor(Color.parseColor("#ffce9e"));
                else
                    imageView.setBackgroundColor(Color.parseColor("#d18b47"));
            }
            else {
                if(position %2 == 0)
                    imageView.setBackgroundColor(Color.parseColor("#d18b47"));
                else
                    imageView.setBackgroundColor(Color.parseColor("#ffce9e"));
            }
        } else {
            imageView = (ImageView)convertView;
        }

        imageView.setImageResource(mThumbIds[position]);
        return imageView;
    }

    // references to pieces (images)
    private Integer[] mThumbIds = {
            R.drawable.b_rook, R.drawable.b_knight, R.drawable.b_bishop, R.drawable.b_queen,
            R.drawable.b_king, R.drawable.b_bishop, R.drawable.b_knight, R.drawable.b_rook,
            R.drawable.b_pawn, R.drawable.b_pawn, R.drawable.b_pawn, R.drawable.b_pawn,
            R.drawable.b_pawn, R.drawable.b_pawn, R.drawable.b_pawn, R.drawable.b_pawn,
            R.drawable.blank, R.drawable.blank, R.drawable.blank, R.drawable.blank,
            R.drawable.blank, R.drawable.blank, R.drawable.blank, R.drawable.blank,
            R.drawable.blank, R.drawable.blank, R.drawable.blank, R.drawable.blank,
            R.drawable.blank, R.drawable.blank, R.drawable.blank, R.drawable.blank,
            R.drawable.blank, R.drawable.blank, R.drawable.blank, R.drawable.blank,
            R.drawable.blank, R.drawable.blank, R.drawable.blank, R.drawable.blank,
            R.drawable.blank, R.drawable.blank, R.drawable.blank, R.drawable.blank,
            R.drawable.blank, R.drawable.blank, R.drawable.blank, R.drawable.blank,
            R.drawable.w_pawn, R.drawable.w_pawn, R.drawable.w_pawn, R.drawable.w_pawn,
            R.drawable.w_pawn, R.drawable.w_pawn, R.drawable.w_pawn, R.drawable.w_pawn,
            R.drawable.w_rook, R.drawable.w_knight, R.drawable.w_bishop, R.drawable.w_queen,
            R.drawable.w_king, R.drawable.w_bishop, R.drawable.w_knight, R.drawable.w_rook
    };
}