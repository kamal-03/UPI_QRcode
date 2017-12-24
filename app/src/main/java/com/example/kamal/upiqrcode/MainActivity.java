package com.example.kamal.upiqrcode;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.encoder.QRCode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity {
    private static final String TAG ="My Activity" ;
    EditText upiVPA,transRef,transCmnt,amount,currCode ,payee;
    TextView msg;
    Button generate, download;
    ImageView QRcode;
    Bitmap image;
    private String URL,payeeaddr,payeename,transrefid,transrefmsg,amnt,code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        msg=findViewById(R.id.message) ;
        upiVPA=findViewById(R.id.upiaddress);
        payee=findViewById(R.id.payeename);
        transRef=findViewById(R.id.transrefid);
        transCmnt=findViewById(R.id.transcmnt);
        amount=findViewById(R.id.transamnt);
        currCode=findViewById(R.id.currcode);
        generate=findViewById(R.id.btn_gen);
        QRcode=findViewById(R.id.imageView);
        download=findViewById(R.id.btn_down);

        final QRCodeWriter writer = new QRCodeWriter();



        generate.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               payeeaddr=upiVPA.getText().toString();
               payeename=payee.getText().toString();
               transrefid=transRef.getText().toString();
               transrefmsg=transCmnt.getText().toString();
               amnt=amount.getText().toString();
               code=currCode.getText().toString();
               URL=getUPIString(payeeaddr,payeename,transrefid,transrefmsg,amnt,code);
               BitMatrix bitMatrix = null;
               try {

                   bitMatrix = writer.encode(URL, BarcodeFormat.QR_CODE, 512, 512);
               } catch (WriterException e) {
                   e.printStackTrace();
               }

               int width = bitMatrix.getWidth();
               int height = bitMatrix.getHeight();
               Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
               for (int x = 0; x < width; x++) {
                   for (int y = 0; y < height; y++) {
                       bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                   }
               }
               QRcode.setImageBitmap(bmp);
               image=bmp;
           }
       });
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaStore.Images.Media.insertImage(getContentResolver(), image,"qrcode","hello");
            }
        });


    }
    private String getUPIString(String payeeAddress, String payeeName, String trxnRefId,
                                String trxnNote, String payeeAmount, String currencyCode) {
        String UPI = "upi://pay?pa=" + payeeAddress + "&pn=" + payeeName
                + "&tr=" + trxnRefId
                + "&tn=" + trxnNote + "&am=" + payeeAmount + "&cu=" + currencyCode;
        return UPI.replace(" ", "+");}
    private void storeImage(Bitmap image) {
        File pictureFile = getOutputMediaFile();
        if (pictureFile == null) {
            Log.d(TAG,
                    "Error creating media file, check storage permissions: ");// e.getMessage());
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }
    }
    private  File getOutputMediaFile(){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this. 
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + getApplicationContext().getPackageName()
                + "/Files");

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        File mediaFile;
        String mImageName="MI_"+ timeStamp +".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }
}
