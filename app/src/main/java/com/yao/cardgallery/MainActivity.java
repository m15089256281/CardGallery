package com.yao.cardgallery;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    CardGalleryView cardGalleryView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cardGalleryView = (CardGalleryView) findViewById(R.id.card_gallery);

        cardGalleryView.setAdapter(new PicAdapter());

    }
}
