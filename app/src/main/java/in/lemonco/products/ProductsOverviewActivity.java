package in.lemonco.products;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

/*
 * ProductsOverviewActivity displays the existing product items
 * in a list
 *To add a new product click on "Add product" button
 * You can delete existing ones via a long press on the item
 */

public class ProductsOverviewActivity extends ListActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {
    private static final int DELETE_ID = Menu.FIRST + 1;
    private SimpleCursorAdapter adapter;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventory_list);
        this.getListView().setDividerHeight(2);
        fillData();
        registerForContextMenu(getListView());
        Button addProductButton = (Button) findViewById(R.id.addProductButton);
        addProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductsOverviewActivity.this, InventoryDetailActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case DELETE_ID:
                AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
                        .getMenuInfo();
                Uri uri = Uri.parse(MyInventoryContentProvider.CONTENT_URI + "/"
                        + info.id);
                getContentResolver().delete(uri, null, null);
                fillData();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    // Opens the second activity if an entry is clicked
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent i = new Intent(this, InventoryDetailActivity.class);
        Uri productUri = Uri.parse(MyInventoryContentProvider.CONTENT_URI + "/" + id);
        i.putExtra(MyInventoryContentProvider.CONTENT_ITEM_TYPE, productUri);

        startActivity(i);
    }


    private void fillData() {

        // Fields from the database (projection)
        // Must include the _id column for the adapter to work
        String[] from = new String[]{InventoryTable.COLUMN_NAME, InventoryTable.COLUMN_PRICE, InventoryTable.COLUMN_QUANTITY, InventoryTable.COLUMN_SALES};
        // Fields on the UI to which we map
        int[] to = new int[]{R.id.productName_value, R.id.price_value, R.id.availableInventory_value, R.id.unitsSold_value,};

        getLoaderManager().initLoader(0, null, this);
        adapter = new CustomSimpleCursorAdapter(this, R.layout.inventory_row, null, from, to, 0);

        setListAdapter(adapter);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_ID, 0, getString(R.string.menu_delete));
    }

    // creates a new loader after the initLoader () call
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {InventoryTable.COLUMN_ID, InventoryTable.COLUMN_NAME, InventoryTable.COLUMN_PRICE, InventoryTable.COLUMN_QUANTITY, InventoryTable.COLUMN_SALES};
        CursorLoader cursorLoader = new CursorLoader(this,
                MyInventoryContentProvider.CONTENT_URI, projection, null, null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // data is not available anymore, delete reference
        adapter.swapCursor(null);
    }


}