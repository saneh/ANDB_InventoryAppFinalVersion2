package in.lemonco.products;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * This class allows to edit a detail view of the inventory.
 * Also has multiple functions like Sell a product, palce shipment order, receive shipment, delete product, update product
 */
public class InventoryDetailActivity extends Activity {
    private EditText mProductName;
    private EditText mPrice;
    private EditText mQuantity;
    private EditText mSales;
    private EditText mSupplier;
    private EditText mImage;

    private Uri productUri;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.inventory_edit);

        mProductName = (EditText) findViewById(R.id.productName_edit);
        mPrice = (EditText) findViewById(R.id.price_edit);
        mQuantity = (EditText) findViewById(R.id.availableInventory_edit);
        mSales = (EditText) findViewById(R.id.sales_edit);
        mSupplier = (EditText) findViewById(R.id.supplier_edit);
        mImage = (EditText) findViewById(R.id.image_edit);

        Button updateButton = (Button) findViewById(R.id.updateButton);
        Button deleteButton = (Button) findViewById(R.id.deleteButton);
        Button receiveShipmentButton = (Button) findViewById(R.id.receive_shipment_button);
        Button orderShipmentButton = (Button) findViewById(R.id.orderShipmentButton);

        Bundle extras = getIntent().getExtras();

        // check from the saved Instance
        productUri = (bundle == null) ? null : (Uri) bundle
                .getParcelable(MyInventoryContentProvider.CONTENT_ITEM_TYPE);

        // Or passed from the other activity
        if (extras != null) {
            productUri = extras
                    .getParcelable(MyInventoryContentProvider.CONTENT_ITEM_TYPE);

            fillData(productUri);
        } else {
            //Make sell, delete, receiveShipment and orderShipment button Invisible
            deleteButton.setVisibility(View.GONE);
            receiveShipmentButton.setVisibility(View.GONE);
            orderShipmentButton.setVisibility(View.GONE);
        }

        updateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (TextUtils.isEmpty(mProductName.getText().toString())) {
                    makeToast(getApplicationContext().getResources().getString(R.string.toast_emptyProductName));
                } else if (TextUtils.isEmpty(mPrice.getText().toString())) {
                    makeToast(getApplicationContext().getResources().getString(R.string.toast_emptyPrice));
                } else if (TextUtils.isEmpty(mQuantity.getText().toString())) {
                    makeToast(getApplicationContext().getResources().getString(R.string.toast_emptyQuantity));
                } else if (TextUtils.isEmpty(mSupplier.getText().toString())) {
                    makeToast(getApplicationContext().getResources().getString(R.string.toast_emptySupplier));
                } else if (TextUtils.isEmpty(mImage.getText().toString())) {
                    makeToast(getApplicationContext().getResources().getString(R.string.toast_emptyImage));
                } else if (TextUtils.isEmpty(mSales.getText().toString())) {
                    makeToast(getApplicationContext().getResources().getString(R.string.toast_emptySales));
                } else {
                    setResult(RESULT_OK);
                    finish();
                }
            }

        });

        //delete buttons deletes the particular record after user confirmation
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(InventoryDetailActivity.this)
                        .setTitle(R.string.delete_entry_title)
                        .setMessage(R.string.delete_message)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                getContentResolver().delete(productUri, null, null);
                                finish();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
        //orderShipmentOrder button sends an intent to email app. With prefilled mailing address, subject and mail text
        orderShipmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/html");
                String productName = mProductName.getText().toString();
                intent.putExtra(Intent.EXTRA_EMAIL, mSupplier.getText().toString());
                intent.putExtra(Intent.EXTRA_SUBJECT, "Request for order of item: " + productName);
                intent.putExtra(Intent.EXTRA_TEXT, "Hi \n Kindly dispatch new order of following item\n" + productName);

                startActivity(Intent.createChooser(intent, "Send Email"));

            }
        });

        //receiveShipment button Listener. Generates a dia
        receiveShipmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(InventoryDetailActivity.this);
                builder.setTitle(R.string.receive_shipment_msg);

                // Set up the input
                final EditText input = new EditText(InventoryDetailActivity.this);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder.setView(input);
                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int shipmentQuantity = Integer.parseInt(input.getText().toString());
                        mQuantity.setText(String.valueOf(Integer.parseInt(mQuantity.getText().toString()) + shipmentQuantity));
                        saveState();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

    }

    private void fillData(Uri uri) {
        String[] projection = {InventoryTable.COLUMN_NAME, InventoryTable.COLUMN_SALES, InventoryTable.COLUMN_PRICE, InventoryTable.COLUMN_QUANTITY, InventoryTable.COLUMN_SUPPLIER, InventoryTable.COLUMN_IMAGE};
        Cursor cursor = getContentResolver().query(uri, projection, null, null,
                null);
        if (cursor != null) {
            cursor.moveToFirst();
            mProductName.setText(cursor.getString(cursor
                    .getColumnIndexOrThrow(InventoryTable.COLUMN_NAME)));
            mPrice.setText(String.valueOf(cursor.getInt(cursor
                    .getColumnIndexOrThrow(InventoryTable.COLUMN_PRICE))));
            mQuantity.setText(String.valueOf(cursor.getInt(cursor
                    .getColumnIndexOrThrow(InventoryTable.COLUMN_QUANTITY))));
            mSales.setText(String.valueOf(cursor.getInt(cursor
                    .getColumnIndexOrThrow(InventoryTable.COLUMN_SALES))));
            mSupplier.setText(cursor.getString(cursor
                    .getColumnIndexOrThrow(InventoryTable.COLUMN_SUPPLIER)));
            mImage.setText(cursor.getString(cursor
                    .getColumnIndexOrThrow(InventoryTable.COLUMN_IMAGE)));

            // always close the cursor
            cursor.close();
        }
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
        outState.putParcelable(MyInventoryContentProvider.CONTENT_ITEM_TYPE, productUri);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveState();
    }

    private void saveState() {
        String productName = mProductName.getText().toString();
        String price = mPrice.getText().toString();
        String quantity = mQuantity.getText().toString();
        String sales = mSales.getText().toString();
        String supplier = mSupplier.getText().toString();
        String image = mImage.getText().toString();

        // only save if either summary or description
        // is available

        if (productName.length() == 0 || price.length() == 0 || quantity.length() == 0 || supplier.length() == 0 || sales.length() == 0) {
            return;
        }

        try {
            ContentValues values = new ContentValues();
            values.put(InventoryTable.COLUMN_NAME, productName);
            values.put(InventoryTable.COLUMN_PRICE, Integer.parseInt(price));
            values.put(InventoryTable.COLUMN_QUANTITY, Integer.parseInt(quantity));
            values.put(InventoryTable.COLUMN_SALES, Integer.parseInt(sales));
            values.put(InventoryTable.COLUMN_SUPPLIER, supplier);
            values.put(InventoryTable.COLUMN_IMAGE, image);
            if (productUri == null) {
                // New product
                productUri = getContentResolver().insert(
                        MyInventoryContentProvider.CONTENT_URI, values);
            } else {
                // Update product
                getContentResolver().update(productUri, values, null, null);
            }
        } catch (NumberFormatException ex) {
            Toast.makeText(this, R.string.toast_numberFormatException, Toast.LENGTH_LONG).show();
        }


    }

    //method to validate input text
    private void makeToast(String field){
       Toast.makeText(InventoryDetailActivity.this,"Please enter a valid "+field,Toast.LENGTH_SHORT).show();
    }


}