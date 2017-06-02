package com.lions.seefood;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import clarifai2.api.request.ClarifaiRequest;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.input.image.ClarifaiImage;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.prediction.Concept;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    ListView listView;
    Button button;
    ClarifaiClient client;
    byte[] Data;
    ArrayList<String> Foods = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        imageView = (ImageView)findViewById(R.id.imageView);


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, 67);

            }
        });
        listView = (ListView)findViewById(R.id.list);
        button = (Button)findViewById(R.id.button);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Data!=null)
                {
                    Get_Image_Details(Data);
                }



            }
        });



         client =         new ClarifaiBuilder("6z4RyMU9hVtJNGcrCI9cGLzujeRKLA4qqKRgk9-7", "yBKLs3O3BXemIm69ebZlWO0LDOmh5Xal7wYMlNk7").buildSync();








    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 67 && resultCode == Activity.RESULT_OK) {
            Bitmap bmp = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            Data = byteArray;
            imageView.setImageBitmap(bmp);

        }
    }


    public void Get_Image_Details( byte[] data)
    {


                client.getDefaultModels().foodModel() // You can also do Clarifai.getModelByID("id") to get custom models
                        .predict()
                        .withInputs(
                                ClarifaiInput.forImage(ClarifaiImage.of(data))
                        )

                        .executeAsync(new ClarifaiRequest.OnSuccess<List<ClarifaiOutput<Concept>>>() {
                            @Override
                            public void onClarifaiResponseSuccess(List<ClarifaiOutput<Concept>> clarifaiOutputs) {

                                Populate_List(clarifaiOutputs);


                            }
                        });
/*        for(int i=0;i<predictionResults.size();i++)
        {

            Log.d("inside_suggestions", ""+predictionResults.get(i).data().get(i).name());

        }
        */


    }


    public void Populate_List(List<ClarifaiOutput<Concept>> clarifaiOutputs)
    {
        final ArrayList<String> foods = new ArrayList<String>();

        for(int i=0;i<clarifaiOutputs.size();i++)
        {
            Log.d("inside_output",""+clarifaiOutputs+" ");
            for(int j=0;j<clarifaiOutputs.get(i).data().size();j++)
            {
                foods.add(""+clarifaiOutputs.get(i).data().get(j).name());

            }

        }


        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                ArrayAdapter arrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, foods);
                listView.setAdapter(arrayAdapter);
                Log.d("array_size",""+foods.size());            }
        });

    }
}
