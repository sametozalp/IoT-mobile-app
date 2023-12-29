package com.ozalp.iot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ozalp.iot.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addLogoAppBar();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();

        seekBarListener();

        temperatureListener();

        getInitialValueSeekBarInFirebase();

    }

    private void temperatureListener() {
        DatabaseReference myRef = database.getReference("temperature");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                binding.textView3.setText("Evinizin güncel sıcaklığı: " + snapshot.getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Hata oluştu: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getInitialValueSeekBarInFirebase() {
        database.getReference()
                .child("settedTemperature")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task != null) {
                            int value = Integer.parseInt(task.getResult().getValue().toString());
                            binding.settedTemperatureTextView.setText("Ayarlanan sıcaklık: " + value);
                            seekBar.setProgress(value);

                        }
                    }
                });
    }

    private void seekBarListener() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                binding.settedTemperatureTextView.setText("Ayarladığınız sıcaklık: " + progress);
                MainActivity.this.progress = progress;
                System.out.println(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void init() {
        seekBar = binding.seekBar;
        database = FirebaseDatabase.getInstance();
    }

    public void sendToFirebase(View view) {
        DatabaseReference ref = database.getReference("settedTemperature");
        ref.setValue(progress).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(), "Ayarlama Başarılı!", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Ayarlama Başarısız!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void addLogoAppBar() {
        getSupportActionBar().setLogo(R.drawable.rm_logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.gradient_background);
        getSupportActionBar().setBackgroundDrawable(drawable);
    }

    private ActivityMainBinding binding;
    private SeekBar seekBar;
    private FirebaseDatabase database;
    private int progress;
}