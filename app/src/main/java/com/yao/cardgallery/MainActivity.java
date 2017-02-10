package com.yao.cardgallery;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    CardGallery cardGallery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cardGallery = (CardGallery) findViewById(R.id.card_gallery);

        cardGallery.setAdapter(new CardGallery.Adapter(cardGallery, new PicAdapter()));

    }
}
