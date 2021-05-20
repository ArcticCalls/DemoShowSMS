package sg.edu.rp.c347.id19045104.demoshowsms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.net.ContentHandler;

public class MainActivity extends AppCompatActivity {

    TextView tvSMS;
    Button btnRetrieve;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvSMS = findViewById(R.id.tv);
        btnRetrieve = findViewById(R.id.btnRetrieve);
        btnRetrieve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int permissionCheck = PermissionChecker.checkSelfPermission(MainActivity.this, Manifest.permission.READ_SMS);

                if(permissionCheck != PermissionChecker.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_SMS}, 0);
                    // stops the action from proceeding further as permission not
                    // granted yet
                    return;
                }

                // create all messages uri
                Uri uri = Uri.parse("content://sms");
                // The columns we want
                // date is when the message took place
                // address is the number of the other party
                // body is the message contetn
                // type 1 is retrieved, type 2 sent
                String[] reqCols = new String[]{"date","address","body","type"};

                // Get Content Resolver object which to
                // query the content provider
                ContentResolver cr = getContentResolver();

                // The filter string
                String filter = "body LIKE ? and body LIKE ?";
                String[] filterArgs = {"%late%", "%min%"};
                // Fetch sms message from Built-in Content-Resolver
                Cursor cursor = cr.query(uri, reqCols, filter, filterArgs,null);
                String smsbody = "";
                if(cursor.moveToFirst()){
                    do{
                        long dateInMillis = cursor.getLong(0);
                        String date = (String) DateFormat.format("dd MM yyyy h:mm:ss aa",dateInMillis);
                        String address = cursor.getString(1);
                        String body = cursor.getString(2);
                        String type = cursor.getString(3);
                        if(type.equalsIgnoreCase("1")){
                            type = "Inbox:";
                        }else{
                            type = "Sent:";
                        }
                        smsbody += type + " " + address + "\n at" + date + "\n\"" + body + "\"\n\n";

                    } while (cursor.moveToNext());
                }
                tvSMS.setText(smsbody);
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode){
            case 0:
                // if request is cancelled, the result arrays are empty
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                    // Permission granted read the sms
                    // as if the btnRetrieve is click
                    btnRetrieve.performClick();
                }else {
                    // permission denied... notify user
                    Toast.makeText(MainActivity.this, "Permission not granted",Toast.LENGTH_SHORT).show();
                }
        }

    }
}