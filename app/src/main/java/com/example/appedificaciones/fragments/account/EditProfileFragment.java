package com.example.appedificaciones.fragments.account;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.appedificaciones.R;
import com.example.appedificaciones.SharedViewModel;
import com.example.appedificaciones.model.database.AppDatabase;
import com.example.appedificaciones.model.database.EdificationRepository;
import com.example.appedificaciones.model.ent.UserEntity;
import com.example.appedificaciones.StorageUtils;


import java.io.File;
import java.util.concurrent.Executors;


public class EditProfileFragment extends Fragment {

    private EditText edtUserName, edtEmail, edtPhone, edtPasword;
    private Button btnSave;
    private int idUser;
    private Uri imageUri;
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imageViewProfile;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private String fileName;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);


        edtUserName = view.findViewById(R.id.edtUserName);
        edtEmail = view.findViewById(R.id.edtEmail);
        edtPasword = view.findViewById(R.id.edtPassword);
        edtPhone = view.findViewById(R.id.edtPhone);

        btnSave = view.findViewById(R.id.btnSave);
        imageViewProfile = view.findViewById(R.id.imageViewProfile);
        Button btnSelectImage = view.findViewById(R.id.btnSelectImage);

        fileName = "default_image.jpg";
        // Inicializa el launcher para manejar el resultado de la selección de imagen
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        // Obtener el nombre del archivo desde el URI
                        fileName = StorageUtils.getFileNameFromUri(getContext(), imageUri);
                        if (fileName == null || fileName.isEmpty()) {
                            fileName = "default_image.jpg"; // Nombre por defecto si no se puede obtener
                        }
                        //imageViewProfile.setImageURI(imageUri); // Muestra la imagen seleccionada
                        Glide.with(this)
                                .load(imageUri)
                                .into(imageViewProfile);

                    }
                });

        // Configura el botón para abrir la galería
        btnSelectImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        // Obtener el SharedViewModel
        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Observar los cambios en el objeto us
        // erLogged
        sharedViewModel.getUserLogged().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                idUser = user.getUserId();
                edtUserName.setText(user.getUser());
                edtEmail.setText(user.getEmail());
                edtPasword.setText(user.getPassword());
                edtPhone.setText(user.getPhone());
                if(user.getPhoto() != null){
                    Glide.with(this)
                        .load(new File(user.getPhoto()))
                        .placeholder(R.drawable.userlogo)  // Imagen de carga
                        .error(R.drawable.userlogo)
                        .into(imageViewProfile);
                }

            } else {
                Log.d("GGG", "No se encontró nombre de usuario");
            }
        });

        // Acciones del botón guardar
        btnSave.setOnClickListener(v -> {
            String newUserName = edtUserName.getText().toString();
            String newEmail = edtEmail.getText().toString();
            String newPassword = edtPasword.getText().toString();
            String newPhone = edtPhone.getText().toString();

            UserEntity userActual = sharedViewModel.getUserLogged().getValue();

            UserEntity updateUser = new UserEntity.Builder()
                    .setUserId(idUser)
                    .setUser(newUserName)
                    .setPassword(newPassword)
                    .setEmail(newEmail)
                    .setPhone(newPhone)
                    .setPhoto(userActual.getPhoto())
                    .build();

            if (imageUri != null) {
                Log.d("NUEVO URI", imageUri.toString());
                // Si hay una foto anterior, eliminarla
                if (userActual != null && userActual.getPhoto() != null) {
                    boolean deleted = StorageUtils.deleteImageFromInternalStorage(userActual.getPhoto());
                    if (deleted) {
                        Log.d("EditProfile", "Imagen anterior eliminada correctamente");
                    } else {
                        Log.d("EditProfile", "No se pudo eliminar la imagen anterior");
                    }
                }
                String photoPath = StorageUtils.saveImageToInternalStorage(getContext(), imageUri, fileName);
                updateUser.setPhoto(photoPath);
            }

            EdificationRepository repository = new EdificationRepository(AppDatabase.getInstance(requireContext()));

            Executors.newSingleThreadExecutor().execute(() -> {
                repository.updateUser(updateUser);
            });

            sharedViewModel.setUserLogged(updateUser);

            // Regresar al fragmento anterior
            requireActivity().getSupportFragmentManager().popBackStack();
        });


        return view;
    }
}