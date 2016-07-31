package in.lemonco.products;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Custom SimpleCursorAdapter
 */
public class CustomSimpleCursorAdapter extends SimpleCursorAdapter {
    private Context mContext;
    private int layout;
    private final LayoutInflater inflater;
    private int name_index;
    private int quantity_index;
    private int price_index;
    private int sales_index;
    private int column_index;

    public CustomSimpleCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        this.layout = layout;
        this.mContext = context;
        this.inflater = LayoutInflater.from(context);

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return inflater.inflate(layout, null);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        super.bindView(view, context, cursor);

        TextView name = (TextView) view.findViewById(R.id.productName_value);
        TextView quantity = (TextView) view.findViewById(R.id.availableInventory_value);
        TextView price = (TextView) view.findViewById(R.id.price_value);
        TextView sales = (TextView) view.findViewById(R.id.unitsSold_value);

        name_index = cursor.getColumnIndexOrThrow(InventoryTable.COLUMN_NAME);
        quantity_index = cursor.getColumnIndexOrThrow(InventoryTable.COLUMN_QUANTITY);
        price_index = cursor.getColumnIndexOrThrow(InventoryTable.COLUMN_PRICE);
        sales_index = cursor.getColumnIndexOrThrow(InventoryTable.COLUMN_SALES);
        column_index = cursor.getColumnIndex(InventoryTable.COLUMN_ID);

        name.setText(cursor.getString(name_index));
        quantity.setText(String.valueOf(cursor.getInt(quantity_index)));
        price.setText(String.valueOf(cursor.getInt(price_index)));
        sales.setText(String.valueOf(cursor.getInt(sales_index)));
        int id = cursor.getInt(column_index);
        view.setTag(id); // to pass on "_id" value to on click listener
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final View view = super.getView(position, convertView, parent);
        Button sellButton = (Button) view.findViewById(R.id.sellButton);
        //setting the onClick listener of "SellButton" in each row
        sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = Integer.parseInt(((TextView) view.findViewById(R.id.availableInventory_value)).getText().toString());
                if(quantity==0){
                    Toast.makeText(view.getContext(),R.string.outOfStock_warning,Toast.LENGTH_SHORT).show();
                }else{
                int unitSold = Integer.parseInt(((TextView) view.findViewById(R.id.unitsSold_value)).getText().toString());
                ContentValues values = new ContentValues();
                values.put(InventoryTable.COLUMN_QUANTITY, quantity - 1);
                values.put(InventoryTable.COLUMN_SALES, unitSold + 1);
                int id = (Integer) view.getTag();
                mContext.getContentResolver().update(Uri.parse(MyInventoryContentProvider.CONTENT_URI + "/" + id), values, null, null);}
            }
        });
        return view;
    }
}
