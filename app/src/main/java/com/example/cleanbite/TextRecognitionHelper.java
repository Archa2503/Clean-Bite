package com.example.cleanbite;

import android.graphics.Bitmap;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import androidx.annotation.NonNull;

public class TextRecognitionHelper {

    public interface TextRecognitionListener {
        void onTextRecognitionSuccess(String recognizedText);
        void onTextRecognitionFailure(Exception e);
    }

    public static void recognizeText(Bitmap bitmap, TextRecognitionListener listener) {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionTextRecognizer textRecognizer = FirebaseVision.getInstance().getOnDeviceTextRecognizer();

        textRecognizer.processImage(image)
                .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                    @Override
                    public void onSuccess(FirebaseVisionText firebaseVisionText) {
                        String recognizedText = firebaseVisionText.getText();
                        listener.onTextRecognitionSuccess(recognizedText);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onTextRecognitionFailure(e);
                    }
                });
    }
}