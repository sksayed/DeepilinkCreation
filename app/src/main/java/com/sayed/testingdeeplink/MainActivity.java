package com.sayed.testingdeeplink;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

public class MainActivity extends AppCompatActivity {
    private String TAG = "sayed";
    private TextView newtext;
    private EditText input;
    private Button createLink;
    private Button goTo;
    private DynamicLink link ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initContents();
        initListeners();
    }

    private void initListeners() {
        createLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newtext.setText(createDynamicLink(input.getText().toString()));
               // createAndShareShortDynamicLink();
            }
        });

        goTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (link != null) {
                    Intent internetIntent = new Intent(Intent.ACTION_VIEW);
                   // internetIntent.setComponent(new ComponentName("com.android.browser", "com.android.browser.BrowserActivity"));
                  //  internetIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    internetIntent.setData(link.getUri());
                    startActivity(internetIntent);
                }
            }
        });

    }

    private void initContents() {
        newtext = findViewById(R.id.text);
        newtext.setMovementMethod(LinkMovementMethod.getInstance());
        input = findViewById(R.id.input);
        createLink = findViewById(R.id.createLink);
        goTo = findViewById(R.id.goTo);
    }

    private String createDynamicLink(String inputData) {
        link = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://www.example.com/?orderId=" + inputData))
                .setDomainUriPrefix("https://dailysense.page.link/")
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder("com.dailygoods.dailysense").build()) //com.melardev.tutorialsfirebase
                .setSocialMetaTagParameters(new DynamicLink.SocialMetaTagParameters.Builder().setTitle("Share this App").setDescription("blabla").build())
                .setGoogleAnalyticsParameters(new DynamicLink.GoogleAnalyticsParameters.Builder().setSource("AndroidApp").build())
                .buildDynamicLink();
        String path = link.getUri().toString();
        Log.i(TAG, path);

        return path;
    }

    public void createAndShareShortDynamicLink() {
        String xyz = input.getText().toString();
         FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLongLink(Uri.parse(createDynamicLink(xyz)))
                .buildShortDynamicLink()
                .addOnCompleteListener(this, new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {
                            // Short link created
                            Uri shortLink = task.getResult().getShortLink();
                            Uri flowchartLink = task.getResult().getPreviewLink(); //flowchart link is a debugging URL
                            newtext.setText(shortLink.toString());
                            Log.d(TAG, shortLink.toString());
                            Log.d(TAG, flowchartLink.toString());
                            /*Intent intent = new Intent();
                            String msg = "visit my awesome website: " + shortLink.toString();
                            intent.setAction(Intent.ACTION_SEND);
                            intent.putExtra(Intent.EXTRA_TEXT, msg);
                            intent.setType("text/plain");
                            startActivity(intent);*/

                        } else {
                            // Error
                            newtext.append("\nError building short link");

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
             @Override
             public void onFailure(@NonNull Exception e) {
                 Log.d(TAG , e.getMessage());
             }
         });

    }
}
